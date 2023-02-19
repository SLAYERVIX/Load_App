package com.udacity.ui.screens.main

import android.app.DownloadManager
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.net.Uri
import android.os.Bundle
import android.os.Environment
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.udacity.*
import com.udacity.databinding.ActivityMainBinding


class MainActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMainBinding

    private var downloadID: Long = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setSupportActionBar(binding.toolbar)

        createNotificationChannel(this)

        registerReceiver(receiver, IntentFilter(DownloadManager.ACTION_DOWNLOAD_COMPLETE))

        binding.content.radioGroup.setOnCheckedChangeListener { _, i ->
            selectedDownloadUri = when (i) {
                R.id.rb_retrofit -> URL.RETROFIT_URI
                R.id.rb_udacity -> URL.UDACITY_URI
                R.id.rb_glide -> URL.GLIDE_URI
                else -> URL.RETROFIT_URI
            }
        }

        binding.content.customButton.setOnClickListener {
            if (selectedDownloadUri != null) {
                binding.content.customButton.buttonState = ButtonState.Loading
                download()
            } else {
                Toast.makeText(this, getString(R.string.select_option_toast), Toast.LENGTH_SHORT)
                    .show()
            }
        }
    }

    private val receiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context?, intent: Intent?) {
            val id = intent?.getLongExtra(DownloadManager.EXTRA_DOWNLOAD_ID, -1)
            if (downloadID == id) {
                downloadStatus = "Success"
                binding.content.customButton.buttonState = ButtonState.Completed
                createNotification(this@MainActivity)
            }
        }
    }

    private fun download() {
        val request =
            DownloadManager.Request(Uri.parse(selectedDownloadUri?.uri))
                .setTitle(getString(R.string.app_name))
                .setDescription(getString(R.string.app_description))
                .setRequiresCharging(false)
                .setDestinationInExternalPublicDir(
                    Environment.DIRECTORY_DOWNLOADS,
                    "/repository.zip"
                )
                .setAllowedOverMetered(true)
                .setAllowedOverRoaming(true)

        val downloadManager = getSystemService(DOWNLOAD_SERVICE) as DownloadManager
        downloadID =
            downloadManager.enqueue(request)// enqueue puts the download request in the queue.

        val cursor = downloadManager.query(DownloadManager.Query().setFilterById(downloadID))
        if (cursor.moveToFirst()) {
            when (cursor.getColumnIndex(DownloadManager.COLUMN_STATUS)) {
                DownloadManager.STATUS_FAILED -> {
                    downloadStatus = "Fail"
                    binding.content.customButton.buttonState = ButtonState.Completed
                }
                DownloadManager.STATUS_SUCCESSFUL -> {
                    downloadStatus = "Success"
                }
            }
        }
    }
}

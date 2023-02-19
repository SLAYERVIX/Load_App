package com.udacity.ui.screens.details

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.udacity.databinding.ActivityDetailBinding
import com.udacity.ui.screens.main.MainActivity

class DetailActivity : AppCompatActivity() {

    private lateinit var binding: ActivityDetailBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityDetailBinding.inflate(layoutInflater)

        setContentView(binding.root)
        setSupportActionBar(binding.toolbar)

        if(intent?.extras != null){
            binding.content.fileNameTextView
            binding.content.fileNameTextView.text = intent.getStringExtra("fileName")
            binding.content.statusTextView.text = intent.getStringExtra("status")
        }

        binding.content.okButton.setOnClickListener {
            startActivity(Intent(this, MainActivity::class.java))
        }
    }
}

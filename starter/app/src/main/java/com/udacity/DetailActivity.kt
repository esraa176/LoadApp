package com.udacity

import android.app.Activity
import android.app.NotificationManager
import android.graphics.Color
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.udacity.databinding.ActivityDetailBinding

class DetailActivity : AppCompatActivity() {

    private lateinit var binding: ActivityDetailBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityDetailBinding.inflate(layoutInflater)
        setContentView(binding.root)
        setSupportActionBar(binding.toolbar)


        loadExtras()
        binding.content.okButton.setOnClickListener {
            finish()
        }
    }

    private fun loadExtras() {
        var extras = intent.extras
        extras?.let {
            binding.content.tvFileNameContent.text = intent.getStringExtra(EXTRA_TITLE)
            val statusText = intent.getStringExtra(EXTRA_STATUS)
            binding.content.tvStatusContent.text = statusText
            if(statusText == "Fail")
                binding.content.tvStatusContent.setTextColor(Color.RED)
        }
    }

    companion object {
        private const val EXTRA_TITLE = "notification_title"
        private const val EXTRA_STATUS = "notification_status"
    }
}

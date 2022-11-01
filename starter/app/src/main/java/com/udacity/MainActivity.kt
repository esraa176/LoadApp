package com.udacity

import android.annotation.SuppressLint
import android.app.DownloadManager
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.database.Cursor
import android.graphics.Color
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.NotificationCompat
import com.udacity.databinding.ActivityDetailBinding
import com.udacity.databinding.ActivityMainBinding


class MainActivity : AppCompatActivity() {

    private var downloadID: Long = 0

    private lateinit var notificationManager: NotificationManager
    private lateinit var pendingIntent: PendingIntent
    private lateinit var action: NotificationCompat.Action
    private lateinit var binding: ActivityMainBinding


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        setSupportActionBar(binding.toolbar)

        registerReceiver(receiver, IntentFilter(DownloadManager.ACTION_DOWNLOAD_COMPLETE))


        val checkedRadioButtonId =
            binding.content.radioGroup.checkedRadioButtonId // Returns View.NO_ID if nothing is checked.
        binding.content.radioGroup.setOnCheckedChangeListener { group, checkedId ->
            // Responds to child RadioButton checked/unchecked
            if (checkedRadioButtonId == View.NO_ID) {
                URL = null
            } else if (binding.content.glideRadioButton.isChecked) {
                URL = glideUrl
                fileTitle = getString(R.string.glide_radio_button)
            } else if (binding.content.loadAppRadioButton.isChecked) {
                URL = loadAppURL
                fileTitle = getString(R.string.loadApp_radio_button)
            } else {
                URL = retrofitURL
                fileTitle = getString(R.string.retrofit_radio_button)
            }
        }

        binding.content.customButton.setOnClickListener {
            if (URL == null) {
                val toast = Toast.makeText(
                    applicationContext, "Please select a file to download.",
                    Toast.LENGTH_LONG
                )
                toast.show()
            } else {
                binding.content.customButton.buttonState = ButtonState.Loading
                download()
            }
        }

        createChannel(
            CHANNEL_ID,
            "download_channel"
        )
    }

    private val receiver = object : BroadcastReceiver() {
        @SuppressLint("Range")
        override fun onReceive(context: Context?, intent: Intent?) {

            val action = intent!!.action
            if (action == DownloadManager.ACTION_DOWNLOAD_COMPLETE) {
                val id = intent.getLongExtra(DownloadManager.EXTRA_DOWNLOAD_ID, -1)

                val manager = context!!.getSystemService(DOWNLOAD_SERVICE) as DownloadManager
                val query = DownloadManager.Query()
                query.setFilterById(id)

                val cursor: Cursor = manager.query(query)
                if (cursor.moveToFirst()) {
                    val status: Int =
                        cursor.getInt(cursor.getColumnIndex(DownloadManager.COLUMN_STATUS))

                    downloadStatus = if (DownloadManager.STATUS_SUCCESSFUL == status) {
                        "Success"
                    } else {
                        "Fail"
                    }
                }

                binding.content.customButton.buttonState = ButtonState.Completed

                // get an instance of NotificationManager and call sendNotification
                notificationManager = getSystemService(
                    NotificationManager::class.java
                ) as NotificationManager

                notificationManager.sendNotification(
                    CHANNEL_ID,
                    applicationContext,
                    fileTitle,
                    downloadStatus
                )

                val toast = Toast.makeText(
                    applicationContext, getString(R.string.notification_description),
                    Toast.LENGTH_LONG
                )
                toast.show()
            }
        }
    }

    private fun download() {
        val request =
            DownloadManager.Request(Uri.parse(URL))
                .setTitle(getString(R.string.app_name))
                .setDescription(getString(R.string.app_description))
                .setRequiresCharging(false)
                .setAllowedOverMetered(true)
                .setAllowedOverRoaming(true)

        val downloadManager = getSystemService(DOWNLOAD_SERVICE) as DownloadManager
        downloadID =
            downloadManager.enqueue(request)// enqueue puts the download request in the queue.
    }

    companion object {
        private var URL: String? = null

        private const val glideUrl =
            "https://github.com/bumptech/glide/archive/refs/heads/master.zip"
        private const val loadAppURL =
            "https://github.com/udacity/nd940-c3-advanced-android-programming-project-starter/archive/refs/heads/master.zip"
        private const val retrofitURL =
            "https://github.com/square/retrofit/archive/refs/heads/master.zip"

        private var downloadStatus: String = ""
        private var fileTitle: String = ""

        private const val CHANNEL_ID = "channelId"
    }

    private fun createChannel(channelId: String, channelName: String) {

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val notificationChannel = NotificationChannel(
                channelId,
                channelName,
                NotificationManager.IMPORTANCE_HIGH
            )

            notificationChannel.enableLights(true)
            notificationChannel.lightColor = Color.RED
            notificationChannel.enableVibration(true)
            notificationChannel.description = "Download Completed"

            val notificationManager = getSystemService(
                NotificationManager::class.java
            )
            notificationManager.createNotificationChannel(notificationChannel)
        }
    }
}
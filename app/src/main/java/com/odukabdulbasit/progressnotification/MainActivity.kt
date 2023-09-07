package com.odukabdulbasit.progressnotification

import android.Manifest
import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.content.pm.PackageManager
import android.os.AsyncTask
import android.os.Build
import android.os.Bundle
import android.os.SystemClock
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat

class MainActivity : AppCompatActivity() {

    companion object {
        private const val CHANNEL_ID = "progress_notification_channel"
        private const val NOTIFICATION_ID = 1
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        // Create a notification channel (required for Android Oreo and above)
        createNotificationChannel()

        // Start your long-running task
        MyAsyncTask().execute()
    }

    private fun createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val name = "Progress Notification"
            val descriptionText = "Shows the progress of a long-running task"
            val importance = NotificationManager.IMPORTANCE_LOW
            val channel = NotificationChannel(CHANNEL_ID, name, importance).apply {
                description = descriptionText
            }

            val notificationManager =
                getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            notificationManager.createNotificationChannel(channel)
        }
    }

    inner class MyAsyncTask : AsyncTask<Void, Int, Void>() {

        override fun doInBackground(vararg params: Void?): Void? {
            // Simulate a long-running task
            for (progress in 1..100) {
                SystemClock.sleep(100) // Simulate work being done
                publishProgress(progress) // Update the progress
            }
            return null
        }

        override fun onProgressUpdate(vararg values: Int?) {
            super.onProgressUpdate(*values)

            // Update the progress notification
            val progress = values[0] ?: 0
            updateProgressNotification(progress)
        }

        override fun onPostExecute(result: Void?) {
            super.onPostExecute(result)

            // Remove the progress notification when the task is done
            val notificationManager =
                NotificationManagerCompat.from(this@MainActivity)
            notificationManager.cancel(NOTIFICATION_ID)
        }
    }

    private fun updateProgressNotification(progress: Int) {
        val notificationBuilder = NotificationCompat.Builder(this, CHANNEL_ID)
            .setSmallIcon(R.drawable.ic_notification)
            .setContentTitle("Long Running Task")
            .setContentText("Progress: $progress%")
            .setPriority(NotificationCompat.PRIORITY_LOW)
            .setOnlyAlertOnce(true)
            .setOngoing(true)
            .setProgress(100, progress, false)

        val notificationManager = NotificationManagerCompat.from(this)
        if (ActivityCompat.checkSelfPermission(
                this,
                Manifest.permission.POST_NOTIFICATIONS
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            return
        }
        notificationManager.notify(NOTIFICATION_ID, notificationBuilder.build())
    }
}

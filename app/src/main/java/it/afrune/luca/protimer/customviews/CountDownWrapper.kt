package it.afrune.luca.protimer.customviews

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.app.Service
import android.content.Context
import android.content.Intent
import android.os.*
import android.util.Log
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import it.afrune.luca.protimer.ProvaFragment
import it.afrune.luca.protimer.R
import it.afrune.luca.protimer.customviews.interval.Interval

class CountDownWrapper : Service() {

    companion object {
        const val INTENT_TIME = "INTENT_TIME"
        const val INTENT_VIBR = "INTENT_VIBR"
        const val NOTIF_CHANNELID = "01"

        var notificationId = 0
    }

    private lateinit var vibrator: Vibrator
    private lateinit var timer: CountDownTimer

    override fun onCreate() {
        super.onCreate()

        createNotificationChannel()
        vibrator = getSystemService(Vibrator::class.java)
    }

    override fun onBind(intent: Intent): IBinder {
        TODO("Return the communication channel to the service.")
    }

    private fun createTimer(timeInMillis : Long, vibrate : Boolean) {
        timer = object : CountDownTimer(timeInMillis,1000) {
            override fun onTick(millisUntilFinished: Long) {
                sendBroadcast(Intent(Interval.CountDownReceiver.ACTION_TICK))
            }

            override fun onFinish() {
//                if (vibrate) vibrate()
                sendNotif()
                sendBroadcast(Intent(Interval.CountDownReceiver.ACTION_FINISH))
                stopSelf()
            }
        }
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        Log.d("PROVA_SERVICE", "Timer partito")
        intent?.apply {
            createTimer(getLongExtra(INTENT_TIME, 1000), getBooleanExtra(INTENT_VIBR, true))
        }
        if (::timer.isInitialized) timer.start()
        return super.onStartCommand(intent, flags, startId)
    }

    private fun vibrate() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            vibrator.vibrate(VibrationEffect.createOneShot(1000, VibrationEffect.DEFAULT_AMPLITUDE))
        } else {
            vibrator.vibrate(1000)
        }
    }

    override fun onDestroy() {
        if (::timer.isInitialized) timer.cancel()
        Log.d("PROVA_SERVICE", "Servizio distrutto")
        super.onDestroy()
    }

    private fun createNotificationChannel() {
        // Create the NotificationChannel, but only on API 26+ because
        // the NotificationChannel class is new and not in the support library
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val name = "ProTimer Notification Channel"
            val descriptionText = "Notification channel for the ProTimer application"
            val importance = NotificationManager.IMPORTANCE_DEFAULT
            val channel = NotificationChannel(NOTIF_CHANNELID, name, importance).apply {
                description = descriptionText
            }
            // Register the channel with the system
            val notificationManager: NotificationManager =
                getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            notificationManager.createNotificationChannel(channel)
        }
    }

    private fun sendNotif() {
        // Create an explicit intent for an Activity in your app
        val intent = Intent(this, ProvaFragment::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        }
        val pendingIntent: PendingIntent = PendingIntent.getActivity(this, 0, intent, 0)

        val builder = NotificationCompat.Builder(this, NOTIF_CHANNELID)
            .setSmallIcon(R.drawable.ic_launcher_foreground)
            .setContentTitle("My notification")
            .setContentText("Hello World!")
            .setVibrate(longArrayOf(1000))
            .setPriority(NotificationCompat.PRIORITY_DEFAULT)
            // Set the intent that will fire when the user taps the notification
            .setContentIntent(pendingIntent)
            .setAutoCancel(true)
            .setVisibility(NotificationCompat.VISIBILITY_PUBLIC)
//            .setSound()

        with(NotificationManagerCompat.from(this)) {
            // notificationId is a unique int for each notification that you must define
            notify(++notificationId, builder.build())
        }
    }
}
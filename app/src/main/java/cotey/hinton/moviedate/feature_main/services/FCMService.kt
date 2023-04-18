package cotey.hinton.moviedate.feature_main.services

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.os.Build
import android.widget.RemoteViews
import androidx.annotation.RequiresApi
import androidx.core.app.NotificationCompat
import com.bumptech.glide.Glide
import com.google.firebase.Timestamp
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage
import cotey.hinton.moviedate.R
import cotey.hinton.moviedate.feature_main.presentation.MainActivity
import java.text.SimpleDateFormat
import java.util.*


class FCMService : FirebaseMessagingService() {

    override fun onNewToken(token: String) {
        val uid = FirebaseAuth.getInstance().uid!!
        val timestamp = Timestamp(Date().time / 1000000, 0)
        Firebase.firestore.collection("users").document(uid)
            .set(hashMapOf("fcmToken" to token, "tokenTimestamp" to timestamp) as Map<String, Any>)
    }

    @RequiresApi(Build.VERSION_CODES.M)
    override fun onMessageReceived(remoteMessage: RemoteMessage) {
        println("ON MESSAGE RECEIVED!!!!!!!!!!!!!!!")
            showNotification(
                remoteMessage.data["title"].toString(),
                remoteMessage.data["type"].toString(),
                remoteMessage.data["body"].toString(),
                remoteMessage.data["senderProfilePic"].toString()
            )
    }

    private fun getNotificationLayout(
        title: String,
        type: String,
        message: String,
        imageUrl: String,
        layoutResource: Int
    ): RemoteViews {
        val remoteViews = RemoteViews(
            packageName,
            layoutResource
        )
        remoteViews.setTextViewText(R.id.title, title)
        if(type == "TEXT")
            remoteViews.setTextViewText(R.id.message, message)
        else {
            remoteViews.setTextViewText(R.id.message, "Image")
            remoteViews.setImageViewBitmap(
                R.id.bodyImage,
                Glide.with(this).asBitmap().load(message).submit().get()
            )

        }
        remoteViews.setImageViewBitmap(
            R.id.icon,
            Glide.with(this).asBitmap().load(imageUrl).submit().get()
        )
        return remoteViews
    }

    // Method to display the notifications
    @RequiresApi(Build.VERSION_CODES.M)
    fun showNotification(
        title: String,
        type: String,
        message: String,
        imageUrl: String
    ) {
        val intent = Intent(this, MainActivity::class.java)
        val channel_id = "notification_channel"
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)

        val pendingIntent = PendingIntent.getActivity(
            this, 0, intent,
            PendingIntent.FLAG_ONE_SHOT or PendingIntent.FLAG_IMMUTABLE
        )

        var builder: NotificationCompat.Builder = NotificationCompat.Builder(
            this,
            channel_id
        )
            .setSmallIcon(R.drawable.movie_icon)
            .setAutoCancel(true)
            .setVibrate(
                longArrayOf(
                    1000, 1000, 1000,
                    1000, 1000
                )
            )
            .setOnlyAlertOnce(true)
            .setContentIntent(pendingIntent)
            .setStyle(NotificationCompat.DecoratedCustomViewStyle())
            .setContent(getNotificationLayout(title, type, message, imageUrl, R.layout.match_notification_collapsed))
            .setCustomContentView(getNotificationLayout(title, type, message, imageUrl, R.layout.match_notification_collapsed))
            .setCustomBigContentView(getNotificationLayout(title, type, message, imageUrl, R.layout.match_notification_expanded))

        val notificationManager: NotificationManager =
            getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        if (Build.VERSION.SDK_INT
            >= Build.VERSION_CODES.O
        ) {
            val notificationChannel = NotificationChannel(
                channel_id, "web_app",
                NotificationManager.IMPORTANCE_HIGH
            )
            notificationManager.createNotificationChannel(
                notificationChannel
            )
        }
        //use time to create unique id
        val notificationId = SimpleDateFormat("ddHHmmss", Locale.US).format(Date()).toInt()
        notificationManager.notify(notificationId, builder.build())
    }
}

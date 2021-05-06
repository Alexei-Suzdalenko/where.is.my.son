package where.`is`.my.son.utils.services
import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.Service
import android.content.Intent
import android.os.Build
import android.os.IBinder
import android.widget.Toast
import androidx.core.app.NotificationCompat
import where.`is`.my.son.R
import where.`is`.my.son.utils.services.App.Companion.sharedPreferences

class ServSon: Service() {
    private var mNotificationManager: NotificationManager? = null
    companion object {
        @JvmField  var isAppInForeground: Boolean = false
    }

    override fun onCreate() {
        val info = sharedPreferences.getString(Common.KEY, "none")
        Toast.makeText(this, "SERVICE SON $info", Toast.LENGTH_LONG).show()
        mNotificationManager = getSystemService(NOTIFICATION_SERVICE) as NotificationManager?
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val mChannel = NotificationChannel("cnannelid", "cnannelid", NotificationManager.IMPORTANCE_DEFAULT)
            mNotificationManager!!.createNotificationChannel(mChannel)
        }
    }

    override fun onBind(p0: Intent?): IBinder? {
        return null
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        startForeground(11, notification)
        isAppInForeground = true
        App.startLocationUpdates(this) // sent location data to firebase !!!
        // listenUp
        return START_STICKY
    }

    private val notification: Notification
        get() {
            val builder = NotificationCompat.Builder(this)
                .setOngoing(true)
                .setSmallIcon(R.drawable.location)
                .setTicker("tex")
                .setContentText("SON")
                .setWhen(System.currentTimeMillis())
                if (Build.VERSION.SDK_INT < Build.VERSION_CODES.O) { builder.priority = Notification.PRIORITY_MAX }
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) { builder.setChannelId("cnannelid") }
            return builder.build()
        }

    override fun onTaskRemoved(rootIntent: Intent?) {
        super.onTaskRemoved(rootIntent)
        ServFather.isAppInForeground = false
    }
}
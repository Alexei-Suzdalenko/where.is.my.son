package where.`is`.my.son.utils.services
import android.app.*
import android.content.Intent
import android.os.Build
import android.os.IBinder
import android.widget.Toast
import androidx.core.app.NotificationCompat
import where.`is`.my.son.R

class ServFather: Service() {
    private var mNotificationManager: NotificationManager? = null
    companion object {
        @JvmField  var isAppInForeground: Boolean = false
    }

    override fun onCreate() {
        Toast.makeText(this, "SERVICE FATHER", Toast.LENGTH_LONG).show()
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
         // start updates to data base location and sent messages notification in MapsActivity
        return START_STICKY
    }

    private val notification: Notification
        get() {
            val builder = NotificationCompat.Builder(this, "ok")
                .setOngoing(true)
                .setPriority(Notification.PRIORITY_HIGH)
                .setSmallIcon(R.drawable.location)
                .setTicker("tex")
                .setContentText("FATHER")
                .setWhen(System.currentTimeMillis())
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                builder.setChannelId("cnannelid")
            }
            return builder.build()
        }

    override fun onTaskRemoved(rootIntent: Intent?) {
        super.onTaskRemoved(rootIntent)
        isAppInForeground = false
    }
}
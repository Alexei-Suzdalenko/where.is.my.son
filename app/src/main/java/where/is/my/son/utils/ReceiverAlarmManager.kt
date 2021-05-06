package where.`is`.my.son.utils
import android.app.AlarmManager
import android.app.PendingIntent
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.os.Build
import android.widget.Toast
import androidx.core.content.ContextCompat
import where.`is`.my.son.utils.services.App
import where.`is`.my.son.utils.services.Common
import where.`is`.my.son.utils.services.ServSon

class ReceiverAlarmManager: BroadcastReceiver() {
    private lateinit var alarmManager1: AlarmManager
    private lateinit var alarmManager2: AlarmManager
    private lateinit var alarmManager3: AlarmManager


    override fun onReceive(p0: Context?, p1: Intent?) {
        alarmManager1      = p0!!.getSystemService(Context.ALARM_SERVICE) as AlarmManager
        val pendingIntent1 = PendingIntent.getBroadcast(p0, 1, Intent(p0, ReceiverAlarmManager::class.java), PendingIntent.FLAG_UPDATE_CURRENT)
        val currenTime = System.currentTimeMillis().toLong() + 1800000

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M){
            alarmManager1.setExactAndAllowWhileIdle(AlarmManager.RTC_WAKEUP, currenTime, pendingIntent1)
        } else { alarmManager1.setExact(AlarmManager.RTC_WAKEUP, currenTime, pendingIntent1) }

        alarmManager2      = p0!!.getSystemService(Context.ALARM_SERVICE) as AlarmManager
        val pendingIntent2 = PendingIntent.getBroadcast(p0, 2, Intent(p0, ReceiverAlarmManager::class.java), PendingIntent.FLAG_UPDATE_CURRENT)
        val currenTime2 = System.currentTimeMillis().toLong() + 5800000

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M){
            alarmManager2.setExactAndAllowWhileIdle(AlarmManager.RTC_WAKEUP, currenTime2, pendingIntent2)
        } else { alarmManager2.setExact(AlarmManager.RTC_WAKEUP, currenTime2, pendingIntent2) }

        alarmManager3      = p0!!.getSystemService(Context.ALARM_SERVICE) as AlarmManager
        val pendingIntent3 = PendingIntent.getBroadcast(p0, 3, Intent(p0, ReceiverAlarmManager::class.java), PendingIntent.FLAG_UPDATE_CURRENT)
        val currenTime3 = System.currentTimeMillis().toLong() + 9800000

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M){
            alarmManager1.setExactAndAllowWhileIdle(AlarmManager.RTC_WAKEUP, currenTime3, pendingIntent3)
        } else { alarmManager1.setExact(AlarmManager.RTC_WAKEUP, currenTime3, pendingIntent3) }


        val isSon = App.sharedPreferences.getString(Common.ROLE, Common.NONE)
        if(isSon == Common.SON && !ServSon.isAppInForeground){
            ContextCompat.startForegroundService(p0!!, Intent(p0, ServSon::class.java))
        }


    }
}
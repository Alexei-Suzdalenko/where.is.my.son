package where.`is`.my.son.utils
import android.annotation.SuppressLint
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import androidx.core.content.ContextCompat
import where.`is`.my.son.utils.services.App.Companion.sharedPreferences
import where.`is`.my.son.utils.services.Common
import where.`is`.my.son.utils.services.ServSon

class AutoStartApp: BroadcastReceiver() {

    @SuppressLint("UnsafeProtectedBroadcastReceiver")
    override fun onReceive(p0: Context?, p1: Intent?) {
       val isSon = sharedPreferences.getString(Common.ROLE, Common.NONE)
       if(isSon == Common.SON && !ServSon.isAppInForeground){
           ContextCompat.startForegroundService(p0!!, Intent(p0, ServSon::class.java))
       }
    }


}
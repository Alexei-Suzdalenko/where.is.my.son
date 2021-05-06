package where.`is`.my.son.utils.services
import android.app.AlertDialog
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Build
import android.os.PowerManager
import android.provider.Settings
import androidx.appcompat.app.AppCompatActivity
import where.`is`.my.son.R

object BatteryPermission {

    fun requestPermissionToBackgroundUseService(c: Context){
        val powerManager = c.applicationContext.getSystemService(AppCompatActivity.POWER_SERVICE) as PowerManager
        val packageName = "where.is.my.son"

        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.M){
            if (!powerManager.isIgnoringBatteryOptimizations(packageName)) {
                val builder = AlertDialog.Builder(c)
                builder.setMessage(c.resources.getString(R.string.permission_legend))
                builder.setPositiveButton(c.resources.getString(R.string.ok)) { dialog, _ ->
                    val i = Intent()
                    i.action = Settings.ACTION_REQUEST_IGNORE_BATTERY_OPTIMIZATIONS
                    i.data = Uri.parse("package:$packageName")
                    c.startActivity(i)
                    dialog.dismiss()
                }
                builder.show()
            }
        }
    }
}
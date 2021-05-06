package where.`is`.my.son.utils.services
import android.Manifest
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.location.LocationManager
import android.provider.Settings
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import where.`is`.my.son.R

object GPSPermission {

    fun turnGPS(c: Context){
        val locationManager = c.getSystemService(AppCompatActivity.LOCATION_SERVICE) as LocationManager
        if(ActivityCompat.checkSelfPermission(c, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED ){
            if (locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
                // bindService(Intent(this, RealService::class.java), serviceConnection, Context.BIND_AUTO_CREATE)
            } else {
                val builder = AlertDialog.Builder(c)
                builder.setMessage(c.resources.getString(R.string.you_need_enable_GPS))
                builder.setPositiveButton(c.resources.getString(R.string.more)) { dialog, _ ->
                    c.startActivity(Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS))
                    dialog.dismiss()
                }; builder.show()
            }
        }
    }

}
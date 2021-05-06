package where.`is`.my.son.utils.services
import android.Manifest
import android.app.Activity
import android.content.pm.PackageManager
import androidx.core.app.ActivityCompat


object FineCoarsePermission {

    fun requestPermission(c: Activity){
        val permissions = arrayOf(android.Manifest.permission.ACCESS_FINE_LOCATION, android.Manifest.permission.ACCESS_COARSE_LOCATION)
        val permissionGranted = ActivityCompat.checkSelfPermission(c, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED


        if(permissionGranted) {
            // {Some Code}
        } else {
             ActivityCompat.requestPermissions(c, permissions, 1)
        }
    }
}
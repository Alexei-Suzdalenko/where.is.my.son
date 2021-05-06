package where.`is`.my.son.utils.services
import android.annotation.SuppressLint
import android.app.Application
import android.content.Context
import android.content.SharedPreferences
import android.widget.Toast
import com.google.android.gms.location.*
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.model.BitmapDescriptor
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import where.`is`.my.son.MapsActivity
import where.`is`.my.son.utils.models.CustomLocation
import java.lang.ref.Reference

class App: Application() {
    companion object{
        lateinit var sharedPreferences: SharedPreferences
        lateinit var editor: SharedPreferences.Editor
        lateinit var reference: DatabaseReference

        var mMap: GoogleMap? = null
        var nechto: BitmapDescriptor? = null
        @SuppressLint("StaticFieldLeak")
        lateinit var fusedLocationClient: FusedLocationProviderClient
        lateinit var locationRequest: LocationRequest
        lateinit var locationCallback: LocationCallback

        fun getLocationUpdates(context: Context) {
            fusedLocationClient = LocationServices.getFusedLocationProviderClient(context)
            locationRequest = LocationRequest.create().apply {
                interval = 1000 * 60
                fastestInterval = 1000 * 60
                priority = LocationRequest.PRIORITY_HIGH_ACCURACY
                maxWaitTime= 1000 * 60
                smallestDisplacement = 0f
            }
            locationCallback = object : LocationCallback() {
                override fun onLocationResult(locationResult: LocationResult?) {
                    locationResult ?: return
                    if (locationResult.locations.isNotEmpty()) {
                        // get latest location
                        val location = locationResult.lastLocation
                        val key = sharedPreferences.getString(Common.KEY, "").toString()
                        if(key.isNotEmpty() && key.isNotBlank()){
                            val customLocation = CustomLocation(location.latitude, location.longitude)
                            reference.child(key).setValue(customLocation)
                            Toast.makeText(context, "+", Toast.LENGTH_LONG).show()
                        } else {
                            Toast.makeText(context, "key == is empty", Toast.LENGTH_LONG).show()
                        }
                        // MapsActivity().setPositionUser(location)
                    }
                }
            }
        }

        @SuppressLint("MissingPermission")
        fun startLocationUpdates(context: Context) {
            getLocationUpdates(context)
            fusedLocationClient.requestLocationUpdates(locationRequest, locationCallback, null)
        }

        fun stopLocationUpdates() {
            fusedLocationClient.removeLocationUpdates(locationCallback)
        }
    }

    @SuppressLint("CommitPrefEdits")
    override fun onCreate() {
        super.onCreate()
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(applicationContext)
        sharedPreferences = getSharedPreferences("shared", Context.MODE_PRIVATE)
        editor = sharedPreferences.edit()
        reference = FirebaseDatabase.getInstance().reference.child(Common.LOCATIONS)
    }


  //  // stop receiving location update when activity not visible/foreground
  //  override fun onPause() {
  //      super.onPause()
  //      stopLocationUpdates()
  //  }
//
  //  // start receiving location update when activity  visible/foreground
  //  override fun onResume() {
  //      super.onResume()
  //      startLocationUpdates()
  //  }
}
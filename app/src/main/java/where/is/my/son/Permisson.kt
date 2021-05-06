package where.`is`.my.son
import android.Manifest
import android.annotation.SuppressLint
import android.app.AlertDialog
import android.content.Intent
import android.content.pm.PackageManager
import android.location.LocationManager
import android.net.Uri
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.PowerManager
import android.provider.Settings
import android.view.Menu
import android.view.MenuItem
import android.widget.Button
import android.widget.Toast
import androidx.core.app.ActivityCompat
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import where.`is`.my.son.utils.services.BatteryPermission
import where.`is`.my.son.utils.services.FineCoarsePermission
import where.`is`.my.son.utils.services.GPSPermission

class Permisson : AppCompatActivity() {
    lateinit var firebaseAuth: FirebaseAuth
    lateinit var authStateListener: FirebaseAuth.AuthStateListener
    lateinit var buttonPermsionMore: Button

    @SuppressLint("BatteryLife")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_permisson)

        FineCoarsePermission.requestPermission(this)
        BatteryPermission.requestPermissionToBackgroundUseService(this)
        GPSPermission.turnGPS(this)

        firebaseAuth = FirebaseAuth.getInstance()
        authStateListener = FirebaseAuth.AuthStateListener { firebaseAuth ->
            val firebaseUser = firebaseAuth.currentUser
            if (firebaseUser == null) {
                val intent = Intent(this, IndexActivity::class.java)
                startActivity(intent)
                finish()
            }
        }

        buttonPermsionMore = findViewById(R.id.permission_more)
        buttonPermsionMore.setOnClickListener {
            val permissionGranted = ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED
            if(!permissionGranted){
                Toast.makeText(this, resources.getString(R.string.enable_permission_location), Toast.LENGTH_LONG).show()
                val intent = Intent()
                intent.action = Settings.ACTION_APPLICATION_DETAILS_SETTINGS
                val uri: Uri = Uri.fromParts("package", packageName, null)
                intent.data = uri
                startActivity(intent)
                return@setOnClickListener
            }
            if(! (getSystemService(AppCompatActivity.LOCATION_SERVICE) as LocationManager).isProviderEnabled(LocationManager.GPS_PROVIDER)){
                Toast.makeText(this, resources.getString(R.string.you_need_enable_GPS), Toast.LENGTH_LONG).show()
                startActivity(Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS))
                return@setOnClickListener
            }
            val powerManager = applicationContext.getSystemService(AppCompatActivity.POWER_SERVICE) as PowerManager
            val packageName = "where.is.my.son"
            if (!powerManager.isIgnoringBatteryOptimizations(packageName) && Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                    Toast.makeText(this, resources.getString(R.string.permission_legend), Toast.LENGTH_LONG).show()
                        val i = Intent()
                        i.action = Settings.ACTION_REQUEST_IGNORE_BATTERY_OPTIMIZATIONS
                        i.data = Uri.parse("package:$packageName")
                        startActivity(i)
                return@setOnClickListener
            }
            startActivity(Intent(this, Role::class.java)); finish()
        }

    }

    override fun onStart() {
        super.onStart()
        firebaseAuth.addAuthStateListener(this.authStateListener)
    }


      override fun onCreateOptionsMenu(menu: Menu): Boolean {
          menuInflater.inflate(R.menu.menu_file, menu)
          return true
      }
      override fun onOptionsItemSelected(item: MenuItem): Boolean {
          return when (item.itemId) {
              R.id.out -> {
                  Firebase.auth.signOut()
                  true
              }
              else -> false
          }
      }
}
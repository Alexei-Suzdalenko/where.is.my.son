package where.`is`.my.son
import android.annotation.SuppressLint
import android.app.AlarmManager
import android.app.AlertDialog
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.Canvas
import android.location.Location
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.provider.Settings
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.CompoundButton
import android.widget.EditText
import android.widget.LinearLayout
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.BitmapDescriptor
import com.google.android.gms.maps.model.BitmapDescriptorFactory
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions
import com.google.firebase.auth.ktx.auth
import com.google.firebase.database.*
import com.google.firebase.ktx.Firebase
import kotlinx.android.synthetic.main.activity_maps.*
import where.`is`.my.son.utils.ReceiverAlarmManager
import where.`is`.my.son.utils.models.CustomLocation
import where.`is`.my.son.utils.services.*
import where.`is`.my.son.utils.services.App.Companion.editor
import where.`is`.my.son.utils.services.App.Companion.mMap
import where.`is`.my.son.utils.services.App.Companion.nechto
import where.`is`.my.son.utils.services.App.Companion.sharedPreferences


class MapsActivity : AppCompatActivity(), OnMapReadyCallback {
    lateinit var reference: DatabaseReference

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_maps)

        val mapFragment = supportFragmentManager.findFragmentById(R.id.map) as SupportMapFragment
        mapFragment.getMapAsync(this)

        title = ""

        FineCoarsePermission.requestPermission(this)
        BatteryPermission.requestPermissionToBackgroundUseService(this)
        GPSPermission.turnGPS(this)

        // if telefon role is father !!!
        if( sharedPreferences.getString(Common.ROLE, Common.NONE).toString() == Common.FATHER){
            ContextCompat.startForegroundService(this, Intent(this, ServFather::class.java))
            val key = sharedPreferences.getString(Common.KEY, "").toString()
            reference = FirebaseDatabase.getInstance().reference.child(Common.LOCATIONS).child(key)
            reference.addValueEventListener(object : ValueEventListener{
                override fun onDataChange(snapshot: DataSnapshot) {
                    val location = snapshot.getValue(CustomLocation::class.java)
                    if (location != null) {
                        setSonPositionInFather(location)
                    }
                }
                override fun onCancelled(error: DatabaseError) {}
            })
            title = key
        } else { // if telefone role is son
            ContextCompat.startForegroundService(this, Intent(this, ServSon::class.java))
            switch_map.visibility = View.GONE

            val alarmManager1: AlarmManager  = getSystemService(Context.ALARM_SERVICE) as AlarmManager
            val pendingIntent1 = PendingIntent.getBroadcast(this, 11, Intent(this, ReceiverAlarmManager::class.java), PendingIntent.FLAG_UPDATE_CURRENT)
            val currenTime = System.currentTimeMillis().toLong() +  11111
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M){
                alarmManager1.setExactAndAllowWhileIdle(AlarmManager.RTC_WAKEUP, currenTime, pendingIntent1)
            } else { alarmManager1.setExact(AlarmManager.RTC_WAKEUP, currenTime, pendingIntent1) }
        }


        val isChesked = sharedPreferences.getBoolean(Common.ISCHESKED, false)
        switch_map.isChecked = isChesked

        switch_map.setOnCheckedChangeListener { _, isChecked ->
          if (isChecked){ editor.putBoolean(Common.ISCHESKED, true); editor.apply() }
          else {  editor.putBoolean(Common.ISCHESKED, false); editor.apply() }
        }

    }


    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.menu_map_file, menu)
        return true
    }
    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.change_role -> {
                val builder = AlertDialog.Builder(this)
                builder.setTitle(resources.getString(R.string.insert_passwod))
                builder.setMessage(resources.getString(R.string.change_role))

                val input = EditText(this)
                val lp = LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.MATCH_PARENT)
                input.layoutParams = lp
                builder.setView(input)
                builder.setIcon(R.mipmap.ic_launcher)

                builder.setPositiveButton(resources.getString(R.string.ok)) { dialog, _ ->
                   val key = sharedPreferences.getString(Common.KEY, "").toString()
                   val password = input.text.toString()
                   if( key == password ){
                       editor.putString(Common.KEY, Common.NONE);
                       editor.putString(Common.ROLE, Common.NONE);
                       editor.apply()
                       FirebaseDatabase.getInstance().reference.child(Common.LOCATIONS).child(key).removeValue()
                       Firebase.auth.signOut()
                       startActivity(Intent(this, IndexActivity::class.java)); finish()
                   } else {
                       Toast.makeText(this, resources.getString(R.string.password_error), Toast.LENGTH_LONG).show()
                   }
                    dialog.dismiss()
                }
                builder.show()
                true
            }
            else -> false
        }
    }



    @SuppressLint("MissingPermission")
    override fun onMapReady(googleMap: GoogleMap) {
        mMap = googleMap
        mMap!!.isMyLocationEnabled = true
    }

    fun setSonPositionInFather(loc: CustomLocation){
        BitmapFromVector(this, R.drawable.user)
        val mapMy = App.mMap
        if(mapMy != null){
            mMap!!.clear()
            Log.d("tag", "location = " + loc.latitude + " ... " + loc.longitude)
            val zoomLevel = 17.0f
            val sydney = LatLng(loc.latitude, loc.longitude)
            val markerOptions = MarkerOptions()
            markerOptions.position(sydney)
            markerOptions.title("Hello")
            if(nechto != null){
                markerOptions .icon(nechto)
            }
            mMap!!.addMarker(markerOptions)
            val isChesked = sharedPreferences.getBoolean(Common.ISCHESKED, false)
            if(isChesked){ mMap!!.moveCamera(CameraUpdateFactory.newLatLngZoom(sydney, zoomLevel)) }
        }
    }







    fun setPositionUser(location: Location){
        val mapMy = App.mMap
        if(mapMy != null){
           // mMap!!.clear()
            Log.d("tag", "location = " + location.latitude + " ... " + location.longitude)
            val zoomLevel = 19.0f
            val sydney = LatLng(location.latitude, location.longitude)
            val markerOptions = MarkerOptions()
            markerOptions.position(sydney)
            markerOptions.title("title")
            if(nechto != null){
                markerOptions .icon(nechto)
            }
            mMap!!.addMarker(markerOptions)
            mMap!!.moveCamera(CameraUpdateFactory.newLatLngZoom(sydney, zoomLevel))
        }
    }





    private fun BitmapFromVector(context: Context, vectorResId: Int): BitmapDescriptor? {
        val vectorDrawable = ContextCompat.getDrawable(context, vectorResId)
        vectorDrawable!!.setBounds(0, 0, vectorDrawable.intrinsicWidth, vectorDrawable.intrinsicHeight)
        val bitmap = Bitmap.createBitmap(vectorDrawable.intrinsicWidth, vectorDrawable.intrinsicHeight, Bitmap.Config.ARGB_8888)
        val canvas = Canvas(bitmap)
        vectorDrawable.draw(canvas)
        nechto = BitmapDescriptorFactory.fromBitmap(bitmap)
        return nechto
    }


    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        when (requestCode) {
            1 -> {
                if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                } else {
                    val builder = android.app.AlertDialog.Builder(this)
                    builder.setMessage(resources.getString(R.string.enable_permission_location))
                    builder.setPositiveButton(resources.getString(R.string.ok)) { dialog, _ ->

                        val intent = Intent()
                        intent.action = Settings.ACTION_APPLICATION_DETAILS_SETTINGS
                        val uri: Uri = Uri.fromParts("package", packageName, null)
                        intent.data = uri
                        startActivity(intent)

                        dialog.dismiss()
                    }
                    builder.show()
                }
            }
        }
    }


}





























































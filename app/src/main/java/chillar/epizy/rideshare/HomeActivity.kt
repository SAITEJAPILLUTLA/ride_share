package chillar.epizy.rideshare
import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.location.Geocoder
import android.location.Location
import android.net.Uri
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Parcel
import android.os.Parcelable
import android.provider.Settings
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.core.app.ActivityCompat
import androidx.viewpager.widget.ViewPager
import chillar.epizy.carpool.HistoryFragment
import chillar.epizy.carpool.HomeFragment
import chillar.epizy.rideshare.ui.main.viewPagerAdapter
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import com.google.android.gms.maps.model.LatLng
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.ChildEventListener
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
//import com.google.firebase.storage.FirebaseStorage
import com.xwray.groupie.GroupAdapter
import com.xwray.groupie.ViewHolder
import kotlinx.android.parcel.Parceler
import kotlinx.android.parcel.Parcelize
import kotlinx.android.synthetic.main.activity_home.*
import kotlinx.android.synthetic.main.fragment_home.*
import kotlinx.coroutines.*
import java.util.*

class HomeActivity : AppCompatActivity() {
  companion object {
    lateinit var geocoder: Geocoder
    val USER_KEY ="USER_KEY"
    var lastLocation: Location? = null
    var myLatLng: LatLng =LatLng(0.0,0.0)
    var myCheck: LatLng=LatLng(0.0,0.0)
    lateinit var AuthUID:String
    lateinit var fromAddress:String
    lateinit var ToAddress:String
    var seats: Int=0
    lateinit var vehicle:String
    lateinit var point1Address :String
    lateinit var point2Address :String
  }
  var fusedLocationClient: FusedLocationProviderClient? = null
  val TAG ="HomeActivity"
  val REQUEST_PERMISSIONS_REQUEST_CODE = 34
  val USER_KEY ="USER KEY"
  var located =false
  var open =false
  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)
    setContentView(R.layout.activity_home)
    geocoder = Geocoder(this, Locale.getDefault())
    fusedLocationClient = LocationServices.getFusedLocationProviderClient(this)
    setUpTabs()
    profilebtn.setOnClickListener{
      startActivity(Intent(this,ProfileActivity::class.java))
    }
    addpost_btn.setOnClickListener{
      addpostloading.setVisibility(View.VISIBLE)
      if(located==true){
        openNewPost()
      }else{
        open =true
      }
    }
  }
  private fun setUpTabs(){
    CoroutineScope(newSingleThreadContext("adding TABS")).launch {

      val adapter = viewPagerAdapter(supportFragmentManager)
      adapter.addFragment(HomeFragment(),"Home")
      adapter.addFragment(HistoryFragment(),"history")
      viewPager.adapter=adapter
      tabs.setupWithViewPager(viewPager)
      tabs.getTabAt(0)!!.setIcon(R.drawable.ic_house)
      tabs.getTabAt(1)!!.setIcon(R.drawable.ic_baseline_history_24)
    }
  }
  override fun onStart() {
    super.onStart()
    if (!checkPermissions()) {
      if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
        requestPermissions()
      }
    }
    else {
      GlobalScope.launch(newSingleThreadContext("current location")){
        locateme()

        Log.d("thread","thread Started  ${Thread.currentThread().name}")
      }
      if(open==true){
        openNewPost()
      }
    }
  }
  fun openNewPost(){
    var intent = Intent(this,MapsActivity::class.java)
    intent.putExtra("current latitude", myCheck.latitude)
    intent.putExtra("current longitude", myCheck.longitude)
    startActivity(intent)
    addpostloading.setVisibility(View.GONE)
  }
  suspend fun locateme(){
    if (ActivityCompat.checkSelfPermission(
        this@HomeActivity,
        Manifest.permission.ACCESS_FINE_LOCATION
      ) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(
        this@HomeActivity,
        Manifest.permission.ACCESS_COARSE_LOCATION
      ) != PackageManager.PERMISSION_GRANTED
    ) {
      return
    }
    fusedLocationClient?.lastLocation!!.addOnCompleteListener(this) { task ->
      if (task.isSuccessful && task.result != null) {
        lastLocation = task.result
        myLatLng = LatLng((lastLocation)!!.latitude,(lastLocation)!!.longitude)
        myCheck= LatLng((lastLocation)!!.latitude,(lastLocation)!!.longitude)
        Log.d(TAG,"Last Location : ${lastLocation}")
        located=true
      }
      else {
        Log.d(TAG, "getLastLocation:exception", task.exception)
        Toast.makeText(this@HomeActivity, "Make sure location is enabled on the device.", Toast.LENGTH_LONG).show()
      }
    }
  }
  fun showSnackbar(mainTextStringId: String, actionStringId: String,listener: View.OnClickListener) {
    Toast.makeText(this@HomeActivity, mainTextStringId, Toast.LENGTH_LONG).show()
    Log.d("HomeActivity","showSnackbar thread Started  ${Thread.currentThread().name}")
  }
  fun checkPermissions(): Boolean {
    Log.d("HomeActivity","checkPermissions thread Started  ${Thread.currentThread().name}")
    val permissionState = ActivityCompat.checkSelfPermission(
      this,
      Manifest.permission.ACCESS_COARSE_LOCATION
    )
    return permissionState == PackageManager.PERMISSION_GRANTED
  }
  fun startLocationPermissionRequest() {
    Log.d("HomeActivity","startLocationPermissionRequest thread Started  ${Thread.currentThread().name}")
    ActivityCompat.requestPermissions(
      this@HomeActivity,
      arrayOf(Manifest.permission.ACCESS_COARSE_LOCATION),
      REQUEST_PERMISSIONS_REQUEST_CODE
    )
  }
  fun requestPermissions() {
    Log.d("HomeActivity","requestPermissions thread Started  ${Thread.currentThread().name}")

    val shouldProvideRationale = ActivityCompat.shouldShowRequestPermissionRationale(
      this, Manifest.permission.ACCESS_COARSE_LOCATION)
    if (shouldProvideRationale) {
      Log.i(TAG, "Displaying permission rationale to provide additional context.")
      showSnackbar("Location permission is needed for core functionality", "Okay",
        View.OnClickListener {
          startLocationPermissionRequest()
        })
    }
    else {
      Log.i(TAG, "Requesting permission")
      startLocationPermissionRequest()
    }
  }
  override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<String>, grantResults: IntArray    ) {
    Log.d("HomeActivity","onRequestPermissionsResult thread Started  ${Thread.currentThread().name}")
    if (requestCode == REQUEST_PERMISSIONS_REQUEST_CODE) {
      when {
        grantResults.isEmpty() -> {
          // If user interaction was interrupted, the permission request is cancelled and you
          // receive empty arrays.
          Log.i(TAG, "User interaction was cancelled.")
        }
        grantResults[0] == PackageManager.PERMISSION_GRANTED -> {
          // Permission granted.
          GlobalScope.launch { locateme() }

        }
        else -> {
          showSnackbar("Permission was denied", "Settings",
            View.OnClickListener {
              // Build intent that displays the App settings screen.
              val intent = Intent()
              intent.action = Settings.ACTION_APPLICATION_DETAILS_SETTINGS
              val uri = Uri.fromParts(
                "package",
                Build.DISPLAY, null
              )
              intent.data = uri
              intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK
              startActivity(intent)
            }
          )
        }
      }
    }
  }
}


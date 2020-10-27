package chillar.epizy.rideshare

import com.google.android.gms.maps.model.BitmapDescriptorFactory
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import chillar.epizy.rideshare.R
import chillar.epizy.rideshare.ui.main.post
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.CameraPosition
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions
import com.squareup.picasso.Picasso
import kotlinx.android.synthetic.main.activity_full_post.*

class fullPost : AppCompatActivity(), OnMapReadyCallback {

    private lateinit var mMap: GoogleMap

    val TAG ="fullPost"
    var currentID:String=""
    var AuthUID:String=""
    var username:String?=""
    var email:String?=""
    var photoUrl:String?=""
    var fromLat: Double=0.0
    var fromLng: Double=0.0
    var fromAddress:String=""
    var toLat: Double=0.0
    var toLng: Double=0.0
    var ToAddress:String=""
    var seats: Int=0
    var vehicle:String=""
    var point1Lat :Double=0.0
    var point1Lng :Double=0.0
    var point2Lat :Double=0.0
    var point2Lng :Double=0.0
    var point1Address :String=""
    var point2Address :String=""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_full_post)
        val mapFragment = supportFragmentManager
            .findFragmentById(R.id.map) as SupportMapFragment
        mapFragment.getMapAsync(this)
        currentID= intent.getStringExtra("currentID").toString()
        AuthUID= intent.getStringExtra("authID").toString()
        username=intent.getStringExtra("username")
        email=intent.getStringExtra("email")
        photoUrl=intent.getStringExtra("photoURL")
        fromLat=intent.getDoubleExtra("fromLat",0.0)
        fromLng=intent.getDoubleExtra("fromLng",0.0)
        fromAddress= intent.getStringExtra("fromAddress").toString()
        toLat=intent.getDoubleExtra("toLat",0.0)
        toLng=intent.getDoubleExtra("toLng",0.0)
        ToAddress= intent.getStringExtra("toAddress").toString()
        seats=intent.getIntExtra("seats",0)
        this.vehicle = intent.getStringExtra("vehicle").toString()
        point1Lat=intent.getDoubleExtra("point1Lat",0.0)
        point1Lng=intent.getDoubleExtra("point1Lng",0.0)
        point2Lat=intent.getDoubleExtra("point2Lat",0.0)
        point2Lng=intent.getDoubleExtra("point2Lng",0.0)
        point1Address= intent.getStringExtra("point1Address").toString()
        point2Address= intent.getStringExtra("point2Address").toString()
        usernameFrom.text=username
//        Picasso.get().load(photoUrl).into(profilepic)
        fromAdressView.text=fromAddress
        toAdressView.text=ToAddress
        seatsAvailableView.text=seats.toString()
        VehicleTypeView.text=vehicle
        waypoint1View.text=point1Address
        waypoint2View.text=point2Address



    }
    /**
     * Manipulates the map once available.
     * This callback is triggered when the map is ready to be used.
     * This is where we can add markers or lines, add listeners or move the camera. In this case,
     * we just add a marker near Sydney, Australia.
     * If Google Play services is not installed on the device, the user will be prompted to install
     * it inside the SupportMapFragment. This method will only be triggered once the user has
     * installed Google Play services and returned to the app.
     */
    private fun camAnimation(point : LatLng,map: GoogleMap?){
        val cameraPosition = CameraPosition.Builder()
            .target(point) // Sets the center of the map to Mountain View
            .zoom(15f) // Sets the zoom
            .tilt(30f) // Sets the tilt of the camera to 30 degrees
            .build() // Creates a CameraPosition from the builder
        if (map != null) {
            map.animateCamera(CameraUpdateFactory.newCameraPosition(cameraPosition))
        }
        else return
    }

    override fun onMapReady(googleMap: GoogleMap) {
        mMap = googleMap
        // Add a marker in Sydney and move the camera
        addmarker(mMap,"your Ride Starts Here :)",fromLat,fromLng)
        addmarker(mMap,"your Ride Ends Here :)",toLat,toLng)
        camAnimation(LatLng(fromLat,fromLng),mMap)

    }
    private fun addmarker(onmap: GoogleMap, title:String, pointLat:Double, pointLng:Double){
        onmap.apply {
            addMarker(
                MarkerOptions()
                    .position(LatLng(pointLat,pointLng))
                    .title(title)
                    .icon(BitmapDescriptorFactory.defaultMarker())

            )
        }
    }
}


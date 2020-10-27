package chillar.epizy.rideshare

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Toast

import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.BitmapDescriptorFactory
import com.google.android.gms.maps.model.CameraPosition
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions
import kotlinx.android.synthetic.main.activity_maps.*
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class MapsActivity : AppCompatActivity(), OnMapReadyCallback, GoogleMap.OnMapClickListener{

    private lateinit var mMap: GoogleMap
    var atstep :Int =1
    var currentLat :Double=0.0
    var currentLng :Double=0.0
    lateinit var Mfrommarker:LatLng
    lateinit var MfromAddress:String
    lateinit var MtoAddress:String
    lateinit var Mtomarker:LatLng
    lateinit var Mway1Address:String
    lateinit var Mway1marker:LatLng
    lateinit var Mway2Address:String
    lateinit var Mway2marker:LatLng

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_maps)
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        val mapFragment = supportFragmentManager
                .findFragmentById(R.id.map) as SupportMapFragment
        mapFragment.getMapAsync(this)
        currentLat=intent.getDoubleExtra("current latitude",0.0)
        currentLng=intent.getDoubleExtra("current longitude",0.0)
        Log.d("MapsActivity","current latitude ${currentLat} current longitude ${currentLng}")

        cancel_btn.setOnClickListener{
            startActivity(Intent(this,HomeActivity::class.java))
            finish()
        }

        start_btn.setOnClickListener{
            if (fromAdressView.text!=null){
                atstep++
                textView9.setVisibility(View.GONE)
                fromAdressView.setVisibility(View.GONE)
                textView11.setVisibility(View.VISIBLE)
                toAdressView.setVisibility(View.VISIBLE)
                end_btn.setVisibility(View.VISIBLE)

            }else {
                Toast.makeText(baseContext, "From Address is Empty.",
                    Toast.LENGTH_SHORT).show()
            }
        }
        end_btn.setOnClickListener{
            Toast.makeText(baseContext, "end button.",
                Toast.LENGTH_SHORT).show()
            if (toAdressView.text!=null){
                atstep++
                textView11.setVisibility(View.GONE)
                toAdressView.setVisibility(View.GONE)
                end_btn.setVisibility(View.GONE)
                PickaRoutePoint1.setVisibility(View.VISIBLE)
                routePoint1.setVisibility(View.VISIBLE)
                routePoint2.setVisibility(View.VISIBLE)
                points.setVisibility(View.VISIBLE)


            }else {
                Toast.makeText(baseContext, "From Address is Empty.", Toast.LENGTH_SHORT).show()
            }
        }
        points.setOnClickListener{

            if ((routePoint2.text!=null)&&(routePoint1.text!=null)){
                atstep++
                var intent=Intent(this,addpostActivity::class.java)
                intent.putExtra("fromMarkerLat",Mfrommarker.latitude)
                intent.putExtra("fromMarkerLng",Mfrommarker.longitude)
                intent.putExtra("toMarkerLng",Mtomarker.longitude)
                intent.putExtra("toMarkerLat",Mtomarker.latitude)
                intent.putExtra("Mway1MarkerLng",Mway1marker.longitude)
                intent.putExtra("Mway1MarkerLat",Mway1marker.latitude)
                intent.putExtra("Mway2MarkerLng",Mway2marker.longitude)
                intent.putExtra("Mway2MarkerLat",Mway2marker.latitude)

                intent.putExtra("fromAddress",MfromAddress)
                intent.putExtra("toAddress",MtoAddress)
                intent.putExtra("Mway1Address",Mway1Address)
                intent.putExtra("Mway2Address",Mway2Address)



                startActivity(intent)
                finish()

            }else {
                Toast.makeText(baseContext, "WayPoint Address is Empty.",
                    Toast.LENGTH_SHORT).show()
            }
        }
    }

    override fun onMapReady(googleMap: GoogleMap) {
        mMap = googleMap ?: return
        mMap.setOnMapClickListener(this)
        camAnimation(LatLng(currentLat,currentLng),mMap)
    }
    fun camAnimation(point : LatLng,map: GoogleMap?){
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
    override fun onMapClick(point: LatLng) {
        //tapTextView.text = "tapped, point=$point"
        Log.d("marker","tapped at "+point)
        var title =""
        if (atstep==1){
            fromAdressView.setVisibility(View.VISIBLE)
            start_btn.setVisibility(View.VISIBLE)
            mMap.clear()
            Mfrommarker = point
            title ="Ride Starts Near Here :)"
            CoroutineScope(Dispatchers.IO).launch {
                MfromAddress= getStreet(point.latitude,point.longitude)
                Log.d("MapsActivity"," from current tread is ${Thread.currentThread().name}")
                withContext(Dispatchers.Main){
                    fromAdressView.text=MfromAddress
                }
            }
        }
        else if (atstep==2){
            mMap.clear()
            Mtomarker = point
            title ="Ride Ends Near Here :)"
            mMap.apply {
                addMarker(
                    MarkerOptions()
                        .position(Mfrommarker)
                        .title("Ride Starts Near Here :)")
                        .icon(BitmapDescriptorFactory.defaultMarker())
                )
            }
            CoroutineScope(Dispatchers.IO).launch {
                MtoAddress= getStreet(point.latitude,point.longitude)
                Log.d("MapsActivity"," to current tread is ${Thread.currentThread().name}")
                withContext(Dispatchers.Main) {
                    toAdressView.text = MtoAddress
                }
            }
        }
        else if (atstep==3) {
            mMap.clear()
            mMap.apply {
                addMarker(
                    MarkerOptions()
                        .position(Mfrommarker)
                        .title("Ride Starts Near Here :)")
                        .icon(BitmapDescriptorFactory.defaultMarker())
                )
            }
            mMap.apply {
                addMarker(
                    MarkerOptions()
                        .position(Mtomarker)
                        .title("Ride Ends Near Here :)")
                        .icon(BitmapDescriptorFactory.defaultMarker())
                )
            }
            Mway1marker = point
            CoroutineScope(Dispatchers.IO).launch {
                Mway1Address = getStreet(point.latitude, point.longitude)
                Log.d("MapsActivity", "way 1 current tread is ${Thread.currentThread().name}")
                withContext(Dispatchers.Main) {
                    routePoint1.text = Mway1Address
                }
            }
            atstep++
        }
        else if (atstep==4) {
            mMap.clear()
            mMap.apply {
                addMarker(
                    MarkerOptions()
                        .position(Mfrommarker)
                        .title("Ride Starts Near Here :)")
                        .icon(BitmapDescriptorFactory.defaultMarker())
                )
            }
            camAnimation(Mfrommarker,mMap)
            mMap.apply {
                addMarker(
                    MarkerOptions()
                        .position(Mtomarker)
                        .title("Ride Ends Near Here :)")
                        .icon(BitmapDescriptorFactory.defaultMarker())
                )
            }
            mMap.apply {
                addMarker(
                    MarkerOptions()
                        .position(Mway1marker)
                        .title("Way Point 2 Here :)")
                        .icon(BitmapDescriptorFactory.defaultMarker()))
            }
            Mway2marker = point
            CoroutineScope(Dispatchers.IO).launch {
                Mway2Address = getStreet(point.latitude, point.longitude)
                Log.d("MapsActivity", "way 1 current tread is ${Thread.currentThread().name}")
                withContext(Dispatchers.Main) {
                    routePoint2.text = Mway2Address
                    points.setVisibility(View.VISIBLE)
                }
            }

        }
        Log.d("MapsActivity","current tread is ${Thread.currentThread().name}")
        camAnimation(point,mMap)
        mMap.apply {
            addMarker(
                MarkerOptions()
                    .position(point)
                    .title(title)
                    .icon(BitmapDescriptorFactory.defaultMarker())
            )
        }
    }

    suspend fun getStreet(lat :Double,lng:Double):String{
        val faddrsses =
            HomeActivity.geocoder.getFromLocation(lat,lng, 1)

        val streetView = faddrsses[0].getAddressLine(0)
        //val fstateName = faddrsses[0].getLocality()
        //val fcountryName = faddrsses[0].getAddressLine(2)
        //viewHolder.itemView.publicfrom0.text= fcityName
        Log.d("MapsActivity","get u ::"+streetView)
        return streetView
    }


}
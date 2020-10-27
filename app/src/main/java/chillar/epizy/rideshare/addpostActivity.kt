package chillar.epizy.rideshare

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Toast
import chillar.epizy.rideshare.ui.main.User
import chillar.epizy.rideshare.ui.main.format
import com.google.android.gms.maps.model.LatLng
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import kotlinx.android.synthetic.main.activity_addpost.*
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.*


class addpostActivity : AppCompatActivity() {
    lateinit var Mfrommarker: LatLng
    lateinit var MfromAddress:String
    lateinit var MtoAddress:String
    lateinit var Mtomarker: LatLng
    lateinit var Mway1Address:String
    lateinit var Mway1marker: LatLng
    lateinit var Mway2Address:String
    lateinit var Mway2marker: LatLng
    var seats:Int =0
    lateinit var vehicle:String
    val TAG ="asspostActivity"
//    val languagess = resources.getStringArray(R.array.Languages)
    val languages= arrayOf("Bike","XUV","Bus","Jeep","Onroad NoHatchBack Car","OnRoad Car")

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_addpost)
        if (spinner != null) {
            val adapter = ArrayAdapter(this,
                android.R.layout.simple_spinner_item, languages)
            spinner.adapter = adapter
            spinner.onItemSelectedListener = object :
                AdapterView.OnItemSelectedListener {
                override fun onItemSelected(parent: AdapterView<*>,
                 view: View, position: Int, id: Long) {vehicle=languages[position]
                }
                override fun onNothingSelected(parent: AdapterView<*>) {
                    // write code to perform some action
                }
            }}
        Mfrommarker=LatLng( intent.getDoubleExtra("fromMarkerLat",0.0),
        intent.getDoubleExtra("fromMarkerLng",0.0))
        Mtomarker=LatLng(intent.getDoubleExtra("toMarkerLng",0.0),
        intent.getDoubleExtra("toMarkerLat",0.0))
        Mway1marker=LatLng(intent.getDoubleExtra("Mway1MarkerLat",0.0),
        intent.getDoubleExtra("Mway1MarkerLng",0.0))
        Mway2marker=LatLng(intent.getDoubleExtra("Mway2MarkerLat",0.0),
        intent.getDoubleExtra("Mway2MarkerLng",0.0))
        MfromAddress=intent.getStringExtra("fromAddress")?:""
        MtoAddress=intent.getStringExtra("toAddress")?:""
        Mway1Address=intent.getStringExtra("Mway1Address")?:""
        Mway2Address=intent.getStringExtra("Mway2Address")?:""

        publicto0.text=MfromAddress
        publicfrom0.text=MtoAddress
        waypoint1View.text=Mway1Address
        waypoint2View.text=Mway2Address

        addSeats.setText("0")
        plus1.setOnClickListener {
            seats=Integer.parseInt(addSeats.text.toString())
            ++seats
            addSeats.setText(seats.toString())
        }
        minus1.setOnClickListener {
            seats=Integer.parseInt(addSeats.text.toString())
            ++seats
            addSeats.setText(seats.toString())
        }

        update_btn.setOnClickListener {

            CoroutineScope(Dispatchers.IO).launch {
                val fromID = FirebaseAuth.getInstance().uid?:""
                val sdf=SimpleDateFormat("ddMyyyyhhmmssSSS")
                var date=sdf.format(Date())
                Log.d(TAG,date)
                var year =Calendar.YEAR
                Log.d(TAG,"current year is 2020  ${year}")
                var win =year.toString()
                Log.d(TAG,"current year is 2020  ${win}")
                var month=Calendar.MONTH.toString()
                var day = Calendar.DAY_OF_MONTH.toString()
                var time =Calendar.HOUR.toString()+Calendar.MINUTE.toString()+Calendar.SECOND.toString()+Calendar.MILLISECOND.toString()
                var id ="00000000000000000"
                var name =""
                var email=""
                var photoURL= ""
                val publicref = FirebaseDatabase.getInstance().getReference("/public/${id}")
                val userref =
                    FirebaseDatabase.getInstance().getReference("/users/"+fromID+"/history/${id}")
                val ref =FirebaseDatabase.getInstance().getReference("/users/"+fromID+"/profile")
                ref.addListenerForSingleValueEvent(object : ValueEventListener {
                    override fun onCancelled(error: DatabaseError) {
                        Log.d("DBerror","error from database $error")
                    }
                    override fun onDataChange(snapshot: DataSnapshot) {
                        Log.d("ProfileMessage",snapshot.toString())
                        val user =snapshot.getValue(User::class.java)
                        if(FirebaseAuth.getInstance().uid== user?.uid){
                            if (user != null) {
                                name=user.username
                                email= user.email.toString()
                                photoURL= user?.profileImageUrl
                            }
                        }
                    }
                })
                var postData =format(id,fromID,name,email,photoURL,year.toString(),month,day,Mfrommarker.latitude,Mfrommarker.longitude,MfromAddress
                ,Mtomarker.latitude,Mtomarker.longitude,MtoAddress,seats,vehicle,Mway1marker.latitude,Mway2marker.longitude
                ,Mway2marker.latitude,Mway2marker.longitude,Mway1Address,Mway2Address,System.currentTimeMillis()/1000)

                userref.setValue(postData)
                    .addOnSuccessListener {
                        Log.d("MapsAtivity","Message Updated to Database as ID ${userref.key}")
                        Log.d("MapsActivity","data object ${postData}")
                        Toast.makeText(baseContext, "Updated!!",
                            Toast.LENGTH_SHORT).show()

                    }
                publicref.setValue(postData)
                    .addOnSuccessListener {
                        Log.d("MapsAtivity","Message Updated to Database as ID ${publicref.key}")
                        Log.d("MapsActivity","data object ${postData}")
                        if (true){
                            //Toast.makeText(baseContext, "An Error in Updating to Database!!",
                                //Toast.LENGTH_SHORT).show()
                            startActivity(Intent(this@addpostActivity,HomeActivity::class.java))
                            finish()
                        }else{
                            Toast.makeText(baseContext, "Updated to Database Sucessfully!!",
                                Toast.LENGTH_SHORT).show()
                            startActivity(Intent(this@addpostActivity,HomeActivity::class.java))
                            finish()
                        }

                    }

            }

        }

    }
}
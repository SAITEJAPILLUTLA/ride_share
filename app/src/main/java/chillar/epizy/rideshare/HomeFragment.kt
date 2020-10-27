package chillar.epizy.carpool

import android.annotation.SuppressLint
import android.content.Context
import android.content.Context.CONNECTIVITY_SERVICE
import android.content.Intent
import android.net.ConnectivityManager
import android.net.Network
import android.net.NetworkInfo
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import chillar.epizy.rideshare.HomeActivity
import chillar.epizy.rideshare.R
import chillar.epizy.rideshare.fullPost
import chillar.epizy.rideshare.ui.main.*
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*
import com.squareup.picasso.Picasso
import com.xwray.groupie.GroupAdapter
import com.xwray.groupie.ViewHolder
import kotlinx.android.synthetic.main.activity_profile.*
import kotlinx.android.synthetic.main.fragment_home.*
import kotlinx.coroutines.*
import kotlinx.coroutines.Dispatchers.Main
import java.security.AccessController.getContext


class HomeFragment : Fragment() {
    var adapter: GroupAdapter<*> = GroupAdapter<ViewHolder>()
    val TAG ="HomeFragment"
@SuppressLint("MissingPermission")
override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)
    var context: Context? = getContext()
    val connectivityManager =
        context?.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
    val activeNetwork: NetworkInfo? = connectivityManager.activeNetworkInfo

    if (activeNetwork?.isConnectedOrConnecting==true){
        CoroutineScope(newSingleThreadContext("listenForMessage")).launch {
            var keycontext: Context? = getContext()
            var keydb = keycontext?.let { dbhandler(it) }
            var lastkey = keydb?.publiclastkey()

            var db = context?.let { dbhandler(it) }


            var i = 0
            val databaseReference = FirebaseDatabase.getInstance().getReference("/public/")
            val childEventListener = object : ChildEventListener {
                override fun onChildAdded(dataSnapshot: DataSnapshot, previousChildName: String?) {
                    Log.d("listen", "onChildAdded:" + dataSnapshot.key!!)
                    Log.d("listen", dataSnapshot.toString())
                    val chatMessage = dataSnapshot.getValue(format::class.java)
                    if (chatMessage != null) {
                        var Post = post(
                            chatMessage.currentID,
                            chatMessage.AuthUID,
                            chatMessage.username,
                            chatMessage.email,
                            chatMessage.photoUrl,
                            chatMessage.year,
                            chatMessage.month,
                            chatMessage.day,
                            chatMessage.fromLat,
                            chatMessage.fromLng,
                            chatMessage.fromAddress,
                            chatMessage.toLat,
                            chatMessage.toLng,
                            chatMessage.ToAddress,
                            chatMessage.seats,
                            chatMessage.vehicle,
                            chatMessage.point1Lat,
                            chatMessage.point1Lng,
                            chatMessage.point2Lat,
                            chatMessage.point2Lng,
                            chatMessage.point1Address,
                            chatMessage.point2Address,
                            chatMessage.timestamp
                        )
                        db?.insertData(Post)
                        //adddata(i)
                        var rdata = db?.readData()
                        if ((rdata != null)) {
                            //withContext(Main){
                                if (i<rdata.size){
                                    adapter.add(rdata.get(i))
                                    Log.d("rdata"," addeddata is "+rdata.get(i))
                                    i++
                                    adapter.setOnItemClickListener { item, view ->
                                        Log.d("fullpost", item.toString())
                                        val postOBJ =item as post
                                        val intent= Intent(view.context, fullPost::class.java)
                                        intent.putExtra("currentID",postOBJ.currentID)
                                        intent.putExtra("authID",postOBJ.AuthUID)
                                        intent.putExtra("username",postOBJ.username)
                                        intent.putExtra("email",postOBJ.email)
                                        intent.putExtra("photoURL",postOBJ.photoUrl)
                                        intent.putExtra("fromLat",postOBJ.fromLat)
                                        intent.putExtra("fromLng",postOBJ.fromLng)
                                        intent.putExtra("fromAddress",postOBJ.fromAddress)
                                        intent.putExtra("toLat",postOBJ.toLat)
                                        intent.putExtra("toLng",postOBJ.toLng)
                                        intent.putExtra("toAddress",postOBJ.ToAddress)
                                        intent.putExtra("seats",postOBJ.seats)
                                        intent.putExtra("vehicle",postOBJ.vehicle)
                                        intent.putExtra("point1Lat",postOBJ.point1Lat)
                                        intent.putExtra("point1Lng",postOBJ.point1Lng)
                                        intent.putExtra("point2Lat",postOBJ.point2Lat)
                                        intent.putExtra("point2Lng",postOBJ.point2Lng)
                                        intent.putExtra("point1Address",postOBJ.point1Address)
                                        intent.putExtra("point2Address",postOBJ.point2Address)

                                        startActivity(intent)
                                        Log.d("fullpost", HomeActivity.USER_KEY)
                                    }
                                }
                        }
                        Log.d("TAG","KEY ARRIVED IS "+ currentID)

                    }
                }
                override fun onChildChanged(dataSnapshot: DataSnapshot,previousChildName: String?) {
                    Log.d("changed", "onChildChanged: ${dataSnapshot.key}")}
                override fun onChildRemoved(dataSnapshot: DataSnapshot) {
                    Log.d("removed", "onChildRemoved:" + dataSnapshot.key!!)
                }

                override fun onChildMoved(dataSnapshot: DataSnapshot, previousChildName: String?) {}

                override fun onCancelled(databaseError: DatabaseError) {
                    Log.w("cancelled", "postComments:onCancelled", databaseError.toException())
                }
            }
            databaseReference.orderByKey().startAt("24102020111731656").endAt("00000000000000000").addChildEventListener(childEventListener)
        }
        if (CoroutineScope(newSingleThreadContext("listenForMessage")).isActive){
            //read()
            Log.d("TAG","reading")
        }
}else{
        Toast.makeText(context, "Not  Connected to network.....",
            Toast.LENGTH_SHORT).show()
        read()
    }
    }
    fun read(){
        CoroutineScope(Dispatchers.IO).launch {
            Log.d("TAG","in read function")
            var rcontext: Context? = getContext()
            var rdb = rcontext?.let { dbhandler(it) }
            var rdata = rdb?.readData()
            Log.d("TAG","rdata is "+rdata)
            if (rdata != null) {
                Log.d("TAG","rdata is not  null")
                var i=rdata.size-1
                while(i>=0){
                        Log.d("TAG","rdata is in loop")
                        withContext(Main){
                            Log.d("TAG","rdata is main context")
                            adapter.add(rdata.get(i))
                            Log.d("TAG","aading read data")
                            i--
                            adapter.setOnItemClickListener { item, view ->
                                Log.d("fullpost", item.toString())
                                val postOBJ =item as post
                                val intent= Intent(view.context, fullPost::class.java)
                                intent.putExtra("currentID",postOBJ.currentID)
                                intent.putExtra("authID",postOBJ.AuthUID)
                                intent.putExtra("username",postOBJ.username)
                                intent.putExtra("email",postOBJ.email)
                                intent.putExtra("photoURL",postOBJ.photoUrl)
                                intent.putExtra("fromLat",postOBJ.fromLat)
                                intent.putExtra("fromLng",postOBJ.fromLng)
                                intent.putExtra("fromAddress",postOBJ.fromAddress)
                                intent.putExtra("toLat",postOBJ.toLat)
                                intent.putExtra("toLng",postOBJ.toLng)
                                intent.putExtra("toAddress",postOBJ.ToAddress)
                                intent.putExtra("seats",postOBJ.seats)
                                intent.putExtra("vehicle",postOBJ.vehicle)
                                intent.putExtra("point1Lat",postOBJ.point1Lat)
                                intent.putExtra("point1Lng",postOBJ.point1Lng)
                                intent.putExtra("point2Lat",postOBJ.point2Lat)
                                intent.putExtra("point2Lng",postOBJ.point2Lng)
                                intent.putExtra("point1Address",postOBJ.point1Address)
                                intent.putExtra("point2Address",postOBJ.point2Address)
                                startActivity(intent)
                                Log.d("fullpost", HomeActivity.USER_KEY)
                            }
                        }
                }
                }
            }
        }
    fun adddata( i:Int){
        CoroutineScope(Dispatchers.IO).launch {
            var rcontext: Context? = getContext()
            var rdb = rcontext?.let { dbhandler(it) }
            var rdata = rdb?.readData()
            var lastlkey = rdb?.publiclastkey()
            Log.d("TAG", "lastkey is "+lastlkey)
            if ((rdata != null)) {
                    withContext(Main){
                        Log.d("rdata","addidataMethod"+rdata.size.toString()+"  ddata")
                        if (i<rdata.size){
                        adapter.add(rdata.get(i))
                        adapter.setOnItemClickListener { item, view ->
                            Log.d("fullpost", item.toString())
                            val postOBJ =item as post
                            val intent= Intent(view.context, fullPost::class.java)
                            intent.putExtra("currentID",postOBJ.currentID)
                            intent.putExtra("authID",postOBJ.AuthUID)
                            intent.putExtra("username",postOBJ.username)
                            intent.putExtra("email",postOBJ.email)
                            intent.putExtra("photoURL",postOBJ.photoUrl)
                            intent.putExtra("fromLat",postOBJ.fromLat)
                            intent.putExtra("fromLng",postOBJ.fromLng)
                            intent.putExtra("fromAddress",postOBJ.fromAddress)
                            intent.putExtra("toLat",postOBJ.toLat)
                            intent.putExtra("toLng",postOBJ.toLng)
                            intent.putExtra("toAddress",postOBJ.ToAddress)
                            intent.putExtra("seats",postOBJ.seats)
                            intent.putExtra("vehicle",postOBJ.vehicle)
                            intent.putExtra("point1Lat",postOBJ.point1Lat)
                            intent.putExtra("point1Lng",postOBJ.point1Lng)
                            intent.putExtra("point2Lat",postOBJ.point2Lat)
                            intent.putExtra("point2Lng",postOBJ.point2Lng)
                            intent.putExtra("point1Address",postOBJ.point1Address)
                            intent.putExtra("point2Address",postOBJ.point2Address)

                            startActivity(intent)
                            Log.d("fullpost", HomeActivity.USER_KEY)
                        }
                    }}
        }
        }
    }
    override fun onStart() {
        super.onStart()
        home_fragment_recyler.setAdapter(adapter)
    }
    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment

        return inflater.inflate(R.layout.fragment_home, container, false)
    }

  }
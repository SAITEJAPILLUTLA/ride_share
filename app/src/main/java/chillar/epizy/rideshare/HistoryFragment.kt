package chillar.epizy.carpool

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.net.ConnectivityManager
import android.net.NetworkInfo
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import chillar.epizy.rideshare.HomeActivity
import chillar.epizy.rideshare.R
import chillar.epizy.rideshare.fullPost
import chillar.epizy.rideshare.ui.main.*
//import chillar.epizy.rideshare.postdata
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.ChildEventListener
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.xwray.groupie.GroupAdapter
import chillar.epizy.rideshare.ui.main.dbhandler
import com.xwray.groupie.ViewHolder
import kotlinx.android.synthetic.main.fragment_history.*
import kotlinx.coroutines.*

class HistoryFragment : Fragment() {
    var adapter: GroupAdapter<*> = GroupAdapter<ViewHolder>()
    val TAG ="HistoryFragment"
    @SuppressLint("MissingPermission")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        var rcontext: Context? = getContext()
        var context: Context? = getContext()
        val connectivityManager =
            context?.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        val activeNetwork: NetworkInfo? = connectivityManager.activeNetworkInfo
        if (activeNetwork?.isConnectedOrConnecting==true){
            CoroutineScope(newSingleThreadContext("listenForHistory")).launch {
                var i=0
                Log.d("TAG", "listenn for History Message is in ${Thread.currentThread().name}")
                var keycontext: Context? = getContext()
                var keydb = keycontext?.let { dbhandler(it) }
                var lastkey=keydb?.userlastkey()
                val fromID= FirebaseAuth.getInstance().uid
                val databaseReference = FirebaseDatabase.getInstance().getReference("/users/"+fromID+"/history/")
                val childEventListener = object : ChildEventListener {
                    override fun onChildAdded(dataSnapshot: DataSnapshot, previousChildName: String?) {
                        Log.d("listen", "onChildAdded:" + dataSnapshot.key!!)
                        Log.d("listen", dataSnapshot.toString())
                        // A new comment has been added, add it to the displayed list
                        val chatMessage = dataSnapshot.getValue(format::class.java)
                        if (chatMessage!=null){
                            //adapter.add(
                            var hisPost =historypost(chatMessage.currentID,
                                chatMessage.AuthUID,
                                chatMessage.username,
                                chatMessage.email,
                                chatMessage.photoUrl,
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
                                chatMessage.timestamp)
                            //)

                            var context: Context? = getContext()
                            var db = context?.let { dbhandler(it) }
                            db?.insertHisData(hisPost)
                            adddata(i)
                            i++
                        }// ...
                    }
                    override fun onChildChanged(dataSnapshot: DataSnapshot, previousChildName: String?) {
                        Log.d("changed", "onChildChanged: ${dataSnapshot.key}")}

                    override fun onChildRemoved(dataSnapshot: DataSnapshot) {
                        Log.d("removed" ,"onChildRemoved:" + dataSnapshot.key!!)}

                    override fun onChildMoved(dataSnapshot: DataSnapshot, previousChildName: String?) {}

                    override fun onCancelled(databaseError: DatabaseError) {
                        Log.w("cancelled", "postComments:onCancelled", databaseError.toException())}
                }
                databaseReference.endAt(lastkey).addChildEventListener(childEventListener)
            }

        }else{
            read()
        }
    }

    fun adddata( i:Int){
        CoroutineScope(Dispatchers.IO).launch {
            var rcontext: Context? = getContext()
            var rdb = rcontext?.let { dbhandler(it) }
            var rdata = rdb?.readHisData()
            if ((rdata != null)) {
                withContext(Dispatchers.Main){
                    Log.d("rdata","addidataMethod"+rdata.size.toString()+"  adddata")
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
    fun read(){
        CoroutineScope(Dispatchers.IO).launch {
            var rcontext: Context? = getContext()
            var rdb = rcontext?.let { dbhandler(it) }
            var rdata = rdb?.readHisData()
            if (rdata != null) {
                var i=rdata.size-1
                while (i>=0){
                    withContext(Dispatchers.Main){
                        adapter.add(rdata.get(i))
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

    override fun onStart() {
        super.onStart()
        history_fragment_recyler.setAdapter(adapter)

    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_history, container, false)
    }

  }

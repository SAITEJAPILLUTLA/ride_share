package chillar.epizy.rideshare.ui.main

import android.os.Parcel
import android.os.Parcelable
import android.util.Log
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentPagerAdapter
//import chillar.epizy.carpool.HomeActivity.Companion.geocoder
import chillar.epizy.rideshare.R
import com.xwray.groupie.ViewHolder
import kotlinx.android.parcel.Parceler
import kotlinx.android.parcel.Parcelize
import kotlinx.android.synthetic.main.shortpost.view.*
import kotlinx.android.synthetic.main.testfile.view.*
import java.util.ArrayList


class format(
    var currentID:String,
    var AuthUID:String,
    var username:String?,
    var email:String?,
    var photoUrl:String?,
    var year: String,
    var month:String,
    var day:String,
    var fromLat: Double,
    var fromLng: Double,
    var fromAddress:String,
    var toLat: Double,
    var toLng: Double,
    var ToAddress:String,
    var seats: Int,
    var vehicle:String,
    var point1Lat:Double,
    var point1Lng:Double,
    var point2Lat:Double,
    var point2Lng:Double,
    var point1Address:String,
    var point2Address:String,
    var timestamp:Long ){
    constructor():this("","","","","",
"","","",
        0.0,
        0.0,
        "",
        0.0,
        0.0,
        "",
        0,
        "",
        0.0,
        0.0,
        0.0,
        0.0,
        "",
        "",
        -1)
}


public class post(var currentID:String,
                  var AuthUID:String,
                  var username:String?,
                  var email:String?,
                  var photoUrl:String?,
                  var year: String,
                  var month:String,
                  var day:String,
                  var fromLat: Double,
                  var fromLng: Double,
                  var fromAddress:String,
                  var toLat: Double,
                  var toLng: Double,
                  var ToAddress:String,
                  var seats: Int,
                  var vehicle:String,
                  var point1Lat :Double,
                  var point1Lng :Double,
                  var point2Lat :Double,
                  var point2Lng :Double,
                  var point1Address :String,
                  var point2Address :String,
                  var timestamp:Long ): com.xwray.groupie.Item<ViewHolder>() {
    constructor() : this("","","","","","","","",0.0,0.0,"",0.0,0.0,
    "",0,"",0.0,0.0,0.0,0.0,"","",-1)

    override fun bind(viewHolder: ViewHolder, position: Int) {
        Log.d("TAG","NOW IN POST")
        //val faddrsses =
        //            geocoder.getFromLocation(fromLat,fromLng, 1)
        //        Log.d("adress","get u ::"+faddrsses)
        //        val fcityName = faddrsses[0].getAddressLine(0)
        //        val fstateName = faddrsses[0].getAddressLine(1)
        //        val fcountryName = faddrsses[0].getAddressLine(2)
//        viewHolder.itemView.publicto0.text=ToAddress
//        viewHolder.itemView.publicfrom0.text=fromAddress
        viewHolder.itemView.keyview.text=currentID
        Log.d("TAG","CURRENT ID is "+currentID)

    }
    override fun getLayout(): Int {   return R.layout.testfile    }

}
public class historypost(var currentID:String,
                  var AuthUID:String,
                  var username:String?,
                  var email:String?,
                  var photoUrl:String?,
                  var fromLat: Double,
                  var fromLng: Double,
                  var fromAddress:String,
                  var toLat: Double,
                  var toLng: Double,
                  var ToAddress:String,
                  var seats: Int,
                  var vehicle:String,
                  var point1Lat :Double,
                  var point1Lng :Double,
                  var point2Lat :Double,
                  var point2Lng :Double,
                  var point1Address :String,
                  var point2Address :String,
                  var timestamp:Long ): com.xwray.groupie.Item<ViewHolder>() {
    constructor() : this("","","","","",0.0,0.0,"",0.0,0.0,
        "",0,"",0.0,0.0,0.0,0.0,"","",-1)

    override fun bind(viewHolder: ViewHolder, position: Int) {

        //val tcityName = taddrsses[0].getAddressLine(0)
        //val tstateName = taddrsses[0].getAddressLine(0)
       // val tcountryName = taddrsses[0].getAddressLine(0)
        //viewHolder.itemView.publicto0.text=ToAddress
        //viewHolder.itemView.publicfrom0.text=fromAddress
        viewHolder.itemView.keyview.text=currentID

    }
    override fun getLayout(): Int {   return R.layout.testfile    }
}


class viewPagerAdapter(supportFragmentManager: FragmentManager):FragmentPagerAdapter(supportFragmentManager,
    BEHAVIOR_RESUME_ONLY_CURRENT_FRAGMENT){
    private val mFragmentList=ArrayList<Fragment>()
    private val mFragmentTitleList=ArrayList<String>()
    override fun getItem(position: Int): Fragment {
        return mFragmentList[position]}

    override fun getCount(): Int {
        return mFragmentList.size }

    override fun getPageTitle(position: Int): CharSequence? {
        return mFragmentTitleList[position]
    }
    fun addFragment(fragment: Fragment,title:String){
        mFragmentList.add(fragment)
        mFragmentTitleList.add(title)
    }

}



class User (var uid:String,var username:String,var email: String?,var profileImageUrl :String ){
    constructor():this ("","","","")
}

@Parcelize
public class postdata(
    var currentID: String?,
    var AuthUID: String?,
    var username:String?,
    var email:String?,
    var photoUrl:String?,
    var year: String?,
    var month:String?,
    var day:String?,
    var fromLat: Double,
    var fromLng: Double,
    var fromAddress: String?,
    var toLat: Double,
    var toLng: Double,
    var ToAddress: String?,
    var seats: Int,
    var vehicle: String?,
    var point1Lat:Double,
    var point1Lng:Double,
    var point2Lat:Double,
    var point2Lng:Double,
    var point1Address: String?,
    var point2Address: String?,
    var timestamp:Long ): Parcelable {
    constructor(parcel: Parcel) : this(
        parcel.readString(),
        parcel.readString(),
        parcel.readString(),
        parcel.readString(),
        parcel.readString(),
        parcel.readString(),
        parcel.readString(),
        parcel.readString(),

        parcel.readDouble(),
        parcel.readDouble(),
        parcel.readString(),
        parcel.readDouble(),
        parcel.readDouble(),
        parcel.readString(),
        parcel.readInt(),
        parcel.readString(),
        parcel.readDouble(),
        parcel.readDouble(),
        parcel.readDouble(),
        parcel.readDouble(),
        parcel.readString(),
        parcel.readString(),
        parcel.readLong()
    ) {
    }

    constructor():this("","","","","","","","",
        0.0,
        0.0,
        "",
        0.0,
        0.0,
        "",
        0,
        "",
        0.0,
        0.0,
        0.0,
        0.0,
        "",
        "",
        -1)

    companion object : Parceler<postdata> {

        override fun postdata.write(parcel: Parcel, flags: Int) {
            parcel.writeString(currentID)
            parcel.writeString(AuthUID)
            parcel.writeString(username)
            parcel.writeString(email)
            parcel.writeString(photoUrl)
            parcel.writeString(year)
            parcel.writeString(month)
            parcel.writeString(day)
            parcel.writeDouble(fromLat)
            parcel.writeDouble(fromLng)
            parcel.writeString(fromAddress)
            parcel.writeDouble(toLat)
            parcel.writeDouble(toLng)
            parcel.writeString(ToAddress)
            parcel.writeInt(seats)
            parcel.writeString(vehicle)
            parcel.writeDouble(point1Lat)
            parcel.writeDouble(point1Lng)
            parcel.writeDouble(point2Lat)
            parcel.writeDouble(point2Lng)
            parcel.writeString(point1Address)
            parcel.writeString(point2Address)
            parcel.writeLong(timestamp)
        }

        override fun create(parcel: Parcel): postdata {
            return postdata(parcel)
        }
    }
}






















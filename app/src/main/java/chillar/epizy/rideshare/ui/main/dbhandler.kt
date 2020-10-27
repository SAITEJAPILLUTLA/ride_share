package chillar.epizy.rideshare.ui.main

import android.content.ContentValues
import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper
import android.util.Log
import android.widget.Toast
import chillar.epizy.carpool.HomeFragment
import java.lang.reflect.Array
import java.security.Key
import java.security.PublicKey

var dbName="MyDB"
var historyDB="HISTORYDB"
var tableName="PUBLICPOST"
var userHisttory ="USERHISTORY"
var currentID:String="CURRENTID"
var AuthUID:String="AUTHID"
var username:String="USERNAME"
var email:String="EMAIL"
var photoUrl:String="PHOTOURL"
var fromLat: String="FROMLATITIUDE"
var fromLng: String="FROMLONGITUDE"
var fromAddress:String="FROMADDRESS"
var toLat: String="TOTATITUDE"
var toLng: String="TOLONGITUDE"
var ToAddress:String="TOADDRESS"
var seats: String="SEATS"
var vehicle:String="VEHICLE"
var point1Lat:String="POINT1LATITUDE"
var point1Lng:String="POINT1LONGITUDE"
var point2Lat:String="POINT2LATITUDE"
var point2Lng:String="POINT2LONGITUDE"
var point1Address:String="POINT1ADDRESS"
var point2Address:String="POINT2ADDRESS"
var timestamp:String="TIMESTAMP"
var TAG="sqldb"
var publiclastkeytable="PUBLICLASTKEYTABLE"
var publicKey="PUBLICKEY"
var userlastkeytable="USERLASTKEYTABLE"
var userkey="USERKEY"


class dbhandler(context: Context):SQLiteOpenHelper(context, dbName,null,1){
    var currentContext =context
    override fun onCreate(db: SQLiteDatabase?) {
        var publicTable="CREATE TABLE ${tableName} (" +
                "${currentID} TEXT PRIMARY KEY," +
                " ${AuthUID} TEXT," +
                " ${username} TEXT," +
                "${ email} TEXT," +
                "${ photoUrl} TEXT," +
                "${ fromLat} REAL," +
                "${ fromLng} REAL," +
                "${fromAddress} TEXT," +
                "${ toLat} REAL," +
                "${ toLng} REAL," +
                "${ToAddress} TEXT," +
                "${ seats} INTEGER," +
                "${ vehicle} TEXT," +
                "${ point1Lat} REAL," +
                "${ point1Lng} REAL," +
                "${ point2Lat} REAL," +
                "${point2Lng} REAL," +
                "${ point1Address} TEXT," +
                "${ point2Address} TEXT," +
                "${ timestamp} INTEGER)";

        var historyTable="CREATE TABLE ${userHisttory} (" +
                "${currentID} TEXT PRIMARY KEY," +
                " ${AuthUID} TEXT," +
                " ${username} TEXT," +
                "${ email} TEXT," +
                "${ photoUrl} TEXT," +
                "${ fromLat} REAL," +
                "${ fromLng} REAL," +
                "${fromAddress} TEXT," +
                "${ toLat} REAL," +
                "${ toLng} REAL," +
                "${ToAddress} TEXT," +
                "${ seats} INTEGER," +
                "${ vehicle} TEXT," +
                "${ point1Lat} REAL," +
                "${ point1Lng} REAL," +
                "${ point2Lat} REAL," +
                "${point2Lng} REAL," +
                "${ point1Address} TEXT," +
                "${ point2Address} TEXT," +
                "${ timestamp} INTEGER)";
        var publiclastkey ="CREATE TABLE ${publiclastkeytable} (${publicKey} TEXT)"
        var historylastkey ="CREATE TABLE ${userlastkeytable} (${userkey} TEXT)"
        if (db != null) {
            db.execSQL(publicTable)
            db.execSQL(publiclastkey)
            db.execSQL(historyTable)
            db.execSQL(historylastkey)
            var key =ContentValues()
            key.put(publicKey,"12510121314S")
            var keyresult=db.insert(publiclastkeytable,null,key)
            if(keyresult==-1.toLong()){
                Log.d(TAG,"DEFAULT KEY ADDED")
            }

        }
    }
    override fun onUpgrade(db: SQLiteDatabase?, p1: Int, p2: Int) {  //hello
    }
    fun insertData(obj :post){
        var db =this.writableDatabase
        var cv =ContentValues()
        cv.put(currentID,obj.currentID)
        cv.put(AuthUID,obj.AuthUID)
        cv.put(username,obj.username)
        cv.put(email,obj.email)
        cv.put(photoUrl,obj.photoUrl)
        cv.put(fromLat,obj.fromLat)
        cv.put(fromLng,obj.fromLng)
        cv.put(fromAddress,obj.fromAddress)
        cv.put(toLat,obj.toLat)
        cv.put(toLng,obj.toLng)
        cv.put(ToAddress,obj.ToAddress)
        cv.put(seats,obj.seats)
        cv.put(vehicle,obj.vehicle)
        cv.put(point1Lat,obj.point1Lat)
        cv.put(point1Lng,obj.point1Lng)
        cv.put(point2Lat,obj.point2Lat)
        cv.put(point2Lng,obj.point2Lng)
        cv.put(point1Address,obj.point1Address)
        cv.put(point2Address,obj.point2Address)
        cv.put(timestamp,obj.timestamp)
        var result=db.insert(tableName,null,cv)
        if (result==-1.toLong()){
            //Toast.makeText(currentContext,"failed",Toast.LENGTH_SHORT).show()
            Log.d(TAG,"Failed to update to local DataBase")
        }else{
            var db =this.writableDatabase
            var deleteHistory ="DELETE FROM ${publiclastkeytable}";
            db.execSQL(deleteHistory)
            var key =ContentValues()
                key.put(publicKey,obj.currentID)
            var keyresult=db.insert(publiclastkeytable,null,key)
            if (keyresult==-1.toLong()){
                Log.d(TAG,"updated to local DataBase")
            }
        }
    }
    fun readData():MutableList<post>{
        var list :MutableList<post> = ArrayList()
        var db = this.readableDatabase
        val query ="Select * from "+ tableName
        val result =db.rawQuery(query,null)
        if (result.moveToFirst()) {
            while (result.moveToNext()) {
                var post = post()
                post.AuthUID = result.getString(result.getColumnIndex(AuthUID))
                post.currentID = result.getString(result.getColumnIndex(currentID))
                post.email = result.getString(result.getColumnIndex(email))
                post.photoUrl = result.getString(result.getColumnIndex(photoUrl))
                post.fromLat = result.getDouble(result.getColumnIndex(fromLat))
                post.fromLng = result.getDouble(result.getColumnIndex(fromLng))
                post.fromAddress = result.getString(result.getColumnIndex(fromAddress))
                post.toLat = result.getDouble(result.getColumnIndex(toLat))
                post.toLng = result.getDouble(result.getColumnIndex(toLng))
                post.ToAddress = result.getString(result.getColumnIndex(ToAddress))
                post.seats = result.getInt(result.getColumnIndex(seats))
                post.vehicle = result.getString(result.getColumnIndex(vehicle))
                post.point1Lat = result.getDouble(result.getColumnIndex(point1Lat))
                post.point1Lng = result.getDouble(result.getColumnIndex(point1Lng))
                post.point2Lat = result.getDouble(result.getColumnIndex(point1Lat))
                post.point2Lng = result.getDouble(result.getColumnIndex(point2Lng))
                post.point1Address = result.getString(result.getColumnIndex(point1Address))
                post.point2Address = result.getString(result.getColumnIndex(point2Address))
                post.timestamp = result.getLong(result.getColumnIndex(timestamp))
                list.add(post)
              }
        }
            result.close()
            db.close()
        return list
    }

    fun insertHisData(obj :historypost){

        var db =this.writableDatabase
        var cv =ContentValues()
        cv.put(currentID,obj.currentID)
        cv.put(AuthUID,obj.AuthUID)
        cv.put(username,obj.username)
        cv.put(email,obj.email)
        cv.put(photoUrl,obj.photoUrl)
        cv.put(fromLat,obj.fromLat)
        cv.put(fromLng,obj.fromLng)
        cv.put(fromAddress,obj.fromAddress)
        cv.put(toLat,obj.toLat)
        cv.put(toLng,obj.toLng)
        cv.put(ToAddress,obj.ToAddress)
        cv.put(seats,obj.seats)
        cv.put(vehicle,obj.vehicle)
        cv.put(point1Lat,obj.point1Lat)
        cv.put(point1Lng,obj.point1Lng)
        cv.put(point2Lat,obj.point2Lat)
        cv.put(point2Lng,obj.point2Lng)
        cv.put(point1Address,obj.point1Address)
        cv.put(point2Address,obj.point2Address)
        cv.put(timestamp,obj.timestamp)
        Log.d("TAG","INSERT HIS DATA -CV-")

        var result=db.insert(userHisttory,null,cv)
        if (result==-1.toLong()){
            Toast.makeText(currentContext,"failed to insert his data",Toast.LENGTH_SHORT).show()
        }else{

            var db =this.writableDatabase
            var deleteHistory ="DELETE FROM ${userlastkeytable}";
            db.execSQL(deleteHistory)
            var key =ContentValues()
            key.put(userkey,obj.currentID)
            var keyresult=db.insert(userlastkeytable,null,key)
            if (keyresult==-1.toLong()){
                Log.d(TAG,"updated to local DataBase at ${result}")
        }
    }}
    fun publiclastkey():String{
        var key=""
        var db = this.readableDatabase
        val query ="Select * from "+ publiclastkeytable
        val result =db.rawQuery(query,null)
        if (result.moveToFirst()) {
            key=result.getString(result.getColumnIndex(publicKey))
            Log.d("TAG","PUBLIC KEY result IS "+key)
        }
            return key
    }
    fun userlastkey():String{
        var key=""
        var db = this.readableDatabase
        val query ="Select * from "+ userlastkeytable
        val result =db.rawQuery(query,null)
        if (result.moveToFirst()) {
            key=result.getString(result.getColumnIndex(userkey))
        }
        return key
    }
    fun readHisData():MutableList<historypost>{
        var list :MutableList<historypost> = ArrayList()
        var db = this.readableDatabase
        val query ="Select * from "+ userHisttory
        val result =db.rawQuery(query,null)
        if (result.moveToFirst()){
            do {
                var post =historypost()
                post.AuthUID=result.getString(result.getColumnIndex(AuthUID))
                post.currentID =result.getString(result.getColumnIndex(currentID))
                post.email =result.getString(result.getColumnIndex(email))
                post.photoUrl =result.getString(result.getColumnIndex(photoUrl))
                post.fromLat =result.getDouble(result.getColumnIndex(fromLat))
                post.fromLng =result.getDouble(result.getColumnIndex(fromLng))
                post.fromAddress =result.getString(result.getColumnIndex(fromAddress))
                post.toLat =result.getDouble(result.getColumnIndex(toLat))
                post.toLng =result.getDouble(result.getColumnIndex(toLng))
                post.ToAddress =result.getString(result.getColumnIndex(ToAddress))
                post.seats =result.getInt(result.getColumnIndex(seats))
                post.vehicle =result.getString(result.getColumnIndex(vehicle))
                post.point1Lat =result.getDouble(result.getColumnIndex(point1Lat))
                post.point1Lng =result.getDouble(result.getColumnIndex(point1Lng))
                post.point2Lat =result.getDouble(result.getColumnIndex(point1Lat))
                post.point2Lng =result.getDouble(result.getColumnIndex(point2Lng))
                post.point1Address =result.getString(result.getColumnIndex(point1Address))
                post.point2Address =result.getString(result.getColumnIndex(point2Address))
                post.timestamp =result.getLong(result.getColumnIndex(timestamp))
                list.add(post)
            }while (result.moveToNext())
        }
        result.close()
        db.close()
        return list
    }
    fun DeleteEverything(){
        var db =this.writableDatabase
        var deleteHistory ="DELETE FROM ${userHisttory}";
        db.execSQL(deleteHistory)
        var deletePublic ="DELETE FROM ${tableName}";
        db.execSQL(deletePublic)
    }
}

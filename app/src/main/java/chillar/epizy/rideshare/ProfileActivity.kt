package chillar.epizy.rideshare

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.provider.MediaStore
import android.util.Log
import android.view.View
import android.widget.Toast
import chillar.epizy.rideshare.ui.main.User
import chillar.epizy.rideshare.ui.main.dbhandler
//import chillar.epizy.rideshare.ui.main.hisdbhandler
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.google.firebase.storage.FirebaseStorage
import com.squareup.picasso.Picasso
import kotlinx.android.synthetic.main.activity_profile.*
import kotlinx.coroutines.Dispatchers.IO
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.launch
import java.util.*
import kotlinx.coroutines.*

import kotlinx.coroutines.newSingleThreadContext
import java.security.AccessController.getContext

class ProfileActivity : AppCompatActivity() {
    var selectedPhotoUri: Uri?=null
    var name=""
    var email=""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_profile)
        var signinAccount= GoogleSignIn.getLastSignedInAccount(this)
        if(signinAccount!=null)
        {
            //gname.text=signinAccount.displayName
            name= signinAccount.displayName.toString()
            //gmail.text=signinAccount.email
            email=signinAccount.email.toString()
            //Picasso.get().load(signinAccount.photoUrl).into(profileimage);
            //imgProfile.setImageResource(signinAccount.photoUrl)
        }
        llProgressBar.visibility = View.GONE
        signOutbtn.setOnClickListener{
            val gso =GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN).build()
            val googleSignInClient =GoogleSignIn.getClient(this,gso)
            googleSignInClient.signOut()
            FirebaseAuth.getInstance().signOut()
            var db = dbhandler(this)
            db.DeleteEverything()
            startActivity(Intent(this,LoginActivity::class.java))
        }
        chillarinc.setOnClickListener{
            openurl("http://chillar.epizy.com")
        }
        uploadprofilebtn.setOnClickListener{
            Log.d("profile","msg uploading")
            var intent = Intent(Intent.ACTION_PICK)
            intent.type ="image/*"
            //intent.type ="csv/*"
                startActivityForResult(intent,0)
        }
        profileLoad()
    }
    private fun profileLoad(){
        val Uuid=FirebaseAuth.getInstance().uid
        val ref =FirebaseDatabase.getInstance().getReference("/users/"+Uuid+"/profile")
        ref.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onCancelled(error: DatabaseError) {Log.d("DBerror","error from database $error")            }
            override fun onDataChange(snapshot: DataSnapshot) {
                    Log.d("ProfileMessage",snapshot.toString())
                    val user =snapshot.getValue(User::class.java)
                    if(FirebaseAuth.getInstance().uid== user?.uid){
                        Picasso.get().load(user?.profileImageUrl).into(profileimage)
                        if (user != null) {
                            gname.setText(user.username)
                            gmail.setText(user.email)
                        }
                    }
                Log.d("profile","adapter started")
            }
        })
    }

    fun openurl(s:String){
        val i =Intent(Intent.ACTION_VIEW,Uri.parse(s))
        startActivity(i)
    }
    override  fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (requestCode ==0&&resultCode == Activity.RESULT_OK&&data!=null){
            // proceed and check image
            Log.d("profile","photo is being selected")
            selectedPhotoUri=data.data
            var bitmap= MediaStore.Images.Media.getBitmap(contentResolver, selectedPhotoUri)
            profileimage.setImageBitmap(bitmap)
            llProgressBar.visibility = View.VISIBLE
            uploadprofilebtn.isClickable=false
            // var bitmapDrawable=BitmapDrawable(bitmap)
            //uploadprofilebtn.setBackgroundDrawable(bitmapDrawable)
            GlobalScope.launch(newSingleThreadContext("UpdatingProfile")) {
                    Log.d("ImageActivity","$selectedPhotoUri")
                    if (selectedPhotoUri==null) return@launch
                    Log.d("ImageActivity","Uri not empty")
                    val filename= UUID.randomUUID().toString()
                    var ref = FirebaseStorage.getInstance().getReference("/images/$filename")
                    ref.putFile(selectedPhotoUri!!)
                        .addOnSuccessListener {
                            Log.d("progile","Image Uploaded Sucessfully at ${it.metadata?.path}")
                            ref.downloadUrl.addOnSuccessListener {

                                Log.d("ImageActivity","File Location $it")

                                saveUserToFirebase(it.toString())
                            }
                        }
            }
        }
    }
    private fun saveUserToFirebase(profileImageUrl: String){
        val uid= FirebaseAuth.getInstance().uid?:""
        var ref= FirebaseDatabase.getInstance().getReference("/users/"+uid+"/profile")
        val user = User(uid,name,email,profileImageUrl)
        ref.setValue(user)
            .addOnSuccessListener {
                Log.d("Profile","user Saved to Firebasee")
                llProgressBar.visibility = View.GONE
                Toast.makeText(baseContext, "Updated :)", Toast.LENGTH_SHORT).show()
            }
    }



}
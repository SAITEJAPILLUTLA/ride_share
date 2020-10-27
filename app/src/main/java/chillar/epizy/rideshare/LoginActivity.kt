package chillar.epizy.rideshare

import android.R.attr
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import chillar.epizy.rideshare.ui.main.User
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.api.ApiException
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.GoogleAuthProvider
import com.google.firebase.database.FirebaseDatabase
import kotlinx.android.synthetic.main.activity_login.*
import kotlinx.android.synthetic.main.activity_profile.*

class LoginActivity : AppCompatActivity() {
    companion object{
        val USER_KEY ="USER_KEY"
    }
    private var TAG:String = "MyActivity"
    //lateinit var auth: FirebaseAuth
    private lateinit var auth: FirebaseAuth
    private lateinit var googleSignInClient:GoogleSignInClient
    var RC_SIGN_IN:Int = 123
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)
        createRequest()
        auth = FirebaseAuth.getInstance()
        btngoolgesignin.setOnClickListener {
            signIn()
        }
    }
    private fun createRequest() {
        val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            .requestIdToken("819025610488-k5c0e6op027meqr51gtrd34u3b2df68b.apps.googleusercontent.com")
            .requestEmail()
            .build()
        // Build a GoogleSignInClient with the options specified by gso.
        googleSignInClient = GoogleSignIn.getClient(this, gso)
    }
    private fun signIn() {
        val signInIntent = googleSignInClient.signInIntent
        startActivityForResult(signInIntent, RC_SIGN_IN)
    }
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        // Result returned from launching the Intent from GoogleSignInApi.getSignInIntent(...);
        if (requestCode == RC_SIGN_IN) {
            val task =
                GoogleSignIn.getSignedInAccountFromIntent(data)
            try {
                // Google Sign In was successful, authenticate with Firebase
                val account = task.getResult(ApiException::class.java)!!
                Log.d(TAG, "firebaseAuthWithGoogle:" + account.id)
                firebaseAuthWithGoogle(account.idToken!!)
            } catch (e: ApiException) {
                // Google Sign In failed, update UI appropriately
                Log.w(TAG, "Google sign in failed", e)
                // ...
            }
        }
    }
    private fun firebaseAuthWithGoogle(idToken: String) {
        Toast.makeText(baseContext, "firebaseAuth now",
            Toast.LENGTH_SHORT).show()
        val credential = GoogleAuthProvider.getCredential(idToken, null)
        auth.signInWithCredential(credential)
            .addOnCompleteListener(this) { task ->
                if (task.isSuccessful) {
                    Log.d(TAG, "signInWithCredential:success")
                    saveUserToFirebase()
                    val intent=Intent(this,HomeActivity::class.java)
                    startActivity(intent)
                    finish()
                    //updateUI(user)
                } else {
                    Log.d(TAG, "signInWithCredential:failure", task.exception)
                    Toast.makeText(baseContext, "Login failed.",
                        Toast.LENGTH_SHORT).show()
                }
                // ...
            }
    }
    public override fun onStart() {
        super.onStart()
        var user=auth.currentUser
        if(user!=null)
        {
            val intent=Intent(this,HomeActivity::class.java)
            startActivity(intent)
            finish()
        }
    }
    private fun updateUI(currentUser : FirebaseUser?) {
        if(currentUser != null)
        {
            val intent=Intent(this,HomeActivity::class.java)
            startActivity(intent)
            finish()
        }
        else
        {
            Toast.makeText(baseContext,"Login Failed.",Toast.LENGTH_SHORT).show()
        }
    }
    override fun onBackPressed() {
        ActivityCompat.finishAffinity(this@LoginActivity)
    }


    private fun saveUserToFirebase(){
        val uid= FirebaseAuth.getInstance().uid?:""
        var signinAccount= GoogleSignIn.getLastSignedInAccount(this)
        var name= signinAccount?.displayName.toString()
        var email= signinAccount?.email.toString()
        var profileImageUrl:String="https://firebasestorage.googleapis.com/v0/b/carpool-1ec9c.appspot.com/o/images%2F7adf0706-5378-4e35-88e9-83578d0d8523?alt=media&token=25bd6495-dadf-4872-a501-ed84ad37a8f9"
        var ref= FirebaseDatabase.getInstance().getReference("/users/"+uid+"/profile")
        val user = User(uid,name,email,profileImageUrl)
        ref.setValue(user)
            .addOnSuccessListener {
                Log.d("Profile","user Saved to Firebasee")
                //llProgressBar.visibility = View.GONE
                Toast.makeText(baseContext, "Updated :)", Toast.LENGTH_SHORT).show()
            }
    }


}



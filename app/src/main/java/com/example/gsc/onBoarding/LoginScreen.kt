package com.example.gsc.onBoarding

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import com.example.gsc.DataClass.MarkerDataClass
import com.example.gsc.MainActivity
import com.example.gsc.Permissions.hasLocationPermission
import com.example.gsc.R
import com.example.gsc.onBoarding.carouselView.CarouselView
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInAccount
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.api.ApiException
import com.google.android.gms.tasks.Task
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.GoogleAuthProvider
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch


class LoginScreen:AppCompatActivity() {
    private lateinit var markerList1:ArrayList<MarkerDataClass>
    private lateinit var mAuth:FirebaseAuth
    private lateinit var googleSignInClient:GoogleSignInClient
    private lateinit var db:FirebaseFirestore

    companion object{
        private const val RC_SIGN_IN = 100

    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.login_screen)
//        markerList1 = ArrayList()
//        GlobalScope.launch(Dispatchers.IO) {
//            fetchData()
//        }
        mAuth= FirebaseAuth.getInstance()
        val gso=GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            .requestIdToken(getString(R.string.default_web_client_id))
            .requestEmail()
            .build()

        googleSignInClient = GoogleSignIn.getClient(this,gso)

        findViewById<Button>(R.id.signInButton).setOnClickListener {
            signInGoogle()
        }
    }

//    private suspend fun fetchData() {
//        db= FirebaseFirestore.getInstance()
//        db.collection("Recent  Alerts")
//            .get()
//            .addOnSuccessListener { result ->
//                for (document in result) {
//                    val myData = document.toObject(MarkerDataClass::class.java)
//                    val latitude = myData.latitude
//                    val longitude = myData.longitude
//                    markerList1.add(myData)
//                }
//            }
//    }

    private fun signInGoogle() {
        val signInIntent = googleSignInClient.signInIntent
        googleSignInClient.signOut()
        startActivityForResult(signInIntent, RC_SIGN_IN)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == RC_SIGN_IN) {
            val task = GoogleSignIn.getSignedInAccountFromIntent(data)
            val exception =task.exception
            if(task.isSuccessful){

                try {
                    // Google Sign In was successful, authenticate with Firebase
                    val account = task.getResult(ApiException::class.java)!!
                    Log.d("Login Activity", "firebaseAuthWithGoogle:" + account.id)
                    firebaseAuthWithGoogle(account.idToken!!)
                } catch (e: ApiException) {
                    // Google Sign In failed, update UI appropriately
                    Log.w("Login Activity", "Google sign in failed", e)
                }
            }
            else{
                Log.w("MainActivity2",exception.toString())
            }

        }
    }

    private fun firebaseAuthWithGoogle(idToken: String) {
        val credential = GoogleAuthProvider.getCredential(idToken, null)
        mAuth.signInWithCredential(credential)
            .addOnCompleteListener(this) { task ->
                if (task.isSuccessful) {
                    // Sign in success, update UI with the signed-in user's information
                    Log.d("MainActivity2", "signInWithCredential:success")


                    val user = mAuth.currentUser
                    val uid = user?.uid
                    val email = user?.email
                    val name = user?.uid

                        db = FirebaseFirestore.getInstance()
                        db.collection("Users").document("$name")
                            .get()
                            .addOnSuccessListener {

                                //Returns value of corresponding fields
                                val b = it["ProfileCreated"].toString()

                                if (b=="1") {
                                    val dashboardIntent = Intent(this, PermissionActivity::class.java)
                                    startActivity(dashboardIntent)
                                    finish()
                                }
                                else {
                                    val data= hashMapOf(
                                        "Name" to user?.displayName.toString(),
                                        "ProfileCreated" to "1"
                                    )
                                    db.collection("Users").document("$uid")
                                        .set(data)
                                        .addOnSuccessListener { docref->
                                            Log.d("Account Addition","DocumentSnapshot written with ID: ${docref}.id")
                                            Toast.makeText(this,"User Created",Toast.LENGTH_SHORT).show()
                                            val dashboardIntent = Intent(this,CarouselView::class.java)
                                            startActivity(dashboardIntent)
                                            finish()
                                        }
                                }
                            }

//                        Toast.makeText(this,"Login Successful",Toast.LENGTH_SHORT).show()
//                        val intent = Intent(this,MainActivity::class.java)
//                        startActivity(intent)
//                        finish()



                    //Toast.makeText(this, "Login Successful", Toast.LENGTH_SHORT).show()


                }
                else {
                    // If sign in fails, display a message to the user
                    Log.w("MainActivity2", "signInWithCredential:failure", task.exception)
                }
            }
    }

//    private fun handleResults(task: Task<GoogleSignInAccount>) {
//        if(task.isSuccessful){
//            val account: GoogleSignInAccount? =task.result
//            if(account!=null){
//                updateUi(account)
//            }
//        }else{
//            Toast.makeText(this,task.exception.toString(), Toast.LENGTH_SHORT).show()
//        }
//    }

//    private fun updateUi(account:GoogleSignInAccount) {
//        val credential= GoogleAuthProvider.getCredential(account.idToken,null)
//        mAuth.signInWithCredential(credential).addOnCompleteListener{
//            if(it.isSuccessful){
//                if(hasLocationPermission(this)){
//                    val intent= Intent(this, MainActivity::class.java)
//                    startActivity(intent)
//                    finish()
//                }else{
//                    startActivity(Intent(this,PermissionActivity::class.java))
//                    finish()
//                }
//
//            }   else{
//                Toast.makeText(this,it.exception.toString(),Toast.LENGTH_SHORT).show()
//            }
//        }
//    }


}
package com.example.gsc.onBoarding

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import com.example.gsc.MainActivity
import com.example.gsc.Permissions.hasLocationPermission
import com.example.gsc.R
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInAccount
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.tasks.Task
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.GoogleAuthProvider


class LoginScreen:AppCompatActivity() {
    private lateinit var mAuth:FirebaseAuth
    private lateinit var googleSignInClient:GoogleSignInClient

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.login_screen)

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
    private fun signInGoogle() {
        val signInIntent = googleSignInClient.signInIntent
        launcher.launch(signInIntent)
    }
    private val launcher=registerForActivityResult(ActivityResultContracts.StartActivityForResult()){
        result->
        if(result.resultCode == Activity.RESULT_OK){
            val task=GoogleSignIn.getSignedInAccountFromIntent(result.data)
            handleResults(task)
        }
    }
    private fun handleResults(task: Task<GoogleSignInAccount>) {
        if(task.isSuccessful){
            val account: GoogleSignInAccount? =task.result
            if(account!=null){
                updateUi(account)
            }
        }else{
            Toast.makeText(this,task.exception.toString(), Toast.LENGTH_SHORT).show()
        }
    }

    private fun updateUi(account:GoogleSignInAccount) {
        val credential= GoogleAuthProvider.getCredential(account.idToken,null)
        mAuth.signInWithCredential(credential).addOnCompleteListener{
            if(it.isSuccessful){
                if(hasLocationPermission(this)){
                    val intent= Intent(this, MainActivity::class.java)
                    startActivity(intent)
                }else{
                    startActivity(Intent(this,PermissionActivity::class.java))
                }

            }   else{
                Toast.makeText(this,it.exception.toString(),Toast.LENGTH_SHORT).show()
            }
        }
    }



}
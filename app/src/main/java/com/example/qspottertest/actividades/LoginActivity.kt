package com.example.qspottertest.actividades

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import com.example.qspottertest.R
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.api.ApiException
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.GoogleAuthProvider

class LoginActivity : AppCompatActivity() {
    private val google_sign_in = 100
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setup()

    }
     fun setup(){


     val config = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
        .requestIdToken(getString(R.string.default_web_client_id)).requestId().requestEmail().build()
     val cliente = GoogleSignIn.getClient(this,config)
     cliente.signOut()
     startActivityForResult(cliente.signInIntent,google_sign_in)

     }
    fun showMap(email:String?){
        val mapIntent = Intent(this, MapsActivity::class.java).apply(){
            putExtra("id",email.toString())
        }
        startActivity(mapIntent)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if(requestCode == google_sign_in){
            val task = GoogleSignIn.getSignedInAccountFromIntent(data)
            try {
                val cuenta = task.getResult(ApiException::class.java)
                if(cuenta != null){
                    val credencial = GoogleAuthProvider.getCredential(cuenta.idToken,null)
                    FirebaseAuth.getInstance().signInWithCredential(credencial).addOnCompleteListener{
                        if(it.isSuccessful){
                            showMap(cuenta.email)
                        }else{
                            Toast.makeText(
                                this@LoginActivity,
                                "Error",
                                Toast.LENGTH_SHORT
                            ).show()
                        }
                    }
                }

            }catch (e: ApiException){
                Toast.makeText(
                    this@LoginActivity,
                    "Error",
                    Toast.LENGTH_SHORT
                ).show()
            }

            

        }
    }
}
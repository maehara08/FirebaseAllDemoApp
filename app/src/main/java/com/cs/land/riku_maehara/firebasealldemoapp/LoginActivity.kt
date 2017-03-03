package com.cs.land.riku_maehara.firebasealldemoapp

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.support.annotation.IdRes
import android.support.v7.app.AppCompatActivity
import android.view.View
import android.widget.Toast
import com.google.android.gms.auth.api.Auth
import com.google.android.gms.auth.api.signin.GoogleSignInAccount
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.auth.api.signin.GoogleSignInResult
import com.google.android.gms.common.SignInButton
import com.google.android.gms.common.api.CommonStatusCodes
import com.google.android.gms.common.api.GoogleApiClient
import com.google.android.gms.tasks.OnCompleteListener
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.GoogleAuthProvider
import kotlin.properties.Delegates


class LoginActivity : AppCompatActivity() {
    companion object {
        const val TAG = "LoginActivity";
        const val REQUEST_CODE_GOOGLE_SIGN_IN = 100

    }

    var googleApiClient: GoogleApiClient by Delegates.notNull()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)

        //setup google
        val googleSignOptions = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(R.string.google_web_client_id))
                .build()
        googleApiClient = GoogleApiClient.Builder(this)
                .enableAutoManage(this, GoogleApiClient.OnConnectionFailedListener {
                    Toast.makeText(this, "Google認証に失敗しました", Toast.LENGTH_SHORT).show()
                }).addApi(Auth.GOOGLE_SIGN_IN_API, googleSignOptions)
                .build()
        val signInGoogleButton: SignInButton = bindView(R.id.google_sign_in_button)
        signInGoogleButton.apply {
            setSize(SignInButton.SIZE_STANDARD)
            setOnClickListener {
                signInWithGoogle()
            }
        }

    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        when (resultCode) {
            REQUEST_CODE_GOOGLE_SIGN_IN -> {
                val result = Auth.GoogleSignInApi.getSignInResultFromIntent(data)

            }
        }
    }

    private fun signInWithGoogle() {
        val signInIntent = Auth.GoogleSignInApi.getSignInIntent(googleApiClient)
        startActivityForResult(signInIntent, REQUEST_CODE_GOOGLE_SIGN_IN)
    }

    private fun handleSignInWithGoogleResult(result: GoogleSignInResult) {
        if (result.isSuccess) {
            // succeed sign in
            val account = result.signInAccount
            firebaseAuthWithGoogle(account!!)
        } else {
            // failed sign in
            Toast.makeText(this ,"failed",Toast.LENGTH_SHORT).show()
        }
    }

    private fun firebaseAuthWithGoogle(account: GoogleSignInAccount) {
        val credential = GoogleAuthProvider.getCredential(account.idToken, null)
        FirebaseAuth.getInstance().signInWithCredential(credential)
                .addOnCompleteListener(this/*Activity*/, OnCompleteListener {
                    Toast.makeText(this,"complete!",Toast.LENGTH_SHORT).show()
                })
    }

}

fun <T : View> Activity.bindView(@IdRes id: Int): T = findViewById(id) as T

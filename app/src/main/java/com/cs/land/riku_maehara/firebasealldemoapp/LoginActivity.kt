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
import com.google.android.gms.common.ConnectionResult
import com.google.android.gms.common.SignInButton
import com.google.android.gms.common.api.CommonStatusCodes
import com.google.android.gms.common.api.GoogleApiClient
import com.google.android.gms.tasks.OnCompleteListener
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.GoogleAuthProvider
import timber.log.Timber
import kotlin.properties.Delegates


class LoginActivity : AppCompatActivity(), GoogleApiClient.OnConnectionFailedListener, FirebaseAuth.AuthStateListener {


    companion object {
        const val TAG = "LoginActivity"
        const val REQUEST_CODE_GOOGLE_SIGN_IN = 100

    }

    var googleApiClient: GoogleApiClient by Delegates.notNull()
    var firebaseAuth: FirebaseAuth by Delegates.notNull()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)

        Timber.d(getString(R.string.default_web_client_id))
        firebaseAuth = FirebaseAuth.getInstance()
        //setup google
        val googleSignOptions = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(resources.getString(R.string.default_web_client_id))
                .requestEmail()
                .build()

        googleApiClient = GoogleApiClient.Builder(this)
                .enableAutoManage(this, this)
                .addApi(Auth.GOOGLE_SIGN_IN_API, googleSignOptions)
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

        when (requestCode) {
            REQUEST_CODE_GOOGLE_SIGN_IN -> {
                val result = Auth.GoogleSignInApi.getSignInResultFromIntent(data)
                handleSignInWithGoogleResult(result)
            }
        }
    }

    override fun onStart() {
        super.onStart()
        firebaseAuth.addAuthStateListener(this)
    }

    override fun onStop() {
        super.onStop()
        firebaseAuth.removeAuthStateListener(this)
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
            Toast.makeText(this, "failed " + CommonStatusCodes.getStatusCodeString(result.status.statusCode), Toast.LENGTH_SHORT).show()
        }
    }

    private fun firebaseAuthWithGoogle(account: GoogleSignInAccount) {
        val credential = GoogleAuthProvider.getCredential(account.idToken, null)
        FirebaseAuth.getInstance().signInWithCredential(credential)
                .addOnCompleteListener(this, OnCompleteListener {
                    Toast.makeText(this, "complete!", Toast.LENGTH_SHORT).show()
                })
    }

    override fun onConnectionFailed(p0: ConnectionResult) {
        Toast.makeText(this, "Google認証に失敗しました" + p0.errorMessage, Toast.LENGTH_SHORT).show()
    }

    override fun onAuthStateChanged(p0: FirebaseAuth) {
        Timber.d("onAuthStatusChanged")
        val firebaseUser = firebaseAuth.currentUser;
        firebaseUser?.let {
            Timber.d("onAuthStatusChanged2")
        }
    }
}

fun <T : View> Activity.bindView(@IdRes id: Int): T = findViewById(id) as T

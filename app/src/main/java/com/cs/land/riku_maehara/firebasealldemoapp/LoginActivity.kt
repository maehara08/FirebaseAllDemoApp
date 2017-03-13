package com.cs.land.riku_maehara.firebasealldemoapp

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.support.annotation.IdRes
import android.support.v7.app.AppCompatActivity
import android.view.View
import android.widget.Button
import android.widget.Toast
import com.facebook.AccessToken
import com.facebook.CallbackManager
import com.facebook.FacebookCallback
import com.facebook.FacebookException
import com.facebook.login.LoginResult
import com.facebook.login.widget.LoginButton
import com.google.android.gms.auth.api.Auth
import com.google.android.gms.auth.api.signin.GoogleSignInAccount
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.auth.api.signin.GoogleSignInResult
import com.google.android.gms.common.ConnectionResult
import com.google.android.gms.common.SignInButton
import com.google.android.gms.common.api.CommonStatusCodes
import com.google.android.gms.common.api.GoogleApiClient
import com.google.firebase.auth.FacebookAuthProvider
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.GoogleAuthProvider
import com.google.firebase.auth.TwitterAuthProvider
import com.twitter.sdk.android.core.Callback
import com.twitter.sdk.android.core.Result
import com.twitter.sdk.android.core.TwitterException
import com.twitter.sdk.android.core.TwitterSession
import com.twitter.sdk.android.core.identity.TwitterLoginButton
import timber.log.Timber
import kotlin.properties.Delegates


class LoginActivity : AppCompatActivity(), GoogleApiClient.OnConnectionFailedListener, FirebaseAuth.AuthStateListener {


    companion object {
        const val TAG = "LoginActivity"
        const val REQUEST_CODE_GOOGLE_SIGN_IN = 100
        // Note: Your consumer key and secret should be obfuscated in your source code before shipping.
        const val TWITTER_KEY = "ha1nN9rncDWHzDjjzyhnN6C0G"
        const val TWITTER_SECRET = "LuMosIvn8DMUDQav5lvHgaC0zwrXjvOHmgmDbvTJDkzrqOtq2O"
    }

    var googleApiClient: GoogleApiClient by Delegates.notNull()
    var firebaseAuth: FirebaseAuth by Delegates.notNull()
    var callbackManager: CallbackManager by Delegates.notNull()
    var twitterLoginButton: TwitterLoginButton by Delegates.notNull()
    var emailButton: Button by Delegates.notNull()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)

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

        //facebook
        callbackManager = CallbackManager.Factory.create()
        val facebookLoginCallback = object : FacebookCallback<LoginResult> {
            override fun onError(error: FacebookException?) {
                error?.message?.let {
                    this@LoginActivity.showToast(it)
                }
            }

            override fun onSuccess(result: LoginResult?) {
                result?.let {
                    firebaseAuthWithFacebook(it.accessToken)
                }
            }

            override fun onCancel() {
            }
        }
        (findViewById(R.id.facebook_login_button) as LoginButton).apply {
            setReadPermissions("email", "public_profile")
            registerCallback(callbackManager, facebookLoginCallback)
        }

        //Twitter
        val twitterLoginCallback = object : Callback<TwitterSession>() {
            override fun success(result: Result<TwitterSession>?) {
                result?.data?.let {
                    firebaseAuthWithTwitter(it)
                }

            }

            override fun failure(exception: TwitterException?) {
                exception?.message?.let {
                    this@LoginActivity.showToast(it)
                }
            }
        }
        twitterLoginButton = bindView(R.id.twitter_login_button)
        twitterLoginButton.apply {
            callback = twitterLoginCallback
        }

        //email
        emailButton = bindView(R.id.email_button)
        emailButton.setOnClickListener {
            val intent =  Intent(this, FormActivity::class.java)
            startActivity(intent)
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        when (requestCode) {
            REQUEST_CODE_GOOGLE_SIGN_IN -> {
                val result = Auth.GoogleSignInApi.getSignInResultFromIntent(data)
                handleSignInWithGoogleResult(result)
            }
            else -> {
                callbackManager.onActivityResult(requestCode, resultCode, data)
                twitterLoginButton.onActivityResult(requestCode, resultCode, data)
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
            showToast("failed " + CommonStatusCodes.getStatusCodeString(result.status.statusCode))
        }
    }

    private fun firebaseAuthWithGoogle(account: GoogleSignInAccount) {
        val credential = GoogleAuthProvider.getCredential(account.idToken, null)
        FirebaseAuth.getInstance().signInWithCredential(credential)
                .addOnCompleteListener {
                    showToast("Complete!")
                }
    }

    private fun firebaseAuthWithFacebook(token: AccessToken) {
        val credential = FacebookAuthProvider.getCredential(token.token)
        firebaseAuth.signInWithCredential(credential)
                .addOnCompleteListener {
                    showToast("Complete!")
                }
    }

    private fun firebaseAuthWithTwitter(session: TwitterSession) {
        val credential = TwitterAuthProvider.getCredential(session.authToken.token, session.authToken.secret)
        firebaseAuth.signInWithCredential(credential)
                .addOnCompleteListener {
                    showToast("Complete!")
                }
    }

    override fun onConnectionFailed(p0: ConnectionResult) {
        showToast("Google認証に失敗しました")
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
fun Activity.showToast(message: String) = Toast.makeText(applicationContext, message, Toast.LENGTH_SHORT).show()

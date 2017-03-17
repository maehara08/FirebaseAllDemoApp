package com.cs.land.riku_maehara.firebasealldemoapp

import android.content.Intent
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.widget.Button
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
import com.google.android.gms.tasks.OnCompleteListener
import com.google.android.gms.tasks.Task
import com.google.firebase.auth.*
import com.twitter.sdk.android.core.Callback
import com.twitter.sdk.android.core.Result
import com.twitter.sdk.android.core.TwitterException
import com.twitter.sdk.android.core.TwitterSession
import com.twitter.sdk.android.core.identity.TwitterLoginButton
import timber.log.Timber
import kotlin.properties.Delegates


class LoginActivity : AppCompatActivity(), GoogleApiClient.OnConnectionFailedListener, FirebaseAuth.AuthStateListener, OnCompleteListener<AuthResult> {

    companion object {
        const val TAG = "LoginActivity"
        const val REQUEST_CODE_GOOGLE_SIGN_IN = 100
        // Note: Your consumer key and secret should be obfuscated in your source code before shipping.
        const val REQUEST_CODE_EMAIL_SIGN_IN = 101
        const val REQUEST_CODE_PROFILE = 102

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
                    this@LoginActivity.showToast(this@LoginActivity, it)
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
                    this@LoginActivity.showToast(this@LoginActivity, it)
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
            val intent = Intent(this, FormActivity::class.java)
            startActivityForResult(intent, REQUEST_CODE_EMAIL_SIGN_IN)
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        when (requestCode) {
            REQUEST_CODE_GOOGLE_SIGN_IN -> {
                val result = Auth.GoogleSignInApi.getSignInResultFromIntent(data)
                handleSignInWithGoogleResult(result)
            }
            REQUEST_CODE_EMAIL_SIGN_IN -> {
                data?.let {
                    firebaseAuthWithEmailAndPassword(it)
                }
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
            showToast(this, "failed " + CommonStatusCodes.getStatusCodeString(result.status.statusCode))
        }
    }

    private fun firebaseAuthWithGoogle(account: GoogleSignInAccount) {
        val credential = GoogleAuthProvider.getCredential(account.idToken, null)
        FirebaseAuth.getInstance().signInWithCredential(credential)
                .addOnCompleteListener(this)
    }

    private fun firebaseAuthWithFacebook(token: AccessToken) {
        val credential = FacebookAuthProvider.getCredential(token.token)
        firebaseAuth.signInWithCredential(credential)
                .addOnCompleteListener(this)
    }

    private fun firebaseAuthWithTwitter(session: TwitterSession) {
        val credential = TwitterAuthProvider.getCredential(session.authToken.token, session.authToken.secret)
        firebaseAuth.signInWithCredential(credential)
                .addOnCompleteListener(this)
    }

    private fun firebaseAuthWithEmailAndPassword(data: Intent) {
        if (data.getBooleanExtra(FormActivity.KEY_HAS_ACCOUNT, true)) {
            firebaseAuth.signInWithEmailAndPassword(data.getStringExtra(FormActivity.KEY_EMAIL),
                    data.getStringExtra(FormActivity.KEY_PASSWORD))
                    .addOnCompleteListener(this)
        } else {
            Timber.d(data.getStringExtra(FormActivity.KEY_PASSWORD))
            firebaseAuth.createUserWithEmailAndPassword(data.getStringExtra(FormActivity.KEY_EMAIL),
                    data.getStringExtra(FormActivity.KEY_PASSWORD))
                    .addOnCompleteListener(this)
        }
    }

    override fun onConnectionFailed(p0: ConnectionResult) {
        showToast(this, "Google認証に失敗しました")
    }

    override fun onAuthStateChanged(firebaseAuth: FirebaseAuth) {
        val user = firebaseAuth.currentUser
        user?.let {
            val userInfoBundle = Bundle()
            userInfoBundle.apply {
                putString(ProfileActivity.KEY_USER_UID, it.uid)
                putString(ProfileActivity.KEY_USER_EMAIL, it.email)
                Timber.d(it.providers?.get(0) ?: "null!")
                if (it.providers?.isEmpty() ?: true) {
                    putString(ProfileActivity.KEY_USER_PROVIDER, getString(R.string.user_profile_unknown))
                } else {
                    putString(ProfileActivity.KEY_USER_PROVIDER, it.providers!!.get(0))
                }
            }
            startActivityForResult(ProfileActivity.createIntent(this, userInfoBundle), REQUEST_CODE_PROFILE)
            finish()
        }
    }

    override fun onComplete(task: Task<AuthResult>) {
        if (!task.isSuccessful) {
            showToast(this, task.exception?.message ?: "null")
            Timber.e(task.exception)
        } else {
            showToast(this, "Complete!")
        }
    }
}
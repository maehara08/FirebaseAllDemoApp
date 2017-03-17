package com.cs.land.riku_maehara.firebasealldemoapp

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.text.TextUtils
import android.widget.Button
import android.widget.TextView
import com.google.firebase.auth.*
import kotlin.properties.Delegates

class ProfileActivity : AppCompatActivity() {
    companion object {
        const val KEY_USER_BUNDLE = "key_user_bundle"
        const val KEY_USER_UID = "key_user_id"
        const val KEY_USER_EMAIL = "key_user_email"
        const val KEY_USER_PROVIDER = "key_user_provider"

        fun createIntent(context: Context, userInfoBundle: Bundle): Intent {
            val intent = Intent(context, ProfileActivity::class.java)
            return intent.putExtra(KEY_USER_BUNDLE, userInfoBundle)
        }
    }

    enum class Provider(val providerId: String) {
        GOOGLE(GoogleAuthProvider.PROVIDER_ID),
        FACEBOOK(FacebookAuthProvider.PROVIDER_ID),
        TWITTER(TwitterAuthProvider.PROVIDER_ID),
        EMAIL(EmailAuthProvider.PROVIDER_ID);
    }

    private var firebaseAuth: FirebaseAuth by Delegates.notNull()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_profile)

        firebaseAuth = FirebaseAuth.getInstance()

        initializeViews()
    }

    private fun initializeViews() {
        val userInfoBundle = intent.getBundleExtra(KEY_USER_BUNDLE)
        val userId = userInfoBundle.getString(KEY_USER_UID)
        val userEmail = userInfoBundle.getString(KEY_USER_EMAIL)
        val userProvider = userInfoBundle.getString(KEY_USER_PROVIDER)

        val userUidTextView: TextView = bindView(R.id.user_uid_text_view)
        val userEmailTextView: TextView = bindView(R.id.user_email_text_view)
        val userProviderTextView: TextView = bindView(R.id.user_provider_text_view)
        val startChatButton: Button = bindView(R.id.start_chat_button)
        val signOutButton: Button = bindView(R.id.sign_out_button)

        userUidTextView.text = userId
        userEmailTextView.text = if (TextUtils.isEmpty(userEmail)) getString(R.string.user_profile_unknown) else userEmail
        userProviderTextView.text = userProvider

        startChatButton.setOnClickListener {
            val intent = Intent(this, MainActivity::class.java)
            startActivity(intent)
            finish()
        }
        signOutButton.setOnClickListener {
            val intent =Intent(this, LoginActivity::class.java)
            firebaseAuth.signOut()
            startActivity(intent)
            finish()
        }
    }
}

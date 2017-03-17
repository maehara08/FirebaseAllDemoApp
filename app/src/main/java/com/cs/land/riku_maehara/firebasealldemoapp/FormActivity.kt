package com.cs.land.riku_maehara.firebasealldemoapp

import android.content.Intent
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.text.TextUtils
import android.view.View
import android.widget.Button
import android.widget.EditText
import kotlin.properties.Delegates

public class FormActivity : AppCompatActivity(), View.OnClickListener {

    companion object {
        const val KEY_EMAIL = "key_email"
        const val KEY_PASSWORD = "key_password"
        const val KEY_HAS_ACCOUNT = "key_has_account"

    }

    private var emailEditText: EditText by Delegates.notNull()
    private var passwordEditText: EditText by Delegates.notNull()
    private var sigininButton: Button by Delegates.notNull()
    private var siginupButton: Button by Delegates.notNull()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_form)

        emailEditText = bindView(R.id.email_edit_text)
        passwordEditText = bindView(R.id.password_edit_text)
        sigininButton = bindView(R.id.sigin_in_button)
        siginupButton = bindView(R.id.sign_up_button)

        sigininButton.setOnClickListener(this)
        siginupButton.setOnClickListener(this)

    }

    override fun onClick(v: View?) {
        if (!TextUtils.isEmpty(emailEditText.text) && !TextUtils.isEmpty(passwordEditText.text)) {
            val intent = Intent().apply {
                putExtra(KEY_EMAIL, emailEditText.text.toString())
                putExtra(KEY_PASSWORD, passwordEditText.text.toString())
                if (v!!.id == R.id.sign_up_button) {
                    putExtra(KEY_HAS_ACCOUNT, false)
                } else {
                    putExtra(KEY_HAS_ACCOUNT, true)
                }
            }
            setResult(RESULT_OK, intent)
            finish()
        } else {
            showToast(this, "plz input")
        }
    }
}
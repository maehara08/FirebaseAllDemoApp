package com.cs.land.riku_maehara.firebasealldemoapp

import android.os.Bundle
import android.os.PersistableBundle
import android.support.v7.app.AppCompatActivity
import android.widget.Button
import android.widget.EditText
import kotlin.properties.Delegates

public class FormActivity : AppCompatActivity() {
    companion object{

    }

    private var emailEditText:EditText by Delegates.notNull()
    private var passwordEditText:EditText by Delegates.notNull()
    private var sigininButton: Button by Delegates.notNull()
    private var siginupButton: Button by Delegates.notNull()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_form)

        emailEditText = bindView(R.id.email_edit_text)
        passwordEditText = bindView(R.id.password_edit_text)
        sigininButton = bindView(R.id.sigin_in_button)
        siginupButton = bindView(R.id.sign_up_button)

        sigininButton.setOnClickListener {

        }

    }
}
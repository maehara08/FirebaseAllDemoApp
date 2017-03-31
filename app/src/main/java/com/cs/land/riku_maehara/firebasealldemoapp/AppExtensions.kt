package com.cs.land.riku_maehara.firebasealldemoapp

import android.app.Activity
import android.support.annotation.IdRes
import android.view.View
import android.widget.Toast

/**
 * Created by riku_maehara on 2017/03/17.
 */

fun <T : View> Activity.bindView(@IdRes id: Int): T = findViewById(id) as T
fun <T : View> View.bindView(@IdRes id: Int): T = findViewById(id) as T

fun Activity.showToast(context: Activity, message: String) = Toast.makeText(context, message, Toast.LENGTH_SHORT).show()

package com.cs.land.riku_maehara.firebasealldemoapp

import android.app.Application
import android.support.multidex.MultiDexApplication
import timber.log.Timber

/**
 * Created by riku_maehara on 2017/03/13.
 */

public class MyApplication : MultiDexApplication() {
    override fun onCreate() {
        super.onCreate()
        if (BuildConfig.DEBUG) {
            Timber.plant(Timber.DebugTree())
        }
    }
}
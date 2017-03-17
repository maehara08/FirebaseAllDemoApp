package com.cs.land.riku_maehara.firebasealldemoapp

import android.support.multidex.MultiDexApplication
import com.facebook.FacebookSdk
import com.twitter.sdk.android.Twitter
import com.twitter.sdk.android.core.TwitterAuthConfig
import io.fabric.sdk.android.Fabric
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
        FacebookSdk.sdkInitialize(applicationContext)
        //twitter
        val authConfig = TwitterAuthConfig(getString(R.string.TWITTER_KEY),getString(R.string.TWITTER_SECRET))
        Fabric.with(this, Twitter(authConfig))
    }
}
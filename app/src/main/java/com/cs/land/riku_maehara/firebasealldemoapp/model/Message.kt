package com.cs.land.riku_maehara.firebasealldemoapp.model

import com.google.firebase.database.Exclude
import com.google.firebase.database.IgnoreExtraProperties
import java.util.*


/**
 * Created by riku_maehara on 2017/03/17.
 */
@IgnoreExtraProperties
class Message() {
    var message: String = ""
    var name: String = ""
    var timeStamp: Long = 1L

    constructor(message: String, name: String, timeStamp: Long) : this() {
        this.message = message
        this.name = name
        this.timeStamp = timeStamp
    }

    @Exclude
    fun toMap(): Map<String, Any> {
        val result = HashMap<String, Any>()
        result.put("name", name)
        result.put("timeStamp", timeStamp)
        result.put("message", message)

        return result
    }
}
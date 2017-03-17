package com.cs.land.riku_maehara.firebasealldemoapp.model

import com.google.firebase.database.IgnoreExtraProperties

/**
 * Created by riku_maehara on 2017/03/17.
 */
@IgnoreExtraProperties
public class Message(){
    var message:String? =""
    var name:String? =""
    var timeStamp:Long =1L
    constructor(message: String,name:String,timeStamp:Long) : this() {
        this.message=message
        this.name = name
        this.timeStamp = timeStamp
    }
}
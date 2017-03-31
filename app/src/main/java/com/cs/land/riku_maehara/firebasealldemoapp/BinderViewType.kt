package com.cs.land.riku_maehara.firebasealldemoapp

import jp.satorufujiwara.binder.ViewType

/**
 * Created by riku_maehara on 2017/03/31.
 */

enum class BinderViewType : ViewType {
    //chat
    MyMessage,
    OtherMessage
    ;

    override fun viewType(): Int = ordinal
}
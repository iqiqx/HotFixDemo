package com.xx.hotfixdemo

import android.app.Application
import android.content.Context

class App : Application() {
    override fun attachBaseContext(base: Context?) {
        super.attachBaseContext(base)
        HotFixSDK(this).install()
    }
}
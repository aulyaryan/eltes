package com.inventaris.app

import android.app.Application
import com.inventaris.app.api.RetrofitClient

class InventarisApp : Application() {
    override fun onCreate() {
        super.onCreate()
        instance = this
        RetrofitClient.init()
    }
    companion object {
        lateinit var instance: InventarisApp; private set
    }
}

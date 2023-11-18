package com.example.restaurantapi.framework

import android.app.Application
import dagger.hilt.android.HiltAndroidApp

@HiltAndroidApp
class CustomerApp : Application() {
    override fun onCreate() {
        super.onCreate()


    }

}
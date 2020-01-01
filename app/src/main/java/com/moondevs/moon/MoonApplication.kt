package com.moondevs.moon

import android.app.Application
import timber.log.Timber

class MoonApplication : Application() {
    override fun onCreate() {
        super.onCreate()
        Timber.plant(Timber.DebugTree())
    }
}
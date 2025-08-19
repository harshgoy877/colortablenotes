package com.colortablenotes

import android.app.Application
import dagger.hilt.android.HiltAndroidApp

@HiltAndroidApp
class NotesApplication : Application() {

    override fun onCreate() {
        super.onCreate()
        setupAppConfiguration()
    }

    private fun setupAppConfiguration() {
        // App-wide initialization - privacy focused, no analytics
    }
}

package com.example.lab5

import android.app.Application
import android.util.Log
import com.example.lab5.analytics.AppMetricaAnalyticsService
import com.yandex.mapkit.MapKitFactory

class Lab5Application : Application() {
    override fun onCreate() {
        super.onCreate()
        AppContainer.initialize(this)
        initMapKit()
        AppMetricaAnalyticsService.activate(this, BuildConfig.APPMETRICA_API_KEY)
    }

    private fun initMapKit() {
        if (BuildConfig.MAPKIT_API_KEY.isBlank()) {
            Log.w("Lab5Application", "MAPKIT_API_KEY is empty. Map screen will show setup message.")
            return
        }
        runCatching {
            MapKitFactory.setApiKey(BuildConfig.MAPKIT_API_KEY)
        }.onFailure { error ->
            Log.w("Lab5Application", "MapKit api key was already set or failed to initialize.", error)
        }
    }
}

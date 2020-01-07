package com.amartin.marvelapplication.data.impl

import android.app.Application
import android.location.Geocoder
import android.location.Location
import com.amartin.marvelapplication.data.source.LocationDataSource
import com.google.android.gms.location.LocationServices
import kotlinx.coroutines.suspendCancellableCoroutine
import kotlin.coroutines.resume

class PlayServicesLocationDataSource(application: Application) : LocationDataSource {

    private val geocoder = Geocoder(application)
    private val fusedLocationClient = LocationServices.getFusedLocationProviderClient(application)

    override suspend fun findLastRegionLanguage(): String? {
        return suspendCancellableCoroutine { cancellableContinuation ->
            fusedLocationClient.lastLocation.addOnCompleteListener {
                cancellableContinuation.resume(it.result.toLanguage())
            }
        }
    }

    private fun Location?.toLanguage(): String? {
        return this?.let {
            geocoder.getFromLocation(latitude, longitude, 1)
        }?.firstOrNull()?.locale?.language
    }

}
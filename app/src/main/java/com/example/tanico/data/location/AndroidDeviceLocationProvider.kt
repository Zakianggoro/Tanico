package com.example.tanico.data.location

import android.Manifest
import android.annotation.SuppressLint
import android.content.Context
import android.content.pm.PackageManager
import android.location.Location
import android.location.LocationListener
import android.location.LocationManager
import android.os.Build
import android.os.CancellationSignal
import android.os.Looper
import androidx.core.content.ContextCompat
import com.example.tanico.domain.location.DeviceLocationProvider
import com.example.tanico.domain.model.DeviceCoordinates
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.suspendCancellableCoroutine
import kotlinx.coroutines.withTimeoutOrNull
import kotlin.coroutines.resume
import kotlin.coroutines.resumeWithException

class AndroidDeviceLocationProvider(
    context: Context
) : DeviceLocationProvider {
    private val appContext = context.applicationContext
    private val locationManager = appContext.getSystemService(Context.LOCATION_SERVICE) as LocationManager

    @SuppressLint("MissingPermission")
    override suspend fun getCurrentLocation(): Result<DeviceCoordinates> = try {
        val hasFinePermission = ContextCompat.checkSelfPermission(
            appContext,
            Manifest.permission.ACCESS_FINE_LOCATION
        ) == PackageManager.PERMISSION_GRANTED
        val hasCoarsePermission = ContextCompat.checkSelfPermission(
            appContext,
            Manifest.permission.ACCESS_COARSE_LOCATION
        ) == PackageManager.PERMISSION_GRANTED
        check(hasFinePermission || hasCoarsePermission) {
            "Izinkan akses lokasi untuk melihat produk terdekat."
        }

        val providers = buildList {
            if (hasFinePermission && locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
                add(LocationManager.GPS_PROVIDER)
            }
            if (locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER)) {
                add(LocationManager.NETWORK_PROVIDER)
            }
        }
        check(providers.isNotEmpty()) {
            "Aktifkan layanan lokasi perangkat, lalu coba lagi."
        }

        val location = providers.firstNotNullOfOrNull { provider ->
            requestCurrentLocation(provider)
        } ?: error("Lokasi perangkat belum tersedia. Coba lagi di area dengan sinyal GPS.")

        Result.success(
            DeviceCoordinates(
                latitude = location.latitude,
                longitude = location.longitude
            )
        )
    } catch (exception: CancellationException) {
        throw exception
    } catch (exception: Exception) {
        Result.failure(exception)
    }

    private suspend fun requestCurrentLocation(provider: String): Location? =
        withTimeoutOrNull(PROVIDER_TIMEOUT_MS) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
                requestCurrentLocationApi30(provider)
            } else {
                requestCurrentLocationLegacy(provider)
            }
        }

    @androidx.annotation.RequiresApi(Build.VERSION_CODES.R)
    @SuppressLint("MissingPermission")
    private suspend fun requestCurrentLocationApi30(provider: String): Location? =
        suspendCancellableCoroutine { continuation ->
            val cancellationSignal = CancellationSignal()
            continuation.invokeOnCancellation { cancellationSignal.cancel() }
            try {
                locationManager.getCurrentLocation(
                    provider,
                    cancellationSignal,
                    appContext.mainExecutor
                ) { location ->
                    if (continuation.isActive) continuation.resume(location)
                }
            } catch (exception: Exception) {
                if (continuation.isActive) continuation.resumeWithException(exception)
            }
        }

    @Suppress("DEPRECATION")
    @SuppressLint("MissingPermission")
    private suspend fun requestCurrentLocationLegacy(provider: String): Location? =
        suspendCancellableCoroutine { continuation ->
            val listener = object : LocationListener {
                override fun onLocationChanged(location: Location) {
                    if (continuation.isActive) continuation.resume(location)
                }

                override fun onProviderDisabled(provider: String) {
                    if (continuation.isActive) continuation.resume(null)
                }

                override fun onProviderEnabled(provider: String) = Unit

                @Suppress("OVERRIDE_DEPRECATION")
                override fun onStatusChanged(
                    provider: String?,
                    status: Int,
                    extras: android.os.Bundle?
                ) = Unit
            }
            continuation.invokeOnCancellation {
                runCatching { locationManager.removeUpdates(listener) }
            }
            try {
                locationManager.requestLocationUpdates(
                    provider,
                    0L,
                    0f,
                    listener,
                    Looper.getMainLooper()
                )
            } catch (exception: Exception) {
                if (continuation.isActive) continuation.resumeWithException(exception)
            }
        }

    private companion object {
        const val PROVIDER_TIMEOUT_MS = 15_000L
    }
}

package com.example.tanico.domain.location

import com.example.tanico.domain.model.DeviceCoordinates

interface DeviceLocationProvider {
    suspend fun getCurrentLocation(): Result<DeviceCoordinates>
}

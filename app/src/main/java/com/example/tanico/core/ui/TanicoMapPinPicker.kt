package com.example.tanico.core.ui

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import org.maplibre.compose.camera.CameraPosition
import org.maplibre.compose.expressions.dsl.const
import org.maplibre.compose.interaction.ClickResult
import org.maplibre.compose.interaction.MapInteractions
import org.maplibre.compose.layers.CircleLayer
import org.maplibre.compose.map.MaplibreMap
import org.maplibre.compose.map.rememberMapState
import org.maplibre.compose.sources.GeoJsonData
import org.maplibre.compose.sources.rememberGeoJsonSource
import org.maplibre.compose.style.BaseStyle
import org.maplibre.spatialk.geojson.Position
import java.util.Locale

@Composable
fun TanicoMapPinPicker(
    latitude: String,
    longitude: String,
    onLocationSelected: (latitude: String, longitude: String) -> Unit,
    modifier: Modifier = Modifier
) {
    val selectedLatitude = latitude.toDoubleOrNull()?.takeIf { it.isFinite() && it in -90.0..90.0 }
    val selectedLongitude = longitude.toDoubleOrNull()?.takeIf { it.isFinite() && it in -180.0..180.0 }
    val hasSelectedLocation = selectedLatitude != null && selectedLongitude != null
    val selectedMarkerData = remember(selectedLatitude, selectedLongitude) {
        GeoJsonData.JsonString(
            if (hasSelectedLocation) {
                """{"type":"FeatureCollection","features":[{"type":"Feature","geometry":{"type":"Point","coordinates":[$selectedLongitude,$selectedLatitude]},"properties":{}}]}"""
            } else {
                """{"type":"FeatureCollection","features":[]}"""
            }
        )
    }
    val mapState = rememberMapState(
        baseStyle = BaseStyle.Uri("https://tiles.openfreemap.org/styles/liberty"),
        initialCameraPosition = CameraPosition(
            center = Position(
                longitude = selectedLongitude ?: INDONESIA_CENTER_LONGITUDE,
                latitude = selectedLatitude ?: INDONESIA_CENTER_LATITUDE
            ),
            zoom = if (hasSelectedLocation) SELECTED_LOCATION_ZOOM else INDONESIA_OVERVIEW_ZOOM
        )
    ) {
        val selectedLocation = rememberGeoJsonSource(selectedMarkerData)
        CircleLayer(
            id = "tanico-selected-store-location",
            source = selectedLocation,
            radius = const(10.dp),
            color = const(MaterialTheme.colorScheme.primary),
            strokeColor = const(Color.White),
            strokeWidth = const(3.dp)
        )
    }
    val currentOnLocationSelected = rememberUpdatedState(onLocationSelected)
    val interactions = remember {
        MapInteractions {
            callbacks {
                click {
                    onEvent { event ->
                        event.position?.let { position ->
                            currentOnLocationSelected.value(
                                formatCoordinate(position.latitude),
                                formatCoordinate(position.longitude)
                            )
                        }
                        ClickResult.Consume
                    }
                }
            }
        }
    }

    Column(
        modifier = modifier,
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        Text(
            text = "Ketuk peta untuk menempatkan pin toko.",
            style = MaterialTheme.typography.bodyMedium
        )
        MaplibreMap(
            state = mapState,
            modifier = Modifier
                .fillMaxWidth()
                .height(260.dp)
                .clip(MaterialTheme.shapes.medium),
            interactions = interactions
        )
        Text(
            text = "© OpenStreetMap contributors · Peta oleh OpenFreeMap",
            style = MaterialTheme.typography.labelSmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
        Surface(
            shape = CircleShape,
            color = MaterialTheme.colorScheme.surfaceVariant
        ) {
            Text(
                text = if (hasSelectedLocation) {
                    "Lokasi dipilih: $latitude, $longitude"
                } else {
                    "Belum ada lokasi yang dipilih"
                },
                modifier = Modifier.padding(horizontal = 12.dp, vertical = 8.dp),
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}

private fun formatCoordinate(value: Double): String = String.format(Locale.US, "%.6f", value)

private const val INDONESIA_CENTER_LATITUDE = -2.5
private const val INDONESIA_CENTER_LONGITUDE = 118.0
private const val INDONESIA_OVERVIEW_ZOOM = 4.5
private const val SELECTED_LOCATION_ZOOM = 14.0

package com.example.tanico.feature.main

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.tanico.core.ui.TanicoEmptyState
import com.example.tanico.domain.model.UserRole
import com.example.tanico.feature.profile.ProfileScreen

enum class MainSection(val label: String, val marker: String) {
    Beranda("Beranda", "⌂"),
    Notifikasi("Notifikasi", "●"),
    Pembelajaran("Pembelajaran", "▤"),
    Profil("Profil", "○")
}

@Composable
fun MainAppScreen(
    role: UserRole,
    fullName: String,
    phoneNumber: String,
    email: String,
    location: String,
    storeName: String,
    storeAddress: String,
    editingProfile: Boolean,
    message: String?,
    onFullNameChanged: (String) -> Unit,
    onPhoneNumberChanged: (String) -> Unit,
    onEmailChanged: (String) -> Unit,
    onLocationChanged: (String) -> Unit,
    onStoreNameChanged: (String) -> Unit,
    onStoreAddressChanged: (String) -> Unit,
    onEditProfile: () -> Unit,
    onSaveProfile: () -> Unit,
    onExitPreview: () -> Unit,
    modifier: Modifier = Modifier
) {
    var selectedSection by rememberSaveable(role) {
        mutableStateOf(MainSection.Beranda.name)
    }
    val section = MainSection.valueOf(selectedSection)

    Scaffold(
        modifier = modifier,
        containerColor = MaterialTheme.colorScheme.background,
        bottomBar = {
            NavigationBar(containerColor = MaterialTheme.colorScheme.surface) {
                MainSection.entries.forEach { item ->
                    NavigationBarItem(
                        selected = section == item,
                        onClick = { selectedSection = item.name },
                        colors = NavigationBarItemDefaults.colors(
                            selectedIconColor = MaterialTheme.colorScheme.primary,
                            selectedTextColor = MaterialTheme.colorScheme.primary,
                            indicatorColor = MaterialTheme.colorScheme.primaryContainer,
                            unselectedIconColor = MaterialTheme.colorScheme.onSurfaceVariant,
                            unselectedTextColor = MaterialTheme.colorScheme.onSurfaceVariant
                        ),
                        icon = { Text(item.marker) },
                        label = { Text(item.label) }
                    )
                }
            }
        }
    ) { innerPadding ->
        when (section) {
            MainSection.Profil -> ProfileScreen(
                role = role,
                fullName = fullName,
                phoneNumber = phoneNumber,
                email = email,
                location = location,
                storeName = storeName,
                storeAddress = storeAddress,
                isEditing = editingProfile,
                message = message,
                onFullNameChanged = onFullNameChanged,
                onPhoneNumberChanged = onPhoneNumberChanged,
                onEmailChanged = onEmailChanged,
                onLocationChanged = onLocationChanged,
                onStoreNameChanged = onStoreNameChanged,
                onStoreAddressChanged = onStoreAddressChanged,
                onEdit = onEditProfile,
                onSave = onSaveProfile,
                onExitPreview = onExitPreview,
                modifier = Modifier
                    .fillMaxSize()
                    .padding(innerPadding)
            )

            MainSection.Beranda -> PreviewHome(
                role = role,
                modifier = Modifier
                    .fillMaxSize()
                    .padding(innerPadding)
            )

            MainSection.Notifikasi -> TanicoEmptyState(
                title = "Belum ada notifikasi",
                message = "Notifikasi akun akan tampil di sini setelah layanan TANICO terhubung.",
                modifier = Modifier
                    .fillMaxSize()
                    .padding(innerPadding)
            )

            MainSection.Pembelajaran -> TanicoEmptyState(
                title = "Pembelajaran TANICO",
                message = "Artikel dan materi edukasi akan tersedia pada tahap pengembangan berikutnya.",
                modifier = Modifier
                    .fillMaxSize()
                    .padding(innerPadding)
            )
        }
    }
}

@Composable
private fun PreviewHome(role: UserRole, modifier: Modifier = Modifier) {
    val roleTitle = if (role == UserRole.Buyer) "Pembeli / Bio-Industri" else "Penjual / Petani & Pengepul"
    Column(
        modifier = modifier.padding(24.dp),
        horizontalAlignment = Alignment.Start,
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        Text(
            text = "TANICO  •  Beranda",
            style = MaterialTheme.typography.titleLarge,
            color = MaterialTheme.colorScheme.primary
        )
        Text(
            text = "Halo, $roleTitle",
            style = MaterialTheme.typography.headlineMedium
        )
        Card(
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
            shape = MaterialTheme.shapes.large
        ) {
            Column(
                modifier = Modifier.padding(18.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Text(
                    text = "Mode pratinjau",
                    style = MaterialTheme.typography.titleLarge
                )
                Text(
                    text = "Anda sedang melihat kerangka aplikasi. Data dan transaksi belum tersedia, dan tidak ada akun yang dibuat.",
                    style = MaterialTheme.typography.bodyLarge,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }
        Text(
            text = "Gunakan tab Profil untuk melihat informasi profil ${if (role == UserRole.Buyer) "pembeli" else "penjual"}.",
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
    }
}

package com.example.tanico.feature.profile

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.tanico.core.ui.TanicoTextField
import com.example.tanico.domain.model.UserRole

@Composable
fun ProfileScreen(
    role: UserRole,
    isPreviewMode: Boolean,
    fullName: String,
    phoneNumber: String,
    email: String,
    location: String,
    storeName: String,
    storeAddress: String,
    isEditing: Boolean,
    isLoading: Boolean,
    message: String?,
    onFullNameChanged: (String) -> Unit,
    onPhoneNumberChanged: (String) -> Unit,
    onEmailChanged: (String) -> Unit,
    onLocationChanged: (String) -> Unit,
    onStoreNameChanged: (String) -> Unit,
    onStoreAddressChanged: (String) -> Unit,
    onEdit: () -> Unit,
    onSave: () -> Unit,
    onSignOut: () -> Unit,
    onExitPreview: () -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .verticalScroll(rememberScrollState())
            .padding(20.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        Text(
            text = "Profil Saya",
            style = MaterialTheme.typography.headlineMedium
        )
        Card(
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
            shape = MaterialTheme.shapes.large
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(18.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(14.dp)
            ) {
                Surface(
                    shape = CircleShape,
                    color = MaterialTheme.colorScheme.primaryContainer
                ) {
                    Text(
                        text = fullName.firstOrNull()?.uppercase() ?: "T",
                        modifier = Modifier.padding(horizontal = 18.dp, vertical = 14.dp),
                        style = MaterialTheme.typography.headlineMedium,
                        color = MaterialTheme.colorScheme.onPrimaryContainer
                    )
                }
                Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                    Text(
                        text = fullName.ifBlank { "Profil TANICO" },
                        style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.SemiBold
                    )
                    Text(
                        text = role.displayName(),
                        color = MaterialTheme.colorScheme.primary,
                        style = MaterialTheme.typography.bodyMedium
                    )
                    if (isPreviewMode) {
                        Text(
                            text = "Mode pratinjau · belum terhubung ke akun",
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                            style = MaterialTheme.typography.bodySmall
                        )
                    }
                }
            }
        }

        Card(
            modifier = Modifier.fillMaxWidth(),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant),
            shape = MaterialTheme.shapes.medium
        ) {
            Column(
                modifier = Modifier.padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(6.dp)
            ) {
                Text("Informasi Akun", style = MaterialTheme.typography.titleLarge)
                if (isEditing) {
                    TanicoTextField(
                        value = fullName,
                        onValueChange = onFullNameChanged,
                        label = "Nama lengkap",
                        modifier = Modifier.fillMaxWidth(),
                        enabled = !isLoading
                    )
                    if (isPreviewMode) {
                        TanicoTextField(
                            value = phoneNumber,
                            onValueChange = onPhoneNumberChanged,
                            label = "Nomor telepon",
                            modifier = Modifier.fillMaxWidth()
                        )
                        TanicoTextField(
                            value = email,
                            onValueChange = onEmailChanged,
                            label = "Alamat email",
                            modifier = Modifier.fillMaxWidth()
                        )
                        TanicoTextField(
                            value = location,
                            onValueChange = onLocationChanged,
                            label = "Domisili",
                            modifier = Modifier.fillMaxWidth()
                        )
                    } else {
                        ProfileDetail(label = "Nomor telepon", value = phoneNumber)
                        ProfileDetail(label = "Alamat email", value = email)
                        ProfileDetail(label = "Domisili", value = location)
                    }
                } else {
                    ProfileDetail(label = "Nama lengkap", value = fullName)
                    ProfileDetail(label = "Nomor telepon", value = phoneNumber)
                    ProfileDetail(label = "Alamat email", value = email)
                    ProfileDetail(label = "Domisili", value = location)
                }
            }
        }

        Card(
            modifier = Modifier.fillMaxWidth(),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
            shape = MaterialTheme.shapes.medium
        ) {
            Column(
                modifier = Modifier.padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(6.dp)
            ) {
                Text(
                    text = if (role == UserRole.Buyer) "Alamat Pengiriman" else "Informasi Toko",
                    style = MaterialTheme.typography.titleLarge
                )
                if (role == UserRole.Buyer) {
                    Text(
                        text = "Alamat pengiriman dapat dikelola setelah fitur alamat TANICO tersedia.",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                } else if (isPreviewMode && isEditing) {
                    TanicoTextField(
                        value = storeName,
                        onValueChange = onStoreNameChanged,
                        label = "Nama toko",
                        modifier = Modifier.fillMaxWidth()
                    )
                    TanicoTextField(
                        value = storeAddress,
                        onValueChange = onStoreAddressChanged,
                        label = "Alamat operasional toko",
                        modifier = Modifier.fillMaxWidth()
                    )
                } else {
                    ProfileDetail(label = "Nama toko", value = storeName)
                    ProfileDetail(label = "Alamat operasional", value = storeAddress)
                    if (!isPreviewMode) {
                        Text(
                            "Pengaturan toko akan tersedia pada tahap onboarding penjual.",
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                            style = MaterialTheme.typography.bodySmall
                        )
                    }
                }
            }
        }

        message?.let {
            Text(
                text = it,
                color = MaterialTheme.colorScheme.primary,
                style = MaterialTheme.typography.bodyMedium
            )
        }

        if (isEditing) {
            Button(
                onClick = onSave,
                modifier = Modifier.fillMaxWidth(),
                enabled = !isLoading
            ) {
                Text(if (isLoading) "Menyimpan..." else if (isPreviewMode) "Simpan pratinjau" else "Simpan perubahan")
            }
        } else {
            OutlinedButton(
                onClick = onEdit,
                modifier = Modifier.fillMaxWidth(),
                enabled = !isLoading
            ) {
                Text("Ubah Profil")
            }
        }
        OutlinedButton(
            onClick = if (isPreviewMode) onExitPreview else onSignOut,
            modifier = Modifier.fillMaxWidth(),
            enabled = !isLoading
        ) {
            Text(if (isPreviewMode) "Kembali ke Login" else "Keluar dari Akun")
        }
    }
}

@Composable
private fun ProfileDetail(label: String, value: String) {
    Column(modifier = Modifier.padding(vertical = 5.dp)) {
        Text(
            text = label,
            style = MaterialTheme.typography.labelMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
        Text(
            text = value.ifBlank { "Belum diisi" },
            style = MaterialTheme.typography.bodyLarge
        )
    }
}

private fun UserRole.displayName(): String = when (this) {
    UserRole.Buyer -> "Pembeli / Bio-Industri"
    UserRole.Seller -> "Penjual / Petani & Pengepul"
}

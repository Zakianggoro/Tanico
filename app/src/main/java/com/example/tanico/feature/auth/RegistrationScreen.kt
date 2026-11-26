package com.example.tanico.feature.auth

import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.selection.selectable
import androidx.compose.foundation.selection.selectableGroup
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Checkbox
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.RadioButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.tanico.core.ui.TanicoMapPinPicker
import com.example.tanico.domain.model.UserRole

@Composable
fun RegistrationScreen(
    state: AuthUiState,
    onRoleSelected: (UserRole) -> Unit,
    onFullNameChanged: (String) -> Unit,
    onPhoneNumberChanged: (String) -> Unit,
    onEmailChanged: (String) -> Unit,
    onLocationChanged: (String) -> Unit,
    onStoreNameChanged: (String) -> Unit,
    onStoreAddressChanged: (String) -> Unit,
    onStoreLatitudeChanged: (String) -> Unit,
    onStoreLongitudeChanged: (String) -> Unit,
    onPasswordChanged: (String) -> Unit,
    onConfirmPasswordChanged: (String) -> Unit,
    onTermsAcceptedChanged: (Boolean) -> Unit,
    onSubmit: () -> Unit,
    onShowLogin: () -> Unit,
    onPreview: (UserRole) -> Unit,
    modifier: Modifier = Modifier
) {
    Scaffold(
        modifier = modifier,
        containerColor = MaterialTheme.colorScheme.background
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .verticalScroll(rememberScrollState())
                .padding(horizontal = 24.dp, vertical = 12.dp),
            verticalArrangement = Arrangement.spacedBy(18.dp)
        ) {
            BrandHeader(onBack = onShowLogin)
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                Surface(
                    shape = CircleShape,
                    color = MaterialTheme.colorScheme.primaryContainer
                ) {
                    Row(
                        modifier = Modifier.padding(horizontal = 12.dp, vertical = 7.dp),
                        horizontalArrangement = Arrangement.spacedBy(6.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text("◈", color = MaterialTheme.colorScheme.primary)
                        Text(
                            "Ekosistem Biomassa Sirkular",
                            style = MaterialTheme.typography.labelMedium,
                            color = MaterialTheme.colorScheme.onPrimaryContainer
                        )
                    }
                }
                Text(
                    "Daftar Akun Baru",
                    style = MaterialTheme.typography.headlineMedium,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onBackground
                )
                Text(
                    "Bergabung bersama petani dan pembeli dalam ekosistem limbah pertanian bernilai.",
                    style = MaterialTheme.typography.bodyLarge,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }

            Column(
                modifier = Modifier.selectableGroup(),
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        "Pilih Peran Utama Anda",
                        modifier = Modifier.weight(1f),
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.SemiBold
                    )
                    Text(
                        "Dapat diubah nanti",
                        style = MaterialTheme.typography.labelSmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
                RegistrationRoleCard(
                    title = "Penjual",
                    tag = "Petani & Pengepul",
                    description = "Jual sekam, jerami, tongkol, dan hasil samping panen.",
                    symbol = "🌱",
                    selected = state.selectedRole == UserRole.Seller,
                    onSelect = { onRoleSelected(UserRole.Seller) }
                )
                RegistrationRoleCard(
                    title = "Pembeli",
                    tag = "Pengolah & Pabrik",
                    description = "Belanja pasokan biomassa terverifikasi untuk kebutuhan usaha.",
                    symbol = "▦",
                    selected = state.selectedRole == UserRole.Buyer,
                    onSelect = { onRoleSelected(UserRole.Buyer) }
                )
            }

            Column(verticalArrangement = Arrangement.spacedBy(14.dp)) {
                RegistrationField(
                    value = state.fullName,
                    onValueChange = onFullNameChanged,
                    label = "Nama Lengkap *",
                    placeholder = "Contoh: Pak Joko Sutrisno / CV Berkah",
                    leadingSymbol = "♙"
                )
                RegistrationField(
                    value = state.phoneNumber,
                    onValueChange = onPhoneNumberChanged,
                    label = "Nomor Telepon Aktif *",
                    placeholder = "+62 812-3456-7890",
                    leadingSymbol = "☎",
                    keyboardType = KeyboardType.Phone,
                    supportingText = "Nomor ini digunakan sebagai informasi kontak akun."
                )
                RegistrationField(
                    value = state.email,
                    onValueChange = onEmailChanged,
                    label = "Alamat Email *",
                    placeholder = "nama@email.com",
                    leadingSymbol = "✉",
                    keyboardType = KeyboardType.Email,
                    supportingText = "Email diperlukan untuk masuk ke akun TANICO."
                )
                RegistrationField(
                    value = state.location,
                    onValueChange = onLocationChanged,
                    label = "Domisili Kabupaten / Kota *",
                    placeholder = "Contoh: Kabupaten Malang",
                    leadingSymbol = "⌖"
                )
            }

            if (state.selectedRole == UserRole.Seller) {
                Card(
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                    shape = MaterialTheme.shapes.large
                ) {
                    Column(
                        modifier = Modifier.padding(18.dp),
                        verticalArrangement = Arrangement.spacedBy(14.dp)
                    ) {
                        Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                            Text(
                                "Informasi Toko",
                                style = MaterialTheme.typography.titleMedium,
                                fontWeight = FontWeight.SemiBold
                            )
                            Text(
                                "Diperlukan untuk membuat profil toko penjual.",
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                        RegistrationField(
                            value = state.storeName,
                            onValueChange = onStoreNameChanged,
                            label = "Nama Toko *",
                            placeholder = "Contoh: Toko Tani Berkah",
                            leadingSymbol = "⌂"
                        )
                        RegistrationField(
                            value = state.storeAddress,
                            onValueChange = onStoreAddressChanged,
                            label = "Alamat Lengkap Toko *",
                            placeholder = "Jalan, desa/kelurahan, kecamatan, kota",
                            leadingSymbol = "⌖"
                        )
                        TanicoMapPinPicker(
                            latitude = state.storeLatitude,
                            longitude = state.storeLongitude,
                            onLocationSelected = { latitude, longitude ->
                                onStoreLatitudeChanged(latitude)
                                onStoreLongitudeChanged(longitude)
                            },
                            modifier = Modifier.fillMaxWidth()
                        )
                    }
                }
            }

            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                var passwordVisible by rememberSaveable { mutableStateOf(false) }
                RegistrationField(
                    value = state.password,
                    onValueChange = onPasswordChanged,
                    label = "Kata Sandi Baru *",
                    placeholder = "Minimal 8 karakter",
                    leadingSymbol = "▣",
                    keyboardType = KeyboardType.Password,
                    visualTransformation = if (passwordVisible) {
                        VisualTransformation.None
                    } else {
                        PasswordVisualTransformation()
                    },
                    trailingAction = {
                        TextButton(onClick = { passwordVisible = !passwordVisible }) {
                            Text(
                                if (passwordVisible) "Sembunyikan" else "Lihat",
                                style = MaterialTheme.typography.labelSmall
                            )
                        }
                    }
                )
                PasswordLengthIndicator(password = state.password)
                var confirmationVisible by rememberSaveable { mutableStateOf(false) }
                RegistrationField(
                    value = state.confirmPassword,
                    onValueChange = onConfirmPasswordChanged,
                    label = "Konfirmasi Kata Sandi *",
                    placeholder = "Ketik ulang kata sandi Anda",
                    leadingSymbol = "▣",
                    keyboardType = KeyboardType.Password,
                    visualTransformation = if (confirmationVisible) {
                        VisualTransformation.None
                    } else {
                        PasswordVisualTransformation()
                    },
                    trailingAction = {
                        TextButton(onClick = { confirmationVisible = !confirmationVisible }) {
                            Text(
                                if (confirmationVisible) "Sembunyikan" else "Lihat",
                                style = MaterialTheme.typography.labelSmall
                            )
                        }
                    }
                )
            }

            Surface(
                color = MaterialTheme.colorScheme.surfaceVariant,
                shape = MaterialTheme.shapes.medium
            ) {
                Row(
                    modifier = Modifier.padding(horizontal = 10.dp, vertical = 8.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Checkbox(
                        checked = state.termsAccepted,
                        onCheckedChange = onTermsAcceptedChanged,
                        enabled = !state.isLoading
                    )
                    Text(
                        "Saya menyetujui Syarat & Ketentuan dan Kebijakan Privasi TANICO untuk transaksi yang transparan.",
                        style = MaterialTheme.typography.bodySmall
                    )
                }
            }

            state.message?.let { message ->
                AuthMessage(message = message, isError = state.messageIsError)
            }
            Button(
                onClick = onSubmit,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(56.dp),
                enabled = !state.isLoading,
                shape = CircleShape
            ) {
                Text(
                    if (state.isLoading) "Memproses..." else "Daftar Akun  →",
                    style = MaterialTheme.typography.titleSmall,
                    fontWeight = FontWeight.SemiBold
                )
            }

            if (state.isPreviewAvailable) {
                Card(
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant),
                    shape = MaterialTheme.shapes.medium
                ) {
                    Column(
                        modifier = Modifier.padding(16.dp),
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Text(
                            "Coba tampilan sesuai peran",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.SemiBold
                        )
                        Text(
                            "Pratinjau tidak membuat akun dan tidak menyimpan data ke server.",
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                        OutlinedButton(
                            onClick = { onPreview(state.selectedRole) },
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Text("Lihat pratinjau sebagai ${state.selectedRole.label()}")
                        }
                    }
                }
            }

            Surface(
                color = MaterialTheme.colorScheme.surfaceVariant,
                shape = MaterialTheme.shapes.medium
            ) {
                Row(
                    modifier = Modifier.padding(16.dp),
                    horizontalArrangement = Arrangement.spacedBy(12.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Surface(
                        modifier = Modifier.size(38.dp),
                        color = MaterialTheme.colorScheme.tertiaryContainer,
                        shape = CircleShape
                    ) {
                        Box(contentAlignment = Alignment.Center) {
                            Text(
                                "↻",
                                color = MaterialTheme.colorScheme.tertiary,
                                style = MaterialTheme.typography.titleLarge
                            )
                        }
                    }
                    Column(verticalArrangement = Arrangement.spacedBy(3.dp)) {
                        Text(
                            "Dukung pemanfaatan hasil panen",
                            style = MaterialTheme.typography.titleSmall,
                            fontWeight = FontWeight.SemiBold
                        )
                        Text(
                            "Bersama TANICO, hasil samping pertanian dapat dimanfaatkan menjadi bahan baku bernilai.",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }
            }

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 8.dp),
                horizontalArrangement = Arrangement.Center,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text("Sudah punya akun TANICO? ")
                TextButton(onClick = onShowLogin) {
                    Text("Masuk di sini", fontWeight = FontWeight.SemiBold)
                }
            }
        }
    }
}

@Composable
private fun RegistrationRoleCard(
    title: String,
    tag: String,
    description: String,
    symbol: String,
    selected: Boolean,
    onSelect: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .border(
                width = if (selected) 1.5.dp else 0.dp,
                color = if (selected) MaterialTheme.colorScheme.primary else Color.Transparent,
                shape = MaterialTheme.shapes.large
            )
            .selectable(
                selected = selected,
                onClick = onSelect,
                role = Role.RadioButton
            ),
        colors = CardDefaults.cardColors(
            containerColor = if (selected) {
                MaterialTheme.colorScheme.surface
            } else {
                MaterialTheme.colorScheme.surfaceVariant
            }
        ),
        shape = MaterialTheme.shapes.large
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(14.dp),
            horizontalArrangement = Arrangement.spacedBy(12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Surface(
                modifier = Modifier.size(48.dp),
                color = if (selected) {
                    MaterialTheme.colorScheme.primary
                } else {
                    MaterialTheme.colorScheme.surface
                },
                shape = RoundedCornerShape(14.dp)
            ) {
                Box(contentAlignment = Alignment.Center) {
                    Text(
                        symbol,
                        style = MaterialTheme.typography.titleLarge,
                        color = if (selected) {
                            MaterialTheme.colorScheme.onPrimary
                        } else {
                            MaterialTheme.colorScheme.secondary
                        }
                    )
                }
            }
            Column(
                modifier = Modifier.weight(1f),
                verticalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                Row(
                    horizontalArrangement = Arrangement.spacedBy(6.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        title,
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold
                    )
                    Surface(
                        color = MaterialTheme.colorScheme.surfaceVariant,
                        shape = RoundedCornerShape(5.dp)
                    ) {
                        Text(
                            tag,
                            modifier = Modifier.padding(horizontal = 6.dp, vertical = 3.dp),
                            style = MaterialTheme.typography.labelSmall
                        )
                    }
                }
                Text(
                    description,
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
            RadioButton(selected = selected, onClick = null)
        }
    }
}

@Composable
private fun RegistrationField(
    value: String,
    onValueChange: (String) -> Unit,
    label: String,
    placeholder: String,
    leadingSymbol: String,
    modifier: Modifier = Modifier,
    keyboardType: KeyboardType = KeyboardType.Text,
    supportingText: String? = null,
    visualTransformation: VisualTransformation = VisualTransformation.None,
    trailingAction: (@Composable () -> Unit)? = null
) {
    Column(
        modifier = modifier,
        verticalArrangement = Arrangement.spacedBy(6.dp)
    ) {
        Text(
            label,
            style = MaterialTheme.typography.labelLarge,
            color = MaterialTheme.colorScheme.onSurface
        )
        OutlinedTextField(
            value = value,
            onValueChange = onValueChange,
            modifier = Modifier.fillMaxWidth(),
            placeholder = { Text(placeholder, color = MaterialTheme.colorScheme.onSurfaceVariant) },
            leadingIcon = {
                Text(
                    leadingSymbol,
                    modifier = Modifier.padding(start = 2.dp),
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    fontSize = 19.sp
                )
            },
            trailingIcon = trailingAction,
            singleLine = true,
            shape = RoundedCornerShape(14.dp),
            visualTransformation = visualTransformation,
            keyboardOptions = androidx.compose.foundation.text.KeyboardOptions(keyboardType = keyboardType),
            colors = OutlinedTextFieldDefaults.colors(
                unfocusedContainerColor = MaterialTheme.colorScheme.surface,
                focusedContainerColor = MaterialTheme.colorScheme.surface,
                unfocusedBorderColor = MaterialTheme.colorScheme.outlineVariant,
                focusedBorderColor = MaterialTheme.colorScheme.primary
            )
        )
        supportingText?.let {
            Text(
                it,
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}

@Composable
private fun PasswordLengthIndicator(password: String) {
    val progress = (password.length / 8f).coerceIn(0f, 1f)
    val filledSegments = (progress * 3).toInt().coerceAtMost(3)
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(5.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        repeat(3) { index ->
            Surface(
                modifier = Modifier
                    .weight(1f)
                    .height(5.dp),
                color = if (index < filledSegments) {
                    MaterialTheme.colorScheme.primary
                } else {
                    MaterialTheme.colorScheme.outlineVariant
                },
                shape = CircleShape
            ) {}
        }
    }
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(
            "Minimal 8 karakter",
            style = MaterialTheme.typography.labelSmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
        Text(
            if (password.length >= 8) "Memenuhi minimum" else "${password.length}/8 karakter",
            style = MaterialTheme.typography.labelSmall,
            color = if (password.length >= 8) {
                MaterialTheme.colorScheme.primary
            } else {
                MaterialTheme.colorScheme.onSurfaceVariant
            }
        )
    }
}

private fun UserRole.label(): String = when (this) {
    UserRole.Buyer -> "Pembeli"
    UserRole.Seller -> "Penjual"
}

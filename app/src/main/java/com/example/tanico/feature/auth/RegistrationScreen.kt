package com.example.tanico.feature.auth

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Checkbox
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import com.example.tanico.domain.model.UserRole

@Composable
fun RegistrationScreen(
    state: AuthUiState,
    onRoleSelected: (UserRole) -> Unit,
    onFullNameChanged: (String) -> Unit,
    onPhoneNumberChanged: (String) -> Unit,
    onEmailChanged: (String) -> Unit,
    onLocationChanged: (String) -> Unit,
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
                .padding(horizontal = 24.dp, vertical = 16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            BrandHeader()
            Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                Text(
                    text = "Daftar Akun Baru",
                    style = MaterialTheme.typography.headlineMedium,
                    color = MaterialTheme.colorScheme.onBackground
                )
                Text(
                    text = "Bergabung bersama petani dan pembeli dalam ekosistem limbah pertanian bernilai.",
                    style = MaterialTheme.typography.bodyLarge,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }

            RoleSelector(
                selectedRole = state.selectedRole,
                onRoleSelected = onRoleSelected,
                heading = "Pilih peran utama Anda"
            )

            Card(
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                shape = MaterialTheme.shapes.large
            ) {
                Column(
                    modifier = Modifier.padding(20.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    AuthField(
                        value = state.fullName,
                        onValueChange = onFullNameChanged,
                        label = "Nama Lengkap *",
                        placeholder = "Nama Anda"
                    )
                    AuthField(
                        value = state.phoneNumber,
                        onValueChange = onPhoneNumberChanged,
                        label = "Nomor Telepon Aktif *",
                        placeholder = "+62 812-3456-7890"
                    )
                    AuthField(
                        value = state.email,
                        onValueChange = onEmailChanged,
                        label = "Alamat Email *",
                        placeholder = "nama@email.com"
                    )
                    AuthField(
                        value = state.location,
                        onValueChange = onLocationChanged,
                        label = "Domisili Kabupaten / Kota *",
                        placeholder = "Contoh: Kabupaten Malang"
                    )
                    OutlinedTextField(
                        value = state.password,
                        onValueChange = onPasswordChanged,
                        modifier = Modifier.fillMaxWidth(),
                        label = { Text("Kata Sandi Baru *") },
                        placeholder = { Text("Minimal 8 karakter") },
                        singleLine = true,
                        visualTransformation = PasswordVisualTransformation()
                    )
                    OutlinedTextField(
                        value = state.confirmPassword,
                        onValueChange = onConfirmPasswordChanged,
                        modifier = Modifier.fillMaxWidth(),
                        label = { Text("Konfirmasi Kata Sandi *") },
                        placeholder = { Text("Ketik ulang kata sandi Anda") },
                        singleLine = true,
                        visualTransformation = PasswordVisualTransformation()
                    )

                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Checkbox(
                            checked = state.termsAccepted,
                            onCheckedChange = onTermsAcceptedChanged,
                            enabled = !state.isLoading
                        )
                        Text(
                            text = "Saya menyetujui Syarat & Ketentuan dan Kebijakan Privasi TANICO.",
                            style = MaterialTheme.typography.bodySmall
                        )
                    }
                    state.message?.let { message ->
                        AuthMessage(message = message, isError = state.messageIsError)
                    }
                    Button(
                        onClick = onSubmit,
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(52.dp),
                        enabled = !state.isLoading
                    ) {
                        Text(if (state.isLoading) "Memproses..." else "Daftar Akun  →")
                    }
                }
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
                            text = "Coba tampilan sesuai peran",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.SemiBold
                        )
                        Text(
                            text = "Pratinjau tidak membuat akun dan tidak menyimpan data ke server.",
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

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.Center,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text("Sudah punya akun? ")
                androidx.compose.material3.TextButton(onClick = onShowLogin) {
                    Text("Masuk di sini")
                }
            }
            Spacer(modifier = Modifier.height(8.dp))
        }
    }
}

private fun UserRole.label(): String = when (this) {
    UserRole.Buyer -> "Pembeli"
    UserRole.Seller -> "Penjual"
}

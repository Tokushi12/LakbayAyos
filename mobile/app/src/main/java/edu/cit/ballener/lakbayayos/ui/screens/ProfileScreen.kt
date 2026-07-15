package edu.cit.ballener.lakbayayos.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import edu.cit.ballener.lakbayayos.ui.theme.*
import edu.cit.ballener.lakbayayos.viewmodel.AuthViewModel

@Composable
fun ProfileScreen(authViewModel: AuthViewModel, onBack: () -> Unit) {
    var fullName by remember { mutableStateOf(authViewModel.profile?.fullName ?: "") }
    var phoneNumber by remember { mutableStateOf(authViewModel.profile?.phoneNumber ?: "") }
    var profileMessage by remember { mutableStateOf<String?>(null) }
    var profileIsError by remember { mutableStateOf(false) }

    var newPassword by remember { mutableStateOf("") }
    var confirmPassword by remember { mutableStateOf("") }
    var passwordMessage by remember { mutableStateOf<String?>(null) }
    var passwordIsError by remember { mutableStateOf(false) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(BackgroundBlack)
            .verticalScroll(rememberScrollState())
            .padding(20.dp)
    ) {
        TextButton(onClick = onBack) { Text("< Back", color = TextSecondary) }

        Text("Edit Profile", color = TextPrimary, fontSize = 24.sp, fontWeight = FontWeight.Bold)
        Text(
            "ACCOUNT",
            color = RacingRed,
            fontSize = 11.sp,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.padding(top = 4.dp, bottom = 20.dp)
        )

        // Personal details section
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .border(1.dp, BorderDark)
                .padding(20.dp)
        ) {
            Text("Personal Details", color = TextPrimary, fontWeight = FontWeight.Bold, fontSize = 16.sp)
            Spacer(modifier = Modifier.height(16.dp))

            profileMessage?.let {
                Text(it, color = if (profileIsError) ErrorRed else SuccessGreen, fontSize = 13.sp)
                Spacer(modifier = Modifier.height(12.dp))
            }

            OutlinedTextField(
                value = fullName,
                onValueChange = { fullName = it },
                label = { Text("Full name") },
                singleLine = true,
                modifier = Modifier.fillMaxWidth()
            )
            Spacer(modifier = Modifier.height(12.dp))

            OutlinedTextField(
                value = phoneNumber,
                onValueChange = { phoneNumber = it },
                label = { Text("Phone number") },
                singleLine = true,
                modifier = Modifier.fillMaxWidth()
            )
            Spacer(modifier = Modifier.height(16.dp))

            Button(
                onClick = {
                    authViewModel.updateProfile(fullName, phoneNumber) { success, error ->
                        profileIsError = !success
                        profileMessage = if (success) "Profile updated successfully." else error
                    }
                },
                enabled = !authViewModel.loading,
                colors = ButtonDefaults.buttonColors(containerColor = RacingRed),
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(if (authViewModel.loading) "Saving..." else "Save Changes")
            }
        }

        Spacer(modifier = Modifier.height(20.dp))

        // Reset password section
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .border(1.dp, BorderDark)
                .padding(20.dp)
        ) {
            Text("Reset Password", color = TextPrimary, fontWeight = FontWeight.Bold, fontSize = 16.sp)
            Spacer(modifier = Modifier.height(16.dp))

            passwordMessage?.let {
                Text(it, color = if (passwordIsError) ErrorRed else SuccessGreen, fontSize = 13.sp)
                Spacer(modifier = Modifier.height(12.dp))
            }

            OutlinedTextField(
                value = newPassword,
                onValueChange = { newPassword = it },
                label = { Text("New password") },
                singleLine = true,
                visualTransformation = PasswordVisualTransformation(),
                modifier = Modifier.fillMaxWidth()
            )
            Spacer(modifier = Modifier.height(12.dp))

            OutlinedTextField(
                value = confirmPassword,
                onValueChange = { confirmPassword = it },
                label = { Text("Confirm new password") },
                singleLine = true,
                visualTransformation = PasswordVisualTransformation(),
                modifier = Modifier.fillMaxWidth()
            )
            Spacer(modifier = Modifier.height(16.dp))

            Button(
                onClick = {
                    when {
                        newPassword.length < 6 -> {
                            passwordIsError = true
                            passwordMessage = "Password must be at least 6 characters."
                        }
                        newPassword != confirmPassword -> {
                            passwordIsError = true
                            passwordMessage = "Passwords do not match."
                        }
                        else -> {
                            authViewModel.updatePassword(newPassword) { success, error ->
                                passwordIsError = !success
                                passwordMessage = if (success) "Password updated successfully." else error
                                if (success) {
                                    newPassword = ""
                                    confirmPassword = ""
                                }
                            }
                        }
                    }
                },
                enabled = !authViewModel.loading,
                colors = ButtonDefaults.buttonColors(containerColor = RacingRed),
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(if (authViewModel.loading) "Updating..." else "Update Password")
            }
        }

        Spacer(modifier = Modifier.height(20.dp))
    }
}

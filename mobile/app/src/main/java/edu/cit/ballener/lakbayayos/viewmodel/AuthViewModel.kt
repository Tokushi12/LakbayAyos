package edu.cit.ballener.lakbayayos.viewmodel

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import edu.cit.ballener.lakbayayos.data.api.ApiClient
import edu.cit.ballener.lakbayayos.data.api.LoginRequest
import edu.cit.ballener.lakbayayos.data.api.SignUpRequest
import edu.cit.ballener.lakbayayos.data.api.SupabaseAuthClient
import edu.cit.ballener.lakbayayos.data.api.UpdatePasswordRequest
import edu.cit.ballener.lakbayayos.data.model.RegisterRequest
import edu.cit.ballener.lakbayayos.data.model.UpdateUserRequest
import edu.cit.ballener.lakbayayos.data.model.User
import kotlinx.coroutines.launch

class AuthViewModel : ViewModel() {

    var accessToken by mutableStateOf<String?>(null)
        private set

    var userId by mutableStateOf<String?>(null)
        private set

    var profile by mutableStateOf<User?>(null)
        private set

    val role: String?
        get() = profile?.role

    var loading by mutableStateOf(false)
        private set

    var errorMessage by mutableStateOf<String?>(null)
        private set

    val isLoggedIn: Boolean
        get() = accessToken != null

    fun clearError() {
        errorMessage = null
    }

    fun login(email: String, password: String, onResult: (User?) -> Unit) {
        loading = true
        errorMessage = null

        viewModelScope.launch {
            try {
                val authResponse = SupabaseAuthClient.authApi.login(
                    request = LoginRequest(email = email, password = password)
                )
                accessToken = authResponse.access_token
                userId = authResponse.user.id

                val userProfile = ApiClient.userApi.getUserById(authResponse.user.id)
                profile = userProfile

                onResult(userProfile)
            } catch (e: Exception) {
                errorMessage = e.message ?: "Incorrect email or password. Please try again."
                onResult(null)
            } finally {
                loading = false
            }
        }
    }

    fun register(
        email: String,
        password: String,
        fullName: String,
        phoneNumber: String,
        onResult: (Boolean) -> Unit
    ) {
        loading = true
        errorMessage = null

        viewModelScope.launch {
            try {
                val authResponse = SupabaseAuthClient.authApi.signUp(
                    request = SignUpRequest(email = email, password = password)
                )

                ApiClient.userApi.registerUser(
                    RegisterRequest(
                        id = authResponse.user.id,
                        email = email,
                        fullName = fullName,
                        phoneNumber = phoneNumber,
                        role = "customer"
                    )
                )

                onResult(true)
            } catch (e: Exception) {
                errorMessage = e.message ?: "Something went wrong. Please try again."
                onResult(false)
            } finally {
                loading = false
            }
        }
    }

    fun updateProfile(fullName: String, phoneNumber: String, onResult: (Boolean, String?) -> Unit) {
        val id = userId ?: return
        loading = true

        viewModelScope.launch {
            try {
                val updated = ApiClient.userApi.updateUser(
                    id,
                    UpdateUserRequest(fullName = fullName, phoneNumber = phoneNumber)
                )
                profile = updated
                onResult(true, null)
            } catch (e: Exception) {
                onResult(false, e.message ?: "Failed to update profile.")
            } finally {
                loading = false
            }
        }
    }

    fun updatePassword(newPassword: String, onResult: (Boolean, String?) -> Unit) {
        val token = accessToken ?: return
        loading = true

        viewModelScope.launch {
            try {
                SupabaseAuthClient.authApi.updatePassword(
                    authorization = "Bearer $token",
                    request = UpdatePasswordRequest(password = newPassword)
                )
                onResult(true, null)
            } catch (e: Exception) {
                onResult(false, e.message ?: "Failed to update password.")
            } finally {
                loading = false
            }
        }
    }

    fun logout() {
        accessToken = null
        userId = null
        profile = null
    }
}
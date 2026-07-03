package com.example.final_project.ui

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.example.final_project.data.SessionManager
import com.example.final_project.data.TaskDatabase
import com.example.final_project.data.UserRepository
import kotlinx.coroutines.launch

class AuthViewModel(application: Application) : AndroidViewModel(application) {

    private val userRepository = UserRepository(
        TaskDatabase.getInstance(application).userDao()
    )
    private val sessionManager = SessionManager(application)

    private val _authEvent = MutableLiveData<AuthEvent>()
    val authEvent: LiveData<AuthEvent> = _authEvent

    val currentUsername: String
        get() = sessionManager.getUsername()

    fun isLoggedIn(): Boolean = sessionManager.isLoggedIn()

    fun login(username: String, password: String) {
        if (username.isBlank() || password.isBlank()) {
            _authEvent.value = AuthEvent.Error(AuthError.EMPTY_FIELDS)
            return
        }

        viewModelScope.launch {
            val user = userRepository.login(username, password)
            if (user != null) {
                sessionManager.saveSession(user.id, user.username)
                _authEvent.value = AuthEvent.LoggedIn
            } else {
                _authEvent.value = AuthEvent.Error(AuthError.INVALID_CREDENTIALS)
            }
        }
    }

    fun register(username: String, password: String) {
        viewModelScope.launch {
            userRepository.register(username, password)
                .onSuccess {
                    _authEvent.value = AuthEvent.Registered
                }
                .onFailure { error ->
                    val authError = when (error.message) {
                        "empty_fields" -> AuthError.EMPTY_FIELDS
                        "user_exists" -> AuthError.USER_EXISTS
                        else -> AuthError.UNKNOWN
                    }
                    _authEvent.value = AuthEvent.Error(authError)
                }
        }
    }

    fun logout() {
        sessionManager.clearSession()
        _authEvent.value = AuthEvent.LoggedOut
    }

    fun clearEvent() {
        _authEvent.value = AuthEvent.Idle
    }
}

sealed class AuthEvent {
    data object Idle : AuthEvent()
    data object LoggedIn : AuthEvent()
    data object Registered : AuthEvent()
    data object LoggedOut : AuthEvent()
    data class Error(val error: AuthError) : AuthEvent()
}

enum class AuthError {
    EMPTY_FIELDS,
    INVALID_CREDENTIALS,
    USER_EXISTS,
    UNKNOWN
}

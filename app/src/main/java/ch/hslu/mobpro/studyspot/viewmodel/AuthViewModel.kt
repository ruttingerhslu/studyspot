package ch.hslu.mobpro.studyspot.viewmodel

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import ch.hslu.mobpro.studyspot.data.local.UserDao
import ch.hslu.mobpro.studyspot.data.model.User
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class AuthViewModel @Inject constructor(
    private val userDao: UserDao
) : ViewModel()
{
    private val _currentUser = MutableStateFlow<User?>(null)
    val currentUser: StateFlow<User?> = _currentUser

    fun setCurrentUser(user: User) {
        _currentUser.value = user
    }

    fun register(name: String, email: String, password: String, onSuccess: () -> Unit, onError: (String) -> Unit) {
        viewModelScope.launch {
            try {
                val newUser = User(name = name, email = email, password = password)
                userDao.registerUser(newUser)
                setCurrentUser(newUser)
                onSuccess()
            } catch (e: Exception) {
                onError("User already exists or registration failed")
            }
        }
    }

    fun login(email: String, password: String, onSuccess: (User) -> Unit, onError: () -> Unit) {
        viewModelScope.launch {
            Log.d("Login", "Attempting login for $email")
            val user = userDao.login(email, password)
            if (user != null) {
                Log.d("Login", "Login successful for ${user.email}")
                onSuccess(user)
            } else {
                Log.e("Login", "Login failed: user not found")
                onError()
            }
        }
    }

    fun logout() {
        viewModelScope.launch {
            _currentUser.value = null
        }
    }

    fun updateUserProfile(name: String, email: String, location: String) {
        viewModelScope.launch {
            val user = currentUser.value
            if (user != null) {
                val updatedUser = user.copy(name = name, email = email, location = location)
                userDao.updateUser(updatedUser)
                _currentUser.value = updatedUser
            }
        }
    }
}
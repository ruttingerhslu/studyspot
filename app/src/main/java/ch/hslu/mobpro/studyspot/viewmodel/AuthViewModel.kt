package ch.hslu.mobpro.studyspot.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import ch.hslu.mobpro.studyspot.data.local.UserDao
import ch.hslu.mobpro.studyspot.data.model.User
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class AuthViewModel @Inject constructor(
    private val userDao: UserDao
) : ViewModel()
{
    fun register(name: String, email: String, password: String, onSuccess: () -> Unit, onError: (String) -> Unit) {
        viewModelScope.launch {
            try {
                userDao.registerUser(User(email, name, password))
                onSuccess()
            } catch (e: Exception) {
                onError("User already exists or registration failed")
            }
        }
    }

    fun login(email: String, password: String, onSuccess: (User) -> Unit, onError: (Any?) -> Unit) {
        viewModelScope.launch {
            val user = userDao.login(email, password)
            if (user != null) onSuccess(user) else onError("Couldn't authenticate user")
        }
    }
}
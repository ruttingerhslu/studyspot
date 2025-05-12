package ch.hslu.mobpro.studyspot.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import ch.hslu.mobpro.studyspot.data.local.AppDatabase
import ch.hslu.mobpro.studyspot.data.model.User
import kotlinx.coroutines.launch

class AuthViewModel(application: Application) : AndroidViewModel(application) {
    private val userDao = AppDatabase.getDatabase(application).userDao()

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
            if (user != null) onSuccess(user) else onError()
        }
    }
}
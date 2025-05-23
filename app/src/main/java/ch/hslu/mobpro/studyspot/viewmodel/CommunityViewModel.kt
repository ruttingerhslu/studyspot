package ch.hslu.mobpro.studyspot.viewmodel

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
class CommunityViewModel @Inject constructor(
    private val userDao: UserDao
) : ViewModel() {
    private val _contactUsers = MutableStateFlow<List<User>>(emptyList())
    val contactUsers: StateFlow<List<User>> = _contactUsers

    fun loadContactsForUser(email: String) {
        viewModelScope.launch {
            val user = userDao.getUserByEmail(email)
            if (user != null) {
                val contacts = user.contacts.mapNotNull { userDao.getUserByEmail(it) }
                _contactUsers.value = contacts
            }
        }
    }

    fun addContactByEmail(ownerEmail: String, contactEmail: String) {
        viewModelScope.launch {
            val owner = userDao.getUserByEmail(ownerEmail)
            val contact = userDao.getUserByEmail(contactEmail)
            if (owner != null && contact != null && contactEmail !in owner.contacts) {
                val updated = owner.copy(contacts = owner.contacts + contactEmail)
                userDao.updateUser(updated)
                loadContactsForUser(ownerEmail) // refresh
            }
        }
    }
}

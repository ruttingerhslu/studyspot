package ch.hslu.mobpro.studyspot.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "users")
data class User(
    @PrimaryKey val email: String,
    val name: String,
    val password: String,
    val location: String? = null,
    val profileImageUrl: String? = null,
    val contacts: List<String> = emptyList(),
    val favoriteStudySpotIds: List<String> = emptyList()
)
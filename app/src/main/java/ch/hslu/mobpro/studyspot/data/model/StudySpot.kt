package ch.hslu.mobpro.studyspot.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "studyspots")
data class StudySpot(
    @PrimaryKey
    val id: String,
    val name: String,
    val location: String,
    val isGroupWorkAllowed: Boolean,
    val isFree: Boolean
)
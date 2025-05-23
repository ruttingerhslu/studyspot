package ch.hslu.mobpro.studyspot.data.model

import androidx.room.Entity

@Entity(tableName = "studyspots")
data class StudySpot(
    val id: String,
    val name: String,
    val location: String,
    val isGroupWorkAllowed: Boolean,
    val isFree: Boolean
)
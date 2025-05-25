package ch.hslu.mobpro.studyspot.data.local

import androidx.room.Dao
import androidx.room.Query
import ch.hslu.mobpro.studyspot.data.model.StudySpot
import kotlinx.coroutines.flow.Flow

@Dao
interface StudySpotDao {
    @Query("SELECT * FROM studyspots")
    fun getAllStudySpots(): Flow<List<StudySpot>>

    @Query("SELECT * FROM studyspots WHERE id = :studySpotId LIMIT 1")
    suspend fun getStudySpotById(studySpotId: String): StudySpot?
}
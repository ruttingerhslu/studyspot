package ch.hslu.mobpro.studyspot.data.local

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverter
import androidx.room.TypeConverters
import ch.hslu.mobpro.studyspot.data.model.User
import ch.hslu.mobpro.studyspot.data.model.StudySpot

@TypeConverters(Converters::class)
@Database(
    entities = [User::class, StudySpot::class],
    version = 4,
    exportSchema = false
)
abstract class AppDatabase : RoomDatabase() {
    abstract fun userDao(): UserDao
    abstract fun studySpotDao(): StudySpotDao
}

class Converters {
    @TypeConverter
    fun fromString(value: String): List<String> =
        if (value.isBlank()) emptyList() else value.split(",")

    @TypeConverter
    fun listToString(list: List<String>): String =
        list.joinToString(",")
}
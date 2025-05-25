package ch.hslu.mobpro.studyspot.di

import android.content.Context
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.sqlite.db.SupportSQLiteDatabase
import ch.hslu.mobpro.studyspot.data.local.AppDatabase
import ch.hslu.mobpro.studyspot.data.local.UserDao
import ch.hslu.mobpro.studyspot.data.local.StudySpotDao
import ch.hslu.mobpro.studyspot.data.model.StudySpot
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object DatabaseModule {

    @Provides
    @Singleton
    fun provideDatabase(@ApplicationContext context: Context): AppDatabase {
        return Room
            .databaseBuilder(
                context,
                AppDatabase::class.java,
                "studyspot_db"
            )
            .fallbackToDestructiveMigration(false)
            .addCallback(StudySpotDatabaseCallback())
            .build()
    }

    @Provides
    fun provideUserDao(db: AppDatabase): UserDao = db.userDao()

    @Provides
    fun provideStudySpotDao(db: AppDatabase): StudySpotDao = db.studySpotDao()

    private class StudySpotDatabaseCallback : RoomDatabase.Callback() {
        override fun onCreate(db: SupportSQLiteDatabase) {
            super.onCreate(db)
            insertInitialData(db)
        }

        //Make it so that it creates the study entries regardless of whether it is being created or not
        override fun onOpen(db: SupportSQLiteDatabase) {
            super.onOpen(db)
            CoroutineScope(Dispatchers.IO).launch {
                val cursor = db.query("SELECT COUNT(*) FROM studyspots")
                cursor.moveToFirst()
                val count = cursor.getInt(0)
                cursor.close()

                if (count == 0) {
                    insertInitialData(db)
                }
            }
        }

        private fun insertInitialData(db: SupportSQLiteDatabase) {
            db.execSQL(
                """
                INSERT INTO studyspots (id, name, location, isGroupWorkAllowed, isFree) VALUES
                ('spot_001', 'Main Library - Silent Zone', 'Building A, 3rd Floor', 0, 1),
                ('spot_002', 'Library Group Study Room A', 'Building A, 2nd Floor', 1, 1),
                ('spot_003', 'Engineering Lab 301', 'Building B, 3rd Floor', 1, 0),
                ('spot_004', 'Computer Lab 205', 'Building C, 2nd Floor', 0, 1),
                ('spot_005', 'Student Commons - Open Area', 'Student Center, Ground Floor', 1, 1),
                ('spot_006', 'Quiet Study Lounge', 'Building D, 4th Floor', 0, 0),
                ('spot_007', 'Outdoor Study Pavilion', 'Campus Garden', 1, 1),
                ('spot_008', 'Media Center Workshop', 'Building E, 1st Floor', 1, 0),
                ('spot_009', '24/7 Study Hall', 'Building A, Ground Floor', 0, 1),
                ('spot_010', 'Cafeteria Study Area', 'Student Center, 2nd Floor', 1, 1)
                """
            )
        }
    }
}
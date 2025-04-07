package com.example.imagyn.data.database


import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase

@Database(entities = [FlipCard::class, SubjectData::class, ChapterData::class], version = 1)
abstract class ImagynDatabase : RoomDatabase() {
    abstract fun cardDao(): CardDao

    companion object {
        @Volatile
        private var Instance: ImagynDatabase? = null
        fun getDatabase(context: Context): ImagynDatabase {
            return Instance ?: synchronized(this) {
                Room.databaseBuilder(context, ImagynDatabase::class.java, "cards_database")
                    .build()
                    .also { Instance = it }
            }
        }

    }
}
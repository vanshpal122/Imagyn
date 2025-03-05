package com.example.imagyn.data.database


import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase

@Database(entities = [FlipCard::class], version = 1)
abstract class CardDatabase : RoomDatabase() {
    abstract fun cardDao(): CardDao

    companion object {
        @Volatile
        private var Instance: CardDatabase? = null
        fun getDatabase(context: Context): CardDatabase {
            return Instance ?: synchronized(this) {
                Room.databaseBuilder(context, CardDatabase::class.java, "cards_database")
                    .build()
                    .also { Instance = it }
            }
        }

    }
}
package com.example.imagyn.data.database

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Update
import kotlinx.coroutines.flow.Flow

@Dao
interface CardDao {
    @Insert
    suspend fun insert(flipCard: FlipCard)
    @Update
    suspend fun update(flipCard: FlipCard)
    @Delete
    suspend fun delete(flipCard: FlipCard)
    @Query("SELECT * from cards WHERE chapter = :chapter ORDER BY priority")
    fun getCardsOfChapter(chapter: String): Flow<List<FlipCard>>
    @Query("SELECT * from cards WHERE subject = :subject")
    fun getChaptersOfSubject(subject: String): Flow<List<FlipCard>>
    @Query("SELECT * from cards")
    fun getAllCards(): Flow<List<FlipCard>>
}
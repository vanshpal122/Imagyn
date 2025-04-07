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
    suspend fun insertFlipCard(flipCard: FlipCard)

    @Insert
    suspend fun insertSubject(subjectData: SubjectData): Long

    @Insert
    suspend fun insertChapter(chapterData: ChapterData): Long

    @Update
    suspend fun updateSubject(subjectData: SubjectData)

    @Update
    suspend fun updateChapter(chapterData: ChapterData)

    @Update
    suspend fun updateFlipCard(flipCard: FlipCard)

    @Delete
    suspend fun deleteFlipCard(flipCard: FlipCard)

    @Delete
    suspend fun deleteChapter(chapterData: ChapterData)

    @Delete
    suspend fun deleteSubject(subjectData: SubjectData)

    @Query("SELECT * from cards WHERE chapter_id = :chapterId ORDER BY priority")
    fun getCardsOfChapter(chapterId: Int): Flow<List<FlipCard>>

    @Query("SELECT * from chapters WHERE subject_id = :subjectId")
    fun getChaptersOfSubject(subjectId: Int): Flow<List<ChapterData>>

    @Query("SELECT * from chapters")
    fun getAllChapters(): Flow<List<ChapterData>>

    @Query("SELECT * from subjects")
    fun getAllSubjects(): Flow<List<SubjectData>>

    @Query("SELECT * FROM cards WHERE card_id = :keyCard ")
    fun getCardWithID(keyCard: Int): Flow<FlipCard>

    @Query("UPDATE cards SET subject_id = :subjectID WHERE chapter_id = :chapterID")
    suspend fun updateCardSubject(chapterID: Int, subjectID: Int)

    @Query("UPDATE cards SET priority = priority - 1 WHERE chapter_id = :chapterID and priority > :priority")
    suspend fun updatePrioritiesAfterDel(chapterID: Int, priority: Int)

    @Query("UPDATE cards SET priority = priority + 1 WHERE chapter_id = :chapterID and priority >= :priority")
    suspend fun updatePrioritiesBeforeAdd(chapterID: Int, priority: Int)

    @Query("DELETE from chapters WHERE chapter_id = :chapterID")
    suspend fun deleteChapterWithID(chapterID: Int)

    @Query("DELETE from subjects WHERE subject_id = :subjectID")
    suspend fun deleteSubjectWithID(subjectID: Int)
}
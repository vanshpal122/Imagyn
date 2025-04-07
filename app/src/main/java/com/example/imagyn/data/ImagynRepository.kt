package com.example.imagyn.data

import com.example.imagyn.data.database.CardDao
import com.example.imagyn.data.database.ChapterData
import com.example.imagyn.data.database.FlipCard
import com.example.imagyn.data.database.SubjectData

class ImagynRepository(private val cardDao: CardDao) {
    suspend fun insertCard(flipCard: FlipCard) = cardDao.insertFlipCard(flipCard)
    suspend fun updateCard(flipCard: FlipCard) = cardDao.updateFlipCard(flipCard)
    suspend fun deleteCard(flipCard: FlipCard) = cardDao.deleteFlipCard(flipCard)
    suspend fun updateCardSubject(chapterID: Int, subjectID: Int) =
        cardDao.updateCardSubject(chapterID = chapterID, subjectID = subjectID)

    suspend fun insertChapter(chapterData: ChapterData) = cardDao.insertChapter(chapterData)
    suspend fun updateChapter(chapterData: ChapterData) = cardDao.updateChapter(chapterData)
    suspend fun deleteChapter(chapterData: ChapterData) = cardDao.deleteChapter(chapterData)
    suspend fun deleteChapterWithId(chapterID: Int) = cardDao.deleteChapterWithID(chapterID)

    suspend fun insertSubject(subjectData: SubjectData) = cardDao.insertSubject(subjectData)
    suspend fun updateSubject(subjectData: SubjectData) = cardDao.updateSubject(subjectData)
    suspend fun deleteSubject(subjectData: SubjectData) = cardDao.deleteSubject(subjectData)

    fun getCardsOfChapter(chapterID: Int) = cardDao.getCardsOfChapter(chapterID)
    fun getChaptersOfSubject(subjectID: Int) = cardDao.getChaptersOfSubject(subjectID)
    fun getAllChapters() = cardDao.getAllChapters()
    fun getAllSubjects() = cardDao.getAllSubjects()
    fun getCardWithID(keyCard: Int) = cardDao.getCardWithID(keyCard)

    suspend fun updatePrioritiesBeforeAdd(chapterID: Int, priority: Int) =
        cardDao.updatePrioritiesBeforeAdd(chapterID, priority)

    suspend fun updatePriorities(chapterID: Int, priority: Int) =
        cardDao.updatePrioritiesAfterDel(chapterID, priority)
}
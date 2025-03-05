package com.example.imagyn.data

import com.example.imagyn.data.database.FlipCard
import com.example.imagyn.data.database.CardDao

class ImagynRepository(val cardDao: CardDao) {
    suspend fun insertCard(flipCard: FlipCard) = cardDao.insert(flipCard)
    suspend fun updateCard(flipCard: FlipCard) = cardDao.update(flipCard)
    suspend fun deleteCard(flipCard: FlipCard) = cardDao.delete(flipCard)
    fun getCardsOfChapter(chapter: String) = cardDao.getCardsOfChapter(chapter)
    fun getChaptersOfSubject(subject: String) = cardDao.getChaptersOfSubject(subject)
    fun getAllCards() = cardDao.getAllCards()
}
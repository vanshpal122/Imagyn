package com.example.imagyn.ui.addEditScreen

import android.util.Log
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.imagyn.data.ImagynRepository
import com.example.imagyn.data.database.FlipCard
import kotlinx.coroutines.flow.filterNotNull
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch

class EditFlipCardScreenViewModel(private val imagynRepository: ImagynRepository) : ViewModel() {
    var card by mutableStateOf(
        FlipCard(
            cardId = 1,
            front = "",
            back = "",
            priority = 1,
            colorValue = 0x248190,
            chapterID = null,
            subjectID = null
        )
    )

    fun getCardWithId(keyCard: Int) {
        viewModelScope.launch {
            try {
                card = imagynRepository.getCardWithID(keyCard).filterNotNull().first()
            } catch (e: Exception) {
                Log.e("CARD", "ErrorGetting Card")
            }
        }
    }

    fun saveFlipCard(flipCard: FlipCard) {
        viewModelScope.launch {
            imagynRepository.updateCard(flipCard = flipCard)
        }
    }
}
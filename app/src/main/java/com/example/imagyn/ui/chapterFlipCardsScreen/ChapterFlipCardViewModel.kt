package com.example.imagyn.ui.chapterFlipCardsScreen

import android.util.Log
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.imagyn.data.ImagynRepository
import com.example.imagyn.data.database.FlipCard
import com.example.imagyn.ui.ChapterFlipCardScreenDestination
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class ChapterFlipCardViewModel(private val imagynRepository: ImagynRepository) : ViewModel() {
    var screen by mutableStateOf(Screens.CHAPTERSCREEN)
        private set

    fun switchScreen(to: Screens) {
        screen = to
    }


    val flipCards = imagynRepository.getCardsOfChapter(ChapterFlipCardScreenDestination.chapterID)
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    fun deleteFlipCard(flipCard: FlipCard, isLastCard: Boolean, onChapterDelete: () -> Unit) {
        viewModelScope.launch {
            try {
                if (isLastCard) {
                    flipCard.chapterID?.let {
                        imagynRepository.deleteChapterWithId(chapterID = it)
                        onChapterDelete()
                    }
                } else {
                    imagynRepository.deleteCard(flipCard)
                    flipCard.chapterID?.let {
                        imagynRepository.updatePriorities(
                            it,
                            flipCard.priority
                        )
                    }
                }
            } catch (e: Exception) {
                Log.e(
                    "DeleteFlipCard",
                    "Error occurred while deleting flip card or updating priorities",
                    e
                )
            }
        }
    }


}
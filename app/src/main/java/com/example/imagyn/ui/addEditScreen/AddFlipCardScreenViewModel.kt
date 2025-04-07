package com.example.imagyn.ui.addEditScreen

import android.util.Log
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.imagyn.data.ImagynRepository
import com.example.imagyn.data.database.ChapterData
import com.example.imagyn.data.database.FlipCard
import kotlinx.coroutines.launch

class AddFlipCardScreenViewModel(private val imagynRepository: ImagynRepository) : ViewModel() {
    fun onSaveButtonClick(
        flipCard: FlipCard,
        chapterName: String
    ) {
        viewModelScope.launch {
            try {
                if (flipCard.chapterID == null) {
                    val chapterID = imagynRepository.insertChapter(ChapterData(chapter = chapterName, subjectID = null)).toInt()
                    imagynRepository.insertCard(flipCard.copy(chapterID = chapterID))
                } else {
                    imagynRepository.updatePrioritiesBeforeAdd(flipCard.chapterID, flipCard.priority)
                    imagynRepository.insertCard(flipCard)
                }
            } catch (e: Exception) {
                Log.e("CreateChapterCard", "Error creating chapter and card")
            }
        }
    }


}
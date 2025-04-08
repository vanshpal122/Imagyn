package com.example.imagyn.ui.subjectChaptersScreen

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.imagyn.data.ImagynRepository
import com.example.imagyn.data.database.SubjectData
import com.example.imagyn.ui.homescreen.ChapterHomeItem
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch

class SubjectChaptersScreenViewModel(private val imagynRepository: ImagynRepository) : ViewModel() {
    private val _chapterFlow = MutableStateFlow(emptyList<ChapterHomeItem>())
    val chapterFlow = _chapterFlow.asStateFlow()

    fun getChapterFlow(subjectID: Int) {
        viewModelScope.launch {
            imagynRepository.getChaptersOfSubject(subjectID)
                .map { list ->
                    list.map { ChapterHomeItem(it, false) }
                }
                .collect {
                    _chapterFlow.value = it
                }
        }
    }

    fun updateNumberOfSelection(): Int {
        var count = 0
        chapterFlow.value.forEach {
            if (it.isSelected) count++
        }
        return count
    }


    fun selectAll(isSelected: Boolean, updateSelection: () -> Unit) {
        viewModelScope.launch {
            chapterFlow.value.forEachIndexed { index, _ ->
                updateSelectedChapter(index, isSelected, updateSelection)
            }
        }
    }

    fun deleteSelectedChapters(
        subjectID: Int,
        subjectName: String,
        onSubjectDelete: () -> Unit,
        numberOfSelection: Int
    ) {
        viewModelScope.launch {
            try {
                if (numberOfSelection == chapterFlow.value.size) {
                    imagynRepository.deleteSubject(
                        SubjectData(
                            subjectID, subjectName
                        )
                    )
                    onSubjectDelete()
                } else {
                    chapterFlow.value.filter { it.isSelected }.forEach { chapter ->
                        imagynRepository.deleteChapter(chapter.chapter)
                    }
                }
            } catch (e: Exception) {
                Log.e("DeleteSubjectChapter", "Error deleting chapters of Subject", e)
            }
        }
    }

    fun getCurrentToggleStatusChapter(index: Int): Boolean {
        return this.chapterFlow.value[index].isSelected
    }

    fun updateSelectedChapter(
        index: Int,
        isSelected: Boolean,
        updateSelectionNumber: () -> Unit
    ) {
        _chapterFlow.value =
            _chapterFlow.value.toMutableList()
                .apply { this[index] = this[index].copy(isSelected = isSelected) }
        updateSelectionNumber()
    }
}
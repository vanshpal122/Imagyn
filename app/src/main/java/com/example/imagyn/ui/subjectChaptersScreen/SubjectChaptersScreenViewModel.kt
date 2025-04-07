package com.example.imagyn.ui.subjectChaptersScreen

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.imagyn.data.ImagynRepository
import com.example.imagyn.data.database.ChapterData
import com.example.imagyn.data.database.SubjectData
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch

class SubjectChaptersScreenViewModel(private val imagynRepository: ImagynRepository) : ViewModel() {
    private val _chapterMapFlow = MutableStateFlow(emptyMap<ChapterData, Boolean>())
    val chapterMapFlow = _chapterMapFlow.asStateFlow()

    fun getChapterFlow(subjectID: Int) {
        viewModelScope.launch {
            imagynRepository.getChaptersOfSubject(subjectID)
                .map { list ->
                    list.associateWith { false }
                }
                .collect {
                    _chapterMapFlow.value = it
                }
        }
    }


    fun selectAll(isSelected: Boolean, updateSelection: () -> Unit) {
        viewModelScope.launch {
            chapterMapFlow.value.forEach { chapter ->
                updateSelectedChapter(chapter.key, isSelected, updateSelection)
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
                if (numberOfSelection == chapterMapFlow.value.size) {
                    imagynRepository.deleteSubject(
                        SubjectData(
                            subjectID, subjectName
                        )
                    )
                    onSubjectDelete()
                } else {
                    chapterMapFlow.value.filter { it.value }.forEach { chapter ->
                        imagynRepository.deleteChapter(chapter.key)
                    }
                }
            } catch (e: Exception) {
                Log.e("DeleteSubjectChapter", "Error deleting chapters of Subject", e)
            }
        }
    }

    fun updateSelectedChapter(
        chapterData: ChapterData,
        isSelected: Boolean,
        updateSelection: () -> Unit
    ) {
        _chapterMapFlow.value =
            _chapterMapFlow.value.toMutableMap().apply { this[chapterData] = isSelected }
        updateSelection()
    }
}
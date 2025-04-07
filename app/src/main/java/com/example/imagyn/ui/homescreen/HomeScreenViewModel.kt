package com.example.imagyn.ui.homescreen

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.imagyn.data.ImagynRepository
import com.example.imagyn.data.database.ChapterData
import com.example.imagyn.data.database.SubjectData
import kotlinx.coroutines.Deferred
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch

class HomeScreenViewModel(private val imagynRepository: ImagynRepository) : ViewModel() {

    private val _subjectMapFlow = MutableStateFlow(emptyMap<SubjectData, Boolean>())
    private val _chapterMapFlow = MutableStateFlow(emptyMap<ChapterData, Boolean>())
    val subjectMapFlow = _subjectMapFlow.asStateFlow()
    val chapterMapFlow = _chapterMapFlow.asStateFlow()

    init {
        viewModelScope.launch {
            imagynRepository.getAllSubjects().map { list ->
                list.associateWith { false }
            }
                .collect {
                    _subjectMapFlow.value = it
                }
        }
        viewModelScope.launch {
            imagynRepository.getAllChapters().map { list ->
                list.filter { it.subjectID == null }.associateWith { false }
            }
                .collect {
                    _chapterMapFlow.value = it
                }
        }
    }

    fun selectAll(isSelected: Boolean, updateSelectionNumber: () -> Unit) {
        viewModelScope.launch {
            chapterMapFlow.value.forEach { chapter ->
                updateSelectedChapter(chapter.key, isSelected, updateSelectionNumber)
            }
        }
        viewModelScope.launch {
            subjectMapFlow.value.forEach { subject ->
                updateSelectedSubject(subject.key, isSelected, updateSelectionNumber)
            }
        }
    }

    fun updateSelectedChapter(
        chapterData: ChapterData,
        isSelected: Boolean,
        updateSelectionNumber: () -> Unit
    ) {
        _chapterMapFlow.value =
            _chapterMapFlow.value.toMutableMap().apply { this[chapterData] = isSelected }
        updateSelectionNumber()
    }

    fun updateSelectedSubject(
        subjectData: SubjectData,
        isSelected: Boolean,
        updateSelectionNumber: () -> Unit
    ) {
        _subjectMapFlow.value =
            _subjectMapFlow.value.toMutableMap().apply { this[subjectData] = isSelected }
        updateSelectionNumber()
    }

    fun deleteSelectedSubjectsAndChapters() {
        viewModelScope.launch {
            try {
                val deleteJobs = mutableListOf<Deferred<Unit>>()
                deleteJobs.addAll(subjectMapFlow.value.filter { it.value }.map { subject ->
                    async(Dispatchers.IO) {
                        imagynRepository.deleteSubject(subject.key)
                    }
                })
                deleteJobs.addAll(chapterMapFlow.value.filter { it.value }.map { chapter ->
                    async(Dispatchers.IO) {
                        imagynRepository.deleteChapter(chapter.key)
                    }
                })
                deleteJobs.awaitAll()
            } catch (e: Exception) {
                Log.e("DeleteSubjectAndChapter", "Error deleting subject or chapter", e)
            }
        }
    }


    fun createSubject(subjectName: String) {
        viewModelScope.launch {
            try {
                val subjectID = imagynRepository.insertSubject(SubjectData(subject = subjectName))
                val updateJobs = chapterMapFlow.value.filter { it.value }.map { chapter ->
                    async(Dispatchers.IO) {
                        imagynRepository.updateCardSubject(
                            chapterID = chapter.key.chapterID,
                            subjectID = subjectID.toInt()
                        )
                        imagynRepository.updateChapter(chapter.key.copy(subjectID = subjectID.toInt()))
                    }
                }
                updateJobs.awaitAll()
            } catch (e: Exception) {
                Log.e("CreateSubject", "Error creating subject or updating cards", e)
            }
        }
    }

}
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

    private val _subjectFlow = MutableStateFlow(emptyList<SubjectHomeItem>())
    private val _chapterFlow = MutableStateFlow(emptyList<ChapterHomeItem>())
    val subjectFlow = _subjectFlow.asStateFlow()
    val chapterFlow = _chapterFlow.asStateFlow()

    var currentFocusedChapter: ChapterData? = null
    var currentFocusedSubject: SubjectData? = null

    fun updateNumberOfSelection(): Int {
        var count = 0
        chapterFlow.value.forEach {
            if (it.isSelected) {
                count++
                if (count == 1) currentFocusedChapter = it.chapter
            }
        }
        subjectFlow.value.forEach {
            if (it.isSelected) {
                count++
                if (count == 1) currentFocusedSubject = it.subjectData
            }
        }
        if (count != 1) {
            currentFocusedSubject = null
            currentFocusedChapter = null
        }
        return count
    }

    fun getNumberOfSubjectSelection(): Int {
        var count = 0
        subjectFlow.value.forEach {
            if (it.isSelected) count++
        }
        return count
    }

    fun getCurrentToggleStatusChapter(index: Int): Boolean {
        return this.chapterFlow.value[index].isSelected
    }

    fun getCurrentToggleStatusSubject(index: Int): Boolean {
        return this.subjectFlow.value[index].isSelected
    }

    init {
        viewModelScope.launch {
            imagynRepository.getAllSubjects().map { list ->
                list.map { SubjectHomeItem(it, false) }
            }
                .collect {
                    _subjectFlow.value = it
                }
        }
        viewModelScope.launch {
            imagynRepository.getAllChapters().map { list ->
                list.filter { it.subjectID == null }.map { ChapterHomeItem(it, false) }
            }
                .collect {
                    _chapterFlow.value = it
                }
        }
    }

    fun selectAll(isSelected: Boolean, updateSelectionNumber: () -> Unit) {
        viewModelScope.launch {
            chapterFlow.value.forEachIndexed { index, _ ->
                updateSelectedChapter(index, isSelected, updateSelectionNumber)
            }
        }
        viewModelScope.launch {
            subjectFlow.value.forEachIndexed { index, _ ->
                updateSelectedSubject(index, isSelected, updateSelectionNumber)
            }
        }
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

    fun updateSelectedSubject(
        index: Int,
        isSelected: Boolean,
        updateSelectionNumber: () -> Unit
    ) {
        _subjectFlow.value =
            _subjectFlow.value.toMutableList()
                .apply { this[index] = this[index].copy(isSelected = isSelected) }
        updateSelectionNumber()
    }

    fun deleteSelectedSubjectsAndChapters(deselectAll: () -> Unit) {
        viewModelScope.launch {
            try {
                val deleteJobs = mutableListOf<Deferred<Unit>>()
                deleteJobs.addAll(subjectFlow.value.filter { it.isSelected }.map { subject ->
                    async(Dispatchers.IO) {
                        imagynRepository.deleteSubject(subject.subjectData)
                    }
                })
                deleteJobs.addAll(chapterFlow.value.filter { it.isSelected }.map { chapter ->
                    async(Dispatchers.IO) {
                        imagynRepository.deleteChapter(chapter.chapter)
                    }
                })
                deleteJobs.awaitAll()
            } catch (e: Exception) {
                Log.e("DeleteSubjectAndChapter", "Error deleting subject or chapter", e)
            }
            deselectAll()
        }
    }

    fun renameSubject(subjectName: String, deselectAll: () -> Unit) {
        viewModelScope.launch {
            try {
                subjectFlow.value.filter { it.isSelected }.map { subject ->
                    imagynRepository.updateSubject(
                        subject.subjectData.copy(subject = subjectName)
                    )
                }
            } catch (e: Exception) {
                Log.e("Renaming Chapter", "ERROR RENAMING CHAPTER")
            }
            deselectAll()
        }
    }

    fun renameCh(chapterName: String, deselectAll: () -> Unit) {
        viewModelScope.launch {
            try {
                chapterFlow.value.filter { it.isSelected }
                    .map { chapter -> imagynRepository.updateChapter(chapter.chapter.copy(chapter = chapterName)) }
            } catch (e: Exception) {
                Log.e("Renaming Chapter", "ERROR RENAMING CHAPTER")
            }
        }
        deselectAll()
    }

    fun moveSelectedChToSubject(subjectID: Int, deselectAll: () -> Unit) {
        viewModelScope.launch {
            try {
                val updateJobs = chapterFlow.value.filter { it.isSelected }.map { chapter ->
                    async(Dispatchers.IO) {
                        imagynRepository.updateCardSubject(
                            chapterID = chapter.chapter.chapterID,
                            subjectID = subjectID
                        )
                        imagynRepository.updateChapter(chapter.chapter.copy(subjectID = subjectID))
                    }
                }
                updateJobs.awaitAll()
            } catch (e: Exception) {
                Log.e("Moving to Subject $subjectID", "Moving to Subject $subjectID failed", e)
            }
            deselectAll()
        }
    }

    fun createSubject(subjectName: String, deselectAll: () -> Unit) {
        viewModelScope.launch {
            try {
                val subjectID = imagynRepository.insertSubject(SubjectData(subject = subjectName))
                val updateJobs = chapterFlow.value.filter { it.isSelected }.map { chapter ->
                    async(Dispatchers.IO) {
                        imagynRepository.updateChapter(chapter.chapter.copy(subjectID = subjectID.toInt()))
                        Log.d("SUBJECTID", "$subjectID")
                        imagynRepository.updateCardSubject(
                            chapterID = chapter.chapter.chapterID,
                            subjectID = subjectID.toInt()
                        )
                    }
                }
                updateJobs.awaitAll()
            } catch (e: Exception) {
                Log.e("CreateSubject", "Error creating subject or updating cards", e)
            }
            deselectAll()
        }
    }
}

data class ChapterHomeItem(
    val chapter: ChapterData,
    val isSelected: Boolean,
)

data class SubjectHomeItem(
    val subjectData: SubjectData,
    val isSelected: Boolean
)
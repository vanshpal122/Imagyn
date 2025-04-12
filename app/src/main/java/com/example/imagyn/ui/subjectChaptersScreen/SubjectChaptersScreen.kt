package com.example.imagyn.ui.subjectChaptersScreen

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.imagyn.ui.AppViewModelProvider
import com.example.imagyn.ui.homescreen.MainAppScreenUI

@Composable
fun SubjectChaptersScreen(
    onChapterCardClick: (Int) -> Unit,
    subjectID: Int,
    onNavigateBack: () -> Unit,
    modifier: Modifier = Modifier,
    subjectScreenViewModel: SubjectChaptersScreenViewModel = viewModel(factory = AppViewModelProvider.Factory),
    subjectName: String
) {
    LaunchedEffect(subjectID) {
        subjectScreenViewModel.getChapterFlow(subjectID)
    }
    val chaptersList by subjectScreenViewModel.chapterFlow.collectAsState()

    MainAppScreenUI(
        chapterList = chaptersList,
        subjectList = emptyList(),
        onAddChapterClick = {},
        onChapterCardClick = onChapterCardClick,
        onSubjectCardClick = { _, _ -> },
        deleteSelectedSubjectsAndChapters = { numberOfSelection ->
            subjectScreenViewModel.deleteSelectedChapters(
                subjectID,
                subjectName,
                onNavigateBack,
                numberOfSelection
            )
        },
        createSubject = {},
        updateSubjectSelectedList = { _, _, _ -> },
        updateChapterSelectedList = { index, b, updateSelection ->
            subjectScreenViewModel.updateSelectedChapter(
                index,
                b,
                updateSelection
            )
        },
        selectAll = { selection, updateSelection ->
            subjectScreenViewModel.selectAll(
                selection,
                updateSelection
            )
        },
        isMainScreen = false,
        onNavigateBack = onNavigateBack,
        title = subjectName,
        updateNumberOfSubjectSelected = {},
        updateSelectionNumber = subjectScreenViewModel::updateNumberOfSelection,
        getChapterToggleStatus = { index ->
            subjectScreenViewModel.getCurrentToggleStatusChapter(
                index
            )
        },
        getSubjectToggleStatus = { false },
        numberOfSubjectSelected = 0
    )
}
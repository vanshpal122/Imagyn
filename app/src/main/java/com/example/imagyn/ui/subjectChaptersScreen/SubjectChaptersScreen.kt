package com.example.imagyn.ui.subjectChaptersScreen

import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.imagyn.data.database.SubjectData
import com.example.imagyn.ui.AppViewModelProvider
import com.example.imagyn.ui.SubjectChapterScreenDestination
import com.example.imagyn.ui.homescreen.MainAppScreenUI

@Composable
fun SubjectChaptersScreen(
    onChapterCardClick: (Int) -> Unit,
    onNavigateBack: () -> Unit,
    modifier: Modifier = Modifier,
    subjectScreenViewModel: SubjectChaptersScreenViewModel = viewModel(factory = AppViewModelProvider.Factory),
    onAddChapterClick: (String) -> Unit
) {
    val chaptersList by subjectScreenViewModel.chapterFlow.collectAsState()

    MainAppScreenUI(
        chapterList = chaptersList,
        subjectList = emptyList(),
        onAddChapterClick = { chapterName -> onAddChapterClick(chapterName) },
        onChapterCardClick = onChapterCardClick,
        onSubjectCardClick = { },
        deleteSelectedSubjectsAndChapters = { numberOfSelection, deselectAll ->
            subjectScreenViewModel.deleteSelectedChapters(
                subjectScreenViewModel.currentSubject,
                onNavigateBack,
                numberOfSelection,
                deselectAll
            )
        },
        createSubject = { _, _ -> },
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
        title = subjectScreenViewModel.currentSubject,
        updateNumberOfSubjectSelected = {},
        updateSelectionNumber = subjectScreenViewModel::updateNumberOfSelection,
        getChapterToggleStatus = { index ->
            subjectScreenViewModel.getCurrentToggleStatusChapter(
                index
            )
        },
        getSubjectToggleStatus = { false },
        numberOfSubjectSelected = 0,
        removeChFromSub = subjectScreenViewModel::removeSelectedChapterFromSub,
        renameSubject = { subjectN, _ ->
            subjectScreenViewModel.renameSubject(
                SubjectData(
                    subjectID = SubjectChapterScreenDestination.subjectID,
                    subject = subjectN
                )
            )
            subjectScreenViewModel.currentSubject = subjectN
        },
        moveChToSubject = { _, _ -> },
        renameCh = { chapterName, deselectAll ->
            subjectScreenViewModel.renameCh(
                chapterName,
                deselectAll
            )
        },
        currentFocusedChapter = subjectScreenViewModel.currentFocusedChapter,
        currentFocusedSubject = SubjectData(
            subjectID = SubjectChapterScreenDestination.subjectID,
            subject = subjectScreenViewModel.currentSubject
        ),
        modifier = modifier
    )
}
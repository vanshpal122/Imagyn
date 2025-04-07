package com.example.imagyn.ui.addEditScreen

import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.imagyn.data.database.FlipCard
import com.example.imagyn.ui.AppViewModelProvider

@Composable
fun AddFlipCardScreen(
    colorValue: Long,
    modifier: Modifier = Modifier,
    addFlipCardScreenViewModel: AddFlipCardScreenViewModel = viewModel(factory = AppViewModelProvider.Factory),
    priority: Int,
    chapterId: Int?,
    subjectId: Int?,
    chapterName: String,
    onNavigate: () -> Unit
) {
    var card by remember {
        mutableStateOf(
            FlipCard(
                chapterID = chapterId,
                subjectID = subjectId,
                front = "",
                back = "",
                priority = priority,
                colorValue = colorValue
            )
        )
    }

    AddEditFlipCardScreen(
        onSaveButtonClick = { front, back ->
            addFlipCardScreenViewModel.onSaveButtonClick(
                card.copy(front = front, back = back), chapterName
            )
            onNavigate()
        },
        onCancelButtonClick = onNavigate,
        colorValue = colorValue,
        frontText = card.front,
        backText = card.back,
        changeFrontText = { card = card.copy(front = it) },
        changeBackText = { card = card.copy(back = it) }
    )
}

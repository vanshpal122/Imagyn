package com.example.imagyn.ui.addEditScreen

import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.imagyn.data.database.FlipCard
import com.example.imagyn.ui.AddCardScreenDestination
import com.example.imagyn.ui.AppViewModelProvider

@Composable
fun AddFlipCardScreen(
    modifier: Modifier = Modifier,
    addFlipCardScreenViewModel: AddFlipCardScreenViewModel = viewModel(factory = AppViewModelProvider.Factory),
    onNavigate: () -> Unit
) {
    var card by remember {
        mutableStateOf(
            FlipCard(
                chapterID = AddCardScreenDestination.chapterID,
                subjectID = AddCardScreenDestination.subjectID,
                front = "",
                back = "",
                priority = AddCardScreenDestination.priority,
                colorValue = AddCardScreenDestination.colorValue
            )
        )
    }

    AddEditFlipCardScreen(
        onSaveButtonClick = { front, back ->
            addFlipCardScreenViewModel.onSaveButtonClick(
                card.copy(front = front, back = back), AddCardScreenDestination.chapterName
            )
            onNavigate()
        },
        onCancelButtonClick = onNavigate,
        colorValue = AddCardScreenDestination.colorValue,
        frontText = card.front,
        backText = card.back,
        changeFrontText = { card = card.copy(front = it) },
        changeBackText = { card = card.copy(back = it) }
    )
}

package com.example.imagyn.ui.addEditScreen

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Modifier
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.imagyn.ui.AppViewModelProvider

@Composable
fun EditFlipCardScreen(
    keyCard: Int,
    modifier: Modifier = Modifier,
    editFlipCardScreenViewModel: EditFlipCardScreenViewModel = viewModel(factory = AppViewModelProvider.Factory),
    onNavBack: () -> Unit,
) {

    LaunchedEffect(keyCard) {
        editFlipCardScreenViewModel.getCardWithId(keyCard)
    }


    AddEditFlipCardScreen(
        onSaveButtonClick = { front, back ->
            editFlipCardScreenViewModel.saveFlipCard(
                flipCard = editFlipCardScreenViewModel.card.copy(
                    front = front,
                    back = back
                )
            )
            onNavBack()
        },
        onCancelButtonClick = onNavBack,
        colorValue = editFlipCardScreenViewModel.card.colorValue,
        frontText = editFlipCardScreenViewModel.card.front,
        backText = editFlipCardScreenViewModel.card.back,
        changeFrontText = {
            editFlipCardScreenViewModel.card = editFlipCardScreenViewModel.card.copy(front = it)
        },
        changeBackText = {
            editFlipCardScreenViewModel.card = editFlipCardScreenViewModel.card.copy(back = it)
        }
    )
}
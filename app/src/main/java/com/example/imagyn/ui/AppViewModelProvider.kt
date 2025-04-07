package com.example.imagyn.ui

import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewmodel.CreationExtras
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import com.example.imagyn.ImagynApplication
import com.example.imagyn.ui.addEditScreen.AddFlipCardScreenViewModel
import com.example.imagyn.ui.addEditScreen.EditFlipCardScreenViewModel
import com.example.imagyn.ui.chapterFlipCardsScreen.ChapterFlipCardViewModel
import com.example.imagyn.ui.homescreen.HomeScreenViewModel
import com.example.imagyn.ui.reorderScreen.reorderlist.ReorderListViewModel
import com.example.imagyn.ui.subjectChaptersScreen.SubjectChaptersScreenViewModel

object AppViewModelProvider {
    val Factory =
        viewModelFactory {
            initializer {
                AddFlipCardScreenViewModel(imagynRepository = imagynApplication().container.imagynRepository)
            }
            initializer {
                EditFlipCardScreenViewModel(imagynRepository = imagynApplication().container.imagynRepository)
            }
            initializer {
                ChapterFlipCardViewModel(imagynRepository = imagynApplication().container.imagynRepository)
            }
            initializer {
                HomeScreenViewModel(imagynRepository = imagynApplication().container.imagynRepository)
            }
            initializer {
                SubjectChaptersScreenViewModel(imagynRepository = imagynApplication().container.imagynRepository)
            }
            initializer {
                ReorderListViewModel(imagynApplication().container.imagynRepository)
            }
        }
}

fun CreationExtras.imagynApplication(): ImagynApplication =
    (this[ViewModelProvider.AndroidViewModelFactory.APPLICATION_KEY] as ImagynApplication)
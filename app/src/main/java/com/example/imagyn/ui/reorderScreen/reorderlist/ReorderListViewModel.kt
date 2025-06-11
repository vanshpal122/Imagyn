/*
 * Copyright 2022 André Claßen
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     https://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.example.imagyn.ui.reorderScreen.reorderlist

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.imagyn.data.ImagynRepository
import com.example.imagyn.data.database.FlipCard
import com.example.imagyn.ui.ReorderDestination
import com.example.imagyn.ui.reorderScreen.reorderable.ItemPosition
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch

class ReorderListViewModel(private val imagynRepository: ImagynRepository) : ViewModel() {
    var cards by mutableStateOf(emptyList<FlipCard>())

    init {
        viewModelScope.launch {
            cards = imagynRepository.getCardsOfChapter(ReorderDestination.chapterID).first()
        }
    }

    fun moveCard(from: ItemPosition, to: ItemPosition) {
        cards = cards.toMutableList().apply {
            add(to.index, removeAt(from.index))
        }
    }

    fun onSaveButtonClick() {
        viewModelScope.launch {
            cards.forEachIndexed { index, flipCard ->
                imagynRepository.updateCard(flipCard.copy(priority = index + 1))
            }
        }
    }

    fun isCardDragEnabled() =
        true
}
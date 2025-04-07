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

import androidx.compose.animation.core.animateDpAsState
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.itemsIndexed
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBarColors
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.imagyn.data.database.FlipCard
import com.example.imagyn.ui.AppViewModelProvider
import com.example.imagyn.ui.cardUtils.ChapterCardUi
import com.example.imagyn.ui.reorderScreen.reorderable.ItemPosition
import com.example.imagyn.ui.reorderScreen.reorderable.ReorderableItem
import com.example.imagyn.ui.reorderScreen.reorderable.detectReorderAfterLongPress
import com.example.imagyn.ui.reorderScreen.reorderable.rememberReorderableLazyGridState
import com.example.imagyn.ui.reorderScreen.reorderable.reorderable

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ReorderGrid(
    chapterId: Int,
    onNavigate: () -> Unit,
    vm: ReorderListViewModel = viewModel(factory = AppViewModelProvider.Factory)
) {
    LaunchedEffect(chapterId) {
        vm.cards = vm.getFlipCards(chapterId)
    }
    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                title = { },
                navigationIcon = {
                    TextButton(onClick = onNavigate) {
                        Text(text = "Cancel", style = MaterialTheme.typography.titleLarge)
                    }
                },
                actions = {
                    TextButton(onClick = {
                        vm.onSaveButtonClick()
                        onNavigate()
                    }) {
                        Text(text = "Save", style = MaterialTheme.typography.titleLarge)
                    }
                },
                colors = TopAppBarColors(
                    containerColor = Color(0xFF152022),
                    actionIconContentColor = Color.White,
                    titleContentColor = Color.White,
                    scrolledContainerColor = Color(0xFF152022),
                    navigationIconContentColor = Color.White
                )
            )
        },
        containerColor = Color(0xFF152022)
    ) { innerPadding ->
        VerticalGrid(
            modifier = Modifier.padding(innerPadding),
            cardList = vm.cards,
            moveCard = { p1, p2 -> vm.moveCard(p1, p2) },
            isCardDragEnabled = { _, _ -> vm.isCardDragEnabled() })
    }
}

@Composable
private fun VerticalGrid(
    moveCard: (ItemPosition, ItemPosition) -> Unit,
    isCardDragEnabled: ((ItemPosition, ItemPosition) -> Boolean)?,
    modifier: Modifier = Modifier,
    cardList: List<FlipCard>,
) {
    val state =
        rememberReorderableLazyGridState(onMove = moveCard, canDragOver = isCardDragEnabled)
    LazyVerticalGrid(
        columns = GridCells.Adaptive(148.dp),
        state = state.gridState,
        contentPadding = PaddingValues(horizontal = 8.dp),
        verticalArrangement = Arrangement.spacedBy(48.dp),
        modifier = modifier
            .reorderable(state)
            .fillMaxSize()
    ) {
        itemsIndexed(cardList, key = { _, card -> card.cardId }) { index, item ->
            ReorderableItem(
                state,
                item.cardId,
                modifier = Modifier.wrapContentSize()
            ) { isDragging ->
                val elevation = animateDpAsState(if (isDragging) 8.dp else 0.dp, label = "")
                Box() {
                    ChapterCardUi(
                        content = item.front,
                        cardColorValue = item.colorValue,
                        textColorValue = 0xFF000000,
                        modifier = Modifier
                            .detectReorderAfterLongPress(state)
                            .shadow(elevation.value)
                    )
                    if (!isDragging) Text(
                        text = "${index + 1}.",
                        style = MaterialTheme.typography.titleLarge,
                        modifier = Modifier
                            .align(Alignment.BottomEnd)
                            .padding(4.dp)
                    )
                }
            }
        }
    }
}

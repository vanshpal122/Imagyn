package com.example.imagyn.ui.chapterFlipCardsScreen

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.AnimatedContentScope
import androidx.compose.animation.ExperimentalSharedTransitionApi
import androidx.compose.animation.SharedTransitionLayout
import androidx.compose.animation.SharedTransitionScope
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.PageSize
import androidx.compose.foundation.pager.PagerState
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowLeft
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowRight
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarColors
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.imagyn.data.database.FlipCard
import com.example.imagyn.ui.AppViewModelProvider
import com.example.imagyn.ui.cardUtils.FlipCardUi
import kotlinx.coroutines.launch

@OptIn(ExperimentalSharedTransitionApi::class)
@Composable
fun ChapterFlipCardsScreen(
    chapterID: Int,
    onEditClick: (Int) -> Unit,
    onBackButtonClick: () -> Unit,
    onAddNewButtonClick: (Int) -> Unit,
    modifier: Modifier = Modifier,
    chapterFlipCardViewModel: ChapterFlipCardViewModel = viewModel(factory = AppViewModelProvider.Factory),
    onReorderButtonClick: () -> Unit
) {
    val flipCardList by chapterFlipCardViewModel.getFlipCards(chapterID).collectAsState()
    var isPlayScreen by remember {
        mutableStateOf(false)
    }
    val pagerState = rememberPagerState {
        flipCardList.size + 1
    }
    SharedTransitionLayout {
        AnimatedContent(targetState = isPlayScreen, label = "flipCardPlay") { targetState ->
            if (!targetState) {
                ChapterFlipCardsScreenUI(
                    flipCardList = flipCardList,
                    onBackButtonClick = onBackButtonClick,
                    onReorderButtonClick = onReorderButtonClick,
                    onAddNewButtonClick = { priority ->
                        onAddNewButtonClick(
                            priority
                        )
                    },
                    onEditClick = { cardId -> onEditClick(cardId) },
                    onDeleteClick = { flipCard, b ->
                        chapterFlipCardViewModel.deleteFlipCard(
                            flipCard,
                            b, onBackButtonClick
                        )
                    },
                    pagerState = pagerState,
                    animatedVisibilityScope = this@AnimatedContent,
                    sharedTransitionScope = this@SharedTransitionLayout,
                    onCardClick = { isPlayScreen = true }
                )
            } else {
                FlipCardPlayScreenUI(
                    flipCard = flipCardList[pagerState.currentPage],
                    onCrossButtonClick = { isPlayScreen = false },
                    onSkipNextButtonClick = { pagerState.requestScrollToPage(pagerState.currentPage + 1) },
                    onSkipPreviousButtonClick = { pagerState.requestScrollToPage(pagerState.currentPage - 1) },
                    canSkipToNext = pagerState.currentPage < (flipCardList.size - 1),
                    canSkipToPrev = pagerState.currentPage > 0,
                    animatedVisibilityScope = this@AnimatedContent,
                    sharedTransitionScope = this@SharedTransitionLayout
                )
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class, ExperimentalSharedTransitionApi::class)
@Composable
fun ChapterFlipCardsScreenUI(
    flipCardList: List<FlipCard>,
    onBackButtonClick: () -> Unit,
    onReorderButtonClick: () -> Unit,
    onAddNewButtonClick: (Int) -> Unit,
    onEditClick: (Int) -> Unit,
    onDeleteClick: (FlipCard, Boolean) -> Unit,
    pagerState: PagerState,
    modifier: Modifier = Modifier,
    animatedVisibilityScope: AnimatedContentScope,
    sharedTransitionScope: SharedTransitionScope,
    onCardClick: () -> Unit,
) {
    var isDropDown by rememberSaveable {
        mutableStateOf(false)
    }
    val coroutineScope = rememberCoroutineScope()
    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                title = { },
                navigationIcon = {
                    IconButton(onClick = onBackButtonClick) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Close"
                        )
                    }
                }, colors = TopAppBarColors(
                    containerColor = Color(0xFF152022),
                    actionIconContentColor = Color.White,
                    titleContentColor = Color.White,
                    scrolledContainerColor = Color(0xFF152022),
                    navigationIconContentColor = Color.White
                ),
                actions = {
                    IconButton(onClick = { isDropDown = !isDropDown }) {
                        Icon(
                            imageVector = Icons.Default.MoreVert,
                            contentDescription = "More Options"
                        )
                        DropdownMenu(
                            expanded = isDropDown,
                            onDismissRequest = { isDropDown = !isDropDown }) {
                            DropdownMenuItem(text = {
                                Text(
                                    text = "Edit",
                                    style = MaterialTheme.typography.labelLarge
                                )
                            }, onClick = {
                                val cardToBePassed =
                                    if (pagerState.currentPage < flipCardList.size) flipCardList[pagerState.currentPage] else flipCardList[pagerState.currentPage - 1]
                                onEditClick(cardToBePassed.cardId)
                                isDropDown = !isDropDown
                            })
                            DropdownMenuItem(text = {
                                Text(
                                    text = "Delete",
                                    style = MaterialTheme.typography.labelLarge
                                )
                            }, onClick = {
                                onDeleteClick(
                                    flipCardList[pagerState.currentPage],
                                    (flipCardList.size == 1)
                                )
                                isDropDown = !isDropDown
                            })
                            if (flipCardList.size > 1) {
                                DropdownMenuItem(text = {
                                    Text(
                                        text = "Re-Order",
                                        style = MaterialTheme.typography.labelLarge
                                    )
                                }, onClick = {
                                    onReorderButtonClick()
                                    isDropDown = !isDropDown
                                })
                            }
                            DropdownMenuItem(text = {
                                Text(
                                    text = "Add New Card",
                                    style = MaterialTheme.typography.labelLarge
                                )
                            }, onClick = {
                                onAddNewButtonClick(pagerState.currentPage + 1)
                                isDropDown = !isDropDown
                            })
                        }
                    }
                }
            )
        },
        containerColor = Color(0xFF152022)
    ) { innerPadding ->
        Box(
            contentAlignment = Alignment.Center, modifier = Modifier
                .fillMaxHeight()
                .padding(innerPadding)
        ) {
            HorizontalPager(
                state = pagerState,
                pageSize = PageSize.Fixed(256.dp),
                beyondViewportPageCount = 1,
            ) { page ->
                if (page < flipCardList.size) {
                    with(sharedTransitionScope) {
                        FlipCardUi(
                            content = flipCardList[page].front,
                            cardColorValue = flipCardList[page].colorValue,
                            modifier = Modifier
                                .offset(x = 64.dp)
                                .sharedElement(
                                    rememberSharedContentState(key = flipCardList[page].cardId),
                                    animatedVisibilityScope = animatedVisibilityScope
                                )
                                .clickable { onCardClick() }
                        )
                    }
                }
            }
            Row(
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier
                    .fillMaxWidth()
            ) {
                if (pagerState.currentPage > 0) IconButton(onClick = {
                    coroutineScope.launch {
                        pagerState.animateScrollToPage(
                            pagerState.currentPage - 1
                        )
                    }
                }) {
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.KeyboardArrowLeft,
                        tint = Color.White,
                        contentDescription = "Move Previous"
                    )
                }
                else Spacer(modifier = Modifier.width(2.dp))
                if (pagerState.currentPage < flipCardList.size - 1) IconButton(onClick = {
                    coroutineScope.launch {
                        pagerState.animateScrollToPage(
                            pagerState.currentPage + 1
                        )
                    }
                }) {
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.KeyboardArrowRight,
                        tint = Color.White,
                        contentDescription = "Move Next"
                    )
                }
                else Spacer(modifier = Modifier.width(2.dp))
            }
        }
    }
}

//@Preview
//@Composable
//fun ChapterFlipCardsScreenPreview() {
//    val pagerState = rememberPagerState {
//        6
//    }
//    ImagynTheme {
//        ChapterFlipCardsScreenUI(
//            flipCardList = listOf(
//                FlipCard(
//                    key = 0,
//                    front = "What is OS ?",
//                    back = "",
//                    chapterID = 0,
//                    subjectID = 0,
//                    colorValue = 0xFF248190,
//                    priority = 0
//                ),
//                FlipCard(
//                    key = 0,
//                    front = "",
//                    back = "",
//                    chapterID = 0,
//                    subjectID = 0,
//                    colorValue = 0xFF248243,
//                    priority = 0
//                ),
//                FlipCard(
//                    key = 0,
//                    front = "",
//                    back = "",
//                    chapterID = 0,
//                    subjectID = 0,
//                    colorValue = 0xFFC48593,
//                    priority = 0
//                ),
//                FlipCard(
//                    key = 0,
//                    front = "",
//                    back = "",
//                    chapterID = 0,
//                    subjectID = 0,
//                    colorValue = 0xFFCC8593,
//                    priority = 0
//                ),
//                FlipCard(
//                    key = 0,
//                    front = "Hello",
//                    back = "",
//                    chapterID = 0,
//                    subjectID = 0,
//                    colorValue = 0xFFCCC593,
//                    priority = 0
//                )
//            ),
//            onBackButtonClick = {},
//            onReorderButtonClick = {},
//            onAddNewButtonClick = {},
//            onEditClick = {},
//            onDeleteClick = { _, _ -> },
//            pagerState = pagerState,
//            animatedVisibilityScope = this@AnimatedContent,
//            sharedTransitionScope = this@SharedTransitionLayout
//        )
//    }
//}
package com.example.imagyn.ui.chapterFlipCardsScreen

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.AnimatedContentScope
import androidx.compose.animation.ExperimentalSharedTransitionApi
import androidx.compose.animation.SharedTransitionLayout
import androidx.compose.animation.SharedTransitionScope
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.snapping.SnapPosition
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
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
import androidx.compose.ui.layout.onSizeChanged
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.DpOffset
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.imagyn.data.database.FlipCard
import com.example.imagyn.ui.AppViewModelProvider
import com.example.imagyn.ui.cardUtils.FlipCardUi
import com.example.imagyn.ui.homescreen.CustomiseAlertDialogBox
import kotlinx.coroutines.launch

enum class Screens { PLAYSCREEN, CHAPTERSCREEN, EXPANDEDSCREEN }

@OptIn(ExperimentalSharedTransitionApi::class)
@Composable
fun ChapterFlipCardsScreen(
    onEditClick: (Int) -> Unit,
    onBackButtonClick: () -> Unit,
    onAddNewButtonClick: (Int) -> Unit,
    modifier: Modifier = Modifier,
    chapterFlipCardViewModel: ChapterFlipCardViewModel = viewModel(factory = AppViewModelProvider.Factory),
    onReorderButtonClick: () -> Unit
) {
    val flipCardList by chapterFlipCardViewModel.flipCards.collectAsState()
    val pagerState = rememberPagerState {
        flipCardList.size
    }
    SharedTransitionLayout {
        AnimatedContent(
            targetState = chapterFlipCardViewModel.screen,
            label = "flipCardPlay"
        ) { targetState ->
            when (targetState) {
                Screens.CHAPTERSCREEN -> {
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
                        onCardClick = {
                            chapterFlipCardViewModel.switchScreen(Screens.PLAYSCREEN)
                        }
                    )
                }

                Screens.PLAYSCREEN -> {
                    FlipCardPlayScreenUI(
                        flipCard = flipCardList[pagerState.currentPage],
                        onCrossButtonClick = { chapterFlipCardViewModel.switchScreen(Screens.CHAPTERSCREEN) },
                        onSkipNextButtonClick = { pagerState.requestScrollToPage(pagerState.currentPage + 1) },
                        onSkipPreviousButtonClick = { pagerState.requestScrollToPage(pagerState.currentPage - 1) },
                        canSkipToNext = pagerState.currentPage < (flipCardList.size - 1),
                        canSkipToPrev = pagerState.currentPage > 0,
                        animatedVisibilityScope = this@AnimatedContent,
                        sharedTransitionScope = this@SharedTransitionLayout,
                        onExpandClick = { chapterFlipCardViewModel.switchScreen(Screens.EXPANDEDSCREEN) }
                    )
                }

                Screens.EXPANDEDSCREEN -> {
                    ExpandedFlipCardScreen(
                        flipCard = flipCardList[pagerState.currentPage],
                        onCrossButtonClick = { chapterFlipCardViewModel.switchScreen(Screens.PLAYSCREEN) },
                        animatedVisibilityScope = this@AnimatedContent,
                        sharedTransitionScope = this@SharedTransitionLayout
                    )
                }
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

    var isDeleteAlertBoxShown by rememberSaveable {
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
                                isDeleteAlertBoxShown = true
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
                                var priority = pagerState.currentPage + 1
                                if(pagerState.currentPage + 1 == pagerState.pageCount) priority = pagerState.currentPage + 2
                                isDropDown = !isDropDown
                                onAddNewButtonClick(priority)
                            })
                        }
                    }
                }
            )
        },
        containerColor = Color(0xFF152022)
    ) { innerPadding ->
        val density = LocalDensity.current
        var offset by remember {
            mutableStateOf(DpOffset.Zero)
        }
        Box(
            contentAlignment = Alignment.Center, modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
        ) {
            HorizontalPager(
                state = pagerState,
                pageSize = PageSize.Fill,
                snapPosition = SnapPosition.Center,
                beyondViewportPageCount = 2,
                modifier = Modifier
                    .fillMaxSize()
                    .onSizeChanged {
                        with(density) {
                            offset = DpOffset(it.width.toDp(), it.height.toDp())
                        }
                    }
            ) { page ->
                if (page < flipCardList.size) {
                    with(sharedTransitionScope) {
                        FlipCardUi(
                            content = flipCardList[page].front,
                            cardColorValue = flipCardList[page].colorValue,
                            modifier = Modifier
                                .offset(offset.x / 2 - 118.dp, offset.y / 56)
                                .sharedElement(
                                    rememberSharedContentState(key = flipCardList[page].cardId),
                                    animatedVisibilityScope = animatedVisibilityScope
                                )
                                .clickable { onCardClick() },
                            onTextOverflow = {}
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
        if (isDeleteAlertBoxShown) {
            CustomiseAlertDialogBox(
                title = "Delete Card",
                confirmTitle = "Delete",
                text = {
                    Text(text = "Are you sure you want to delete this card ?")
                },
                dismissAlertBox = {
                    isDeleteAlertBoxShown = false
                },
                onConfirmButtonClick = {
                    isDeleteAlertBoxShown = false
                    onDeleteClick(
                        flipCardList[pagerState.currentPage],
                        (flipCardList.size == 1)
                    )
                },
                isConfirmButtonEnabled = true
            )
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
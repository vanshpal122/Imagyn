package com.example.imagyn.ui.chapterFlipCardsScreen

import androidx.activity.compose.BackHandler
import androidx.compose.animation.AnimatedContentScope
import androidx.compose.animation.ExperimentalSharedTransitionApi
import androidx.compose.animation.SharedTransitionScope
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.TopAppBarColors
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import com.example.imagyn.data.database.FlipCard
import com.example.imagyn.ui.cardUtils.FlipCardExpanded

@Composable
@OptIn(ExperimentalMaterial3Api::class, ExperimentalSharedTransitionApi::class)
fun ExpandedFlipCardScreen(
    flipCard: FlipCard,
    animatedVisibilityScope: AnimatedContentScope,
    sharedTransitionScope: SharedTransitionScope,
    modifier: Modifier = Modifier,
    onCrossButtonClick: () -> Unit
) {
    BackHandler {
        onCrossButtonClick()
    }
    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                title = { },
                navigationIcon = {
                    IconButton(onClick = onCrossButtonClick) {
                        Icon(
                            imageVector = Icons.Default.Close,
                            contentDescription = "Close"
                        )
                    }
                }, colors = TopAppBarColors(
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
        Column(
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
        ) {
            with(sharedTransitionScope) {
                FlipCardExpanded(
                    content = flipCard.back,
                    cardColorValue = flipCard.colorValue,
                    modifier = Modifier.sharedElement(
                        rememberSharedContentState(key = flipCard.cardId),
                        animatedVisibilityScope = animatedVisibilityScope
                    )
                )
            }
        }
    }
}

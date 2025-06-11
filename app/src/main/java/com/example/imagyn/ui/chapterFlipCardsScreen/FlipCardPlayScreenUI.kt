package com.example.imagyn.ui.chapterFlipCardsScreen

import androidx.activity.compose.BackHandler
import androidx.compose.animation.AnimatedContentScope
import androidx.compose.animation.ExperimentalSharedTransitionApi
import androidx.compose.animation.SharedTransitionScope
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.ButtonColors
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBarColors
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.blur
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import com.example.imagyn.R
import com.example.imagyn.data.database.FlipCard
import com.example.imagyn.ui.cardUtils.FlipCardUi

@OptIn(ExperimentalSharedTransitionApi::class, ExperimentalMaterial3Api::class)
@Composable
fun FlipCardPlayScreenUI(
    flipCard: FlipCard,
    onCrossButtonClick: () -> Unit,
    onSkipNextButtonClick: () -> Unit,
    onSkipPreviousButtonClick: () -> Unit,
    canSkipToNext: Boolean,
    canSkipToPrev: Boolean,
    animatedVisibilityScope: AnimatedContentScope,
    sharedTransitionScope: SharedTransitionScope,
    onExpandClick: () -> Unit
) {
    var isFront by rememberSaveable {
        mutableStateOf(true)
    }
    var isBlurred by rememberSaveable {
        mutableStateOf(false)
    }
    var isTextOverflow by rememberSaveable {
        mutableStateOf(false)
    }
    val rotationValue by animateFloatAsState(
        targetValue = if (!isFront) 0f else 360f,
        label = "rotation"
    )
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
            verticalArrangement = Arrangement.SpaceEvenly,
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
        ) {
            if (isFront && (canSkipToNext || canSkipToPrev)) {
                Row(
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    if (canSkipToPrev) {
                        IconButton(onClick = onSkipPreviousButtonClick) {
                            Icon(
                                painter = painterResource(id = R.drawable.double_arrow_left),
                                contentDescription = "Skip to Previous",
                                tint = Color.White
                            )
                        }
                    } else {
                        Spacer(modifier = Modifier.width(2.dp))
                    }
                    if (canSkipToNext) {
                        IconButton(onClick = onSkipNextButtonClick) {
                            Icon(
                                painter = painterResource(id = R.drawable.double_arrow_right),
                                contentDescription = "Skip to Next",
                                tint = Color.White
                            )
                        }
                    } else {
                        Spacer(modifier = Modifier.width(12.dp))
                    }
                }
            } else {
                if (!isFront && isTextOverflow) IconButton(
                    onClick = onExpandClick, modifier = Modifier
                        .align(Alignment.End)
                        .padding(end = 8.dp)
                ) {
                    Icon(
                        painterResource(R.drawable.fullscreen_portrait),
                        tint = Color.White,
                        contentDescription = "Expanded View"
                    )
                }
                else {
                    Spacer(Modifier.padding(8.dp))
                }
            }
            with(sharedTransitionScope) {
                FlipCardUi(
                    content = if (isFront) flipCard.front else flipCard.back,
                    cardColorValue = flipCard.colorValue,
                    modifier = Modifier
                        .clickable {
                            isFront = !isFront
                            if (isBlurred) isBlurred = false
                        }
                        .graphicsLayer { this.rotationY = rotationValue }
                        .then(
                            if (isBlurred) Modifier.blur(6.dp) else Modifier
                        )
                        .sharedElement(
                            rememberSharedContentState(key = flipCard.cardId),
                            animatedVisibilityScope = animatedVisibilityScope
                        ),
                    onTextOverflow = { isOverflow -> isTextOverflow = isOverflow }
                )
            }
            if (isFront && !isBlurred) {
                TextButton(
                    onClick = {
                        isFront = false
                        isBlurred = true
                    },
                    colors = ButtonColors(
                        containerColor = Color.White,
                        disabledContainerColor = Color.White,
                        contentColor = Color.Black,
                        disabledContentColor = Color.Black
                    ),
                    shape = RoundedCornerShape(8.dp)
                ) {
                    Text(text = "Take Blurry Hint", style = MaterialTheme.typography.titleLarge)
                }
            } else {
                Spacer(modifier = Modifier.size(48.dp))
            }
        }
    }
}


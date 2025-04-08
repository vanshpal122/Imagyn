package com.example.imagyn.ui.addEditScreen

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.text.BasicTextField
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
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.PathEffect
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.imagyn.R
import com.example.imagyn.ui.cardUtils.FlipCardUi
import com.example.imagyn.ui.theme.ImagynTheme
import com.example.imagyn.ui.theme.displayFontFamily
import kotlin.math.roundToInt

//import androidx.compose.foundation.BorderStroke
//import androidx.compose.foundation.Canvas
//import androidx.compose.foundation.background
//import androidx.compose.foundation.border
//import androidx.compose.foundation.gestures.detectDragGestures
//import androidx.compose.foundation.layout.fillMaxWidth
//import androidx.compose.foundation.layout.wrapContentHeight
//import androidx.compose.foundation.shape.CircleShape
//import androidx.compose.material3.TextFieldColors
//import androidx.compose.ui.draw.drawWithContent
//import androidx.compose.ui.graphics.Brush
//import androidx.compose.ui.graphics.RectangleShape
//import androidx.compose.ui.input.pointer.pointerInput
//import androidx.compose.ui.layout.onGloballyPositioned

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddEditFlipCardScreen(
    onSaveButtonClick: (String, String) -> Unit,
    onCancelButtonClick: () -> Unit,
    colorValue: Long,
    modifier: Modifier = Modifier,
    frontText: String,
    backText: String,
    changeFrontText: (String) -> Unit,
    changeBackText: (String) -> Unit
) {
    var isFront by rememberSaveable {
        mutableStateOf(true)
    }
    var isFrontEditing by rememberSaveable {
        mutableStateOf(true)
    }

    var isBackEditing by rememberSaveable {
        mutableStateOf(true)
    }

    val rotationValue by animateFloatAsState(
        targetValue = if (!isFront) 0f else 360f,
        label = "rotation"
    )
    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                title = { },
                navigationIcon = {
                    TextButton(onClick = onCancelButtonClick) {
                        Text(text = "Cancel", style = MaterialTheme.typography.titleLarge)
                    }
                },
                actions = {
                    TextButton(onClick = { onSaveButtonClick(frontText, backText) }) {
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
        Column(
            verticalArrangement = Arrangement.SpaceEvenly,
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
        ) {
            Text(
                text = if (isFront) "Front" else "Back",
                style = MaterialTheme.typography.displaySmall,
                color = Color.White
            )
            Row(
                horizontalArrangement = Arrangement.SpaceEvenly,
                verticalAlignment = Alignment.CenterVertically
            ) {
                IconButton(onClick = {
                    isFront = true
                    isBackEditing = false
                }) {
                    if (!isFront) Icon(
                        painter = painterResource(id = R.drawable.flip),
                        contentDescription = "Flip Back",
                        tint = Color.White
                    )
                }
                Box(modifier = Modifier, contentAlignment = Alignment.TopCenter) {
                    FlipCardUi(
                        content = if (isFront && !isFrontEditing) frontText else if (!isFront && !isBackEditing) backText else "",
                        cardColorValue = colorValue,
                        modifier = Modifier
                            .clickable {
                                if (isFront) isFrontEditing = !isFrontEditing else isBackEditing =
                                    !isBackEditing
                            }
                            .graphicsLayer {
                                this.rotationY = rotationValue
                            }
                            .padding(start = 16.dp, end = 16.dp)
                    )
                    if ((isFront && isFrontEditing) || (!isFront && isBackEditing)) {
                        ResizableDraggableTextField(
                            textValue = if (isFront) frontText else backText,
                            onChangeTextValue = {
                                if (isFront) changeFrontText(it) else changeBackText(
                                    it
                                )
                            },
                            modifier = Modifier
                        )
                    }
                }
                IconButton(onClick = {
                    isFront = false
                    isFrontEditing = false
                }) {
                    if (isFront) Icon(
                        painter = painterResource(id = R.drawable.flip),
                        contentDescription = "Flip Back",
                        tint = Color.White
                    )
                }
            }
//            TextButton(onClick = { isEditing = true }) {
//                Text(text = "Insert Text", style = MaterialTheme.typography.titleLarge)
//            }
        }
    }
}


@Composable
fun ResizableDraggableTextField(
    textValue: String,
    onChangeTextValue: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    var position by remember { mutableStateOf(Offset(4f, 200f)) }
    var size by remember { mutableStateOf(Size(168f, 112f)) }
//    var isResizing by remember { mutableStateOf(false) }
//    var lastTouchPosition by remember { mutableStateOf(Offset.Zero) }
//    val maxWidth = 236.dp.value
//    val maxHeight = 560.dp.value

    Box(
        modifier = modifier
            .offset { IntOffset(position.x.roundToInt(), position.y.roundToInt()) }
            .size(size.width.dp, size.height.dp)
            .borderDotted(width = 2.dp, color = Color.Gray)
//            .pointerInput(Unit) {
//                detectDragGestures(
//                    onDragStart = {
//                        lastTouchPosition = it
//                        isResizing = it.x > size.width - 40 && it.y > size.height - 40
//                    },
//                    onDrag = { change, dragAmount ->
//                        change.consume()
//                        if (isResizing) {
//                            size = Size(
//                                (size.width + dragAmount.x).coerceAtLeast(50f).coerceAtMost(200f),
//                                (size.height + dragAmount.y).coerceAtLeast(30f).coerceAtMost(100f)
//                            )
//                        } else {
//                            val newX = (position.x + dragAmount.x).coerceIn(0f, maxWidth - size.width)
//                            val newY = (position.y + dragAmount.y).coerceIn(0f, maxHeight - size.height)
//                            position = Offset(newX, newY)
//                        }
//                    },
//                    onDragEnd = { isResizing = false }
//                )
//            }
    )
    {
        BasicTextField(
            value = textValue,
            onValueChange = onChangeTextValue,
            modifier = Modifier
                .fillMaxSize()
                .padding(8.dp),
            textStyle = TextStyle(fontSize = 28.sp, fontFamily = displayFontFamily),
            decorationBox = @Composable { innerTextField ->
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.TopStart
                ) {
                    if (textValue.isEmpty()) {
                        Text(
                            text = "Enter text here",
                            fontSize = 28.sp,
                            fontFamily = displayFontFamily,
                            color = Color.Gray
                        )
                    }
                    innerTextField()
                }
            }
        )

    }
}

fun Modifier.borderDotted(width: Dp, color: Color): Modifier = this.then(
    Modifier.drawBehind {
        val pathEffect = PathEffect.dashPathEffect(floatArrayOf(10f, 10f), 0f)
        drawRect(
            color = color,
            size = size,
            style = Stroke(width = width.toPx(), pathEffect = pathEffect)
        )
    }
)


@Preview
@Composable
fun AddEditFlipCardScreenPreview() {
    ImagynTheme {
        AddEditFlipCardScreen(
            onSaveButtonClick = { _, _ -> {} },
            onCancelButtonClick = {},
            colorValue = 0xFF248190,
            frontText = "",
            backText = "",
            changeFrontText = {},
            changeBackText = {}
        )
    }
}
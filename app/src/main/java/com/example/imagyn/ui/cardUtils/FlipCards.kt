package com.example.imagyn.ui.cardUtils

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardColors
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.imagyn.ui.theme.CardBlue
import com.example.imagyn.ui.theme.ImagynTheme
import com.example.imagyn.ui.theme.WhiteColor
import com.example.imagyn.ui.theme.displayFontFamily

@Composable
fun ChapterCardUi(
    content: String,
    modifier: Modifier = Modifier,
    cardColorValue: Long = 0xFF248190,
    textColorValue: Long = 0xFFFFFFFF,
    cardWidth: Dp = 144.dp,
    cardHeight: Dp = 172.dp,
    fontStyle: TextStyle = TextStyle(
        fontFamily = displayFontFamily,
        fontSize = 20.sp,
        lineHeight = 24.sp
    )
) {
    Box(
        contentAlignment = Alignment.Center,
        modifier = modifier
    ) {
        Card(
            colors = CardColors(
                containerColor = Color(cardColorValue),
                contentColor = WhiteColor,
                disabledContentColor = CardBlue,
                disabledContainerColor = Color.Black
            ),
            modifier = Modifier.size(width = cardWidth, height = cardHeight)
        ) {

        }
        Box(
            contentAlignment = Alignment.Center,
            modifier = Modifier
                .height(cardHeight - 72.dp)
                .width(cardWidth - 12.dp)
        ) {
            Text(
                text = content,
                color = Color(textColorValue),
                style = fontStyle,
                overflow = TextOverflow.Ellipsis,
                textAlign = TextAlign.Center,
            )
        }
    }
}

@Composable
fun SubjectCardUi(
    content: String,
    modifier: Modifier = Modifier
) {
    Box(contentAlignment = Alignment.Center, modifier = modifier.wrapContentSize()) {
        ChapterCardUi(
            content = content,
        )
        Row(modifier = Modifier.align(Alignment.TopEnd)) {
            Card(
                colors = CardColors(
                    contentColor = WhiteColor,
                    containerColor = WhiteColor,
                    disabledContentColor = WhiteColor,
                    disabledContainerColor = WhiteColor
                ),
                shape = RoundedCornerShape(1.dp),
                modifier = Modifier
                    .height(18.dp)
                    .width(12.dp)
            ) {
            }
            Spacer(modifier = Modifier.padding(start = 12.dp))
        }
    }
}

@Composable
fun FlipCardUi(
    content: String,
    cardColorValue: Long,
    modifier: Modifier = Modifier
) {
    ChapterCardUi(
        content = content,
        modifier = modifier,
        cardColorValue = cardColorValue,
        textColorValue = 0xFF000000,
        cardWidth = 236.dp,
        cardHeight = 340.dp,
        fontStyle = TextStyle(fontFamily = displayFontFamily, fontSize = 28.sp, lineHeight = 32.sp)
    )
}


@Preview
@Composable
fun SubjectCardUiPreview() {
    ImagynTheme {
        SubjectCardUi(content = "What is Operating System ?")
    }
}

@Preview
@Composable
fun FlipCardUiPreview() {
    ImagynTheme {
        FlipCardUi(content = "What is Operating System ?", cardColorValue = 0xFF90D24)
    }
}

@Preview
@Composable
fun ChapterCardPreview() {
    ImagynTheme {
        ChapterCardUi(
            content = "What is Operating System ?"
        )
    }
}
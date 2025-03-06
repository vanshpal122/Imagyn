package com.example.imagyn.ui

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardColors
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.imagyn.ui.theme.CardBlue
import com.example.imagyn.ui.theme.ImagynTheme
import com.example.imagyn.ui.theme.WhiteColor

@Composable
fun ChapterCardUi(
    content: String,
    onClick: () -> Unit,
    cardColorValue: Long = 0xFF248190,
    textColorValue: Long = 0xFFFFFFFF,
    cardWidth: Dp = 144.dp,
    cardHeight: Dp = 172.dp
) {
    Box(
        contentAlignment = Alignment.Center,
        modifier = Modifier.clickable { onClick() }
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
                style = MaterialTheme.typography.displaySmall,
                overflow = TextOverflow.Ellipsis,
                fontSize = 20.sp,
                lineHeight = 24.sp,
                textAlign = TextAlign.Center,
            )
        }
    }
}

@Composable
fun SubjectCardUi(content: String, onClick: () -> Unit) {
    Box(contentAlignment = Alignment.TopEnd) {
        ChapterCardUi(content = content, onClick = onClick)
        Row {
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
                Text(text = "  ") //2Spaces
            }
            Spacer(modifier = Modifier.width(12.dp))
        }
    }
}

@Composable
fun FlipCardUi(content: String, cardColorValue: Long, onClick: () -> Unit) {
    ChapterCardUi(
        content = content,
        cardWidth = 234.dp,
        cardHeight = 340.dp,
        onClick = onClick,
        cardColorValue = cardColorValue,
        textColorValue = 0xFF000000,
    )
}

@Preview
@Composable
fun SubjectCardUiPreview() {
    ImagynTheme {
        SubjectCardUi(content = "What is Operating System ?") {}
    }
}
@Preview
@Composable
fun FlipCardUiPreview() {
    ImagynTheme {
        FlipCardUi(content = "What is Operating System ?", cardColorValue = 0xFF90D24, onClick = {})
    }
}

@Preview
@Composable
fun ChapterCardPreview() {
    ImagynTheme {
        ChapterCardUi(content = "What is Operating System ?", onClick = {})
    }
}
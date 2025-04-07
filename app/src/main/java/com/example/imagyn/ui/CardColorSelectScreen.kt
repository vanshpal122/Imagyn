package com.example.imagyn.ui

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.LargeTopAppBar
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBarColors
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.imagyn.ui.cardUtils.ChapterCardUi
import com.example.imagyn.ui.theme.ImagynTheme

val colors: List<Long> =
    listOf(0xFFFFFFFF, 0xFF904D24, 0xFF4A40C1, 0xFFC14042, 0xFF3F2C2C, 0xFF248190)


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CardColorSelectScreen(
    modifier: Modifier = Modifier,
    onNavigateBack: () -> Unit,
    onColorSelected: (Long) -> Unit
) {
    Scaffold(
        topBar = {
            LargeTopAppBar(
                title = {
                    Row(
                        horizontalArrangement = Arrangement.Center,
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text(text = "Choose Color")
                    }
                },
                navigationIcon = {
                    TextButton(onClick = onNavigateBack) {
                        Text(
                            text = "Cancel",
                            style = MaterialTheme.typography.titleLarge,
                            color = MaterialTheme.colorScheme.primary
                        )
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
        LazyVerticalGrid(
            columns = GridCells.Fixed(2),
            modifier = Modifier.padding(innerPadding),
            verticalArrangement = Arrangement.spacedBy(48.dp)
        ) {
            items(colors) { colorValue ->
                ChapterCardUi(
                    content = "",
                    modifier = Modifier.clickable {
                        onColorSelected(
                            colorValue
                        )
                    },
                    cardColorValue = colorValue
                )
            }
        }
    }
}

@Preview
@Composable
fun CardColorSelectionPreview() {
    ImagynTheme {
        CardColorSelectScreen(
            onNavigateBack = {},
            onColorSelected = { }
        )
    }
}
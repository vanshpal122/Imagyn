package com.example.imagyn.ui

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.toRoute
import com.example.imagyn.ui.addEditScreen.AddFlipCardScreen
import com.example.imagyn.ui.addEditScreen.EditFlipCardScreen
import com.example.imagyn.ui.chapterFlipCardsScreen.ChapterFlipCardsScreen
import com.example.imagyn.ui.homescreen.MainAppScreen
import com.example.imagyn.ui.reorderScreen.reorderlist.ReorderGrid
import com.example.imagyn.ui.subjectChaptersScreen.SubjectChaptersScreen
import kotlinx.serialization.Serializable

@Serializable
object HomeScreen

@Serializable
class SubjectChapterScreenDestination(
    val subjectID: Int, val subjectName: String
)

@Serializable
data class ChapterFlipCardScreenDestination(
    val chapterID: Int, val subjectID: Int?
)

@Serializable
data class AddCardScreenDestination(
    val colorValue: Long,
    val chapterName: String,
    val chapterID: Int?,
    val subjectID: Int?,
    val priority: Int,
    val isNewChapterRoute: Boolean
)

@Serializable
data class EditScreenDestination(
    val cardKey: Int
)

@Serializable
data class ColorSelectDestination(
    val chapterName: String,
    val chapterID: Int?,
    val subjectID: Int?,
    val priority: Int,
    val isNewChapterRoute: Boolean
)

@Serializable
data class ReorderDestination(
    val chapterID: Int
)

@Composable
fun ImagynNavHost(navController: NavHostController, modifier: Modifier = Modifier) {
    NavHost(navController = navController, startDestination = HomeScreen) {
        composable<HomeScreen> {
            MainAppScreen(onChapterCardClick = { chapterID ->
                navController.navigate(ChapterFlipCardScreenDestination(chapterID, null))
            }, onSubjectCardClick = { subjectID, subjectName ->
                navController.navigate(SubjectChapterScreenDestination(subjectID, subjectName))
            }, onAddCardsClick = { chapterName ->
                navController.navigate(
                    ColorSelectDestination(
                        chapterName = chapterName,
                        chapterID = null,
                        subjectID = null,
                        priority = 1,
                        isNewChapterRoute = true
                    )
                )
            }, modifier = modifier
            )
        }
        composable<SubjectChapterScreenDestination> { entry ->
            val arguments = entry.toRoute<SubjectChapterScreenDestination>()
            SubjectChaptersScreen(
                onChapterCardClick = { chapterID ->
                    navController.navigate(
                        ChapterFlipCardScreenDestination(
                            chapterID, arguments.subjectID
                        )
                    )
                },
                subjectID = arguments.subjectID,
                onNavigateBack = { navController.popBackStack() },
                modifier = modifier,
                subjectName = arguments.subjectName
            )
        }
        composable<ColorSelectDestination> { entry ->
            val arguments = entry.toRoute<ColorSelectDestination>()
            CardColorSelectScreen(
                onNavigateBack = { navController.popBackStack() },
                onColorSelected = { colorValue ->
                    navController.navigate(
                        AddCardScreenDestination(
                            colorValue,
                            arguments.chapterName,
                            arguments.chapterID,
                            arguments.subjectID,
                            arguments.priority,
                            arguments.isNewChapterRoute
                        )
                    )
                },
                modifier = modifier
            )
        }
        composable<AddCardScreenDestination> { entry ->
            val arguments = entry.toRoute<AddCardScreenDestination>()
            AddFlipCardScreen(colorValue = arguments.colorValue,
                modifier = modifier,
                priority = arguments.priority,
                chapterId = arguments.chapterID,
                subjectId = arguments.subjectID,
                chapterName = arguments.chapterName,
                onNavigate = {
                    if (arguments.isNewChapterRoute) navController.popBackStack(
                        HomeScreen, false
                    ) else if (arguments.chapterID != null) navController.popBackStack(
                        ChapterFlipCardScreenDestination(
                            chapterID = arguments.chapterID, subjectID = arguments.subjectID
                        ), false
                    )
                })
        }
        composable<ChapterFlipCardScreenDestination> { entry ->
            val arguments = entry.toRoute<ChapterFlipCardScreenDestination>()
            ChapterFlipCardsScreen(
                chapterID = arguments.chapterID,
                onEditClick = { cardKey -> navController.navigate(EditScreenDestination(cardKey)) },
                onBackButtonClick = { navController.popBackStack() },
                onAddNewButtonClick = { priority ->
                    navController.navigate(
                        ColorSelectDestination(
                            "",
                            arguments.chapterID,
                            arguments.subjectID,
                            priority = priority,
                            isNewChapterRoute = false
                        )
                    )
                },
                modifier = modifier,
                onReorderButtonClick = { navController.navigate(ReorderDestination(arguments.chapterID)) })
        }
        composable<EditScreenDestination> { entry ->
            val arguments = entry.toRoute<EditScreenDestination>()
            EditFlipCardScreen(keyCard = arguments.cardKey, modifier = modifier, onNavBack = {
                navController.popBackStack()
            })
        }

        composable<ReorderDestination> { entry ->
            val arguments = entry.toRoute<ReorderDestination>()
            ReorderGrid(arguments.chapterID, { navController.popBackStack() })
//            ReorderScreen(tempCardList = emptyList(), onBackClick = { navController.popBackStack() }) {
//
//            }
        }
    }
}
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
object SubjectChapterScreenDestination {
    var subjectID: Int = 0
        private set

    fun set(subjectID: Int) {
        this.subjectID = subjectID
    }
}

@Serializable
object ChapterFlipCardScreenDestination {
    var chapterID: Int = 0
        private set
    var subjectID: Int? = null
        private set

    fun set(chapterID: Int, subjectID: Int?) {
        this.chapterID = chapterID
        this.subjectID = subjectID
    }
}

@Serializable
object AddCardScreenDestination {
    var colorValue: Long = 0L
        private set
    var chapterName: String = ""
        private set
    var chapterID: Int? = null
        private set
    var subjectID: Int? = null
        private set
    var priority: Int = 0
        private set
    var isNewChapterRoute: Boolean = false
        private set


    fun set(
        colorValue: Long,
        chapterName: String,
        chapterID: Int?,
        subjectID: Int?,
        priority: Int,
        isNewChapterRoute: Boolean
    ) {
        this.colorValue = colorValue
        this.chapterName = chapterName
        this.chapterID = chapterID
        this.subjectID = subjectID
        this.priority = priority
        this.isNewChapterRoute = isNewChapterRoute
    }
}

@Serializable
object EditScreenDestination {
    var cardKey: Int = 0
        private set

    fun set(cardKey: Int) {
        this.cardKey = cardKey
    }
}


@Serializable
object ColorSelectDestination {
    var chapterName: String = ""
        private set
    var chapterID: Int? = null
        private set
    var subjectID: Int? = null
        private set
    var priority: Int = 0
        private set
    var isNewChapterRoute: Boolean = false
        private set

    fun set(
        chapterName: String,
        chapterID: Int?,
        subjectID: Int?,
        priority: Int,
        isNewChapterRoute: Boolean
    ) {
        this.chapterName = chapterName
        this.chapterID = chapterID
        this.subjectID = subjectID
        this.priority = priority
        this.isNewChapterRoute = isNewChapterRoute
    }


}

@Serializable
object ReorderDestination {
    var chapterID: Int = 0
        private set

    fun set(chapterID: Int) {
        this.chapterID = chapterID
    }
}


@Composable
fun ImagynNavHost(navController: NavHostController, modifier: Modifier = Modifier) {
    NavHost(navController = navController, startDestination = HomeScreen) {
        composable<HomeScreen> {
            MainAppScreen(
                onChapterCardClick = { chapterID ->
                    ChapterFlipCardScreenDestination.set(chapterID, null)
                    navController.navigate(ChapterFlipCardScreenDestination)
                }, onSubjectCardClick = { subjectID ->
                    SubjectChapterScreenDestination.set(subjectID)
                    navController.navigate(SubjectChapterScreenDestination)
                }, onAddCardsClick = { chapterName ->
                    ColorSelectDestination.set(chapterName, null, null, 1, true)
                    navController.navigate(ColorSelectDestination)
                }, modifier = modifier
            )
        }
        composable<SubjectChapterScreenDestination> { entry ->
            val arguments = entry.toRoute<SubjectChapterScreenDestination>()
            SubjectChaptersScreen(
                onChapterCardClick = { chapterID ->
                    ChapterFlipCardScreenDestination.set(chapterID, arguments.subjectID)
                    navController.navigate(ChapterFlipCardScreenDestination)
                },
                onNavigateBack = { navController.popBackStack() },
                modifier = modifier,
                onAddChapterClick = { chapterName ->
                    ColorSelectDestination.set(chapterName, null, arguments.subjectID, 1, true)
                    navController.navigate(ColorSelectDestination)
                }
            )
        }
        composable<ColorSelectDestination> { entry ->
            val arguments = entry.toRoute<ColorSelectDestination>()
            CardColorSelectScreen(
                onNavigateBack = { navController.popBackStack() },
                onColorSelected = { colorValue ->
                    AddCardScreenDestination.set(
                        colorValue,
                        arguments.chapterName,
                        arguments.chapterID,
                        arguments.subjectID,
                        arguments.priority,
                        arguments.isNewChapterRoute
                    )
                    navController.navigate(AddCardScreenDestination)
                },
                modifier = modifier
            )
        }
        composable<AddCardScreenDestination> { entry ->
            val arguments = entry.toRoute<AddCardScreenDestination>()
            AddFlipCardScreen(
                modifier = modifier,
                onNavigate = {
                    if (arguments.isNewChapterRoute) navController.popBackStack(
                        HomeScreen, false
                    ) else if (arguments.chapterID != null) {
                        ChapterFlipCardScreenDestination.set(
                            arguments.chapterID!!,
                            arguments.subjectID
                        )
                        navController.popBackStack(ChapterFlipCardScreenDestination, false)
                    }
                })
        }
        composable<ChapterFlipCardScreenDestination> { entry ->
            val arguments = entry.toRoute<ChapterFlipCardScreenDestination>()
            ChapterFlipCardsScreen(
                onEditClick = { cardKey ->
                    EditScreenDestination.set(cardKey)
                    navController.navigate(EditScreenDestination)
                },
                onBackButtonClick = { navController.popBackStack() },
                onAddNewButtonClick = { priority ->
                    ColorSelectDestination.set(
                        "",
                        arguments.chapterID,
                        arguments.subjectID,
                        priority,
                        false
                    )
                    navController.navigate(ColorSelectDestination)
                },
                modifier = modifier,
                onReorderButtonClick = {
                    ReorderDestination.set(arguments.chapterID)
                    navController.navigate(ReorderDestination)
                })
        }
        composable<EditScreenDestination> { entry ->
            entry.toRoute<EditScreenDestination>()
            EditFlipCardScreen(modifier = modifier, onNavBack = {
                navController.popBackStack()
            })
        }

        composable<ReorderDestination> {
            ReorderGrid({ navController.popBackStack() })
        }
    }
}
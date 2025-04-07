package com.example.imagyn.ui.homescreen


import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.BottomAppBar
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.Checkbox
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBarColors
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.Saver
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawWithContent
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.withTransform
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.imagyn.R
import com.example.imagyn.data.database.ChapterData
import com.example.imagyn.data.database.SubjectData
import com.example.imagyn.ui.AppViewModelProvider
import com.example.imagyn.ui.cardUtils.ChapterCardUi
import com.example.imagyn.ui.cardUtils.SubjectCardUi
import com.example.imagyn.ui.theme.ImagynTheme

@Composable
fun MainAppScreen(
    homeScreenViewModel: HomeScreenViewModel = viewModel(factory = AppViewModelProvider.Factory),
    onChapterCardClick: (Int) -> Unit,
    onSubjectCardClick: (Int, String) -> Unit,
    onAddCardsClick: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    val chapterList by homeScreenViewModel.chapterMapFlow.collectAsState()
    val subjectList by homeScreenViewModel.subjectMapFlow.collectAsState()

    var numberOfSubjectSelected by rememberSaveable {
        mutableIntStateOf(0)
    }
    MainAppScreenUI(
        chapterList = chapterList,
        subjectList = subjectList,
        modifier = modifier,
        onAddChapterClick = { onAddCardsClick(it) },
        onChapterCardClick = onChapterCardClick,
        onSubjectCardClick = { subjectId, subjectName ->
            onSubjectCardClick(
                subjectId,
                subjectName
            )
        },
        deleteSelectedSubjectsAndChapters = { homeScreenViewModel.deleteSelectedSubjectsAndChapters() },
        createSubject = { homeScreenViewModel.createSubject(it) },
        updateSubjectSelectedList = { subjectData, b, updateSelection ->
            homeScreenViewModel.updateSelectedSubject(
                subjectData = subjectData,
                isSelected = b,
                updateSelectionNumber = updateSelection
            )
        },
        updateChapterSelectedList = { chapterData, b, updateSelection ->
            homeScreenViewModel.updateSelectedChapter(
                chapterData = chapterData,
                isSelected = b,
                updateSelectionNumber = updateSelection
            )
        },
        selectAll = { selection, updateSelection ->
            homeScreenViewModel.selectAll(
                selection,
                updateSelection
            )
        },
        isMainScreen = true,
        onNavigateBack = {},
        title = "Imagyn",
        numberOfSubjectSelected = numberOfSubjectSelected,
        incrementSubjectSelected = { numberOfSubjectSelected++ },
        decrementSubjectSelected = { numberOfSubjectSelected-- }
    )
}

@Composable
@OptIn(ExperimentalMaterial3Api::class)
fun MainAppScreenUI(
    chapterList: Map<ChapterData, Boolean>,
    subjectList: Map<SubjectData, Boolean>,
    modifier: Modifier = Modifier,
    onAddChapterClick: (String) -> Unit,
    onChapterCardClick: (Int) -> Unit,
    onSubjectCardClick: (Int, String) -> Unit,
    deleteSelectedSubjectsAndChapters: (Int) -> Unit,
    createSubject: (String) -> Unit,
    updateSubjectSelectedList: (SubjectData, Boolean, () -> Unit) -> Unit,
    updateChapterSelectedList: (ChapterData, Boolean, () -> Unit) -> Unit,
    selectAll: (Boolean, () -> Unit) -> Unit,
    isMainScreen: Boolean,
    onNavigateBack: () -> Unit,
    title: String,
    numberOfSubjectSelected: Int,
    incrementSubjectSelected: () -> Unit,
    decrementSubjectSelected: () -> Unit
) {
    var isDropDown by rememberSaveable {
        mutableStateOf(false)
    }
    var isChapterAlertBoxShown by rememberSaveable {
        mutableStateOf(false)
    }

    var numberOfSelection by rememberSaveable {
        mutableIntStateOf(0)
    }

    var selectableState by rememberSaveable {
        mutableStateOf(false)
    }

    var isSubjectAlertBoxShown by rememberSaveable {
        mutableStateOf(false)
    }


    Scaffold(
        topBar = {
            if (!selectableState) {
                CenterAlignedTopAppBar(
                    navigationIcon = {
                        if (!isMainScreen) {
                            IconButton(onClick = onNavigateBack) {
                                Icon(
                                    imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                                    contentDescription = null,
                                    tint = Color.White
                                )
                            }
                        }
                    },
                    title = { Text(text = title) },
                    actions = {
                        if (chapterList.isNotEmpty() || subjectList.isNotEmpty()) {
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
                                            text = "Select",
                                            style = MaterialTheme.typography.labelLarge
                                        )
                                    }, onClick = {
                                        selectableState = true
                                        isDropDown = false
                                    })
                                }
                            }
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
            } else {
                CenterAlignedTopAppBar(
                    title = {
                        Text(text = "$numberOfSelection selected")
                    },
                    actions = {
                        TextButton(onClick = {
                            selectableState = false
                            selectAll(false) { numberOfSelection = 0 }
                        }) {
                            Text(text = "Cancel")
                        }
                    },
                    navigationIcon = {
                        TextButton(
                            onClick = {
                                deleteSelectedSubjectsAndChapters(numberOfSelection)
                                selectableState = false
                                selectAll(false) { numberOfSelection = 0 }
                            },
                            enabled = numberOfSelection > 0
                        ) {
                            Text(text = "Delete")
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
            }
        },
        bottomBar = {
            AnimatedVisibility(selectableState) {
                BottomAppBar(
                    actions = {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = if (isMainScreen) Arrangement.SpaceBetween else Arrangement.Center,
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(8.dp)
                        ) {
                            Column(
                                verticalArrangement = Arrangement.Center,
                                horizontalAlignment = Alignment.CenterHorizontally,
                                modifier = Modifier.clickable {
                                    if (numberOfSelection < (chapterList.size + subjectList.size)) {
                                        selectAll(true) {
                                            numberOfSelection = chapterList.size + subjectList.size
                                        }
                                    } else {
                                        selectAll(false) { numberOfSelection = 0 }
                                    }
                                }
                            ) {
                                Icon(
                                    painter = painterResource(id = if (numberOfSelection == (chapterList.size + subjectList.size)) R.drawable.check_box else R.drawable.check_box_outline_blank),
                                    contentDescription = null,
                                    tint = Color.White
                                )
                                Text(
                                    text = "Select All",
                                    fontSize = 10.sp,
                                    color = Color.White
                                )
                            }
                            if (isMainScreen && numberOfSubjectSelected == 0) {
                                Column(
                                    verticalArrangement = Arrangement.Center,
                                    horizontalAlignment = Alignment.CenterHorizontally,
                                    modifier = Modifier
                                        .clickable { isSubjectAlertBoxShown = true }
                                ) {
                                    Icon(
                                        painter = painterResource(id = R.drawable.create_subject),
                                        modifier = Modifier,
                                        contentDescription = null,
                                        tint = Color.White
                                    )
                                    Text(
                                        text = "Create Subject",
                                        fontSize = 10.sp,
                                        color = Color.White,
                                        modifier = Modifier
                                    )
                                }
                            }
                        }
                    },
                    containerColor = Color(0xFF152022),
                    modifier = Modifier.height(64.dp)
                )
            }
        },
        containerColor = Color(0xFF152022)
    ) { innerPadding ->
        Box(
            modifier = Modifier
                .padding(innerPadding)
                .fillMaxSize()
        ) {
            LazyVerticalGrid(
                columns = GridCells.Adaptive(148.dp),
                verticalArrangement = Arrangement.spacedBy(48.dp),
                modifier = Modifier
                    .then(
                        if (!selectableState && isMainScreen) Modifier.drawWithContent {
                            drawContent()
                            withTransform({
                                translate(top = size.height / 2)
                                scale(1.0f, 0.13f)
                            }) {
                                drawArc(
                                    color = Color.Gray,
                                    startAngle = 180f,
                                    sweepAngle = 180f,
                                    useCenter = false
                                )
                            }
                        } else Modifier
                    )
                    .fillMaxSize()
            ) {
                items(subjectList.keys.toList()) { subject ->
                    Box(
                        modifier = Modifier
                            .wrapContentSize()
                    ) {
                        if (subject.subject != null) {
                            SubjectCardUi(
                                content = subject.subject,
                                modifier = Modifier.pointerInput(Unit) {
                                    detectTapGestures(
                                        onTap = {
                                            if (!selectableState) {
                                                onSubjectCardClick(subject.subjectID, subject.subject)
                                            } else {
                                                val toggleState = subjectList[subject]
                                                updateSubjectSelectedList(
                                                    subject,
                                                    !(toggleState!!)
                                                ) {
                                                    if (toggleState) {
                                                        numberOfSelection--
                                                        decrementSubjectSelected()
                                                    } else {
                                                        numberOfSelection++
                                                        incrementSubjectSelected()
                                                    }
                                                }
                                            }
                                        },
                                        onLongPress = {
                                            selectableState = true
                                            val toggleState = subjectList[subject]
                                            updateSubjectSelectedList(
                                                subject,
                                                !(toggleState!!)
                                            ) {
                                                if (toggleState) {
                                                    numberOfSelection--
                                                    decrementSubjectSelected()
                                                } else {
                                                    numberOfSelection++
                                                    incrementSubjectSelected()
                                                }
                                            }
                                        }
                                    )
                                }
                            )
                            if (selectableState) {
                                subjectList[subject]?.let {
                                    Checkbox(
                                        checked = it,
                                        onCheckedChange = { select ->
                                            updateSubjectSelectedList(
                                                subject,
                                                select
                                            ) {
                                                if (select) {
                                                    numberOfSelection++
                                                    incrementSubjectSelected()
                                                } else {
                                                    numberOfSelection--
                                                    decrementSubjectSelected()
                                                }
                                            }
                                        },
                                        modifier = Modifier.align(
                                            Alignment.TopEnd
                                        )
                                    )
                                }
                            }
                        }

                    }
                }
                items(chapterList.keys.toList()) { chapter ->
                    Box(modifier = Modifier.wrapContentSize()) {
                        ChapterCardUi(
                            content = chapter.chapter,
                            modifier = Modifier.pointerInput(Unit) {
                                detectTapGestures (
                                    onTap = {
                                        if (!selectableState) {
                                            onChapterCardClick(chapter.chapterID)
                                        } else {
                                            val currentToggle = chapterList[chapter]
                                            updateChapterSelectedList(
                                                chapter,
                                                !(currentToggle)!!
                                            ) { if (currentToggle) numberOfSelection-- else numberOfSelection++ }
                                        }
                                    },
                                    onLongPress = {
                                        selectableState = true
                                        val currentToggle = chapterList[chapter]
                                        updateChapterSelectedList(
                                            chapter,
                                            !(currentToggle)!!
                                        ) { if (currentToggle) numberOfSelection-- else numberOfSelection++ }
                                    }
                                )
                            },
                        )
                        if (selectableState) {
                            chapterList[chapter]?.let {
                                Checkbox(
                                    checked = it,
                                    onCheckedChange = { select ->
                                        updateChapterSelectedList(
                                            chapter,
                                            select
                                        ) { if (select) numberOfSelection++ else numberOfSelection-- }
                                    },
                                    modifier = Modifier.align(
                                        Alignment.TopEnd
                                    )
                                )
                            }
                        }
                    }
                }
            }
            if (isChapterAlertBoxShown) {
                val alertBoxState = rememberAlertBoxState()
                CustomiseAlertDialogBox(
                    title = "Chapter Name",
                    confirmTitle = "Add Cards",
                    alertBoxState = alertBoxState,
                    dismissAlertBox = {
                        isChapterAlertBoxShown = false
                    },
                    onConfirmButtonClick = {
                        isChapterAlertBoxShown = false
                        onAddChapterClick(it)
                    }
                )
            }
            if (isSubjectAlertBoxShown) {
                val alertBoxState = rememberAlertBoxState()
                CustomiseAlertDialogBox(
                    title = "Subject Name",
                    confirmTitle = "Create",
                    alertBoxState = alertBoxState,
                    dismissAlertBox = {
                        isSubjectAlertBoxShown = false
                        selectableState = false
                    },
                    onConfirmButtonClick = {
                        isSubjectAlertBoxShown = false
                        selectableState = false
                        createSubject(it)
                    }
                )
            }
            if (!selectableState && isMainScreen) {
                IconButton(
                    onClick = { isChapterAlertBoxShown = true },
                    modifier = Modifier
                        .align(Alignment.BottomCenter)
                        .width(200.dp)
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.Center
                    ) {
                        Icon(
                            painter = painterResource(id = R.drawable.add_chapter_icon),
                            contentDescription = null
                        )
                        Text(text = "New Chapter", style = MaterialTheme.typography.titleLarge)
                    }
                }
            }
        }
    }
}

@Composable
fun rememberAlertBoxState(
    initialInput: String = ""
): AlertBoxState {
    return rememberSaveable(
        initialInput,
        saver = AlertBoxState.saver()
    ) {
        AlertBoxState(initialInput)
    }
}

class AlertBoxState(
    initialInput: String,
) {
    var textInput by mutableStateOf(initialInput)
        private set

    fun changeInput(value: String) {
        textInput = value
    }

    companion object {
        fun saver(): Saver<AlertBoxState, *> = Saver(
            save = {
                with(TextFieldValue.Saver) { save(TextFieldValue(it.textInput)) }
            },
            restore = {
                TextFieldValue.Saver.restore(it)?.let { input ->
                    AlertBoxState(input.text)
                }
            }
        )
    }
}

@Composable
fun CustomiseAlertDialogBox(
    title: String,
    confirmTitle: String,
    alertBoxState: AlertBoxState,
    dismissAlertBox: () -> Unit,
    onConfirmButtonClick: (String) -> Unit
) {
    AlertDialog(
        title = { Text(text = title, style = MaterialTheme.typography.titleLarge) },
        dismissButton = {
            Row(
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.fillMaxWidth()
            ) {
                Button(
                    onClick = dismissAlertBox,
                    shape = RoundedCornerShape(8.dp),
                    colors = ButtonDefaults.buttonColors()
                        .copy(contentColor = Color.Black, containerColor = Color.Transparent),
                    border = BorderStroke(2.dp, Color.Black)
                ) {
                    Text(
                        text = "Cancel",
                        style = MaterialTheme.typography.titleMedium,
                        modifier = Modifier
                    )
                }
                Button(
                    onClick = { onConfirmButtonClick(alertBoxState.textInput) },
                    enabled = alertBoxState.textInput.isNotEmpty(),
                    shape = RoundedCornerShape(8.dp)
                ) {
                    Text(
                        text = confirmTitle,
                        style = MaterialTheme.typography.titleMedium,
                        modifier = Modifier
                    )
                }
            }
        },
        onDismissRequest = dismissAlertBox,
        confirmButton = {
        },
        text = {
            OutlinedTextField(
                value = alertBoxState.textInput,
                onValueChange = { alertBoxState.changeInput(it) },
                singleLine = true
            )
        }
    )
}

@Preview
@Composable
fun MainAppScreenPreview() {
    ImagynTheme {
        MainAppScreenUI(
            chapterList = mapOf(
                ChapterData(0, chapter = "HELLO", subjectID = null) to false,
                ChapterData(1, chapter = "HELLO", subjectID = null) to false,
                ChapterData(2, chapter = "HELLO", subjectID = null) to false
            ),
            subjectList = mapOf(
                SubjectData(3, subject = "OS") to false,
                SubjectData(4, subject = "OS") to false
            ),
            onAddChapterClick = {},
            onChapterCardClick = {},
            onSubjectCardClick = { _, _ -> },
            deleteSelectedSubjectsAndChapters = {},
            createSubject = {},
            updateSubjectSelectedList = { _, _, _ -> },
            updateChapterSelectedList = { _, _, _ -> },
            selectAll = { _, _ -> },
            isMainScreen = true,
            onNavigateBack = {},
            title = "Imagyn",
            numberOfSubjectSelected = 0,
            incrementSubjectSelected = {},
            decrementSubjectSelected = {}
        )
    }
}

//@Preview
//@Composable
//fun AlertBoxPreview() {
//    val alertBoxState = rememberAlertBoxState()
//    ImagynTheme {
//        CustomiseAlertDialogBox(
//            title = "Subject Name",
//            confirmTitle = "Add Cards",
//            alertBoxState = alertBoxState,
//            dismissAlertBox = { /*TODO*/ }) {
//
//        }
//    }
//}
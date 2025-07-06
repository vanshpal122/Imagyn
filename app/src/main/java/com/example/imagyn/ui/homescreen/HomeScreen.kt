package com.example.imagyn.ui.homescreen


import androidx.activity.compose.BackHandler
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.animateContentSize
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
import androidx.compose.foundation.lazy.grid.itemsIndexed
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
import androidx.compose.material3.OutlinedCard
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
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.Saver
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.withTransform
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.onSizeChanged
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
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
    modifier: Modifier = Modifier,
    homeScreenViewModel: HomeScreenViewModel = viewModel(factory = AppViewModelProvider.Factory),
    onChapterCardClick: (Int) -> Unit,
    onSubjectCardClick: (Int) -> Unit,
    onAddCardsClick: (String) -> Unit,
) {
    val chapterList by homeScreenViewModel.chapterFlow.collectAsState()
    val subjectList by homeScreenViewModel.subjectFlow.collectAsState()
    var numberOfSubjectSelected by rememberSaveable {
        mutableIntStateOf(0)
    }


    MainAppScreenUI(
        chapterList = chapterList,
        subjectList = subjectList,
        modifier = modifier,
        onAddChapterClick = { onAddCardsClick(it) },
        onChapterCardClick = onChapterCardClick,
        onSubjectCardClick = { subjectId ->
            onSubjectCardClick(subjectId)
        },
        deleteSelectedSubjectsAndChapters = { _, deselectAll ->
            homeScreenViewModel.deleteSelectedSubjectsAndChapters(
                deselectAll
            )
        },
        createSubject = { subjectName, deselectAll ->
            homeScreenViewModel.createSubject(
                subjectName,
                deselectAll
            )
        },
        updateSubjectSelectedList = { index, b, updateSelection ->
            homeScreenViewModel.updateSelectedSubject(
                index = index,
                isSelected = b,
                updateSelectionNumber = updateSelection
            )
        },
        updateChapterSelectedList = { index, b, updateSelection ->
            homeScreenViewModel.updateSelectedChapter(
                index = index,
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
        updateNumberOfSubjectSelected = {
            numberOfSubjectSelected = homeScreenViewModel.getNumberOfSubjectSelection()
        },
        updateSelectionNumber = homeScreenViewModel::updateNumberOfSelection,
        getChapterToggleStatus = { index -> homeScreenViewModel.getCurrentToggleStatusChapter(index) },
        getSubjectToggleStatus = { index -> homeScreenViewModel.getCurrentToggleStatusSubject(index) },
        numberOfSubjectSelected = numberOfSubjectSelected,
        removeChFromSub = {},
        renameSubject = { subjectName, deselectAll ->
            homeScreenViewModel.renameSubject(
                subjectName,
                deselectAll
            )
        },
        moveChToSubject = { subjectID, deselectAll ->
            homeScreenViewModel.moveSelectedChToSubject(
                subjectID,
                deselectAll
            )
        },
        renameCh = { chapterName, deselectAll ->
            homeScreenViewModel.renameCh(
                chapterName,
                deselectAll
            )
        },
        currentFocusedChapter = homeScreenViewModel.currentFocusedChapter,
        currentFocusedSubject = homeScreenViewModel.currentFocusedSubject
    )
}

@Composable
@OptIn(ExperimentalMaterial3Api::class)
fun MainAppScreenUI(
    chapterList: List<ChapterHomeItem>,
    subjectList: List<SubjectHomeItem>,
    modifier: Modifier = Modifier,
    onAddChapterClick: (String) -> Unit,
    onChapterCardClick: (Int) -> Unit,
    onSubjectCardClick: (Int) -> Unit,
    deleteSelectedSubjectsAndChapters: (Int, () -> Unit) -> Unit,
    createSubject: (String, () -> Unit) -> Unit,
    updateSubjectSelectedList: (Int, Boolean, () -> Unit) -> Unit,
    updateChapterSelectedList: (Int, Boolean, () -> Unit) -> Unit,
    selectAll: (Boolean, () -> Unit) -> Unit,
    isMainScreen: Boolean,
    onNavigateBack: () -> Unit,
    title: String,
    updateNumberOfSubjectSelected: () -> Unit,
    updateSelectionNumber: () -> Int,
    getChapterToggleStatus: (Int) -> Boolean,
    getSubjectToggleStatus: (Int) -> Boolean,
    numberOfSubjectSelected: Int,
    removeChFromSub: () -> Unit,
    renameSubject: (String, () -> Unit) -> Unit,
    moveChToSubject: (Int, () -> Unit) -> Unit,
    renameCh: (String, () -> Unit) -> Unit,
    currentFocusedChapter: ChapterData?,
    currentFocusedSubject: SubjectData?
) {
    var isMoreOptionsDropDown by rememberSaveable {
        mutableStateOf(false)
    }

    var isSubjectNameDropDown by rememberSaveable {
        mutableStateOf(false)
    }

    var isChapterAlertBoxShown by rememberSaveable {
        mutableStateOf(false)
    }

    var isRenameSubjectAlertBoxShown by rememberSaveable {
        mutableStateOf(false)
    }

    var isRenameChapterAlertBoxShown by rememberSaveable {
        mutableStateOf(false)
    }

    var isDeleteAlertBoxShown by rememberSaveable {
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

    var isMoveToSubjectDialogBoxVisible by rememberSaveable {
        mutableStateOf(false)
    }

    BackHandler(enabled = selectableState) {
        selectableState = false
        selectAll(false) {
            numberOfSelection = updateSelectionNumber()
            updateNumberOfSubjectSelected()
        }
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
                        if ((chapterList.isNotEmpty() || subjectList.isNotEmpty()) || (!isMainScreen)) {
                            IconButton(onClick = {
                                isMoreOptionsDropDown = !isMoreOptionsDropDown
                            }) {
                                Icon(
                                    imageVector = Icons.Default.MoreVert,
                                    contentDescription = "More Options"
                                )
                                DropdownMenu(
                                    expanded = isMoreOptionsDropDown,
                                    onDismissRequest = {
                                        isMoreOptionsDropDown = !isMoreOptionsDropDown
                                    }) {
                                    if (chapterList.isNotEmpty() || subjectList.isNotEmpty()) {
                                        DropdownMenuItem(text = {
                                            Text(
                                                text = "Select",
                                                style = MaterialTheme.typography.labelLarge
                                            )
                                        }, onClick = {
                                            selectableState = true
                                            isMoreOptionsDropDown = false
                                        })
                                    }
                                    if (!isMainScreen) {
                                        DropdownMenuItem(text = {
                                            Text(
                                                text = "Rename",
                                                style = MaterialTheme.typography.labelLarge
                                            )
                                        }, onClick = {
                                            isMoreOptionsDropDown = false
                                            isRenameSubjectAlertBoxShown = true
                                        })
                                    }
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
                            selectAll(false) {
                                numberOfSelection = updateSelectionNumber()
                                updateNumberOfSubjectSelected()
                            }
                        }) {
                            Text(text = "Cancel")
                        }
                    },
                    navigationIcon = {
                        TextButton(
                            onClick = {
                                isDeleteAlertBoxShown = true
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
                            horizontalArrangement = if ((isMainScreen && numberOfSubjectSelected == 0 && numberOfSelection > 0) ||
                                (!isMainScreen && numberOfSelection > 0) || (isMainScreen && numberOfSelection == 1)
                            ) Arrangement.SpaceBetween else Arrangement.Center,
                            modifier = Modifier
                                .fillMaxWidth()
                                .animateContentSize()
                                .padding(8.dp)
                        ) {
                            BottomNavItem(
                                title = "Select All",
                                icon = if (numberOfSelection == (chapterList.size + subjectList.size)) R.drawable.check_box else R.drawable.check_box_outline_blank
                            ) {
                                if (numberOfSelection < (chapterList.size + subjectList.size)) {
                                    selectAll(true) {
                                        numberOfSelection = updateSelectionNumber()
                                        updateNumberOfSubjectSelected()
                                    }
                                } else {
                                    selectAll(false) {
                                        numberOfSelection = updateSelectionNumber()
                                        updateNumberOfSubjectSelected()
                                    }
                                }
                            }
                            if (numberOfSelection == 1) {
                                BottomNavItem(
                                    title = "Rename",
                                    icon = R.drawable.edit_square
                                ) {
                                    if (numberOfSubjectSelected == 1) isRenameSubjectAlertBoxShown =
                                        true
                                    else isRenameChapterAlertBoxShown = true
                                }
                            }
                            if ((isMainScreen && numberOfSubjectSelected == 0 && numberOfSelection > 0) ||
                                (!isMainScreen && numberOfSelection > 0)
                            ) {
                                if (isMainScreen) {
                                    BottomNavItem(
                                        title = "Create Subject",
                                        icon = R.drawable.create_subject
                                    ) {
                                        isSubjectAlertBoxShown = true
                                    }
                                    if (subjectList.isNotEmpty()) {
                                        BottomNavItem(
                                            title = "Move To Subject",
                                            icon = R.drawable.movetosubject
                                        ) {
                                            isMoveToSubjectDialogBoxVisible = true
                                        }
                                    }
                                } else {
                                    BottomNavItem(
                                        title = "Move Out",
                                        icon = R.drawable.stack_off,
                                    ) {
                                        removeChFromSub()
                                        selectableState = false
                                        selectAll(false) {
                                            numberOfSelection = updateSelectionNumber()
                                        }
                                    }
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
                    .fillMaxSize()
            ) {
                itemsIndexed(
                    subjectList
                ) { index, subject ->
                    Box(
                        modifier = Modifier
                            .wrapContentSize()
                    ) {
                        subject.subjectData.subject?.let { subjectName ->
                            SubjectCardUi(
                                content = subjectName,
                                modifier = Modifier.pointerInput(Unit) {
                                    detectTapGestures(
                                        onTap = {
                                            if (!selectableState) {
                                                onSubjectCardClick(subject.subjectData.subjectID)
                                            } else {
                                                updateSubjectSelectedList(
                                                    index,
                                                    !getSubjectToggleStatus(index)
                                                ) {
                                                    updateNumberOfSubjectSelected()
                                                    numberOfSelection = updateSelectionNumber()
                                                }
                                            }
                                        },
                                        onLongPress = {
                                            selectableState = true
                                            updateSubjectSelectedList(
                                                index,
                                                !getSubjectToggleStatus(index)
                                            ) {
                                                updateNumberOfSubjectSelected()
                                                numberOfSelection = updateSelectionNumber()
                                            }
                                        }
                                    )
                                }
                            )
                        }
                        if (selectableState) {
                            Checkbox(
                                checked = subject.isSelected,
                                onCheckedChange = { select ->
                                    updateSubjectSelectedList(
                                        index,
                                        select
                                    ) {
                                        numberOfSelection = updateSelectionNumber()
                                        updateNumberOfSubjectSelected()
                                    }
                                },
                                modifier = Modifier.align(
                                    Alignment.TopEnd
                                )
                            )
                        }

                    }
                }
                itemsIndexed(
                    chapterList
                ) { index, chapter ->
                    Box(modifier = Modifier.wrapContentSize()) {
                        ChapterCardUi(
                            content = chapter.chapter.chapter,
                            modifier = Modifier.pointerInput(Unit) {
                                detectTapGestures(
                                    onTap = {
                                        if (!selectableState) {
                                            onChapterCardClick(chapter.chapter.chapterID)
                                        } else {
                                            updateChapterSelectedList(
                                                index,
                                                !getChapterToggleStatus(index)
                                            ) { numberOfSelection = updateSelectionNumber() }
                                        }
                                    },
                                    onLongPress = {
                                        selectableState = true
                                        updateChapterSelectedList(
                                            index,
                                            !getChapterToggleStatus(index)
                                        ) { numberOfSelection = updateSelectionNumber() }
                                    }
                                )
                            },
                            onTextOverflow = {},
                        )
                        if (selectableState) {
                            Checkbox(
                                checked = chapter.isSelected,
                                onCheckedChange = { select ->
                                    updateChapterSelectedList(
                                        index,
                                        select
                                    ) { numberOfSelection = updateSelectionNumber() }
                                },
                                modifier = Modifier.align(
                                    Alignment.TopEnd
                                )
                            )
                        }
                    }
                }
            }
            if (isChapterAlertBoxShown) {
                val alertBoxState = rememberAlertBoxState()
                CustomiseAlertInputDialogBox(
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
            if (isMoveToSubjectDialogBoxVisible) {
                var currentSubject = subjectList.first()
                val density = LocalDensity.current
                var dropDownWidthDp by remember { mutableStateOf(0.dp) }
                CustomiseAlertDialogBox(
                    title = "Move to Subject",
                    confirmTitle = "Move In",
                    text = {
                        OutlinedCard(
                            onClick = { isSubjectNameDropDown = !isSubjectNameDropDown },
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(48.dp)
                        ) {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.SpaceBetween,
                                modifier = Modifier
                                    .fillMaxSize()
                                    .onSizeChanged {
                                        with(density) {
                                            dropDownWidthDp = it.width.toDp()
                                        }
                                    }
                            ) {
                                currentSubject.subjectData.subject?.let {
                                    Text(
                                        text = it,
                                        modifier = Modifier.padding(4.dp)
                                    )
                                }
                                Icon(
                                    painter = painterResource(if (isSubjectNameDropDown) R.drawable.arrow_drop_up else R.drawable.arrow_drop_down),
                                    contentDescription = "Dropdown"
                                )
                            }
                            DropdownMenu(
                                expanded = isSubjectNameDropDown,
                                onDismissRequest = {
                                    isSubjectNameDropDown = !isSubjectNameDropDown
                                },
                                modifier = Modifier.width(dropDownWidthDp)
                            ) {
                                subjectList.forEach {
                                    DropdownMenuItem(
                                        text = {
                                            it.subjectData.subject?.let { it1 ->
                                                Text(
                                                    text = it1,
                                                    style = MaterialTheme.typography.labelLarge
                                                )
                                            }
                                        }, onClick = {
                                            currentSubject = it
                                            isSubjectNameDropDown = false
                                        },
                                        modifier = Modifier.fillMaxWidth()
                                    )
                                }
                            }
                        }
                    },
                    dismissAlertBox = {
                        isMoveToSubjectDialogBoxVisible = false
                        selectableState = false
                        selectAll(false) {
                            numberOfSelection = updateSelectionNumber()
                            updateNumberOfSubjectSelected()
                        }
                    },
                    onConfirmButtonClick = {
                        isMoveToSubjectDialogBoxVisible = false
                        moveChToSubject(currentSubject.subjectData.subjectID) {
                            selectAll(false) {
                                numberOfSelection = updateSelectionNumber()
                            }
                        }
                        selectableState = false
                    },
                    isConfirmButtonEnabled = true
                )
            }
            if (isSubjectAlertBoxShown) {
                val alertBoxState = rememberAlertBoxState()
                CustomiseAlertInputDialogBox(
                    title = "Subject Name",
                    confirmTitle = "Create",
                    alertBoxState = alertBoxState,
                    dismissAlertBox = {
                        isSubjectAlertBoxShown = false
                        selectableState = false
                        selectAll(false) {
                            numberOfSelection = updateSelectionNumber()
                        }
                    },
                    onConfirmButtonClick = {
                        createSubject(it) {
                            selectAll(false) {
                                numberOfSelection = updateSelectionNumber()
                            }
                        }
                        isSubjectAlertBoxShown = false
                        selectableState = false
                    }
                )
            }
            if (isDeleteAlertBoxShown) {
                CustomiseAlertDialogBox(
                    title =
                        if (numberOfSelection == numberOfSubjectSelected) stringResource(R.string.delete_sub_title)
                        else if (numberOfSubjectSelected == 0) stringResource(R.string.delete_ch_title)
                        else stringResource(R.string.delete_sub_ch_title),
                    confirmTitle = "Delete",
                    text = {
                        Text(
                            text =
                                if (numberOfSelection == numberOfSubjectSelected) stringResource(R.string.deleting_subject_state)
                                else if (numberOfSubjectSelected == 0) stringResource(R.string.delete_chapter_state)
                                else stringResource(R.string.delete_sub_ch_state)
                        )
                    },
                    dismissAlertBox = {
                        isDeleteAlertBoxShown = false
                        selectableState = false
                        selectAll(false) {
                            numberOfSelection = updateSelectionNumber()
                            updateNumberOfSubjectSelected()
                        }
                    },
                    onConfirmButtonClick = {
                        isDeleteAlertBoxShown = false
                        deleteSelectedSubjectsAndChapters(numberOfSelection) {
                            selectAll(false) {
                                numberOfSelection = updateSelectionNumber()
                                updateNumberOfSubjectSelected()
                            }
                        }
                        selectableState = false
                    },
                    isConfirmButtonEnabled = true
                )
            }
            if (isRenameChapterAlertBoxShown) {
                val alertBoxState = rememberAlertBoxState(currentFocusedChapter?.chapter ?: "")
                CustomiseAlertInputDialogBox(
                    title = "Chapter Name",
                    confirmTitle = "Rename",
                    alertBoxState = alertBoxState,
                    dismissAlertBox = {
                        isRenameChapterAlertBoxShown = false
                        selectableState = false
                        selectAll(false) {
                            numberOfSelection = updateSelectionNumber()
                        }
                    },
                    onConfirmButtonClick = {
                        renameCh(it) {
                            selectAll(false) {
                                numberOfSelection = updateSelectionNumber()
                            }
                        }
                        isRenameChapterAlertBoxShown = false
                        selectableState = false
                    }
                )
            }
            if (isRenameSubjectAlertBoxShown) {
                val alertBoxState = rememberAlertBoxState(currentFocusedSubject?.subject ?: "")
                CustomiseAlertInputDialogBox(
                    title = "Subject Name",
                    confirmTitle = "Rename",
                    alertBoxState = alertBoxState,
                    dismissAlertBox = {
                        isRenameSubjectAlertBoxShown = false
                        selectableState = false
                        selectAll(false) {
                            numberOfSelection = updateSelectionNumber()
                            updateNumberOfSubjectSelected()
                        }
                    },
                    onConfirmButtonClick = {
                        renameSubject(it) {
                            selectAll(false) {
                                numberOfSelection = updateSelectionNumber()
                                updateNumberOfSubjectSelected()
                            }
                        }
                        selectableState = false
                        isRenameSubjectAlertBoxShown = false
                    }
                )
            }
            if (!selectableState) {
                Box(
                    modifier = Modifier
                        .align(Alignment.BottomCenter)
                        .fillMaxWidth()
                        .drawBehind {
                            withTransform(
                                { translate(0f, size.height) }
                            ) {
                                drawArc(
                                    color = Color.Gray,
                                    topLeft = Offset(0f, -size.height),
                                    startAngle = 0f,
                                    sweepAngle = -180f,
                                    useCenter = true
                                )
                            }
                        }
                ) {
                    Row(
                        verticalAlignment = Alignment.Bottom,
                        horizontalArrangement = Arrangement.Center,
                        modifier = Modifier.padding(4.dp).clickable {
                            isChapterAlertBoxShown = true
                        }.align(Alignment.Center)
                    ) {
                        Icon(
                            painter = painterResource(id = R.drawable.add_chapter_icon),
                            contentDescription = null
                        )
                        Text(
                            text = if (isMainScreen) "New Chapter" else "Add Chapter",
                            style = MaterialTheme.typography.titleLarge
                        )
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
fun CustomiseAlertInputDialogBox(
    title: String,
    confirmTitle: String,
    alertBoxState: AlertBoxState,
    dismissAlertBox: () -> Unit,
    onConfirmButtonClick: (String) -> Unit
) {
    CustomiseAlertDialogBox(
        title = title,
        confirmTitle = confirmTitle,
        text = {
            OutlinedTextField(
                value = alertBoxState.textInput,
                onValueChange = { alertBoxState.changeInput(it) },
                singleLine = true
            )
        },
        dismissAlertBox = dismissAlertBox,
        onConfirmButtonClick = { onConfirmButtonClick(alertBoxState.textInput) },
        isConfirmButtonEnabled = alertBoxState.textInput.isNotEmpty()
    )
}

@Composable
fun CustomiseAlertDialogBox(
    title: String,
    confirmTitle: String,
    text: @Composable (() -> Unit)?,
    dismissAlertBox: () -> Unit,
    onConfirmButtonClick: () -> Unit,
    isConfirmButtonEnabled: Boolean
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
                    onClick = onConfirmButtonClick,
                    enabled = isConfirmButtonEnabled,
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
        text = text
    )
}

@Composable
fun BottomNavItem(
    title: String,
    icon: Int,
    onClick: () -> Unit
) {
    Column(
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier
            .clickable {
                onClick()
            }
    ) {
        Icon(
            painter = painterResource(id = icon),
            modifier = Modifier,
            contentDescription = null,
            tint = Color.White
        )
        Text(
            text = title,
            fontSize = 10.sp,
            color = Color.White,
            modifier = Modifier
        )
    }
}

@Preview
@Composable
fun MainAppScreenPreview() {
    ImagynTheme {
        MainAppScreenUI(
            chapterList = listOf(
                ChapterHomeItem(ChapterData(0, chapter = "HELLO1", subjectID = null), false),
                ChapterHomeItem(ChapterData(1, chapter = "HELLO2", subjectID = null), false),
                ChapterHomeItem(ChapterData(2, chapter = "HELLO3", subjectID = null), false),
            ),
            subjectList = listOf(
                SubjectHomeItem(SubjectData(3, subject = "OS"), false),
                SubjectHomeItem(SubjectData(4, subject = "OS"), false)
            ),
            onAddChapterClick = {},
            onChapterCardClick = {},
            onSubjectCardClick = { },
            deleteSelectedSubjectsAndChapters = { _, _ -> },
            createSubject = { _, _ -> },
            updateSubjectSelectedList = { _, _, _ -> },
            updateChapterSelectedList = { _, _, _ -> },
            selectAll = { _, _ -> },
            isMainScreen = true,
            onNavigateBack = {},
            title = "Imagyn",
            updateNumberOfSubjectSelected = { },
            updateSelectionNumber = { 0 },
            getChapterToggleStatus = { false },
            getSubjectToggleStatus = { false },
            numberOfSubjectSelected = 0,
            removeChFromSub = {},
            renameSubject = { _, _ -> },
            moveChToSubject = { _, _ -> },
            renameCh = { _, _ -> },
            currentFocusedChapter = null,
            currentFocusedSubject = null
        )
    }
}

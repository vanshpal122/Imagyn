package com.example.imagyn.data.database

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.PrimaryKey

@Entity(
    tableName = "cards",
    foreignKeys = [
        ForeignKey(
            entity = SubjectData::class,
            parentColumns = ["subject_id"],
            childColumns = ["subject_id"],
            onDelete = ForeignKey.CASCADE,
            onUpdate = ForeignKey.CASCADE
        ),
        ForeignKey(
            entity = ChapterData::class,
            parentColumns = ["chapter_id"],
            childColumns = ["chapter_id"],
            onDelete = ForeignKey.CASCADE,
            onUpdate = ForeignKey.CASCADE
        )
    ]
)

data class FlipCard(
    @ColumnInfo(name = "card_id") @PrimaryKey(autoGenerate = true) val cardId: Int = 0,
    var front: String,
    var back: String,
    val priority: Int,
    @ColumnInfo(name = "color_value") val colorValue: Long,
    @ColumnInfo(name = "chapter_id") val chapterID: Int?,
    @ColumnInfo(name = "subject_id") val subjectID: Int?
)

@Entity(tableName = "subjects")
data class SubjectData(
    @ColumnInfo(name = "subject_id") @PrimaryKey(autoGenerate = true) val subjectID: Int = 0,
    val subject: String?,
)

@Entity(tableName = "chapters",
    foreignKeys = [
        ForeignKey(
            entity = SubjectData::class,
            parentColumns = ["subject_id"],
            childColumns = ["subject_id"],
            onDelete = ForeignKey.CASCADE,
            onUpdate = ForeignKey.CASCADE
        )
    ])
data class ChapterData(
    @ColumnInfo(name = "chapter_id") @PrimaryKey(autoGenerate = true) val chapterID: Int = 0,
    val chapter: String,
    @ColumnInfo(name = "subject_id") val subjectID: Int?
)
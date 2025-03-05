package com.example.imagyn.data.database

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "cards")
data class FlipCard(
    @PrimaryKey(autoGenerate = true) val key: Int = 0,
    val front: String,
    val back: String,
    val priority: Int,
    @ColumnInfo(name = "color_value") val colorValue: String,
    val chapter: String,
    val subject: String?
)
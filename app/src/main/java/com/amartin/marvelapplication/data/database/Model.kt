package com.amartin.marvelapplication.data.database

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity
data class Character(
    @PrimaryKey
    val id: Int,
    val name: String,
    val description: String,
    val resourceURI: String,
    val path: String,
    val extension: String
)

@Entity
data class Comic(
    @PrimaryKey
    val id: Int,
    val title: String,
    val description: String
)
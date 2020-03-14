package com.amartin.marvelapplication.data.database

import androidx.room.*

@Entity data class Character(
    @PrimaryKey
    val id: Int,
    val name: String,
    val description: String?,
    val path: String,
    val extension: String
)

@Entity data class Comic(
    @PrimaryKey
    val id: Int,
    val title: String,
    val description: String?,
    val path: String,
    val extension: String
)

@Entity(primaryKeys = ["characterId", "comicId"], indices = [Index(value = ["characterId"]), Index(value = ["comicId"])])
data class CharacterComicRelation(val characterId: Int, val comicId: Int)

data class CharacterWithComics(
    @Embedded var character: Character,
    @Relation(
        parentColumn = "id",
        entityColumn = "id",
        entity = Comic::class,
        associateBy = Junction(
            value = CharacterComicRelation::class,
            parentColumn = "characterId",
            entityColumn = "comicId"
        )
    ) var comics: List<Comic>
)
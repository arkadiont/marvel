package com.amartin.marvelapplication.data.mappers

import com.amartin.marvelapplication.data.database.Character
import com.amartin.marvelapplication.data.database.CharacterWithComics
import com.amartin.marvelapplication.data.database.Comic
import com.amartin.marvelapplication.data.model.CharacterComicData
import com.amartin.marvelapplication.data.model.CharacterData
import com.amartin.marvelapplication.data.model.ComicData
import com.amartin.marvelapplication.data.model.Thumbnail

fun Character.toCharacterData(): CharacterData = CharacterData(
    id, name, description?: "", "", Thumbnail(path, extension), listOf())

fun CharacterData.toCharacter(): Character = Character(
    id, name, description, thumbnail.path, thumbnail.extension)

fun Comic.toComicData(): ComicData = ComicData(
    id, title, description?: "", Thumbnail(path, extension), listOf())

fun ComicData.toComic(): Comic = Comic(
    id, title, description, thumbnail.path, thumbnail.extension)

fun CharacterComicData.toCharacterWithComics(): CharacterWithComics = CharacterWithComics(
    characterData.toCharacter(), comicData.map { it.toComic() }
)

fun CharacterWithComics.toCharacterComicData(): CharacterComicData = CharacterComicData(
    character.toCharacterData(), comics.map { it.toComicData() }
)
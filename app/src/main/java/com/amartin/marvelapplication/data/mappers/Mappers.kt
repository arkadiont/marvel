package com.amartin.marvelapplication.data.mappers

import com.amartin.marvelapplication.data.database.Character
import com.amartin.marvelapplication.data.database.CharacterWithComics
import com.amartin.marvelapplication.data.database.Comic
import com.amartin.marvelapplication.data.database.Url
import com.amartin.marvelapplication.data.model.*

fun Character.toCharacterData(urls: List<Url>): CharacterData = CharacterData(
    id, name, description?: "", "", Thumbnail(path, extension), urls.map { Url(it.type, it.url) }
)

fun CharacterData.toCharacter(): Character = Character(
    id, name, description, thumbnail.path, thumbnail.extension)

fun Comic.toComicData(): ComicData = ComicData(
    id, title, description?: "", Thumbnail(path, extension), listOf())

fun ComicData.toComic(): Comic = Comic(
    id, title, description, thumbnail.path, thumbnail.extension)

fun CharacterComicData.toCharacterWithComics(): CharacterWithComics = CharacterWithComics(
    characterData.toCharacter(),
    comicData.map { it.toComic() },
    characterData.urls.map { Url(0, characterData.id, it.type, it.url) })

fun CharacterWithComics.toCharacterComicData(): CharacterComicData = CharacterComicData(
    character.toCharacterData(urls), comics.map { it.toComicData() }
)
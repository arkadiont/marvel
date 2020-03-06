package com.amartin.marvelapplication.data.mappers

import com.amartin.marvelapplication.data.database.Character
import com.amartin.marvelapplication.data.model.CharacterData
import com.amartin.marvelapplication.data.model.Thumbnail

fun Character.toCharacterData(): CharacterData = CharacterData(
    id, name, description, resourceURI, Thumbnail(path, extension), listOf())

fun CharacterData.toCharacter(): Character = Character(
    id,
    name,
    description,
    resourceURI,
    thumbnail.path,
    thumbnail.extension)
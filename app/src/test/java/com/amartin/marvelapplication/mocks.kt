package com.amartin.marvelapplication

import com.amartin.marvelapplication.api.model.CharacterDataContainer
import com.amartin.marvelapplication.api.model.CharacterDataWrapper
import com.amartin.marvelapplication.api.model.ComicDataContainer
import com.amartin.marvelapplication.api.model.ComicDataWrapper
import com.amartin.marvelapplication.data.model.CharacterComicData
import com.amartin.marvelapplication.data.model.CharacterData
import com.amartin.marvelapplication.data.model.ComicData
import com.amartin.marvelapplication.data.model.Thumbnail

val mockedCharacter = CharacterData(1, "Iron Man", "Not available", "", Thumbnail("", ""), listOf())

fun getMockedCharacterWrapper(id: Int) =
    CharacterDataWrapper(200, "OK", CharacterDataContainer(0, 1, 1, 1, listOf(mockedCharacter.copy(id = id))))

val mockedComic = ComicData(1, "X-Men", "Not available", Thumbnail("", ""), listOf())

fun getMockedComicWrapper(id: Int) =
    ComicDataWrapper(200, "OK", ComicDataContainer(0, 1, 1, 1, listOf(mockedComic.copy(id = id))))

val mockCharacterComicData = CharacterComicData(mockedCharacter, listOf(mockedComic))
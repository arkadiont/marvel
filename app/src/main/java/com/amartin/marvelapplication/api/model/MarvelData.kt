package com.amartin.marvelapplication.api.model

import com.amartin.marvelapplication.data.model.CharacterData
import com.amartin.marvelapplication.data.model.ComicData

data class MarvelError(val code: Int, val status: String)

data class CharacterDataWrapper(
    val code: Int,
    val status: String,
    val data: CharacterDataContainer
)

data class CharacterDataContainer(
    val offset: Int,
    val limit: Int,
    val total: Int,
    val count: Int,
    val results: List<CharacterData>
)

data class ComicDataWrapper(
    val code: Int,
    val status: String,
    val data: ComicDataContainer
)

data class ComicDataContainer(
    val offset: Int,
    val limit: Int,
    val total: Int,
    val count: Int,
    val results: List<ComicData>
)
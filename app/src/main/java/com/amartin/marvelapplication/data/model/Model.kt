package com.amartin.marvelapplication.data.model

data class CharacterData(
    val id: Int,
    val name: String,
    val description: String,
    val resourceURI: String,
    val thumbnail: Thumbnail,
    val urls: List<Url>
)

data class CharacterComicData(
    val characterData: CharacterData,
    val comicData: List<ComicData>
)

data class Thumbnail(
    val path: String,
    val extension: String
)

data class Url(
    val type: String,
    val url: String
)

data class ComicData(
    val id: Int,
    val title: String,
    val description: String,
    val thumbnail: Thumbnail,
    val urls: List<Url>
)
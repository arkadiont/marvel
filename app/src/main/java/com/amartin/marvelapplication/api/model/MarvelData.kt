package com.amartin.marvelapplication.api.model

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

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

@Parcelize
data class CharacterData(
    val id: Int,
    val name: String,
    val description: String,
    val resourceURI: String,
    val thumbnail: Thumbnail,
    val urls: List<Url>
) : Parcelable

@Parcelize
data class Thumbnail(
    val path: String,
    val extension: String
) : Parcelable

@Parcelize
data class Url(
    val type: String,
    val url: String
) : Parcelable

// TODO reuse general typed class
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

@Parcelize
data class ComicData(
    val id: Int,
    val title: String,
    val description: String,
    val thumbnail: Thumbnail,
    val urls: List<Url>
) : Parcelable
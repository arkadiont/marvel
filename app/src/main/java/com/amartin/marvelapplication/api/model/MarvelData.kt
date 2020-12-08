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

/*  Scrapped  */
data class ScrappedElement(val type: String, val data: String) {
    override fun toString(): String {
        if (type != "") {
            return "<${type}>${data}</${type}>"
        }
        return data
    }
}

data class ScrappedPowerGrid(val label: String, val powerRating: Double)

data class PhysicalTraits(val eyeColor: String, val height: String, val weight: String, val hairColor: String, val gender: String)

data class ScrappedCharacter(val physicalTraits: PhysicalTraits?, val bioData: Map<String, String>)

data class Scrapped(val scrappedElementList: List<ScrappedElement>, val scrappedPowerGridList: List<ScrappedPowerGrid>, val scrappedCharacter: ScrappedCharacter?) {
    fun hasValues(): Boolean = scrappedCharacter != null || scrappedElementList.isNotEmpty() || scrappedPowerGridList.isNotEmpty()
}
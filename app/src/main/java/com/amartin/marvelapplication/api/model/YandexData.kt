package com.amartin.marvelapplication.api.model

data class Translation(val code: Int, val lang: String, val text: List<String>)

data class YandexError(val code: Int, val message: String)
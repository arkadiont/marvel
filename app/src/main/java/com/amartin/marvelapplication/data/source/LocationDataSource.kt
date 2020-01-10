package com.amartin.marvelapplication.data.source

interface LocationDataSource {
    suspend fun findLastRegionLanguage(): String?
}
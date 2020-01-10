package com.amartin.marvelapplication.data.repository

import com.amartin.marvelapplication.data.source.LocationDataSource
import com.amartin.marvelapplication.data.source.PermissionChecker

class RegionRepository(private val locationDataSource: LocationDataSource,
                       private val permissionChecker: PermissionChecker) {

    companion object {
        const val DEFAULT_REGION_LANGUAGE = "en"
    }

    suspend fun findLastRegionLanguage(): String {
        return if (permissionChecker.check(PermissionChecker.Permission.COARSE_LOCATION)) {
            locationDataSource.findLastRegionLanguage() ?: DEFAULT_REGION_LANGUAGE
        } else {
            DEFAULT_REGION_LANGUAGE
        }
    }
}
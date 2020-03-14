package com.amartin.marvelapplication

import com.amartin.marvelapplication.data.repository.RegionRepository
import com.amartin.marvelapplication.data.source.LocationDataSource
import com.amartin.marvelapplication.data.source.PermissionChecker
import com.amartin.marvelapplication.data.source.PermissionChecker.Permission.COARSE_LOCATION
import com.nhaarman.mockitokotlin2.whenever
import kotlinx.coroutines.runBlocking
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.Mock
import org.mockito.junit.MockitoJUnitRunner

@RunWith(MockitoJUnitRunner::class)
class RegionRepositoryTest {

    @Mock
    lateinit var locationDataSource: LocationDataSource

    @Mock
    lateinit var permissionChecker: PermissionChecker

    lateinit var regionRepository: RegionRepository

    @Before
    fun setup() {
        regionRepository = RegionRepository(locationDataSource, permissionChecker)
    }

    @Test
    fun `return default when coarse permission not granted`() {
        runBlocking {
            whenever(permissionChecker.check(COARSE_LOCATION)).thenReturn(false)
            val language = regionRepository.findLastRegionLanguage()
            assertEquals(RegionRepository.DEFAULT_REGION_LANGUAGE, language)
        }
    }

    @Test
    fun `return region from location data source when coarse permission granted`() {
        runBlocking {
            val language = "es"
            whenever(permissionChecker.check(COARSE_LOCATION)).thenReturn(true)
            whenever(locationDataSource.findLastRegionLanguage()).thenReturn(language)
            assertEquals(language, regionRepository.findLastRegionLanguage())
        }
    }

}
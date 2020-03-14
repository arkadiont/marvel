package com.amartin.marvelapplication

import com.amartin.marvelapplication.api.Result.Success
import com.amartin.marvelapplication.data.repository.MarvelRepository
import com.amartin.marvelapplication.data.source.LocalMarvelDataSource
import com.amartin.marvelapplication.data.source.RemoteMarvelDataSource
import com.nhaarman.mockitokotlin2.verify
import com.nhaarman.mockitokotlin2.whenever
import kotlinx.coroutines.runBlocking
import org.junit.Assert.*
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.Mock
import org.mockito.junit.MockitoJUnitRunner

@RunWith(MockitoJUnitRunner::class)
class MarvelRepositoryTest {

    @Mock
    lateinit var localDataSource: LocalMarvelDataSource

    @Mock
    lateinit var remoteMarvelDataSource: RemoteMarvelDataSource

    private lateinit var marvelRepository: MarvelRepository

    @Before
    fun setup() {
        marvelRepository = MarvelRepository(remoteMarvelDataSource, localDataSource)
    }

    @Test
    fun `get remote characters by id`() {
        runBlocking {
            val id = 5
            val character = Success(getMockedCharacterWrapper(id))
            whenever(remoteMarvelDataSource.getCharacter(id)).thenReturn(character)
            val result = marvelRepository.getCharacter(id)
            assertEquals(character, result)
        }
    }

    @Test
    fun `get remote comic by character id`() {
        runBlocking {
            val id = 5
            val comic = Success(getMockedComicWrapper(id))
            whenever(remoteMarvelDataSource.getComicsOfCharacter(id)).thenReturn(comic)
            val result = marvelRepository.getComicsOfCharacter(id)
            assertEquals(comic, result)
        }
    }

    @Test
    fun `save favourite character into database`() {
        runBlocking {
            val character = mockedCharacter.copy(id = 42)
            marvelRepository.saveFavouriteCharacter(character)
            verify(localDataSource).saveFavouriteCharacter(character)
        }
    }

    @Test
    fun `delete favourite character from database`() {
        runBlocking {
            val character = mockedCharacter.copy(id = 42)
            marvelRepository.deleteFavouriteCharacter(character)
            verify(localDataSource).deleteFavouriteCharacter(character)
        }
    }

    @Test
    fun `count favourite character from database`() {
        runBlocking {
            val id = 42
            marvelRepository.getCountFavouriteCharacter(id)
            verify(localDataSource).getCountFavouriteCharacter(id)
        }
    }
}
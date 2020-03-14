package com.amartin.marvelapplication.viewmodel

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.lifecycle.Observer
import com.amartin.marvelapplication.api.Result
import com.amartin.marvelapplication.api.TranslateService
import com.amartin.marvelapplication.api.model.Translation
import com.amartin.marvelapplication.common.Event
import com.amartin.marvelapplication.common.getUrl
import com.amartin.marvelapplication.data.model.Thumbnail
import com.amartin.marvelapplication.data.repository.MarvelRepository
import com.amartin.marvelapplication.data.repository.RegionRepository
import com.amartin.marvelapplication.getMockedCharacterWrapper
import com.amartin.marvelapplication.getMockedComicWrapper
import com.amartin.marvelapplication.mockedCharacter
import com.amartin.marvelapplication.mockedComic
import com.amartin.marvelapplication.ui.detail.DetailViewModel
import com.nhaarman.mockitokotlin2.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.runBlocking
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.ArgumentMatchers.anyInt
import org.mockito.Mock
import org.mockito.junit.MockitoJUnitRunner

@RunWith(MockitoJUnitRunner::class)
class DetailViewModelTest {

    private val characterId = 42

    @Mock
    lateinit var observerCharacterModel: Observer<DetailViewModel.UiCharacterModel>

    @Mock
    lateinit var observerComicModel: Observer<DetailViewModel.UiComicModel>

    @Mock
    lateinit var observerNavigation: Observer<DetailViewModel.Navigate>

    @Mock
    lateinit var observerTranslation: Observer<Event<String>>

    @Mock
    lateinit var marvelRepository: MarvelRepository

    @Mock
    lateinit var regionRepository: RegionRepository

    @Mock
    lateinit var translateService: TranslateService

    @get:Rule
    val rule = InstantTaskExecutorRule()

    private lateinit var vm: DetailViewModel

    @Before
    fun setUp() {
        vm = DetailViewModel(marvelRepository, regionRepository, translateService,
            characterId, Dispatchers.Unconfined)
    }

    @Test
    fun `after loading, receive content character data`() {
        val charactersMock = getMockedCharacterWrapper(characterId)
        val charactersWrapped = Result.Success(charactersMock)

        runBlocking {
            whenever(marvelRepository.getCountFavouriteCharacter(characterId)).thenReturn(0)
            whenever(marvelRepository.getCharacter(characterId)).thenReturn(charactersWrapped)
            vm.characterModel.observeForever(observerCharacterModel)
        }
        when(val value = vm.characterModel.value) {
            is DetailViewModel.UiCharacterModel.CharacterContent ->
                assertEquals(charactersMock.data.results[0], value.character)
            else -> assert(false)
        }
    }

    @Test
    fun `after loading, receive content favourite character data`() {
        val charactersMock = getMockedCharacterWrapper(characterId)
        val charactersWrapped = Result.Success(charactersMock)
        runBlocking {
            whenever(marvelRepository.getCountFavouriteCharacter(characterId)).thenReturn(1)
            whenever(marvelRepository.getCharacter(characterId)).thenReturn(charactersWrapped)
            vm.characterModel.observeForever(observerCharacterModel)
        }
        when(val value = vm.characterModel.value) {
            is DetailViewModel.UiCharacterModel.CharacterContent -> assert(value.isFavourite)
            else -> assert(false)
        }
    }

    @Test
    fun `after loading, receive content comic data`() {
        val comicsMock = getMockedComicWrapper(characterId)
        val comicsWrapped = Result.Success(comicsMock)
        runBlocking {
            whenever(marvelRepository.getComicsOfCharacter(characterId)).thenReturn(comicsWrapped)
            vm.comicModel.observeForever(observerComicModel)
        }
        when(val value = vm.comicModel.value) {
            is DetailViewModel.UiComicModel.ComicsContent ->
                assertEquals(comicsMock.data.results, value.comics)
            else -> assert(false)
        }
    }

    @Test
    fun `on character image click receive this character`() {
        val mini = Thumbnail("http://example", "jpg")
        vm.navigation.observeForever(observerNavigation)
        vm.onCharacterImageClick(mockedCharacter.copy(thumbnail = mini))
        when(val value = vm.navigation.value) {
            is DetailViewModel.Navigate.ActivityImageViewer ->
                assertEquals(mini.getUrl(), value.url.getContentIfNotHandled())
            else -> assert(false)
        }
    }

    @Test
    fun `on comic click image receive this comic`() {
        val mini = Thumbnail("http://example", "jpg")
        vm.navigation.observeForever(observerNavigation)
        vm.onComicImageClick(mockedComic.copy(thumbnail = mini))
        when(val value = vm.navigation.value) {
            is DetailViewModel.Navigate.ActivityImageViewer ->
                assertEquals(mini.getUrl(), value.url.getContentIfNotHandled())
            else -> assert(false)
        }
    }

    @Test
    fun `when favorite clicked, receive same character with no favorite`() {
        val isFavourite = true
        val characterWrapper = getMockedCharacterWrapper(1)
        val character = characterWrapper.data.results[0]
        runBlocking {
            whenever(marvelRepository.deleteFavouriteCharacter(character)).thenReturn(Unit)
            whenever(marvelRepository.getCountFavouriteCharacter(anyInt())).thenReturn(0)
            whenever(marvelRepository.getCharacter(anyInt())).thenReturn(Result.Success(characterWrapper))
            vm.characterModel.observeForever(observerCharacterModel)
            vm.characterFavoriteClick(isFavourite, character)
        }
        when(val value = vm.characterModel.value) {
            is DetailViewModel.UiCharacterModel.CharacterContent -> assertTrue(value.isFavourite == !isFavourite)
            else -> assert(false)
        }
    }
    @Test
    fun `when no favorite clicked, receive same character with favorite`() {
        val idComic = 1
        val idCharacter = 1
        val isFavourite = false
        val comics = getMockedComicWrapper(idComic)
        val characterWrapper = getMockedCharacterWrapper(idCharacter)
        val character = characterWrapper.data.results[0]
        runBlocking {
            whenever(marvelRepository.getComicsOfCharacter(idCharacter)).thenReturn(Result.Success(comics))
            whenever(marvelRepository.saveFavouriteCharacter(character, comics.data.results)).thenReturn(Unit)
            whenever(marvelRepository.getCountFavouriteCharacter(anyInt())).thenReturn(0)
            whenever(marvelRepository.getCharacter(anyInt())).thenReturn(Result.Success(characterWrapper))
            vm.characterModel.observeForever(observerCharacterModel)
            vm.characterFavoriteClick(isFavourite, character)
        }
        when(val value = vm.characterModel.value) {
            is DetailViewModel.UiCharacterModel.CharacterContent -> assertTrue(value.isFavourite == !isFavourite)
            else -> assert(false)
        }
    }

    @Test
    fun `when translate button click receive translation`() {
        val language = "es"
        val translate = "hola mundo"
        val description = "hello world"
        runBlocking {
            whenever(translateService.translate(description, language)).thenReturn(Result.Success(
                Translation(200, language, listOf(translate))))
            whenever(regionRepository.findLastRegionLanguage()).thenReturn(language)
            vm.translateModel.observeForever(observerTranslation)
        }
        vm.onTranslateButtonClick(description)
        when(val value = vm.translateModel.value) {
            is Event<String> -> assertEquals(translate, value.getContentIfNotHandled())
            else -> assert(false)
        }

    }
}
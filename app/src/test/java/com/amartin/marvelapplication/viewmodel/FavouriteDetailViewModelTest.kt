package com.amartin.marvelapplication.viewmodel

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.lifecycle.Observer
import com.amartin.marvelapplication.common.Event
import com.amartin.marvelapplication.common.getUrl
import com.amartin.marvelapplication.data.model.Thumbnail
import com.amartin.marvelapplication.data.source.LocalMarvelDataSource
import com.amartin.marvelapplication.mockCharacterComicData
import com.amartin.marvelapplication.mockedCharacter
import com.amartin.marvelapplication.mockedComic
import com.amartin.marvelapplication.ui.favorite_detail.FavouriteDetailViewModel
import com.nhaarman.mockitokotlin2.whenever
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.runBlocking
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.Mock
import org.mockito.junit.MockitoJUnitRunner
import kotlin.math.min

@RunWith(MockitoJUnitRunner::class)
class FavouriteDetailViewModelTest {

     @Mock
     lateinit var observerModel: Observer<FavouriteDetailViewModel.UiModel>

    @Mock
    lateinit var observerNavigate: Observer<Event<String>>

    @Mock
    lateinit var localMarvelDataSource: LocalMarvelDataSource

    @get:Rule
    val rule = InstantTaskExecutorRule()

    private val characterId = 42

    private lateinit var vm: FavouriteDetailViewModel

    @Before
    fun setUp(){
        vm = FavouriteDetailViewModel(localMarvelDataSource, characterId, Dispatchers.Unconfined)
    }

    @Test
    fun `after loading, receive content favourite character data`() {
        runBlocking {
            whenever(localMarvelDataSource.getFavouriteCharacterWithComics(characterId)).thenReturn(mockCharacterComicData)
            vm.model.observeForever(observerModel)
        }
        when(val value = vm.model.value) {
            is FavouriteDetailViewModel.UiModel.Content -> assertEquals(mockCharacterComicData, value.characterComicData)
        }
    }

    @Test
    fun `on comic click image receive this comic`() {
        val mini = Thumbnail("http://example", "jpg")
        vm.navigate.observeForever(observerNavigate)
        vm.onComicImageClick(mockedComic.copy(thumbnail = mini))
        when(val value = vm.navigate.value) {
            is Event<String> -> assertEquals(mini.getUrl(), value.getContentIfNotHandled())
            else -> assert(false)
        }
    }

    @Test
    fun `on character image click receive this character`() {
        val mini = Thumbnail("http://example", "jpg")
        vm.navigate.observeForever(observerNavigate)
        vm.onCharacterClick(mockedCharacter.copy(thumbnail = mini))
        when(val value = vm.navigate.value) {
            is Event<String> -> assertEquals(mini.getUrl(), value.getContentIfNotHandled())
            else -> assert(false)
        }
    }
}
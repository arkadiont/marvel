package com.amartin.marvelapplication.viewmodel

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.lifecycle.Observer
import com.amartin.marvelapplication.common.Event
import com.amartin.marvelapplication.data.source.LocalMarvelDataSource
import com.amartin.marvelapplication.mockedCharacter
import com.amartin.marvelapplication.ui.favourite.FavouriteViewModel
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

@RunWith(MockitoJUnitRunner::class)
class FavouriteViewModelTest {

    @Mock
    lateinit var localMarvelDataSource: LocalMarvelDataSource

    @Mock
    lateinit var observerModel: Observer<FavouriteViewModel.UiModel>

    @Mock
    lateinit var observerNavigate: Observer<Event<Int>>

    private lateinit var vm: FavouriteViewModel

    @get:Rule
    val rule = InstantTaskExecutorRule()

    @Before
    fun setUp() {
        vm = FavouriteViewModel(localMarvelDataSource, Dispatchers.Unconfined)
    }

    @Test
    fun `after loading, receive content all favourite characters data`() {
        val allCharacters = listOf(mockedCharacter)
        runBlocking {
            whenever(localMarvelDataSource.getFavouriteCharacters()).thenReturn(allCharacters)
            vm.model.observeForever(observerModel)
        }
        when(val value = vm.model.value) {
            is FavouriteViewModel.UiModel.Content -> assertEquals(allCharacters, value.characters)
            else -> assert(false)
        }
    }

    @Test
    fun `on click character receive a event with character id`() {
        val character = mockedCharacter
        vm.navigate.observeForever(observerNavigate)
        vm.onCharacterClick(mockedCharacter)
        when(val value = vm.navigate.value) {
            is Event<Int> -> assertEquals(character.id, value.getContentIfNotHandled())
            else -> assert(false)
        }
    }
}
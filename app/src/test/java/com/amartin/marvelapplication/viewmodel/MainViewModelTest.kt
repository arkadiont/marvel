package com.amartin.marvelapplication.viewmodel

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.lifecycle.Observer
import com.amartin.marvelapplication.api.Result
import com.amartin.marvelapplication.data.model.CharacterData
import com.amartin.marvelapplication.data.repository.MarvelRepository
import com.amartin.marvelapplication.getMockedCharacterWrapper
import com.amartin.marvelapplication.mockedCharacter
import com.amartin.marvelapplication.ui.main.MainViewModel
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
class MainViewModelTest {

    @get:Rule
    val rule = InstantTaskExecutorRule()

    @Mock
    lateinit var observerModel: Observer<MainViewModel.UiModel>

    @Mock
    lateinit var marvelRepository: MarvelRepository

    private lateinit var vm: MainViewModel

    @Before
    fun setUp() {
        vm = MainViewModel(marvelRepository, Dispatchers.Unconfined)
    }

//    @Test
//    fun `observing LiveData launch request, loading is show`() {
//        val charactersMock = getMockedCharacterWrapper(0)
//        val charactersWrapped = Result.Success(charactersMock)
//        runBlocking {
//            whenever(marvelRepository.getAllCharacters(ArgumentMatchers.anyInt())).thenReturn(charactersWrapped)
//            vm.model.observeForever(observerModel)
//        }
//        verify(observerModel).onChanged(MainViewModel.UiModel.Loading)
//    }

    @Test
    fun `after loading, receive content data`() {
        val charactersMock = getMockedCharacterWrapper(42)
        val charactersWrapped = Result.Success(charactersMock)
        runBlocking {
            whenever(marvelRepository.getAllCharacters()).thenReturn(charactersWrapped)
            vm.model.observeForever(observerModel)
        }
        when (val value = vm.model.value) {
            is MainViewModel.UiModel.Content ->
                assertEquals(charactersMock.data.results, value.characters)
            else -> assert(false)
        }
    }

    @Test
    fun `on character click receive this character for navigate`() {
        val characterClicked = mockedCharacter
        vm.onCharacterClick(characterClicked)
        when (val value = vm.navigation.value?.getContentIfNotHandled()) {
            is CharacterData -> assertEquals(characterClicked, value)
            else -> assert(false)
        }
    }


}
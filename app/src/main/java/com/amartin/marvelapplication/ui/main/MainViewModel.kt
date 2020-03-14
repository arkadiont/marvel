package com.amartin.marvelapplication.ui.main

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.amartin.marvelapplication.api.onError
import com.amartin.marvelapplication.api.onSuccess
import com.amartin.marvelapplication.common.Event
import com.amartin.marvelapplication.common.ViewModelScope
import com.amartin.marvelapplication.data.model.CharacterData
import com.amartin.marvelapplication.data.repository.MarvelRepository
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.launch

class MainViewModel(private val marvelRepository: MarvelRepository,
                    uiDispatcher: CoroutineDispatcher) : ViewModelScope(uiDispatcher) {

    companion object {
        private const val VISIBLE_THRESHOLD = 5
    }

    sealed class UiModel {
        object Loading : UiModel()
        class Content(val characters: List<CharacterData>) : UiModel()
    }

    private var offset = 0
    private var isRequestInProgress = false

    private val _model = MutableLiveData<UiModel>()
    val model: LiveData<UiModel>
        get() {
            if (_model.value == null) refresh()
            return _model
        }

    private val _navigation = MutableLiveData<Event<CharacterData>>()
    val navigation: LiveData<Event<CharacterData>> = _navigation

    private val _error = MutableLiveData<Event<String>>()
    val error: LiveData<Event<String>> = _error

    private fun refresh() {
        launch {
            _model.value = UiModel.Loading
            if (!isRequestInProgress) {
                isRequestInProgress = true

                marvelRepository.getAllCharacters(offset)
                    .onSuccess {
                        _model.value = UiModel.Content(it.data.results)
                    }.onError {
                        _error.value = Event(it)
                    }

                isRequestInProgress = false
            }
        }
    }

    fun listScrolled(visibleItemCount: Int, lastVisibleItemPosition: Int, totalItemCount: Int, characterSize: Int) {
        if (visibleItemCount + lastVisibleItemPosition + VISIBLE_THRESHOLD >= totalItemCount) {
            offset = characterSize
            refresh()
        }
    }

    fun onCharacterClick(character: CharacterData) {
        _navigation.value = Event(character)
    }
}

@Suppress("UNCHECKED_CAST")
class MainViewModelFactory(private val marvelRepository: MarvelRepository,
                           private val uiDispatcher: CoroutineDispatcher) : ViewModelProvider.Factory {
    override fun <T : ViewModel?> create(modelClass: Class<T>): T =
        MainViewModel(marvelRepository, uiDispatcher) as T

}
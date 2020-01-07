package com.amartin.marvelapplication.ui.detail

import androidx.lifecycle.*
import com.amartin.marvelapplication.api.model.CharacterData
import com.amartin.marvelapplication.api.model.ComicData
import com.amartin.marvelapplication.api.model.Url
import com.amartin.marvelapplication.api.onError
import com.amartin.marvelapplication.api.onSuccess
import com.amartin.marvelapplication.common.Event
import com.amartin.marvelapplication.common.Scope
import com.amartin.marvelapplication.data.repository.MarvelRepository
import kotlinx.coroutines.launch

class DetailViewModel(
    private val marvelRepository: MarvelRepository,
    private val characterId: Int) : ViewModel(), Scope by Scope.Impl() {

    init {
        initScope()
    }

    private val _error = MutableLiveData<Event<String>>()
    val error: LiveData<Event<String>> = _error

    private val _navigation = MutableLiveData<Event<Url>>()
    val navigation: LiveData<Event<Url>> = _navigation

    sealed class UiCharacterModel {
        object Loading : UiCharacterModel()
        class CharacterContent(val character: CharacterData) : UiCharacterModel()
    }

    private val _characterModel = MutableLiveData<UiCharacterModel>()
    val characterModel: LiveData<UiCharacterModel>
        get() {
            if (_characterModel.value == null) getCharacterData()
            return _characterModel
        }

    private fun getCharacterData() {
        launch {
            _characterModel.value = UiCharacterModel.Loading
            marvelRepository.getCharacter(characterId)
                .onSuccess {
                    _characterModel.value = UiCharacterModel.CharacterContent(it.data.results[0])
                }.onError {
                    _error.value = Event(it)
                }
        }
    }

    fun onUrlClick(url: Url) {
        _navigation.value = Event(url)
    }

    sealed class UiComicModel {
        object Loading : UiComicModel()
        class ComicsContent(val comics: List<ComicData>) : UiComicModel()
    }

    private val _comicsModel = MutableLiveData<UiComicModel>()
    val comicModel: LiveData<UiComicModel>
        get() {
            if (_comicsModel.value == null) getComicData()
            return _comicsModel
        }

    private fun getComicData() {
        launch {
            _comicsModel.value = UiComicModel.Loading
            marvelRepository.getComicsOfCharacter(characterId)
                .onSuccess {
                    _comicsModel.value = UiComicModel.ComicsContent(it.data.results)
                }
                .onError {
                    _error.value = Event(it)
                }
        }
    }

    override fun onCleared() {
        cancelScope()
        super.onCleared()
    }
}

@Suppress("UNCHECKED_CAST")
class DetailViewModelFactory(
    private val marvelRepository: MarvelRepository,
    private val characterId: Int) : ViewModelProvider.Factory {
    override fun <T : ViewModel?> create(modelClass: Class<T>): T =
        DetailViewModel(marvelRepository, characterId) as T
}
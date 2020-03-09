package com.amartin.marvelapplication.ui.detail

import androidx.lifecycle.*
import com.amartin.marvelapplication.api.YandexService
import com.amartin.marvelapplication.api.onError
import com.amartin.marvelapplication.api.onSuccess
import com.amartin.marvelapplication.common.Event
import com.amartin.marvelapplication.common.ViewModelScope
import com.amartin.marvelapplication.common.getUrl
import com.amartin.marvelapplication.data.model.CharacterData
import com.amartin.marvelapplication.data.model.ComicData
import com.amartin.marvelapplication.data.repository.MarvelRepository
import com.amartin.marvelapplication.data.repository.RegionRepository
import com.amartin.marvelapplication.ui.detail.DetailViewModel.Navigate.ActivityImageViewer
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class DetailViewModel(
    private val marvelRepository: MarvelRepository,
    private val regionRepository: RegionRepository,
    private val yandexService: YandexService,
    private val characterId: Int) : ViewModelScope() {

    private val _error = MutableLiveData<Event<String>>()
    val error: LiveData<Event<String>> = _error

    sealed class Navigate {
        class ActivityImageViewer(val url: Event<String>) : Navigate()
    }

    private val _navigation = MutableLiveData<Navigate>()
    val navigation: LiveData<Navigate> = _navigation

    sealed class UiCharacterModel {
        object Loading : UiCharacterModel()
        class CharacterContent(val character: CharacterData, val isFavourite: Boolean) : UiCharacterModel()
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
            val isFavourite = marvelRepository.getCountFavouriteCharacter(characterId) == 1
            marvelRepository.getCharacter(characterId)
                .onSuccess {
                    _characterModel.value = UiCharacterModel.CharacterContent(it.data.results[0], isFavourite)
                }.onError {
                    _error.value = Event(it)
                }
        }
    }

    fun characterFavoriteClick(isFavourite: Boolean, character: CharacterData) {
        launch {
            _characterModel.value = UiCharacterModel.Loading
            if (isFavourite) {
                marvelRepository.deleteFavouriteCharacter(character)
            }else {
                marvelRepository.getComicsOfCharacter(character.id)
                    .onSuccess {
                        launch(Dispatchers.IO) {
                            marvelRepository.saveFavouriteCharacter(character, it.data.results)
                        }
                    }.onError {
                        _error.value = Event(it)
                    }
            }
            _characterModel.value = UiCharacterModel.CharacterContent(character, !isFavourite)
        }
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

    fun onComicImageClick(comic: ComicData) {
        _navigation.value = ActivityImageViewer(Event(comic.thumbnail.getUrl()))
    }

    fun onCharacterImageClick(character: CharacterData) {
        _navigation.value = ActivityImageViewer(Event(character.thumbnail.getUrl()))
    }

    private val _translateModel = MutableLiveData<Event<String>>()
    val translateModel: LiveData<Event<String>> = _translateModel

    fun onTranslateButtonClick(text: String) {
        launch {
            val language = regionRepository.findLastRegionLanguage()
            yandexService.translate(text, language)
                .onSuccess {
                    _translateModel.value = Event(it.text[0])
                }
                .onError {
                    _error.value = Event(it)
                }
        }
    }
}

@Suppress("UNCHECKED_CAST")
class DetailViewModelFactory(
    private val regionRepository: RegionRepository,
    private val marvelRepository: MarvelRepository,
    private val yandexService: YandexService,
    private val characterId: Int) : ViewModelProvider.Factory {
    override fun <T : ViewModel?> create(modelClass: Class<T>): T =
        DetailViewModel(marvelRepository, regionRepository, yandexService, characterId) as T
}
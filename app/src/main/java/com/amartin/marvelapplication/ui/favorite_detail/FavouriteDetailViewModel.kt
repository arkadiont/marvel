package com.amartin.marvelapplication.ui.favorite_detail

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.amartin.marvelapplication.common.Event
import com.amartin.marvelapplication.common.ViewModelScope
import com.amartin.marvelapplication.common.getUrl
import com.amartin.marvelapplication.data.model.CharacterComicData
import com.amartin.marvelapplication.data.model.CharacterData
import com.amartin.marvelapplication.data.model.ComicData
import com.amartin.marvelapplication.data.source.LocalMarvelDataSource
import com.amartin.marvelapplication.ui.favorite_detail.FavouriteDetailViewModel.UiModel.*
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.launch

class FavouriteDetailViewModel(
    private val localMarvelDataSource: LocalMarvelDataSource,
    private val characterId: Int, uiDispatcher: CoroutineDispatcher): ViewModelScope(uiDispatcher) {

    sealed class UiModel {
        object Loading : UiModel()
        class Content(val characterComicData: CharacterComicData) : UiModel()
    }
    private val _model = MutableLiveData<UiModel>()
    val model: LiveData<UiModel>
        get() {
            if (_model.value == null) refresh()
            return _model
        }

    private fun refresh() {
        launch {
            _model.value = Loading
            _model.value = Content(localMarvelDataSource.getFavouriteCharacterWithComics(characterId))
        }
    }

    private val _navigate = MutableLiveData<Event<String>>()
    val navigate: LiveData<Event<String>> = _navigate

    fun onComicImageClick(comic: ComicData) {
        _navigate.value = Event(comic.thumbnail.getUrl())
    }

    fun onCharacterClick(characterData: CharacterData) {
        _navigate.value = Event(characterData.thumbnail.getUrl())
    }
}

@Suppress("UNCHECKED_CAST")
class FavouriteDetailViewModelFactory(
    private val localMarvelDataSource: LocalMarvelDataSource,
    private val characterId: Int,
    private val uiDispatcher: CoroutineDispatcher): ViewModelProvider.Factory {
    override fun <T : ViewModel?> create(modelClass: Class<T>): T =
        FavouriteDetailViewModel(localMarvelDataSource, characterId, uiDispatcher) as T

}
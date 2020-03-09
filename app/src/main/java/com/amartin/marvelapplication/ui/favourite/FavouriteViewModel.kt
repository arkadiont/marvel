package com.amartin.marvelapplication.ui.favourite

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.amartin.marvelapplication.common.Event
import com.amartin.marvelapplication.common.ViewModelScope
import com.amartin.marvelapplication.data.model.CharacterData
import com.amartin.marvelapplication.data.source.LocalMarvelDataSource
import kotlinx.coroutines.launch

class FavouriteViewModel(private val localMarvelDataSource: LocalMarvelDataSource): ViewModelScope() {

    sealed class UiModel {
        object Loading : UiModel()
        class Content(val characters: List<CharacterData>): UiModel()
    }

    private val _model = MutableLiveData<UiModel>()
    val model: LiveData<UiModel>
        get() {
            if (_model.value == null) refresh()
            return _model
        }

    private fun refresh() {
        launch {
            _model.value = UiModel.Loading
            _model.value = UiModel.Content(localMarvelDataSource.getFavouriteCharacters())
        }
    }

    private val _navigate = MutableLiveData<Event<Int>>()
    val navigate: LiveData<Event<Int>> = _navigate

    fun onCharacterClick(character: CharacterData) {
        _navigate.value = Event(character.id)
    }
}

@Suppress("UNCHECKED_CAST")
class FavouriteViewModelFactory(private val localMarvelDataSource: LocalMarvelDataSource): ViewModelProvider.Factory {
    override fun <T : ViewModel?> create(modelClass: Class<T>): T =
        FavouriteViewModel(localMarvelDataSource) as T
}
package com.amartin.marvelapplication.common

import androidx.lifecycle.ViewModel
import kotlinx.coroutines.Job

open class ViewModelScope: ViewModel(), Scope {
    override  lateinit var job: Job

    init {
        this.initScope()
    }

    override fun onCleared() {
        cancelScope()
        super.onCleared()
    }
}
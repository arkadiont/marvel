package com.amartin.marvelapplication.common

import androidx.lifecycle.ViewModel
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Job
import kotlin.coroutines.CoroutineContext

open class ViewModelScope(private val uiDispatcher: CoroutineDispatcher): ViewModel(), Scope {
    override  lateinit var job: Job

    override val coroutineContext: CoroutineContext
        get() = uiDispatcher + job

    init {
        this.initScope()
    }

    override fun onCleared() {
        this.cancelScope()
        super.onCleared()
    }
}
package com.impulsa.app.navigation

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.cancel

/**
 * ViewModel mínimo y multiplataforma (sin depender de androidx.lifecycle).
 * Cada pantalla tiene uno; vive mientras su entrada esté en la pila de navegación.
 */
abstract class ViewModel {
    protected val viewModelScope = CoroutineScope(SupervisorJob() + Dispatchers.Main)

    fun clear() {
        viewModelScope.cancel()
        onCleared()
    }

    protected open fun onCleared() {}
}

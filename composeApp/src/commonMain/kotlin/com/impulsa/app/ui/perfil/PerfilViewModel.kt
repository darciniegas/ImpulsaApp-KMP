package com.impulsa.app.ui.perfil

import com.impulsa.app.domain.Asesor
import com.impulsa.app.domain.ImpulsaRepository
import com.impulsa.app.navigation.ViewModel
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

data class PerfilEstado(
    val usuario: Asesor? = null,
    val notificaciones: Boolean = true,
    val resumenSemanal: Boolean = false
)

class PerfilViewModel(private val repositorio: ImpulsaRepository) : ViewModel() {

    val estado: StateFlow<PerfilEstado> = combine(
        repositorio.usuarioActual,
        repositorio.preferencia(ImpulsaRepository.PREF_NOTIFICACIONES, porDefecto = true),
        repositorio.preferencia(ImpulsaRepository.PREF_RESUMEN_SEMANAL, porDefecto = false)
    ) { usuario, notificaciones, resumen ->
        PerfilEstado(usuario, notificaciones, resumen)
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), PerfilEstado())

    /** Las preferencias quedan guardadas en la base de datos. */
    fun alCambiarNotificaciones(valor: Boolean) {
        viewModelScope.launch { repositorio.guardarPreferencia(ImpulsaRepository.PREF_NOTIFICACIONES, valor) }
    }

    fun alCambiarResumenSemanal(valor: Boolean) {
        viewModelScope.launch { repositorio.guardarPreferencia(ImpulsaRepository.PREF_RESUMEN_SEMANAL, valor) }
    }

    fun cerrarSesion(alTerminar: () -> Unit) {
        viewModelScope.launch {
            repositorio.cerrarSesion()
            alTerminar()
        }
    }
}

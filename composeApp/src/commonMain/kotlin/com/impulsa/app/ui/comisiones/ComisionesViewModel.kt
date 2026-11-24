package com.impulsa.app.ui.comisiones

import com.impulsa.app.domain.Asesor
import com.impulsa.app.domain.ImpulsaRepository
import com.impulsa.app.navigation.ViewModel
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn

data class ComisionesEstado(
    val usuario: Asesor? = null,
    val equipo: List<Asesor> = emptyList()
) {
    /** Porcentaje real de comisión = comisión / ingresos del usuario. */
    val porcentajeUsuario: Double
        get() = usuario?.let { if (it.ingresos > 0.0) (it.comision / it.ingresos) * 100 else 0.0 } ?: 0.0
}

class ComisionesViewModel(repositorio: ImpulsaRepository) : ViewModel() {

    val estado: StateFlow<ComisionesEstado> = combine(repositorio.asesores, repositorio.usuarioActual) { asesores, usuario ->
        ComisionesEstado(usuario = usuario, equipo = asesores.sortedByDescending { it.comision })
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), ComisionesEstado())
}

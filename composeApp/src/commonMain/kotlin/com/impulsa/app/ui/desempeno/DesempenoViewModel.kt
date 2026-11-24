package com.impulsa.app.ui.desempeno

import com.impulsa.app.domain.Asesor
import com.impulsa.app.domain.ImpulsaRepository
import com.impulsa.app.navigation.ViewModel
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn

data class DesempenoEstado(
    val ingresosEquipo: Double = 0.0,
    val cumplimientoEquipo: Int = 0,
    val usuario: Asesor? = null,
    val ranking: List<Asesor> = emptyList()
)

class DesempenoViewModel(repositorio: ImpulsaRepository) : ViewModel() {

    val estado: StateFlow<DesempenoEstado> = combine(repositorio.asesores, repositorio.usuarioActual) { asesores, usuario ->
        val ingresos = asesores.sumOf { it.ingresos }
        val meta = asesores.sumOf { it.meta }
        DesempenoEstado(
            ingresosEquipo = ingresos,
            cumplimientoEquipo = if (meta > 0.0) ((ingresos / meta) * 100).toInt() else 0,
            usuario = usuario,
            ranking = asesores.sortedByDescending { it.ingresos }   // mayor a menor por ingresos
        )
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), DesempenoEstado())
}

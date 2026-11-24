package com.impulsa.app.ui.detalle

import com.impulsa.app.domain.ImpulsaRepository
import com.impulsa.app.domain.Venta
import com.impulsa.app.navigation.ViewModel
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

data class DetalleEstado(
    val cargando: Boolean = true,
    val venta: Venta? = null
)

class DetalleVentaViewModel(
    private val repositorio: ImpulsaRepository,
    private val ventaId: String
) : ViewModel() {

    val estado: StateFlow<DetalleEstado> = repositorio.venta(ventaId)
        .map { DetalleEstado(cargando = false, venta = it) }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), DetalleEstado())

    fun eliminar(alTerminar: () -> Unit) {
        viewModelScope.launch {
            repositorio.eliminarVenta(ventaId)
            alTerminar()
        }
    }
}

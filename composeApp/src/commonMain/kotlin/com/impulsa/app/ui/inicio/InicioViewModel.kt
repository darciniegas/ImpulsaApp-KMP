package com.impulsa.app.ui.inicio

import com.impulsa.app.domain.Asesor
import com.impulsa.app.domain.ImpulsaRepository
import com.impulsa.app.domain.Venta
import com.impulsa.app.navigation.ViewModel
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn

data class InicioEstado(
    val usuario: Asesor? = null,
    val ventasRecientes: List<Venta> = emptyList()
)

class InicioViewModel(repositorio: ImpulsaRepository) : ViewModel() {

    @OptIn(ExperimentalCoroutinesApi::class)
    val estado: StateFlow<InicioEstado> = repositorio.usuarioActual
        .flatMapLatest { usuario ->
            if (usuario == null) {
                flowOf(InicioEstado())
            } else {
                repositorio.ventasDe(usuario.nombre).map { ventas ->
                    InicioEstado(usuario = usuario, ventasRecientes = ventas.take(3))
                }
            }
        }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), InicioEstado())
}

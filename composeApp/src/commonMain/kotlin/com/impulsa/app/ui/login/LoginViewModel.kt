package com.impulsa.app.ui.login

import com.impulsa.app.core.Textos
import com.impulsa.app.core.esCorreoValido
import com.impulsa.app.domain.ImpulsaRepository
import com.impulsa.app.navigation.ViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

data class LoginEstado(
    // Valores de prueba prellenados, igual que en el prototipo original
    val correo: String = "daniel@impulsa.co",
    val password: String = "impulsa123",
    val mostrarPassword: Boolean = false,
    val errorCorreo: String? = null,
    val errorPassword: String? = null
)

class LoginViewModel(private val repositorio: ImpulsaRepository) : ViewModel() {

    private val _estado = MutableStateFlow(LoginEstado())
    val estado: StateFlow<LoginEstado> = _estado.asStateFlow()

    fun alCambiarCorreo(valor: String) = _estado.update { it.copy(correo = valor, errorCorreo = null) }

    fun alCambiarPassword(valor: String) = _estado.update { it.copy(password = valor, errorPassword = null) }

    fun alternarPassword() = _estado.update { it.copy(mostrarPassword = !it.mostrarPassword) }

    /** Valida los campos; si son correctos guarda la sesión y avisa para navegar. */
    fun entrar(alExito: () -> Unit) {
        val actual = _estado.value
        val correo = actual.correo.trim()

        val errorCorreo = if (correo.isEmpty() || !correo.esCorreoValido()) Textos.LOGIN_ERROR_CORREO else null
        val errorPassword = if (actual.password.length < 6) Textos.LOGIN_ERROR_PASS else null
        _estado.update { it.copy(errorCorreo = errorCorreo, errorPassword = errorPassword) }
        if (errorCorreo != null || errorPassword != null) return

        viewModelScope.launch {
            repositorio.iniciarSesion(correo)
            alExito()
        }
    }
}

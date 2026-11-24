package com.impulsa.app.domain

import kotlinx.coroutines.flow.Flow

/**
 * Contrato de acceso a datos. La UI y los ViewModels dependen solo de esta interfaz;
 * la implementación real (SQLDelight) vive en la capa `data`.
 */
interface ImpulsaRepository {

    // ───────── Consultas reactivas ─────────
    val asesores: Flow<List<Asesor>>
    val ventas: Flow<List<Venta>>
    val usuarioActual: Flow<Asesor?>

    fun venta(id: String): Flow<Venta?>
    fun ventasDe(nombreAsesor: String): Flow<List<Venta>>

    // ───────── Sesión ─────────
    /** true si hay una sesión guardada (consulta síncrona, para decidir la pantalla inicial). */
    fun haySesion(): Boolean

    /** Inicia sesión y la deja guardada. Devuelve el asesor asociado al correo. */
    suspend fun iniciarSesion(correo: String): Asesor

    suspend fun cerrarSesion()

    // ───────── Ventas (CRUD) ─────────
    suspend fun registrarVenta(datos: NuevaVenta, asesor: Asesor): Venta
    suspend fun actualizarVenta(id: String, datos: NuevaVenta)
    suspend fun eliminarVenta(id: String)

    // ───────── Preferencias ─────────
    fun preferencia(clave: String, porDefecto: Boolean): Flow<Boolean>
    suspend fun guardarPreferencia(clave: String, valor: Boolean)

    companion object {
        const val PREF_NOTIFICACIONES = "pref_notificaciones"
        const val PREF_RESUMEN_SEMANAL = "pref_resumen_semanal"
    }
}

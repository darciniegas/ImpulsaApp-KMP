package com.impulsa.app.data

import app.cash.sqldelight.coroutines.asFlow
import app.cash.sqldelight.coroutines.mapToList
import app.cash.sqldelight.coroutines.mapToOneOrNull
import com.impulsa.app.db.AsesorEntity
import com.impulsa.app.db.ImpulsaDatabase
import com.impulsa.app.db.VentaEntity
import com.impulsa.app.domain.Asesor
import com.impulsa.app.domain.ImpulsaRepository
import com.impulsa.app.domain.NuevaVenta
import com.impulsa.app.domain.Venta
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.withContext

/**
 * Implementación del repositorio sobre SQLDelight (SQLite).
 * Es 100 % código común: funciona igual en Android y Desktop.
 */
class ImpulsaRepositoryImpl(
    private val db: ImpulsaDatabase,
    private val dispatcher: CoroutineDispatcher = Dispatchers.Default
) : ImpulsaRepository {

    private val q = db.impulsaQueries

    // ───────────────────────── Consultas ─────────────────────────

    override val asesores: Flow<List<Asesor>> =
        q.selectAsesores().asFlow().mapToList(dispatcher).map { lista -> lista.map { it.aDominio() } }

    override val ventas: Flow<List<Venta>> =
        q.selectVentas().asFlow().mapToList(dispatcher).map { lista -> lista.map { it.aDominio() } }

    @OptIn(ExperimentalCoroutinesApi::class)
    override val usuarioActual: Flow<Asesor?> =
        q.selectConfig(CLAVE_SESION).asFlow().mapToOneOrNull(dispatcher).flatMapLatest { idAsesor ->
            if (idAsesor == null) {
                flowOf(null)
            } else {
                q.selectAsesorPorId(idAsesor).asFlow().mapToOneOrNull(dispatcher).map { it?.aDominio() }
            }
        }

    override fun venta(id: String): Flow<Venta?> =
        q.selectVentaPorId(id).asFlow().mapToOneOrNull(dispatcher).map { it?.aDominio() }

    override fun ventasDe(nombreAsesor: String): Flow<List<Venta>> =
        q.selectVentasDe(nombreAsesor).asFlow().mapToList(dispatcher).map { lista -> lista.map { it.aDominio() } }

    // ───────────────────────── Sesión ─────────────────────────

    override fun haySesion(): Boolean {
        val id = q.selectConfig(CLAVE_SESION).executeAsOneOrNull() ?: return false
        return q.selectAsesorPorId(id).executeAsOneOrNull() != null
    }

    override suspend fun iniciarSesion(correo: String): Asesor = withContext(dispatcher) {
        // Autenticación del prototipo: si el correo corresponde a un asesor se usa ese;
        // en caso contrario se usa el usuario por defecto (Ana García), igual que el original.
        val asesor = q.selectAsesorPorCorreo(correo.trim()).executeAsOneOrNull()
            ?: q.selectAsesorPorId(SeedData.ID_USUARIO_POR_DEFECTO).executeAsOne()
        q.upsertConfig(CLAVE_SESION, asesor.id)
        asesor.aDominio()
    }

    override suspend fun cerrarSesion() = withContext(dispatcher) {
        q.eliminarConfig(CLAVE_SESION)
        Unit
    }

    // ───────────────────────── Ventas (CRUD) ─────────────────────────

    override suspend fun registrarVenta(datos: NuevaVenta, asesor: Asesor): Venta = withContext(dispatcher) {
        db.transactionWithResult {
            val id = siguienteIdVenta()
            q.insertarVenta(
                id, datos.cliente, datos.concepto, datos.valor,
                asesor.nombre, datos.formaPago, datos.fecha, datos.nota
            )
            ajustarAsesor(asesor.nombre, deltaVentas = 1L, deltaIngresos = datos.valor)
            Venta(
                id = id,
                cliente = datos.cliente,
                concepto = datos.concepto,
                valor = datos.valor,
                asesor = asesor.nombre,
                formaPago = datos.formaPago,
                fecha = datos.fecha,
                nota = datos.nota
            )
        }
    }

    override suspend fun actualizarVenta(id: String, datos: NuevaVenta): Unit = withContext(dispatcher) {
        db.transaction {
            val actual = q.selectVentaPorId(id).executeAsOneOrNull() ?: return@transaction
            q.actualizarVenta(datos.cliente, datos.concepto, datos.valor, datos.formaPago, datos.fecha, datos.nota, id)
            val diferencia = datos.valor - actual.valor
            if (diferencia != 0.0) {
                ajustarAsesor(actual.asesor, deltaVentas = 0L, deltaIngresos = diferencia)
            }
        }
    }

    override suspend fun eliminarVenta(id: String): Unit = withContext(dispatcher) {
        db.transaction {
            val actual = q.selectVentaPorId(id).executeAsOneOrNull() ?: return@transaction
            q.eliminarVenta(id)
            ajustarAsesor(actual.asesor, deltaVentas = -1L, deltaIngresos = -actual.valor)
        }
    }

    // ───────────────────────── Preferencias ─────────────────────────

    override fun preferencia(clave: String, porDefecto: Boolean): Flow<Boolean> =
        q.selectConfig(clave).asFlow().mapToOneOrNull(dispatcher).map { valor ->
            if (valor == null) porDefecto else valor == "1"
        }

    override suspend fun guardarPreferencia(clave: String, valor: Boolean): Unit = withContext(dispatcher) {
        q.upsertConfig(clave, if (valor) "1" else "0")
        Unit
    }

    // ───────────────────────── Inicialización ─────────────────────────

    /** Carga los datos de ejemplo del prototipo si la base está vacía. */
    fun sembrarSiVacia() {
        if (q.contarAsesores().executeAsOne() > 0L) return
        db.transaction {
            SeedData.asesores.forEach { a ->
                q.insertarAsesor(a.id, a.nombre, a.correo, a.telefono, a.ventas.toLong(), a.ingresos, a.comision, a.meta)
            }
            val total = SeedData.ventas.size
            SeedData.ventas.forEachIndexed { indice, v ->
                // La primera de la lista es la más reciente => mayor valor de "orden"
                q.insertarVentaConOrden(
                    v.id, v.cliente, v.concepto, v.valor, v.asesor,
                    v.formaPago, v.fecha, v.nota, (total - indice).toLong()
                )
            }
        }
    }

    // ───────────────────────── Internos ─────────────────────────

    /** Siguiente código, ej. V-2408-009 (máximo sufijo existente + 1). */
    private fun siguienteIdVenta(): String {
        val maximo = q.selectIdsVentas().executeAsList()
            .mapNotNull { it.substringAfterLast('-').toIntOrNull() }
            .maxOrNull() ?: 0
        return SeedData.PREFIJO_ID_VENTA + (maximo + 1).toString().padStart(3, '0')
    }

    /**
     * Actualiza los acumulados del asesor (ventas, ingresos y comisión).
     * La comisión se ajusta manteniendo la tasa actual del asesor (comisión / ingresos).
     */
    private fun ajustarAsesor(nombre: String, deltaVentas: Long, deltaIngresos: Double) {
        val asesor = q.selectAsesorPorNombre(nombre).executeAsOneOrNull() ?: return
        val tasa = if (asesor.ingresos > 0.0) asesor.comision / asesor.ingresos else 0.0
        q.ajustarAcumulados(deltaVentas, deltaIngresos, deltaIngresos * tasa, nombre)
    }

    private companion object {
        const val CLAVE_SESION = "sesion_asesor_id"
    }
}

// ───────────────────────── Mapeos entidad -> dominio ─────────────────────────

private fun AsesorEntity.aDominio() = Asesor(
    id = id,
    nombre = nombre,
    correo = correo,
    telefono = telefono,
    ventas = ventas.toInt(),
    ingresos = ingresos,
    comision = comision,
    meta = meta
)

private fun VentaEntity.aDominio() = Venta(
    id = id,
    cliente = cliente,
    concepto = concepto,
    valor = valor,
    asesor = asesor,
    formaPago = formaPago,
    fecha = fecha,
    nota = nota
)

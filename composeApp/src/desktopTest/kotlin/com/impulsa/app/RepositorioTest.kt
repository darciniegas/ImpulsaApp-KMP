package com.impulsa.app

import app.cash.sqldelight.driver.jdbc.sqlite.JdbcSqliteDriver
import com.impulsa.app.data.ImpulsaRepositoryImpl
import com.impulsa.app.db.ImpulsaDatabase
import com.impulsa.app.domain.ImpulsaRepository
import com.impulsa.app.domain.NuevaVenta
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.test.runTest
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertNull
import kotlin.test.assertTrue

/** Pruebas de persistencia con SQLite real (en memoria) y el mismo esquema que usa la app. */
class RepositorioTest {

    private fun repo(): ImpulsaRepositoryImpl {
        val driver = JdbcSqliteDriver(JdbcSqliteDriver.IN_MEMORY)
        ImpulsaDatabase.Schema.create(driver)
        return ImpulsaRepositoryImpl(ImpulsaDatabase(driver)).also { it.sembrarSiVacia() }
    }

    private val datos = NuevaVenta("Cliente X", "Plan Y", 1000.0, "Efectivo", "01/09/2026", "")

    @Test fun siembra_los_datos_del_prototipo() = runTest {
        val r = repo()
        assertEquals(5, r.asesores.first().size)
        val ventas = r.ventas.first()
        assertEquals(8, ventas.size)
        assertEquals("V-2408-001", ventas.first().id)   // la más reciente primero
    }

    @Test fun crear_actualiza_acumulados_y_queda_de_primera() = runTest {
        val r = repo()
        val Daniel = r.iniciarSesion("daniel@impulsa.co")
        val nueva = r.registrarVenta(datos, Daniel)
        assertEquals("V-2408-009", nueva.id)
        assertEquals(nueva.id, r.ventas.first().first().id)
        val despues = r.usuarioActual.first()!!
        assertEquals(Daniel.ventas + 1, despues.ventas)
        assertEquals(Daniel.ingresos + 1000.0, despues.ingresos)
    }

    @Test fun actualizar_y_eliminar_revierten_acumulados() = runTest {
        val r = repo()
        val ana = r.iniciarSesion("ana@impulsa.co")
        val nueva = r.registrarVenta(datos, ana)

        r.actualizarVenta(nueva.id, datos.copy(valor = 3000.0, cliente = "Otro"))
        assertEquals("Otro", r.venta(nueva.id).first()!!.cliente)
        assertEquals(ana.ingresos + 3000.0, r.usuarioActual.first()!!.ingresos)

        r.eliminarVenta(nueva.id)
        assertNull(r.venta(nueva.id).first())
        assertEquals(ana.ingresos, r.usuarioActual.first()!!.ingresos)
        assertEquals(ana.ventas, r.usuarioActual.first()!!.ventas)
    }

    @Test fun sesion_y_preferencias_persisten() = runTest {
        val r = repo()
        assertFalse(r.haySesion())
        r.iniciarSesion("pedro@impulsa.co")
        assertTrue(r.haySesion())
        assertEquals("Pedro Ruiz", r.usuarioActual.first()!!.nombre)

        assertTrue(r.preferencia(ImpulsaRepository.PREF_NOTIFICACIONES, true).first())
        r.guardarPreferencia(ImpulsaRepository.PREF_NOTIFICACIONES, false)
        assertFalse(r.preferencia(ImpulsaRepository.PREF_NOTIFICACIONES, true).first())

        r.cerrarSesion()
        assertFalse(r.haySesion())
    }

    @Test fun correo_desconocido_usa_usuario_por_defecto() = runTest {
        val r = repo()
        assertEquals("Ana García", r.iniciarSesion("nadie@correo.com").nombre)
    }
}

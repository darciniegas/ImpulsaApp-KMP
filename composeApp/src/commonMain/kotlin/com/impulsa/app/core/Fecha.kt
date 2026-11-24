package com.impulsa.app.core

import kotlinx.datetime.Clock
import kotlinx.datetime.LocalDate
import kotlinx.datetime.TimeZone
import kotlinx.datetime.atStartOfDayIn
import kotlinx.datetime.toLocalDateTime
import kotlinx.datetime.Instant

/** Fecha de hoy en formato dd/MM/yyyy (zona horaria del sistema). */
fun fechaHoy(): String {
    val fecha = Clock.System.now().toLocalDateTime(TimeZone.currentSystemDefault()).date
    return formatearFecha(fecha.dayOfMonth, fecha.monthNumber, fecha.year)
}

/** El DatePicker de Material entrega milisegundos en UTC; los convierte a dd/MM/yyyy. */
fun fechaDesdeMillisUtc(millis: Long): String {
    val fecha = Instant.fromEpochMilliseconds(millis).toLocalDateTime(TimeZone.UTC).date
    return formatearFecha(fecha.dayOfMonth, fecha.monthNumber, fecha.year)
}

/** Inversa de la anterior: "23/08/2026" -> millis UTC (o null si el texto no es una fecha válida). */
fun millisUtcDeFecha(texto: String): Long? {
    val partes = texto.split("/")
    if (partes.size != 3) return null
    val dia = partes[0].toIntOrNull() ?: return null
    val mes = partes[1].toIntOrNull() ?: return null
    val anio = partes[2].toIntOrNull() ?: return null
    return try {
        LocalDate(anio, mes, dia).atStartOfDayIn(TimeZone.UTC).toEpochMilliseconds()
    } catch (e: IllegalArgumentException) {
        null
    }
}

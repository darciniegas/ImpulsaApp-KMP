package com.impulsa.app.core

import kotlin.math.abs
import kotlin.math.roundToLong

/**
 * Utilidades de formato compartidas (reemplazan a NumberFormat/Locale/String.format de la JVM,
 * que no existen en código común).
 */

/** Formatea un número como moneda colombiana: 54200.0 -> "$ 54.200" */
fun Double.aMoneda(): String {
    val entero = roundToLong()
    val negativo = entero < 0
    val digitos = abs(entero).toString()
    val agrupado = digitos.reversed().chunked(3).joinToString(".").reversed()
    return "$ " + (if (negativo) "-" else "") + agrupado
}

/** Porcentaje con un decimal y coma decimal: 8.0 -> "8,0" */
fun Double.aPorcentajeUnDecimal(): String {
    val decimas = (this * 10).roundToLong()
    val signo = if (decimas < 0) "-" else ""
    val absoluto = abs(decimas)
    return "$signo${absoluto / 10},${absoluto % 10}"
}

/** Convierte "dd/MM/yyyy" a partes; null si el texto no es una fecha válida. */
fun formatearFecha(dia: Int, mes: Int, anio: Int): String =
    "${dia.toString().padStart(2, '0')}/${mes.toString().padStart(2, '0')}/${anio.toString().padStart(4, '0')}"

/** Validación de correo (equivalente a Patterns.EMAIL_ADDRESS de Android, sin depender de él). */
private val REGEX_CORREO = Regex("^[A-Za-z0-9._%+\\-]+@[A-Za-z0-9.\\-]+\\.[A-Za-z]{2,}$")

fun String.esCorreoValido(): Boolean = REGEX_CORREO.matches(this)

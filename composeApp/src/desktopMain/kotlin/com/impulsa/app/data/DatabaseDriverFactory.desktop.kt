package com.impulsa.app.data

import app.cash.sqldelight.db.SqlDriver
import app.cash.sqldelight.driver.jdbc.sqlite.JdbcSqliteDriver
import com.impulsa.app.db.ImpulsaDatabase
import java.io.File

/**
 * Desktop: la base de datos vive en un archivo dentro de la carpeta del usuario:
 *   Windows -> C:\Users\<usuario>\.impulsa\impulsa.db
 * Así los datos se conservan al cerrar y volver a abrir la aplicación.
 */
actual class DatabaseDriverFactory {
    actual fun createDriver(): SqlDriver {
        val carpeta = File(System.getProperty("user.home"), ".impulsa").apply { mkdirs() }
        val archivo = File(carpeta, "impulsa.db")
        val esNueva = !archivo.exists()

        val driver = JdbcSqliteDriver("jdbc:sqlite:${archivo.absolutePath}")
        if (esNueva) {
            ImpulsaDatabase.Schema.create(driver)
        }
        return driver
    }
}

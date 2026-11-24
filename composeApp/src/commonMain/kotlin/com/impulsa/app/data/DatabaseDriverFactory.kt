package com.impulsa.app.data

import app.cash.sqldelight.db.SqlDriver

/**
 * Crea el driver de SQLite de cada plataforma.
 *  - Android: AndroidSqliteDriver (necesita un Context).
 *  - Desktop: JdbcSqliteDriver (archivo en la carpeta del usuario).
 */
expect class DatabaseDriverFactory {
    fun createDriver(): SqlDriver
}

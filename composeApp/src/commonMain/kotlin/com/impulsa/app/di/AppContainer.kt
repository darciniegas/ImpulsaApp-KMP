package com.impulsa.app.di

import com.impulsa.app.data.DatabaseDriverFactory
import com.impulsa.app.data.ImpulsaRepositoryImpl
import com.impulsa.app.db.ImpulsaDatabase
import com.impulsa.app.domain.ImpulsaRepository

/**
 * Contenedor de dependencias (inyección manual, sin librerías extra).
 * Crea la base de datos con el driver de cada plataforma y el repositorio compartido.
 */
class AppContainer(driverFactory: DatabaseDriverFactory) {
    private val baseDeDatos = ImpulsaDatabase(driverFactory.createDriver())

    val repositorio: ImpulsaRepository = ImpulsaRepositoryImpl(baseDeDatos).also { it.sembrarSiVacia() }
}

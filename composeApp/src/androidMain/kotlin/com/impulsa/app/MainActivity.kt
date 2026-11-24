package com.impulsa.app

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import com.impulsa.app.data.DatabaseDriverFactory

/** Única Activity de Android: solo aloja la UI compartida (Compose Multiplatform). */
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            ImpulsaApp(driverFactory = DatabaseDriverFactory(applicationContext))
        }
    }
}

package com.impulsa.app.ui.login

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.impulsa.app.core.Textos
import com.impulsa.app.resources.Res
import com.impulsa.app.resources.impulsa
import com.impulsa.app.ui.theme.ImpulsaIcons
import kotlinx.coroutines.launch
import org.jetbrains.compose.resources.painterResource

/** PANTALLA 1 — Inicio de sesión. */
@Composable
fun LoginScreen(
    viewModel: LoginViewModel,
    alIniciarSesion: () -> Unit,
    alAbrirAcercaDe: () -> Unit
) {
    val estado by viewModel.estado.collectAsState()
    val snackbarHostState = remember { SnackbarHostState() }
    val scope = rememberCoroutineScope()

    Scaffold(
        snackbarHost = { SnackbarHost(snackbarHostState) },
        containerColor = MaterialTheme.colorScheme.surface
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .verticalScroll(rememberScrollState())
                .padding(horizontal = 28.dp, vertical = 32.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Top
        ) {
            Column(
                modifier = Modifier.widthIn(max = 440.dp).fillMaxWidth(),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                // Logo oficial (el JPG trae fondo blanco, por eso va sobre una tarjeta blanca)
                Surface(
                    shape = RoundedCornerShape(28.dp),
                    color = Color.White,
                    shadowElevation = 2.dp
                ) {
                    Image(
                        painter = painterResource(Res.drawable.impulsa),
                        contentDescription = Textos.DESC_LOGO,
                        modifier = Modifier.size(220.dp)
                    )
                }

                Text(
                    text = Textos.LOGIN_SUBTITULO,
                    modifier = Modifier.padding(top = 20.dp, bottom = 32.dp),
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )

                // Campo: correo
                OutlinedTextField(
                    value = estado.correo,
                    onValueChange = viewModel::alCambiarCorreo,
                    modifier = Modifier.fillMaxWidth(),
                    label = { Text(Textos.LOGIN_CORREO) },
                    leadingIcon = { Icon(ImpulsaIcons.Correo, contentDescription = null) },
                    isError = estado.errorCorreo != null,
                    supportingText = estado.errorCorreo?.let { mensaje -> { Text(mensaje) } },
                    singleLine = true,
                    shape = RoundedCornerShape(16.dp),
                    keyboardOptions = KeyboardOptions(
                        keyboardType = KeyboardType.Email,
                        imeAction = ImeAction.Next
                    )
                )

                Spacer(Modifier.height(6.dp))

                // Campo: contraseña
                OutlinedTextField(
                    value = estado.password,
                    onValueChange = viewModel::alCambiarPassword,
                    modifier = Modifier.fillMaxWidth(),
                    label = { Text(Textos.LOGIN_PASSWORD) },
                    trailingIcon = {
                        TextButton(onClick = viewModel::alternarPassword) {
                            Text(if (estado.mostrarPassword) "Ocultar" else "Mostrar")
                        }
                    },
                    visualTransformation = if (estado.mostrarPassword) {
                        VisualTransformation.None
                    } else {
                        PasswordVisualTransformation()
                    },
                    isError = estado.errorPassword != null,
                    supportingText = estado.errorPassword?.let { mensaje -> { Text(mensaje) } },
                    singleLine = true,
                    shape = RoundedCornerShape(16.dp),
                    keyboardOptions = KeyboardOptions(
                        keyboardType = KeyboardType.Password,
                        imeAction = ImeAction.Done
                    ),
                    keyboardActions = KeyboardActions(onDone = { viewModel.entrar(alIniciarSesion) })
                )

                Spacer(Modifier.height(22.dp))

                // Botón principal
                Button(
                    onClick = { viewModel.entrar(alIniciarSesion) },
                    modifier = Modifier.fillMaxWidth().height(56.dp),
                    shape = RoundedCornerShape(18.dp)
                ) {
                    Text(Textos.LOGIN_ENTRAR, fontSize = 16.sp)
                }

                TextButton(
                    onClick = {
                        scope.launch { snackbarHostState.showSnackbar(Textos.LOGIN_RECUPERACION) }
                    },
                    modifier = Modifier.padding(top = 8.dp)
                ) {
                    Text(Textos.LOGIN_OLVIDE)
                }

                TextButton(onClick = alAbrirAcercaDe) {
                    Text(Textos.ACERCA_DE)
                }
            }
        }
    }
}

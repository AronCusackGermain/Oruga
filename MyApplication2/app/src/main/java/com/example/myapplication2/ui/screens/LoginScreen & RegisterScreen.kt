package com.example.myapplication.ui.screens

import android.widget.Toast
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.example.myapplication.ui.viewmodel.UsuarioViewModel

/**
 * Pantalla de Login MEJORADA - Experiencia 3
 * Requisito 3: Control de excepciones robusto y mensajes intuitivos
 */
@Composable
fun LoginScreen(
    navController: NavController,
    usuarioViewModel: UsuarioViewModel
) {
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var passwordVisible by remember { mutableStateOf(false) }
    var isLoading by remember { mutableStateOf(false) }
    var errorMessage by remember { mutableStateOf("") }
    var intentosFallidos by remember { mutableStateOf(0) }
    var bloqueadoHasta by remember { mutableStateOf<Long?>(null) }

    // Validaciones en tiempo real
    var emailError by remember { mutableStateOf("") }
    var passwordError by remember { mutableStateOf("") }

    val context = LocalContext.current
    val maxIntentos = 5
    val tiempoBloqueo = 30000L // 30 segundos

    // Verificar si estÃ¡ bloqueado
    val estaBloqueado = bloqueadoHasta?.let { it > System.currentTimeMillis() } ?: false
    val segundosRestantes = if (estaBloqueado) {
        ((bloqueadoHasta!! - System.currentTimeMillis()) / 1000).toInt()
    } else 0

    // Actualizar cada segundo si estÃ¡ bloqueado
    LaunchedEffect(estaBloqueado) {
        if (estaBloqueado) {
            while (bloqueadoHasta!! > System.currentTimeMillis()) {
                kotlinx.coroutines.delay(1000)
            }
            bloqueadoHasta = null
            intentosFallidos = 0
        }
    }

    // ValidaciÃ³n de email en tiempo real
    LaunchedEffect(email) {
        emailError = when {
            email.isEmpty() -> ""
            !android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches() ->
                "Formato de email invÃ¡lido"
            else -> ""
        }
    }

    // ValidaciÃ³n de contraseÃ±a en tiempo real
    LaunchedEffect(password) {
        passwordError = when {
            password.isEmpty() -> ""
            password.length < 6 -> "MÃ­nimo 6 caracteres"
            else -> ""
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        // Logo
        Icon(
            imageVector = Icons.Default.BugReport,
            contentDescription = "Logo",
            tint = MaterialTheme.colorScheme.primary,
            modifier = Modifier.size(80.dp)
        )

        Spacer(modifier = Modifier.height(16.dp))

        Text(
            text = "Iniciar SesiÃ³n",
            fontSize = 28.sp,
            fontWeight = FontWeight.Bold
        )

        Spacer(modifier = Modifier.height(8.dp))

        Text(
            text = "Bienvenido de vuelta a Oruga",
            fontSize = 14.sp,
            color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f)
        )

        Spacer(modifier = Modifier.height(32.dp))

        // Mensaje de error general
        if (errorMessage.isNotEmpty()) {
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.errorContainer
                )
            ) {
                Row(
                    modifier = Modifier.padding(12.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        imageVector = Icons.Default.Error,
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.error,
                        modifier = Modifier.size(24.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = errorMessage,
                        color = MaterialTheme.colorScheme.onErrorContainer,
                        fontSize = 14.sp
                    )
                }
            }
            Spacer(modifier = Modifier.height(16.dp))
        }

        // Mensaje de bloqueo temporal
        if (estaBloqueado) {
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.errorContainer
                )
            ) {
                Column(
                    modifier = Modifier.padding(16.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Icon(
                        imageVector = Icons.Default.Lock,
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.error,
                        modifier = Modifier.size(48.dp)
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = "Demasiados intentos fallidos",
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onErrorContainer
                    )
                    Text(
                        text = "Espera $segundosRestantes segundos",
                        fontSize = 14.sp,
                        color = MaterialTheme.colorScheme.onErrorContainer
                    )
                }
            }
            Spacer(modifier = Modifier.height(16.dp))
        }

        // Campo de email
        OutlinedTextField(
            value = email,
            onValueChange = {
                email = it
                errorMessage = "" // Limpiar error general
            },
            label = { Text("Correo electrÃ³nico") },
            leadingIcon = {
                Icon(Icons.Default.Email, contentDescription = "Email")
            },
            trailingIcon = {
                if (emailError.isNotEmpty() && email.isNotEmpty()) {
                    Icon(
                        Icons.Default.Error,
                        contentDescription = "Error",
                        tint = MaterialTheme.colorScheme.error
                    )
                } else if (emailError.isEmpty() && email.isNotEmpty()) {
                    Icon(
                        Icons.Default.CheckCircle,
                        contentDescription = "VÃ¡lido",
                        tint = MaterialTheme.colorScheme.primary
                    )
                }
            },
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email),
            modifier = Modifier.fillMaxWidth(),
            singleLine = true,
            isError = emailError.isNotEmpty() && email.isNotEmpty(),
            supportingText = {
                if (emailError.isNotEmpty() && email.isNotEmpty()) {
                    Text(
                        text = emailError,
                        color = MaterialTheme.colorScheme.error
                    )
                }
            },
            enabled = !estaBloqueado
        )

        Spacer(modifier = Modifier.height(16.dp))

        // Campo de contraseÃ±a
        OutlinedTextField(
            value = password,
            onValueChange = {
                password = it
                errorMessage = "" // Limpiar error general
            },
            label = { Text("ContraseÃ±a") },
            leadingIcon = {
                Icon(Icons.Default.Lock, contentDescription = "Password")
            },
            trailingIcon = {
                IconButton(onClick = { passwordVisible = !passwordVisible }) {
                    Icon(
                        imageVector = if (passwordVisible) Icons.Default.Visibility
                        else Icons.Default.VisibilityOff,
                        contentDescription = if (passwordVisible) "Ocultar contraseÃ±a"
                        else "Mostrar contraseÃ±a"
                    )
                }
            },
            visualTransformation = if (passwordVisible) VisualTransformation.None
            else PasswordVisualTransformation(),
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
            modifier = Modifier.fillMaxWidth(),
            singleLine = true,
            isError = passwordError.isNotEmpty() && password.isNotEmpty(),
            supportingText = {
                if (passwordError.isNotEmpty() && password.isNotEmpty()) {
                    Text(
                        text = passwordError,
                        color = MaterialTheme.colorScheme.error
                    )
                }
            },
            enabled = !estaBloqueado
        )

        // Indicador de intentos fallidos
        if (intentosFallidos > 0 && !estaBloqueado) {
            Spacer(modifier = Modifier.height(8.dp))
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.End
            ) {
                Text(
                    text = "Intentos: $intentosFallidos/$maxIntentos",
                    fontSize = 12.sp,
                    color = if (intentosFallidos >= 3)
                        MaterialTheme.colorScheme.error
                    else
                        MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f)
                )
            }
        }

        Spacer(modifier = Modifier.height(24.dp))

        // BotÃ³n de login
        Button(
            onClick = {
                // Validar antes de enviar
                if (email.isBlank()) {
                    errorMessage = "Por favor ingresa tu email"
                    return@Button
                }

                if (password.isBlank()) {
                    errorMessage = "Por favor ingresa tu contraseÃ±a"
                    return@Button
                }

                if (emailError.isNotEmpty() || passwordError.isNotEmpty()) {
                    errorMessage = "Por favor corrige los errores antes de continuar"
                    return@Button
                }

                isLoading = true
                errorMessage = ""

                usuarioViewModel.login(email, password) { success, message ->
                    isLoading = false

                    if (success) {
                        // Login exitoso
                        intentosFallidos = 0
                        bloqueadoHasta = null
                        Toast.makeText(
                            context,
                            message,
                            Toast.LENGTH_SHORT
                        ).show()
                        navController.navigate("home") {
                            popUpTo("login") { inclusive = true }
                        }
                    } else {
                        // Login fallido
                        intentosFallidos++
                        errorMessage = message

                        // Bloquear si excede intentos
                        if (intentosFallidos >= maxIntentos) {
                            bloqueadoHasta = System.currentTimeMillis() + tiempoBloqueo
                            errorMessage = "Demasiados intentos fallidos. Bloqueado por 30 segundos."
                        }
                    }
                }
            },
            modifier = Modifier
                .fillMaxWidth()
                .height(56.dp),
            enabled = !isLoading && !estaBloqueado && emailError.isEmpty() && passwordError.isEmpty()
        ) {
            if (isLoading) {
                Row(
                    horizontalArrangement = Arrangement.Center,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    CircularProgressIndicator(
                        color = MaterialTheme.colorScheme.onPrimary,
                        modifier = Modifier.size(24.dp),
                        strokeWidth = 2.dp
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("Validando...")
                }
            } else {
                Text("INGRESAR", fontSize = 16.sp)
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Link a registro
        Row(
            horizontalArrangement = Arrangement.Center,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "Â¿No tienes cuenta? ",
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
            Text(
                text = "RegÃ­strate",
                color = MaterialTheme.colorScheme.primary,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.clickable {
                    navController.navigate("registro")
                }
            )
        }

        Spacer(modifier = Modifier.height(24.dp))

        // InformaciÃ³n de ayuda
        Card(
            modifier = Modifier.fillMaxWidth(),
            colors = CardDefaults.cardColors(
                containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f)
            )
        ) {
            Row(
                modifier = Modifier.padding(12.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(
                    imageVector = Icons.Default.Info,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.primary,
                    modifier = Modifier.size(20.dp)
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = "Usa tus credenciales de registro o el cÃ³digo de moderador si aplica",
                    fontSize = 12.sp,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }
    }
}

/**
 * Pantalla de Registro
 */
@Composable
fun RegisterScreen(
    navController: NavController,
    usuarioViewModel: UsuarioViewModel
) {
    // Estados de los campos
    var email by remember { mutableStateOf("") }
    var nombreUsuario by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var confirmPassword by remember { mutableStateOf("") }
    var codigoModerador by remember { mutableStateOf("") }

    // Estados de visibilidad
    var passwordVisible by remember { mutableStateOf(false) }
    var confirmPasswordVisible by remember { mutableStateOf(false) }
    var mostrarCodigoModerador by remember { mutableStateOf(false) }

    // Estados de validaciÃ³n
    var isLoading by remember { mutableStateOf(false) }
    var emailError by remember { mutableStateOf("") }
    var nombreUsuarioError by remember { mutableStateOf("") }
    var passwordError by remember { mutableStateOf("") }
    var confirmPasswordError by remember { mutableStateOf("") }

    // Estado de fortaleza de contraseÃ±a
    var passwordStrength by remember { mutableStateOf(0) }

    val context = LocalContext.current

    // ========================================
    // VALIDACIONES EN TIEMPO REAL
    // ========================================

    // ValidaciÃ³n de email
    LaunchedEffect(email) {
        emailError = when {
            email.isEmpty() -> ""
            email.length < 5 -> "Email muy corto"
            !email.contains("@") -> "Debe contener @"
            !android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches() ->
                "Formato de email invÃ¡lido"
            email.endsWith("@duocuc.cl") || email.endsWith("@duoc.cl") -> "" // Email vÃ¡lido de Duoc
            else -> ""
        }
    }

    // ValidaciÃ³n de nombre de usuario
    LaunchedEffect(nombreUsuario) {
        nombreUsuarioError = when {
            nombreUsuario.isEmpty() -> ""
            nombreUsuario.length < 3 -> "MÃ­nimo 3 caracteres"
            nombreUsuario.length > 20 -> "MÃ¡ximo 20 caracteres"
            !nombreUsuario.matches(Regex("^[a-zA-Z0-9_]+$")) ->
                "Solo letras, nÃºmeros y guiÃ³n bajo"
            else -> ""
        }
    }

    // ValidaciÃ³n de contraseÃ±a y cÃ¡lculo de fortaleza
    LaunchedEffect(password) {
        passwordError = when {
            password.isEmpty() -> ""
            password.length < 6 -> "MÃ­nimo 6 caracteres"
            password.length > 30 -> "MÃ¡ximo 30 caracteres"
            else -> ""
        }

        // Calcular fortaleza de contraseÃ±a (0-4)
        passwordStrength = when {
            password.isEmpty() -> 0
            password.length < 6 -> 1
            password.length < 8 -> 2
            password.length >= 8 && password.any { it.isDigit() } -> 3
            password.length >= 8 && password.any { it.isDigit() } &&
                    password.any { it.isUpperCase() } -> 4
            else -> 2
        }
    }

    // ValidaciÃ³n de confirmaciÃ³n de contraseÃ±a
    LaunchedEffect(confirmPassword, password) {
        confirmPasswordError = when {
            confirmPassword.isEmpty() -> ""
            password != confirmPassword -> "Las contraseÃ±as no coinciden"
            else -> ""
        }
    }

    // ========================================
    // FUNCIONES DE VALIDACIÃ“N FINAL
    // ========================================

    fun validarFormulario(): Pair<Boolean, String> {
        // Validar campos vacÃ­os
        if (email.isBlank()) return Pair(false, "âŒ El email es obligatorio")
        if (nombreUsuario.isBlank()) return Pair(false, "âŒ El nombre de usuario es obligatorio")
        if (password.isBlank()) return Pair(false, "âŒ La contraseÃ±a es obligatoria")
        if (confirmPassword.isBlank()) return Pair(false, "âŒ Debes confirmar tu contraseÃ±a")

        // Validar errores existentes
        if (emailError.isNotEmpty()) return Pair(false, "âŒ $emailError")
        if (nombreUsuarioError.isNotEmpty()) return Pair(false, "âŒ $nombreUsuarioError")
        if (passwordError.isNotEmpty()) return Pair(false, "âŒ $passwordError")
        if (confirmPasswordError.isNotEmpty()) return Pair(false, "âŒ Las contraseÃ±as no coinciden")

        // ValidaciÃ³n final de coincidencia
        if (password != confirmPassword) {
            return Pair(false, "âŒ Las contraseÃ±as no coinciden")
        }

        // Si todo estÃ¡ bien
        return Pair(true, "")
    }

    fun realizarRegistro() {
        val (esValido, mensajeError) = validarFormulario()

        if (!esValido) {
            Toast.makeText(context, mensajeError, Toast.LENGTH_SHORT).show()
            return
        }

        isLoading = true

        // Llamar al ViewModel con manejo de excepciones
        usuarioViewModel.registrar(
            email = email.trim(),
            password = password,
            nombreUsuario = nombreUsuario.trim(),
            codigoModerador = if (mostrarCodigoModerador) codigoModerador.trim() else ""
        ) { success, message ->
            isLoading = false

            if (success) {
                Toast.makeText(
                    context,
                    "âœ… $message",
                    Toast.LENGTH_LONG
                ).show()

                // Navegar a home y limpiar stack
                navController.navigate("home") {
                    popUpTo("registro") { inclusive = true }
                    popUpTo("login") { inclusive = true }
                }
            } else {
                // Mostrar error especÃ­fico
                Toast.makeText(
                    context,
                    "âŒ $message",
                    Toast.LENGTH_LONG
                ).show()
            }
        }
    }

    // ========================================
    // UI PRINCIPAL
    // ========================================

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        // Logo
        Icon(
            imageVector = Icons.Default.PersonAdd,
            contentDescription = "Registro",
            tint = MaterialTheme.colorScheme.primary,
            modifier = Modifier.size(72.dp)
        )

        Spacer(modifier = Modifier.height(16.dp))

        Text(
            text = "Crear Cuenta",
            fontSize = 32.sp,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.onSurface
        )

        Spacer(modifier = Modifier.height(8.dp))

        Text(
            text = "Ãšnete a la comunidad Oruga",
            fontSize = 14.sp,
            color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f)
        )

        Spacer(modifier = Modifier.height(32.dp))

        // ========================================
        // CAMPO: EMAIL
        // ========================================
        OutlinedTextField(
            value = email,
            onValueChange = { email = it },
            label = { Text("Correo electrÃ³nico *") },
            placeholder = { Text("usuario@duocuc.cl") },
            leadingIcon = {
                Icon(Icons.Default.Email, contentDescription = "Email")
            },
            trailingIcon = {
                when {
                    email.isEmpty() -> null
                    emailError.isNotEmpty() -> Icon(
                        Icons.Default.Error,
                        contentDescription = "Error",
                        tint = MaterialTheme.colorScheme.error
                    )
                    else -> Icon(
                        Icons.Default.CheckCircle,
                        contentDescription = "VÃ¡lido",
                        tint = Color(0xFF4CAF50)
                    )
                }
            },
            isError = emailError.isNotEmpty() && email.isNotEmpty(),
            supportingText = {
                if (emailError.isNotEmpty() && email.isNotEmpty()) {
                    Text(
                        text = emailError,
                        color = MaterialTheme.colorScheme.error,
                        fontSize = 12.sp
                    )
                }
            },
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email),
            modifier = Modifier.fillMaxWidth(),
            singleLine = true,
            enabled = !isLoading
        )

        Spacer(modifier = Modifier.height(12.dp))

        // ========================================
        // CAMPO: NOMBRE DE USUARIO
        // ========================================
        OutlinedTextField(
            value = nombreUsuario,
            onValueChange = { nombreUsuario = it },
            label = { Text("Nombre de usuario *") },
            placeholder = { Text("usuario123") },
            leadingIcon = {
                Icon(Icons.Default.Person, contentDescription = "Usuario")
            },
            trailingIcon = {
                when {
                    nombreUsuario.isEmpty() -> null
                    nombreUsuarioError.isNotEmpty() -> Icon(
                        Icons.Default.Error,
                        contentDescription = "Error",
                        tint = MaterialTheme.colorScheme.error
                    )
                    else -> Icon(
                        Icons.Default.CheckCircle,
                        contentDescription = "VÃ¡lido",
                        tint = Color(0xFF4CAF50)
                    )
                }
            },
            isError = nombreUsuarioError.isNotEmpty() && nombreUsuario.isNotEmpty(),
            supportingText = {
                when {
                    nombreUsuarioError.isNotEmpty() && nombreUsuario.isNotEmpty() -> {
                        Text(
                            text = nombreUsuarioError,
                            color = MaterialTheme.colorScheme.error,
                            fontSize = 12.sp
                        )
                    }
                    nombreUsuario.isNotEmpty() -> {
                        Text(
                            text = "âœ“ Nombre de usuario vÃ¡lido",
                            color = Color(0xFF4CAF50),
                            fontSize = 12.sp
                        )
                    }
                }
            },
            modifier = Modifier.fillMaxWidth(),
            singleLine = true,
            enabled = !isLoading
        )

        Spacer(modifier = Modifier.height(12.dp))

        // ========================================
        // CAMPO: CONTRASEÃ‘A
        // ========================================
        OutlinedTextField(
            value = password,
            onValueChange = { password = it },
            label = { Text("ContraseÃ±a *") },
            placeholder = { Text("MÃ­nimo 6 caracteres") },
            leadingIcon = {
                Icon(Icons.Default.Lock, contentDescription = "ContraseÃ±a")
            },
            trailingIcon = {
                IconButton(onClick = { passwordVisible = !passwordVisible }) {
                    Icon(
                        if (passwordVisible) Icons.Default.Visibility
                        else Icons.Default.VisibilityOff,
                        contentDescription = if (passwordVisible) "Ocultar" else "Mostrar"
                    )
                }
            },
            isError = passwordError.isNotEmpty() && password.isNotEmpty(),
            supportingText = {
                if (passwordError.isNotEmpty() && password.isNotEmpty()) {
                    Text(
                        text = passwordError,
                        color = MaterialTheme.colorScheme.error,
                        fontSize = 12.sp
                    )
                }
            },
            visualTransformation = if (passwordVisible)
                VisualTransformation.None
            else
                PasswordVisualTransformation(),
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
            modifier = Modifier.fillMaxWidth(),
            singleLine = true,
            enabled = !isLoading
        )

        // Indicador de fortaleza de contraseÃ±a
        if (password.isNotEmpty()) {
            Spacer(modifier = Modifier.height(8.dp))
            PasswordStrengthIndicator(strength = passwordStrength)
        }

        Spacer(modifier = Modifier.height(12.dp))

        // ========================================
        // CAMPO: CONFIRMAR CONTRASEÃ‘A
        // ========================================
        OutlinedTextField(
            value = confirmPassword,
            onValueChange = { confirmPassword = it },
            label = { Text("Confirmar contraseÃ±a *") },
            placeholder = { Text("Repite tu contraseÃ±a") },
            leadingIcon = {
                Icon(Icons.Default.Lock, contentDescription = "Confirmar")
            },
            trailingIcon = {
                Row {
                    IconButton(onClick = { confirmPasswordVisible = !confirmPasswordVisible }) {
                        Icon(
                            if (confirmPasswordVisible) Icons.Default.Visibility
                            else Icons.Default.VisibilityOff,
                            contentDescription = if (confirmPasswordVisible) "Ocultar" else "Mostrar"
                        )
                    }
                    if (confirmPassword.isNotEmpty()) {
                        if (confirmPasswordError.isEmpty()) {
                            Icon(
                                Icons.Default.CheckCircle,
                                contentDescription = "Coincide",
                                tint = Color(0xFF4CAF50)
                            )
                        } else {
                            Icon(
                                Icons.Default.Error,
                                contentDescription = "No coincide",
                                tint = MaterialTheme.colorScheme.error
                            )
                        }
                    }
                }
            },
            isError = confirmPasswordError.isNotEmpty() && confirmPassword.isNotEmpty(),
            supportingText = {
                when {
                    confirmPasswordError.isNotEmpty() && confirmPassword.isNotEmpty() -> {
                        Text(
                            text = confirmPasswordError,
                            color = MaterialTheme.colorScheme.error,
                            fontSize = 12.sp
                        )
                    }
                    confirmPassword.isNotEmpty() && confirmPasswordError.isEmpty() -> {
                        Text(
                            text = "âœ“ Las contraseÃ±as coinciden",
                            color = Color(0xFF4CAF50),
                            fontSize = 12.sp
                        )
                    }
                }
            },
            visualTransformation = if (confirmPasswordVisible)
                VisualTransformation.None
            else
                PasswordVisualTransformation(),
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
            modifier = Modifier.fillMaxWidth(),
            singleLine = true,
            enabled = !isLoading
        )

        Spacer(modifier = Modifier.height(20.dp))

        // ========================================
        // SECCIÃ“N: CÃ“DIGO DE MODERADOR (OPCIONAL)
        // ========================================
        Card(
            modifier = Modifier.fillMaxWidth(),
            colors = CardDefaults.cardColors(
                containerColor = MaterialTheme.colorScheme.secondaryContainer.copy(alpha = 0.3f)
            )
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            imageVector = Icons.Default.Shield,
                            contentDescription = "Moderador",
                            tint = MaterialTheme.colorScheme.primary,
                            modifier = Modifier.size(20.dp)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = "Â¿Eres moderador?",
                            fontSize = 15.sp,
                            fontWeight = FontWeight.Medium
                        )
                    }
                    Switch(
                        checked = mostrarCodigoModerador,
                        onCheckedChange = { mostrarCodigoModerador = it },
                        enabled = !isLoading
                    )
                }

                AnimatedVisibility(visible = mostrarCodigoModerador) {
                    Column {
                        Spacer(modifier = Modifier.height(12.dp))

                        OutlinedTextField(
                            value = codigoModerador,
                            onValueChange = { codigoModerador = it },
                            label = { Text("CÃ³digo de moderador") },
                            placeholder = { Text("Ingresa tu cÃ³digo") },
                            leadingIcon = {
                                Icon(Icons.Default.Shield, contentDescription = null)
                            },
                            modifier = Modifier.fillMaxWidth(),
                            singleLine = true,
                            enabled = !isLoading,
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedBorderColor = MaterialTheme.colorScheme.primary,
                                focusedLabelColor = MaterialTheme.colorScheme.primary
                            )
                        )

                        Spacer(modifier = Modifier.height(8.dp))

                        Text(
                            text = "â„¹ï¸ Si tienes un cÃ³digo de moderador proporcionado por los administradores, ingrÃ©salo aquÃ­. De lo contrario, puedes dejarlo en blanco.",
                            fontSize = 11.sp,
                            color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f),
                            lineHeight = 14.sp
                        )
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(24.dp))

        // ========================================
        // BOTÃ“N: REGISTRARSE
        // ========================================
        Button(
            onClick = { realizarRegistro() },
            modifier = Modifier
                .fillMaxWidth()
                .height(56.dp),
            enabled = !isLoading,
            colors = ButtonDefaults.buttonColors(
                containerColor = MaterialTheme.colorScheme.primary
            )
        ) {
            if (isLoading) {
                CircularProgressIndicator(
                    color = MaterialTheme.colorScheme.onPrimary,
                    modifier = Modifier.size(24.dp),
                    strokeWidth = 2.dp
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text("REGISTRANDO...", fontSize = 16.sp, fontWeight = FontWeight.Bold)
            } else {
                Icon(
                    imageVector = Icons.Default.PersonAdd,
                    contentDescription = null,
                    modifier = Modifier.size(20.dp)
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text("CREAR CUENTA", fontSize = 16.sp, fontWeight = FontWeight.Bold)
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Texto de tÃ©rminos y condiciones
        Text(
            text = "Al registrarte, aceptas nuestros TÃ©rminos y Condiciones",
            fontSize = 11.sp,
            color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.5f),
            modifier = Modifier.padding(horizontal = 16.dp)
        )

        Spacer(modifier = Modifier.height(16.dp))

        // Link a Login
        Row(
            horizontalArrangement = Arrangement.Center,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "Â¿Ya tienes cuenta? ",
                fontSize = 14.sp,
                color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f)
            )
            Text(
                text = "Inicia sesiÃ³n",
                fontSize = 14.sp,
                color = MaterialTheme.colorScheme.primary,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.clickable(enabled = !isLoading) {
                    navController.navigate("login") {
                        popUpTo("registro") { inclusive = true }
                    }
                }
            )
        }
    }
}

/**
 * Componente: Indicador de fortaleza de contraseÃ±a
 * Muestra una barra visual que indica quÃ© tan segura es la contraseÃ±a
 */
@Composable
fun PasswordStrengthIndicator(strength: Int) {
    val strengthColor = when (strength) {
        0 -> Color.Gray
        1 -> Color(0xFFF44336) // Rojo
        2 -> Color(0xFFFF9800) // Naranja
        3 -> Color(0xFFFFEB3B) // Amarillo
        4 -> Color(0xFF4CAF50) // Verde
        else -> Color.Gray
    }

    val strengthText = when (strength) {
        0 -> ""
        1 -> "Muy dÃ©bil"
        2 -> "DÃ©bil"
        3 -> "Media"
        4 -> "Fuerte"
        else -> ""
    }

    val animatedProgress by animateFloatAsState(
        targetValue = strength / 4f,
        label = "password_strength"
    )

    val animatedColor by animateColorAsState(
        targetValue = strengthColor,
        label = "strength_color"
    )

    Column(modifier = Modifier.fillMaxWidth()) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "Fortaleza de contraseÃ±a:",
                fontSize = 12.sp,
                color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f)
            )
            Text(
                text = strengthText,
                fontSize = 12.sp,
                fontWeight = FontWeight.Bold,
                color = animatedColor
            )
        }

        Spacer(modifier = Modifier.height(4.dp))

        LinearProgressIndicator(
            progress = { animatedProgress },
            modifier = Modifier
                .fillMaxWidth()
                .height(8.dp),
            color = animatedColor,
            trackColor = MaterialTheme.colorScheme.surfaceVariant,
        )

        Spacer(modifier = Modifier.height(4.dp))

        Text(
            text = "ðŸ’¡ Usa mayÃºsculas, nÃºmeros y al menos 8 caracteres para una contraseÃ±a fuerte",
            fontSize = 10.sp,
            color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.5f),
            lineHeight = 12.sp
        )
    }
}

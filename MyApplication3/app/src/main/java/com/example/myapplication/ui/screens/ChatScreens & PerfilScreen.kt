package com.example.myapplication.ui.screens

import android.net.Uri
import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.example.myapplication.data.remote.dto.MensajeResponse
import com.example.myapplication.ui.componentes.ImageFromPath
import com.example.myapplication.ui.componentes.ImagePicker
import com.example.myapplication.ui.viewmodel.MensajeViewModel
import com.example.myapplication.ui.viewmodel.UsuarioViewModel
import com.example.myapplication.utils.ImageUtils
import kotlinx.coroutines.launch

/**
 * Pantalla de Chat Grupal - ACTUALIZADA con soporte de imÃ¡genes
 */
@Composable
fun ChatGrupalScreen(
    usuarioViewModel: UsuarioViewModel,
    mensajeViewModel: MensajeViewModel
) {
    val usuarioActual by usuarioViewModel.usuarioActual.collectAsState()
    val mensajes by mensajeViewModel.mensajesGrupales.collectAsState()
    var textoMensaje by remember { mutableStateOf("") }
    var imagenSeleccionada by remember { mutableStateOf<Uri?>(null) }
    var mostrarSelectorImagen by remember { mutableStateOf(false) }

    val context = LocalContext.current
    val listState = rememberLazyListState()
    val coroutineScope = rememberCoroutineScope()

    LaunchedEffect(Unit) {
        mensajeViewModel.cargarMensajesGrupales()
    }

    // Auto scroll cuando llegan nuevos mensajes
    LaunchedEffect(mensajes.size) {
        if (mensajes.isNotEmpty()) {
            coroutineScope.launch {
                listState.animateScrollToItem(mensajes.size - 1)
            }
        }
    }

    Column(
        modifier = Modifier.fillMaxSize()
    ) {
        // Lista de mensajes
        LazyColumn(
            modifier = Modifier
                .weight(1f)
                .fillMaxWidth()
                .padding(horizontal = 8.dp),
            state = listState
        ) {
            items(mensajes) { mensaje ->
                MensajeBurbujaConImagen(
                    mensaje = mensaje,
                    esPropio = mensaje.remitenteId == usuarioActual?.id
                )
            }
        }

        // Preview de imagen seleccionada
        if (imagenSeleccionada != null) {
            Surface(
                modifier = Modifier.fillMaxWidth(),
                color = MaterialTheme.colorScheme.surfaceVariant
            ) {
                Row(
                    modifier = Modifier.padding(8.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    ImagePicker(
                        imageUri = imagenSeleccionada,
                        onImageSelected = { imagenSeleccionada = it },
                        onImageRemoved = { imagenSeleccionada = null },
                        modifier = Modifier
                            .height(80.dp)
                            .fillMaxWidth()
                    )
                }
            }
        }

        // Campo de entrada
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(8.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // BotÃ³n adjuntar imagen
            IconButton(
                onClick = { mostrarSelectorImagen = true }
            ) {
                Icon(
                    imageVector = Icons.Default.Image,
                    contentDescription = "Adjuntar imagen",
                    tint = MaterialTheme.colorScheme.primary
                )
            }

            OutlinedTextField(
                value = textoMensaje,
                onValueChange = { textoMensaje = it },
                modifier = Modifier.weight(1f),
                placeholder = { Text("Escribe un mensaje...") },
                shape = RoundedCornerShape(24.dp),
                maxLines = 3
            )

            Spacer(modifier = Modifier.width(8.dp))

            IconButton(
                onClick = {
                    if ((textoMensaje.isNotBlank() || imagenSeleccionada != null) && usuarioActual != null) {
                        // Guardar imagen si existe
                        val imagenPath = imagenSeleccionada?.let { uri ->
                            ImageUtils.guardarImagenDesdeUri(context, uri, "mensajes")
                        } ?: ""

                        mensajeViewModel.enviarMensajeGrupal(
                            remitenteId = usuarioActual!!.id,
                            remitenteNombre = usuarioActual!!.nombreUsuario,
                            contenido = textoMensaje,
                            imagenPath = imagenPath
                        ) { success, message ->
                            if (success) {
                                textoMensaje = ""
                                imagenSeleccionada = null
                            } else {
                                Toast.makeText(context, message, Toast.LENGTH_SHORT).show()
                            }
                        }
                    }
                },
                modifier = Modifier
                    .size(48.dp)
                    .background(
                        color = MaterialTheme.colorScheme.primary,
                        shape = CircleShape
                    )
            ) {
                Icon(
                    imageVector = Icons.Default.Send,
                    contentDescription = "Enviar",
                    tint = MaterialTheme.colorScheme.onPrimary
                )
            }
        }
    }

    // DiÃ¡logo selector de imagen
    if (mostrarSelectorImagen) {
        AlertDialog(
            onDismissRequest = { mostrarSelectorImagen = false },
            title = { Text("Adjuntar Imagen") },
            text = {
                ImagePicker(
                    imageUri = imagenSeleccionada,
                    onImageSelected = {
                        imagenSeleccionada = it
                        mostrarSelectorImagen = false
                    },
                    onImageRemoved = {
                        imagenSeleccionada = null
                    }
                )
            },
            confirmButton = {
                TextButton(onClick = { mostrarSelectorImagen = false }) {
                    Text("Cerrar")
                }
            }
        )
    }
}

/**
 * Burbuja de mensaje CON soporte de imÃ¡genes
 */
@Composable
fun MensajeBurbujaConImagen(mensaje: MensajeResponse, esPropio: Boolean) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp),
        horizontalAlignment = if (esPropio) Alignment.End else Alignment.Start
    ) {
        if (!esPropio) {
            Text(
                text = mensaje.remitenteNombre,
                fontSize = 12.sp,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.primary,
                modifier = Modifier.padding(horizontal = 12.dp, vertical = 2.dp)
            )
        }

        Surface(
            shape = RoundedCornerShape(
                topStart = 16.dp,
                topEnd = 16.dp,
                bottomStart = if (esPropio) 16.dp else 4.dp,
                bottomEnd = if (esPropio) 4.dp else 16.dp
            ),
            color = if (esPropio)
                MaterialTheme.colorScheme.primary
            else
                MaterialTheme.colorScheme.surfaceVariant,
            modifier = Modifier.widthIn(max = 280.dp)
        ) {
            Column(modifier = Modifier.padding(12.dp)) {
                // Mostrar imagen si existe
                if (mensaje.imagenUrl.isNotBlank()) {
                    ImageFromPath(
                        path = mensaje.imagenUrl,
                        modifier = Modifier
                            .fillMaxWidth()
                            .heightIn(max = 200.dp)
                            .clip(RoundedCornerShape(8.dp))
                    )

                    if (mensaje.contenido.isNotBlank()) {
                        Spacer(modifier = Modifier.height(8.dp))
                    }
                }

                // Mostrar texto si existe
                if (mensaje.contenido.isNotBlank()) {
                    Text(
                        text = mensaje.contenido,
                        color = if (esPropio)
                            MaterialTheme.colorScheme.onPrimary
                        else
                            MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }
        }

        Text(
            text = formatearFecha(mensaje.fechaEnvio),
            fontSize = 10.sp,
            color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.5f),
            modifier = Modifier.padding(horizontal = 12.dp, vertical = 2.dp)
        )
    }
}

/**
 * Pantalla de lista de Chats Privados
 */
@Composable
fun ChatsPrivadosScreen(
    navController: NavController,
    usuarioViewModel: UsuarioViewModel,
    mensajeViewModel: MensajeViewModel
) {
    val todosLosUsuarios by usuarioViewModel.todosLosUsuarios.collectAsState()
    val usuarioActual by usuarioViewModel.usuarioActual.collectAsState()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        Text(
            text = "Chats Privados",
            fontSize = 24.sp,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.padding(bottom = 16.dp)
        )

        if (todosLosUsuarios.isEmpty()) {
            Box(
                modifier = Modifier.fillMaxSize(),
                contentAlignment = Alignment.Center
            ) {
                Text("No hay otros usuarios disponibles")
            }
        } else {
            LazyColumn(
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                items(todosLosUsuarios.filter { it.id != usuarioActual?.id }) { usuario ->
                    Card(
                        modifier = Modifier
                            .fillMaxWidth(),
                        onClick = {
                            navController.navigate("chat_privado/${usuario.id}")
                        }
                    ) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(16.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Box(
                                modifier = Modifier
                                    .size(48.dp)
                                    .clip(CircleShape)
                                    .background(MaterialTheme.colorScheme.primary),
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Person,
                                    contentDescription = null,
                                    tint = MaterialTheme.colorScheme.onPrimary
                                )
                            }

                            Spacer(modifier = Modifier.width(12.dp))

                            Column(modifier = Modifier.weight(1f)) {
                                Text(
                                    text = usuario.nombreUsuario,
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 16.sp
                                )
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Box(
                                        modifier = Modifier
                                            .size(8.dp)
                                            .clip(CircleShape)
                                            .background(
                                                if (usuario.estadoConexion)
                                                    MaterialTheme.colorScheme.primary
                                                else
                                                    MaterialTheme.colorScheme.outline
                                            )
                                    )
                                    Spacer(modifier = Modifier.width(6.dp))
                                    Text(
                                        text = if (usuario.estadoConexion) "En lÃ­nea" else "Desconectado",
                                        fontSize = 12.sp,
                                        color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f)
                                    )
                                }
                            }

                            Icon(
                                imageVector = Icons.Default.ChevronRight,
                                contentDescription = "Abrir chat"
                            )
                        }
                    }
                }
            }
        }
    }
}



/**
 * Pantalla de Chat Privado Individual - ACTUALIZADA con imÃ¡genes
 */
@Composable
fun ChatPrivadoScreen(
    navController: NavController,
    usuarioViewModel: UsuarioViewModel,
    mensajeViewModel: MensajeViewModel,
    otroUsuarioId: Int
) {
    val usuarioActual by usuarioViewModel.usuarioActual.collectAsState()
    val mensajes by mensajeViewModel.mensajesPrivados.collectAsState()
    var textoMensaje by remember { mutableStateOf("") }
    var imagenSeleccionada by remember { mutableStateOf<Uri?>(null) }
    var mostrarSelectorImagen by remember { mutableStateOf(false) }

    val context = LocalContext.current
    val listState = rememberLazyListState()
    val coroutineScope = rememberCoroutineScope()

    LaunchedEffect(otroUsuarioId) {
        if (usuarioActual != null) {
            mensajeViewModel.cargarMensajesPrivados(usuarioActual!!.id, otroUsuarioId.toLong())
        }
    }

    LaunchedEffect(mensajes.size) {
        if (mensajes.isNotEmpty()) {
            coroutineScope.launch {
                listState.animateScrollToItem(mensajes.size - 1)
            }
        }
    }

    Column(modifier = Modifier.fillMaxSize()) {
        // Lista de mensajes
        LazyColumn(
            modifier = Modifier
                .weight(1f)
                .fillMaxWidth()
                .padding(horizontal = 8.dp),
            state = listState
        ) {
            items(mensajes) { mensaje ->
                MensajeBurbujaConImagen(
                    mensaje = mensaje,
                    esPropio = mensaje.remitenteId == usuarioActual?.id
                )
            }
        }

        // Preview de imagen seleccionada
        if (imagenSeleccionada != null) {
            Surface(
                modifier = Modifier.fillMaxWidth(),
                color = MaterialTheme.colorScheme.surfaceVariant
            ) {
                Row(
                    modifier = Modifier.padding(8.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    ImagePicker(
                        imageUri = imagenSeleccionada,
                        onImageSelected = { imagenSeleccionada = it },
                        onImageRemoved = { imagenSeleccionada = null },
                        modifier = Modifier
                            .height(80.dp)
                            .fillMaxWidth()
                    )
                }
            }
        }

        // Campo de entrada
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(8.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // BotÃ³n adjuntar imagen
            IconButton(
                onClick = { mostrarSelectorImagen = true }
            ) {
                Icon(
                    imageVector = Icons.Default.Image,
                    contentDescription = "Adjuntar imagen",
                    tint = MaterialTheme.colorScheme.primary
                )
            }

            OutlinedTextField(
                value = textoMensaje,
                onValueChange = { textoMensaje = it },
                modifier = Modifier.weight(1f),
                placeholder = { Text("Escribe un mensaje...") },
                shape = RoundedCornerShape(24.dp),
                maxLines = 3
            )

            Spacer(modifier = Modifier.width(8.dp))

            IconButton(
                onClick = {
                    if ((textoMensaje.isNotBlank() || imagenSeleccionada != null) && usuarioActual != null) {
                        // Guardar imagen si existe
                        val imagenPath = imagenSeleccionada?.let { uri ->
                            ImageUtils.guardarImagenDesdeUri(context, uri, "mensajes")
                        } ?: ""

                        mensajeViewModel.enviarMensajePrivado(
                            remitenteId = usuarioActual!!.id,
                            remitenteNombre = usuarioActual!!.nombreUsuario,
                            destinatarioId = otroUsuarioId.toLong(),
                            contenido = textoMensaje,
                            imagenPath = imagenPath
                        ) { success, message ->
                            if (success) {
                                textoMensaje = ""
                                imagenSeleccionada = null
                            } else {
                                Toast.makeText(context, message, Toast.LENGTH_SHORT).show()
                            }
                        }
                    }
                },
                modifier = Modifier
                    .size(48.dp)
                    .background(
                        color = MaterialTheme.colorScheme.primary,
                        shape = CircleShape
                    )
            ) {
                Icon(
                    imageVector = Icons.Default.Send,
                    contentDescription = "Enviar",
                    tint = MaterialTheme.colorScheme.onPrimary
                )
            }
        }
    }

    // DiÃ¡logo selector de imagen
    if (mostrarSelectorImagen) {
        AlertDialog(
            onDismissRequest = { mostrarSelectorImagen = false },
            title = { Text("Adjuntar Imagen") },
            text = {
                ImagePicker(
                    imageUri = imagenSeleccionada,
                    onImageSelected = {
                        imagenSeleccionada = it
                        mostrarSelectorImagen = false
                    },
                    onImageRemoved = {
                        imagenSeleccionada = null
                    }
                )
            },
            confirmButton = {
                TextButton(onClick = { mostrarSelectorImagen = false }) {
                    Text("Cerrar")
                }
            }
        )
    }
}

/**
 * Pantalla de Perfil del Usuario
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PerfilScreen(
    navController: NavController,
    usuarioViewModel: UsuarioViewModel
) {
    val usuario by usuarioViewModel.usuarioActual.collectAsState()
    val isLoading by usuarioViewModel.isLoading.collectAsState()

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Mi Perfil") },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(Icons.Default.ArrowBack, "Volver")
                    }
                }
            )
        }
    ) { padding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding),
            contentAlignment = Alignment.Center
        ) {
            when {
                isLoading -> CircularProgressIndicator()
                usuario != null -> {
                    Column(
                        modifier = Modifier.padding(16.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        // Avatar
                        Surface(
                            modifier = Modifier.size(100.dp),
                            shape = CircleShape,
                            color = MaterialTheme.colorScheme.primaryContainer
                        ) {
                            Icon(
                                imageVector = Icons.Default.Person,
                                contentDescription = null,
                                modifier = Modifier
                                    .size(64.dp)
                                    .padding(16.dp)
                            )
                        }

                        Spacer(modifier = Modifier.height(16.dp))

                        Text(
                            text = usuario!!.nombreUsuario,
                            style = MaterialTheme.typography.headlineMedium
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(
                            text = usuario!!.email,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )

                        Spacer(modifier = Modifier.height(24.dp))

                        // BotÃ³n Logout
                        Button(
                            onClick = {
                                usuarioViewModel.logout {
                                    navController.navigate("login") {
                                        popUpTo(0) { inclusive = true }
                                    }
                                }
                            },
                            colors = ButtonDefaults.buttonColors(
                                containerColor = MaterialTheme.colorScheme.error
                            ),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Icon(Icons.Default.ExitToApp, contentDescription = null)
                            Spacer(Modifier.width(8.dp))
                            Text("Cerrar SesiÃ³n")
                        }
                    }
                }
                else -> Text("No se pudo cargar el perfil")
            }
        }
    }
}

package com.example.myapplication.ui.screens

import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.example.myapplication.data.remote.dto.PublicacionResponse
import com.example.myapplication.data.remote.dto.ReporteResponse
import com.example.myapplication.data.remote.dto.UsuarioResponse
import com.example.myapplication.ui.viewmodel.ModeracionViewModel
import com.example.myapplication.ui.viewmodel.PublicacionViewModel
import com.example.myapplication.ui.viewmodel.UsuarioViewModel


/**
 * Pantalla de Miembros
 * Muestra todos los miembros de la comunidad
 * Separados por estado: conectados y desconectados
 */
@Composable
fun MiembrosScreen(
    navController: NavController,
    usuarioViewModel: UsuarioViewModel
) {
    val usuariosConectados by usuarioViewModel.usuariosConectados.collectAsState()
    val usuariosDesconectados by usuarioViewModel.usuariosDesconectados.collectAsState()
    val usuarioActual by usuarioViewModel.usuarioActual.collectAsState()

    var tabIndex by remember { mutableStateOf(0) }
    val tabs = listOf("En linea", "Desconectados", "Todos")

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        Text(
            text = "Miembros de Oruga",
            fontSize = 24.sp,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.padding(bottom = 16.dp)
        )

        // Tabs para filtrar
        TabRow(
            selectedTabIndex = tabIndex,
            containerColor = MaterialTheme.colorScheme.surface
        ) {
            tabs.forEachIndexed { index, title ->
                Tab(
                    selected = tabIndex == index,
                    onClick = { tabIndex = index },
                    text = {
                        Text(
                            text = when (index) {
                                0 -> "$title (${usuariosConectados.size})"
                                1 -> "$title (${usuariosDesconectados.size})"
                                else -> "$title (${usuariosConectados.size + usuariosDesconectados.size})"
                            }
                        )
                    }
                )
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Lista de miembros según tab seleccionado
        val usuariosAMostrar = when (tabIndex) {
            0 -> usuariosConectados
            1 -> usuariosDesconectados
            else -> usuariosConectados + usuariosDesconectados
        }

        if (usuariosAMostrar.isEmpty()) {
            Box(
                modifier = Modifier.fillMaxSize(),
                contentAlignment = Alignment.Center
            ) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Icon(
                        imageVector = Icons.Default.PersonOff,
                        contentDescription = null,
                        modifier = Modifier.size(64.dp),
                        tint = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.3f)
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = "No hay miembros en esta categoria",
                        color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.5f)
                    )
                }
            }
        } else {
            LazyColumn(
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                items(usuariosAMostrar.filter { it.id != usuarioActual?.id }) { usuario ->
                    MiembroCard(
                        usuario = usuario,
                        onChatClick = {
                            navController.navigate("chat_privado/${usuario.id}")
                        }
                    )
                }
            }
        }
    }
}

@Composable
fun MiembroCard(
    usuario: UsuarioResponse,
    onChatClick: () -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Avatar con indicador de estado
            Box {
                Box(
                    modifier = Modifier
                        .size(56.dp)
                        .clip(CircleShape)
                        .background(MaterialTheme.colorScheme.primary),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Default.Person,
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.onPrimary,
                        modifier = Modifier.size(32.dp)
                    )
                }

                // Indicador de estado online/offline
                Box(
                    modifier = Modifier
                        .size(16.dp)
                        .clip(CircleShape)
                        .background(
                            if (usuario.estadoConexion)
                                MaterialTheme.colorScheme.primary
                            else
                                MaterialTheme.colorScheme.outline
                        )
                        .align(Alignment.BottomEnd)
                )
            }

            Spacer(modifier = Modifier.width(16.dp))

            // Información del usuario
            Column(modifier = Modifier.weight(1f)) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(
                        text = usuario.nombreUsuario,
                        fontWeight = FontWeight.Bold,
                        fontSize = 16.sp
                    )

                    if (usuario.esModerador) {
                        Spacer(modifier = Modifier.width(8.dp))
                        Surface(
                            color = MaterialTheme.colorScheme.primaryContainer,
                            shape = MaterialTheme.shapes.extraSmall
                        ) {
                            Row(
                                modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Shield,
                                    contentDescription = "Moderador",
                                    modifier = Modifier.size(12.dp),
                                    tint = MaterialTheme.colorScheme.onPrimaryContainer
                                )
                                Spacer(modifier = Modifier.width(2.dp))
                                Text(
                                    text = "MOD",
                                    fontSize = 10.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = MaterialTheme.colorScheme.onPrimaryContainer
                                )
                            }
                        }
                    }
                }

                Spacer(modifier = Modifier.height(4.dp))

                Row(verticalAlignment = Alignment.CenterVertically) {
                    Box(
                        modifier = Modifier
                            .size(10.dp)
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
                        text = if (usuario.estadoConexion) "En línea" else "Desconectado",
                        fontSize = 12.sp,
                        color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f)
                    )
                }

                // CORRECCIÓN: Manejo de nulos para descripción
                if (!usuario.descripcion.isNullOrBlank()) {
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = usuario.descripcion!!,
                        fontSize = 12.sp,
                        color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.5f),
                        maxLines = 1
                    )
                }
            }

            // Botón de chat
            IconButton(onClick = onChatClick) {
                Icon(
                    imageVector = Icons.Default.Message,
                    contentDescription = "Enviar mensaje",
                    tint = MaterialTheme.colorScheme.primary
                )
            }
        }
    }
}

/**
 * Panel de Moderador
 */
@Composable
fun PanelModeradorScreen(
    usuarioViewModel: UsuarioViewModel,
    publicacionViewModel: PublicacionViewModel,
    moderacionViewModel: ModeracionViewModel
) {
    val usuarioActual by usuarioViewModel.usuarioActual.collectAsState()
    val publicaciones by publicacionViewModel.publicaciones.collectAsState()
    val todosLosUsuarios by usuarioViewModel.todosLosUsuarios.collectAsState()
    val reportes by moderacionViewModel.reportes.collectAsState()
    val reportesPendientesCount by moderacionViewModel.reportesPendientesCount.collectAsState()
    val usuariosBaneados by moderacionViewModel.usuariosBaneados.collectAsState()
    val estadisticas by moderacionViewModel.estadisticas.collectAsState()

    val context = LocalContext.current
    var mostrarDialogoAnuncio by remember { mutableStateOf(false) }
    var mostrarDialogoBanear by remember { mutableStateOf(false) }
    var usuarioSeleccionado by remember { mutableStateOf<UsuarioResponse?>(null) }
    var tabSeleccionado by remember { mutableStateOf(0) }
    val tabs = listOf("Estadisticas", "Reportes", "Usuarios", "Publicaciones")

    if (usuarioActual?.esModerador != true) {
        Box(
            modifier = Modifier.fillMaxSize(),
            contentAlignment = Alignment.Center
        ) {
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Icon(
                    imageVector = Icons.Default.Block,
                    contentDescription = null,
                    modifier = Modifier.size(64.dp),
                    tint = MaterialTheme.colorScheme.error
                )
                Spacer(modifier = Modifier.height(16.dp))
                Text("Acceso denegado", fontSize = 20.sp, fontWeight = FontWeight.Bold)
                Text("Solo moderadores pueden acceder a esta seccion")
            }
        }
        return
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        // Header
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                imageVector = Icons.Default.Shield,
                contentDescription = "Moderador",
                tint = MaterialTheme.colorScheme.primary,
                modifier = Modifier.size(32.dp)
            )
            Spacer(modifier = Modifier.width(12.dp))
            Column {
                Text(
                    text = "Panel de Moderador",
                    fontSize = 24.sp,
                    fontWeight = FontWeight.Bold
                )
                Text(
                    text = "Gestiona la comunidad de Oruga",
                    fontSize = 14.sp,
                    color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f)
                )
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Tabs
        TabRow(selectedTabIndex = tabSeleccionado) {
            tabs.forEachIndexed { index, title ->
                Tab(
                    selected = tabSeleccionado == index,
                    onClick = { tabSeleccionado = index },
                    text = {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Text(title)
                            if (index == 1 && reportesPendientesCount > 0) {
                                Spacer(modifier = Modifier.width(8.dp))
                                Badge { Text(reportesPendientesCount.toString()) }
                            }
                        }
                    }
                )
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Contenido según tab seleccionado
        when (tabSeleccionado) {
            0 -> {
                EstadisticasTab(
                    totalUsuarios = estadisticas?.totalUsuarios ?: todosLosUsuarios.size,
                    usuariosConectados = estadisticas?.usuariosActivos ?: todosLosUsuarios.count { it.estadoConexion },
                    totalPublicaciones = estadisticas?.totalPublicaciones ?: publicaciones.size,
                    reportesPendientes = estadisticas?.reportesPendientes ?: reportesPendientesCount,
                    usuariosBaneados = estadisticas?.usuariosBaneados ?: usuariosBaneados.size,
                    onCrearAnuncio = { mostrarDialogoAnuncio = true }
                )
            }
            1 -> {
                ReportesTab(
                    reportes = reportes,
                    moderacionViewModel = moderacionViewModel,
                    context = context
                )
            }
            2 -> {
                UsuariosTab(
                    usuarios = todosLosUsuarios,
                    usuariosBaneados = usuariosBaneados,
                    onBanear = { usuario ->
                        usuarioSeleccionado = usuario
                        mostrarDialogoBanear = true
                    },
                    onDesbanear = { usuario ->
                        moderacionViewModel.desbanearUsuario(usuario.id) { success, message ->
                            Toast.makeText(context, message, Toast.LENGTH_SHORT).show()
                        }
                    }
                )
            }
            3 -> {
                PublicacionesTab(
                    publicaciones = publicaciones,
                    onEliminar = { publicacion ->
                        moderacionViewModel.eliminarPublicacion(
                            publicacionId = publicacion.id,
                            razon = "Contenido inapropiado"
                        ) { success, message ->
                            Toast.makeText(context, message, Toast.LENGTH_SHORT).show()
                        }
                    }
                )
            }
        }
    }

    // Diálogo para crear anuncio
    if (mostrarDialogoAnuncio) {
        CrearPublicacionDialog(
            onDismiss = { mostrarDialogoAnuncio = false },
            onConfirm = { titulo, contenido ->
                usuarioActual?.let { usuario ->
                    publicacionViewModel.crearPublicacion(
                        autorId = usuario.id,
                        autorNombre = usuario.nombreUsuario,
                        titulo = titulo,
                        contenido = contenido,
                        esAnuncio = true
                    ) { success, message ->
                        Toast.makeText(context, message, Toast.LENGTH_SHORT).show()
                        if (success) mostrarDialogoAnuncio = false
                    }
                }
            }
        )
    }

    // Diálogo para banear usuario
    if (mostrarDialogoBanear && usuarioSeleccionado != null) {
        BanearUsuarioDialog(
            usuario = usuarioSeleccionado!!,
            onDismiss = {
                mostrarDialogoBanear = false
                usuarioSeleccionado = null
            },
            onConfirm = { razon ->
                moderacionViewModel.banearUsuario(
                    usuarioId = usuarioSeleccionado!!.id,
                    razon = razon
                ) { success, message ->
                    Toast.makeText(context, message, Toast.LENGTH_SHORT).show()
                    if (success) {
                        mostrarDialogoBanear = false
                        usuarioSeleccionado = null
                    }
                }
            }
        )
    }
}

@Composable
fun EstadisticasTab(
    totalUsuarios: Int,
    usuariosConectados: Int,
    totalPublicaciones: Int,
    reportesPendientes: Int,
    usuariosBaneados: Int,
    onCrearAnuncio: () -> Unit,
) {
    Column(modifier = Modifier.fillMaxSize()) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceEvenly
        ) {
            EstadisticaCard(
                titulo = "Usuarios",
                valor = totalUsuarios.toString(),
                icono = Icons.Default.People
            )
            EstadisticaCard(
                titulo = "Online",
                valor = usuariosConectados.toString(),
                icono = Icons.Default.Circle
            )
        }

        Spacer(modifier = Modifier.height(12.dp))

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceEvenly
        ) {
            EstadisticaCard(
                titulo = "Publicaciones",
                valor = totalPublicaciones.toString(),
                icono = Icons.Default.Description
            )
            EstadisticaCard(
                titulo = "Reportes",
                valor = reportesPendientes.toString(),
                icono = Icons.Default.Flag,
                colorFondo = if (reportesPendientes > 0)
                    MaterialTheme.colorScheme.errorContainer
                else MaterialTheme.colorScheme.primaryContainer
            )
        }

        Spacer(modifier = Modifier.height(12.dp))

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceEvenly
        ) {
            EstadisticaCard(
                titulo = "Baneados",
                valor = usuariosBaneados.toString(),
                icono = Icons.Default.Block,
                colorFondo = MaterialTheme.colorScheme.errorContainer
            )
        }

        Spacer(modifier = Modifier.height(24.dp))

        Text(
            text = "Acciones Rapidas",
            fontSize = 18.sp,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.padding(bottom = 12.dp)
        )

        Card(
            modifier = Modifier.fillMaxWidth(),
            onClick = onCrearAnuncio
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(
                    imageVector = Icons.Default.Campaign,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.primary,
                    modifier = Modifier.size(32.dp)
                )
                Spacer(modifier = Modifier.width(16.dp))
                Column {
                    Text(
                        text = "Crear Anuncio Oficial",
                        fontWeight = FontWeight.Bold,
                        fontSize = 16.sp
                    )
                    Text(
                        text = "Publica información importante",
                        fontSize = 12.sp,
                        color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f)
                    )
                }
                Spacer(modifier = Modifier.weight(1f))
                Icon(Icons.Default.ChevronRight, contentDescription = null)
            }
        }
    }
}

@Composable
fun ReportesTab(
    reportes: List<ReporteResponse>,
    moderacionViewModel: ModeracionViewModel,
    context: android.content.Context
) {
    if (reportes.isEmpty()) {
        Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Icon(Icons.Default.CheckCircle, contentDescription = null, modifier = Modifier.size(64.dp))
                Text("No hay reportes pendientes")
            }
        }
    } else {
        LazyColumn(
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            items(reportes) { reporte ->
                ReporteCard(
                    reporte = reporte,
                    onResolver = { accion, aceptado ->
                        moderacionViewModel.resolverReporte(
                            reporteId = reporte.id,
                            aceptado = aceptado,
                            accionTomada = accion
                        ) { success, message ->
                            Toast.makeText(context, message, Toast.LENGTH_SHORT).show()
                        }
                    }
                )
            }
        }
    }
}

@Composable
fun ReporteCard(
    reporte: ReporteResponse,
    onResolver: (String, Boolean) -> Unit
) {
    var mostrarDialog by remember { mutableStateOf(false) }

    Card(modifier = Modifier.fillMaxWidth()) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(reporte.razon, fontWeight = FontWeight.Bold)
                Surface(
                    color = if (reporte.estado == "RESUELTO") MaterialTheme.colorScheme.primaryContainer else MaterialTheme.colorScheme.errorContainer,
                    shape = MaterialTheme.shapes.small
                ) {
                    Text(
                        text = reporte.estado,
                        fontSize = 11.sp,
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.height(8.dp))
            Text("Reportado por: ${reporte.reportadoPorNombre}", fontSize = 12.sp)
            Text("Tipo: ${reporte.tipoReporte}", fontSize = 12.sp)
            
            // CORRECCIÓN: Manejo de nulos para descripción de reporte
            if (!reporte.descripcion.isNullOrBlank()) {
                Text(reporte.descripcion!!, fontSize = 12.sp, maxLines = 2)
            }

            if (reporte.estado == "PENDIENTE") {
                Spacer(modifier = Modifier.height(12.dp))
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.End
                ) {
                    TextButton(onClick = { onResolver("Reporte rechazado", false) }) {
                        Text("Rechazar")
                    }
                    Button(onClick = { mostrarDialog = true }) {
                        Text("Resolver")
                    }
                }
            }
        }
    }

    if (mostrarDialog) {
        var accion by remember { mutableStateOf("") }
        AlertDialog(
            onDismissRequest = { mostrarDialog = false },
            title = { Text("Resolver Reporte") },
            text = {
                OutlinedTextField(
                    value = accion,
                    onValueChange = { accion = it },
                    label = { Text("Acción tomada") },
                    modifier = Modifier.fillMaxWidth()
                )
            },
            confirmButton = {
                Button(onClick = {
                    onResolver(accion, true)
                    mostrarDialog = false
                }) {
                    Text("Confirmar")
                }
            },
            dismissButton = {
                TextButton(onClick = { mostrarDialog = false }) {
                    Text("Cancelar")
                }
            }
        )
    }
}

@Composable
fun UsuariosTab(
    usuarios: List<UsuarioResponse>,
    usuariosBaneados: List<UsuarioResponse>,
    onBanear: (UsuarioResponse) -> Unit,
    onDesbanear: (UsuarioResponse) -> Unit
) {
    var mostrarBaneados by remember { mutableStateOf(false) }

    Column(modifier = Modifier.fillMaxSize()) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text("Gestion de Usuarios", fontSize = 18.sp, fontWeight = FontWeight.Bold)
            Row(verticalAlignment = Alignment.CenterVertically) {
                Text(if (mostrarBaneados) "Baneados" else "Activos")
                Switch(
                    checked = mostrarBaneados,
                    onCheckedChange = { mostrarBaneados = it }
                )
            }
        }

        Spacer(modifier = Modifier.height(12.dp))

        LazyColumn(verticalArrangement = Arrangement.spacedBy(8.dp)) {
            val listaAMostrar = if (mostrarBaneados) usuariosBaneados else usuarios
            items(listaAMostrar) { usuario ->
                UsuarioModeradorCard(
                    usuario = usuario,
                    onBanear = { onBanear(usuario) },
                    onDesbanear = { onDesbanear(usuario) }
                )
            }
        }
    }
}

@Composable
fun UsuarioModeradorCard(
    usuario: UsuarioResponse,
    onBanear: () -> Unit,
    onDesbanear: () -> Unit
) {
    Card(modifier = Modifier.fillMaxWidth()) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(48.dp)
                    .clip(CircleShape)
                    .background(MaterialTheme.colorScheme.primary),
                contentAlignment = Alignment.Center
            ) {
                Icon(Icons.Default.Person, contentDescription = null, tint = Color.White)
            }

            Spacer(modifier = Modifier.width(12.dp))

            Column(modifier = Modifier.weight(1f)) {
                Text(usuario.nombreUsuario, fontWeight = FontWeight.Bold)
                Text(usuario.email, fontSize = 12.sp)
                if (usuario.estaBaneado) {
                    Text("Baneado: ${usuario.razonBaneo ?: "Sin razón"}", fontSize = 11.sp, color = MaterialTheme.colorScheme.error)
                }
            }

            if (usuario.estaBaneado) {
                Button(onClick = onDesbanear, colors = ButtonDefaults.buttonColors(
                    containerColor = MaterialTheme.colorScheme.primary
                )) {
                    Text("Desbanear")
                }
            } else {
                IconButton(onClick = onBanear) {
                    Icon(Icons.Default.Block, contentDescription = "Banear", tint = MaterialTheme.colorScheme.error)
                }
            }
        }
    }
}

@Composable
fun PublicacionesTab(
    publicaciones: List<PublicacionResponse>,
    onEliminar: (PublicacionResponse) -> Unit
) {
    LazyColumn(verticalArrangement = Arrangement.spacedBy(8.dp)) {
        items(publicaciones) { publicacion ->
            Card(modifier = Modifier.fillMaxWidth()) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(12.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column(modifier = Modifier.weight(1f)) {
                        Text(publicacion.titulo, fontWeight = FontWeight.Bold, maxLines = 1)
                        Text("Por ${publicacion.autorNombre}", fontSize = 12.sp)
                        Text("${publicacion.cantidadLikes} likes • ${publicacion.cantidadComentarios} comentarios", fontSize = 11.sp)
                    }
                    IconButton(onClick = { onEliminar(publicacion) }) {
                        Icon(Icons.Default.Delete, contentDescription = "Eliminar", tint = MaterialTheme.colorScheme.error)
                    }
                }
            }
        }
    }
}

@Composable
fun BanearUsuarioDialog(
    usuario: UsuarioResponse,
    onDismiss: () -> Unit,
    onConfirm: (String) -> Unit
) {
    var razon by remember { mutableStateOf("") }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Banear Usuario") },
        text = {
            Column {
                Text("¿Está seguro de banear a ${usuario.nombreUsuario}?")
                Spacer(modifier = Modifier.height(12.dp))
                OutlinedTextField(
                    value = razon,
                    onValueChange = { razon = it },
                    label = { Text("Razón del baneo") },
                    modifier = Modifier.fillMaxWidth(),
                    minLines = 2
                )
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    if (razon.isNotBlank()) {
                        onConfirm(razon)
                    }
                },
                colors = ButtonDefaults.buttonColors(
                    containerColor = MaterialTheme.colorScheme.error
                )
            ) {
                Text("Banear")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Cancelar")
            }
        }
    )
}

@Composable
fun EstadisticaCard(
    titulo: String,
    valor: String,
    icono: androidx.compose.ui.graphics.vector.ImageVector,
    colorFondo: androidx.compose.ui.graphics.Color = MaterialTheme.colorScheme.primaryContainer
) {
    Card(
        modifier = Modifier.width(110.dp),
        colors = CardDefaults.cardColors(
            containerColor = colorFondo
        )
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Icon(
                imageVector = icono,
                contentDescription = null,
                tint = MaterialTheme.colorScheme.onPrimaryContainer,
                modifier = Modifier.size(28.dp)
            )
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = valor,
                fontSize = 20.sp,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onPrimaryContainer
            )
            Text(
                text = titulo,
                fontSize = 11.sp,
                color = MaterialTheme.colorScheme.onPrimaryContainer.copy(alpha = 0.7f)
            )
        }
    }
}

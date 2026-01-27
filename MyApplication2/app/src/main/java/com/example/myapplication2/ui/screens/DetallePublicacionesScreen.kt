package com.example.myapplication.ui.screens

import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
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
import com.example.myapplication.data.models.Comentario
import com.example.myapplication.data.models.Publicacion
import com.example.myapplication.ui.componentes.ImageGallery
import com.example.myapplication.ui.viewmodel.ComentarioViewModel
import com.example.myapplication.ui.viewmodel.PublicacionViewModel
import com.example.myapplication.ui.viewmodel.UsuarioViewModel
import com.example.myapplication.utils.ImageUtils

/**
 * Pantalla de Detalle de PublicaciÃ³n con Comentarios
 */
@Composable
fun DetallePublicacionScreen(
    navController: NavController,
    publicacionId: Int,
    usuarioViewModel: UsuarioViewModel,
    publicacionViewModel: PublicacionViewModel,
    comentarioViewModel: ComentarioViewModel
) {
    val usuarioActual by usuarioViewModel.usuarioActual.collectAsState()
    val comentarios by comentarioViewModel.comentarios.collectAsState()
    val context = LocalContext.current

    var publicacion by remember { mutableStateOf<Publicacion?>(null) }
    var textoComentario by remember { mutableStateOf("") }

    LaunchedEffect(publicacionId) {
        // Cargar publicaciÃ³n y comentarios
        // AquÃ­ deberÃ­as obtener la publicaciÃ³n desde el ViewModel
        comentarioViewModel.cargarComentarios(publicacionId)
    }

    Column(modifier = Modifier.fillMaxSize()) {
        // Lista con publicaciÃ³n y comentarios
        LazyColumn(
            modifier = Modifier.weight(1f),
            contentPadding = PaddingValues(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            // PublicaciÃ³n principal
            item {
                publicacion?.let { pub ->
                    PublicacionDetalleCard(pub)
                }
            }

            // Encabezado de comentarios
            item {
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = "Comentarios (${comentarios.size})",
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Bold
                )
                Spacer(modifier = Modifier.height(8.dp))
            }

            // Lista de comentarios
            if (comentarios.isEmpty()) {
                item {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(32.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Icon(
                                Icons.Default.CommentBank,
                                contentDescription = null,
                                modifier = Modifier.size(48.dp),
                                tint = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.3f)
                            )
                            Spacer(modifier = Modifier.height(8.dp))
                            Text(
                                "el primero en comentar",
                                color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.5f)
                            )
                        }
                    }
                }
            } else {
                items(comentarios) { comentario ->
                    ComentarioCard(
                        comentario = comentario,
                        esAutor = comentario.autorId == usuarioActual?.id,
                        onEliminar = {
                            comentarioViewModel.eliminarComentario(comentario) { success, message ->
                                Toast.makeText(context, message, Toast.LENGTH_SHORT).show()
                            }
                        }
                    )
                }
            }
        }

        // Campo para escribir comentario
        Surface(
            modifier = Modifier.fillMaxWidth(),
            shadowElevation = 8.dp,
            color = MaterialTheme.colorScheme.surface
        ) {
            Row(
                modifier = Modifier.padding(12.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                OutlinedTextField(
                    value = textoComentario,
                    onValueChange = { textoComentario = it },
                    modifier = Modifier.weight(1f),
                    placeholder = { Text("Escribe un comentario...") },
                    shape = RoundedCornerShape(24.dp),
                    maxLines = 3
                )

                Spacer(modifier = Modifier.width(8.dp))

                IconButton(
                    onClick = {
                        if (textoComentario.isNotBlank() && usuarioActual != null) {
                            comentarioViewModel.crearComentario(
                                publicacionId = publicacionId,
                                autorId = usuarioActual!!.id,
                                autorNombre = usuarioActual!!.nombreUsuario,
                                contenido = textoComentario
                            ) { success, message ->
                                if (success) {
                                    textoComentario = ""
                                    Toast.makeText(context, message, Toast.LENGTH_SHORT).show()
                                } else {
                                    Toast.makeText(context, message, Toast.LENGTH_SHORT).show()
                                }
                            }
                        }
                    },
                    modifier = Modifier
                        .size(48.dp)
                        .background(
                            MaterialTheme.colorScheme.primary,
                            CircleShape
                        )
                ) {
                    Icon(
                        Icons.Default.Send,
                        contentDescription = "Enviar",
                        tint = MaterialTheme.colorScheme.onPrimary
                    )
                }
            }
        }
    }
}

@Composable
fun PublicacionDetalleCard(publicacion: Publicacion) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            // Header con autor
            Row(verticalAlignment = Alignment.CenterVertically) {
                Box(
                    modifier = Modifier
                        .size(48.dp)
                        .clip(CircleShape)
                        .background(MaterialTheme.colorScheme.primary),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        Icons.Default.Person,
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.onPrimary
                    )
                }

                Spacer(modifier = Modifier.width(12.dp))

                Column {
                    Text(
                        publicacion.autorNombre,
                        fontWeight = FontWeight.Bold,
                        fontSize = 16.sp
                    )
                    Text(
                        formatearFecha(publicacion.fechaPublicacion),
                        fontSize = 12.sp,
                        color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f)
                    )
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // TÃ­tulo
            Text(
                text = publicacion.titulo,
                fontSize = 22.sp,
                fontWeight = FontWeight.Bold
            )

            Spacer(modifier = Modifier.height(12.dp))

            // Contenido
            Text(
                text = publicacion.contenido,
                fontSize = 16.sp,
                lineHeight = 24.sp
            )

            Spacer(modifier = Modifier.height(16.dp))

            // GALERÃA DE IMÃGENES
            val imagenes = remember(publicacion) {
                ImageUtils.stringAPaths(publicacion.imagenesUrls)
            }
            if (imagenes.isNotEmpty()) {
                ImageGallery(
                    imagePaths = imagenes,
                    imageHeight = 250,
                    modifier = Modifier.fillMaxWidth()
                )
                Spacer(modifier = Modifier.height(16.dp))
            }

            Divider()

            Spacer(modifier = Modifier.height(12.dp))

            // EstadÃ­sticas
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceAround
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        Icons.Default.ThumbUp,
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.primary,
                        modifier = Modifier.size(20.dp)
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Text("${publicacion.cantidadLikes}")
                }

                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        Icons.Default.Comment,
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.primary,
                        modifier = Modifier.size(20.dp)
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Text("${publicacion.cantidadComentarios}")
                }
            }
        }
    }
}

@Composable
fun ComentarioCard(
    comentario: Comentario,
    esAutor: Boolean,
    onEliminar: () -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(
            containerColor = if (esAutor)
                MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.3f)
            else
                MaterialTheme.colorScheme.surface
        )
    ) {
        Row(
            modifier = Modifier.padding(12.dp),
            verticalAlignment = Alignment.Top
        ) {
            Box(
                modifier = Modifier
                    .size(40.dp)
                    .clip(CircleShape)
                    .background(MaterialTheme.colorScheme.primary),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    Icons.Default.Person,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.onPrimary,
                    modifier = Modifier.size(24.dp)
                )
            }

            Spacer(modifier = Modifier.width(12.dp))

            Column(modifier = Modifier.weight(1f)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text(
                        comentario.autorNombre,
                        fontWeight = FontWeight.Bold,
                        fontSize = 14.sp
                    )

                    if (esAutor) {
                        IconButton(
                            onClick = onEliminar,
                            modifier = Modifier.size(24.dp)
                        ) {
                            Icon(
                                Icons.Default.Delete,
                                contentDescription = "Eliminar",
                                tint = MaterialTheme.colorScheme.error,
                                modifier = Modifier.size(18.dp)
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(4.dp))

                Text(
                    comentario.contenido,
                    fontSize = 14.sp,
                    lineHeight = 20.sp
                )

                Spacer(modifier = Modifier.height(4.dp))

                Text(
                    formatearFecha(comentario.fechaComentario),
                    fontSize = 11.sp,
                    color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.5f)
                )
            }
        }
    }
}

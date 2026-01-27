package com.example.myapplication.ui.componentes

import android.net.Uri
import android.widget.Toast
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import com.example.myapplication.utils.ImageUtils

/**
 * DiÃ¡logo para crear publicaciÃ³n con soporte de imÃ¡genes
 */
@Composable
fun CrearPublicacionConImagenesDialog(
    onDismiss: () -> Unit,
    onConfirm: (String, String, List<String>) -> Unit // tÃ­tulo, contenido, imagePaths
) {
    var titulo by remember { mutableStateOf("") }
    var contenido by remember { mutableStateOf("") }
    var imagenesSeleccionadas by remember { mutableStateOf<List<Uri>>(emptyList()) }
    var isLoading by remember { mutableStateOf(false) }

    val context = LocalContext.current

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Nueva PublicaciÃ³n") },
        text = {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .verticalScroll(rememberScrollState())
            ) {
                // Campo tÃ­tulo
                OutlinedTextField(
                    value = titulo,
                    onValueChange = { titulo = it },
                    label = { Text("TÃ­tulo") },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth()
                )

                Spacer(modifier = Modifier.height(8.dp))

                // Campo contenido
                OutlinedTextField(
                    value = contenido,
                    onValueChange = { contenido = it },
                    label = { Text("Contenido") },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(150.dp),
                    maxLines = 5
                )

                Spacer(modifier = Modifier.height(12.dp))

                // Selector de imÃ¡genes
                MultipleImagePicker(
                    imageUris = imagenesSeleccionadas,
                    onImagesSelected = { uris ->
                        imagenesSeleccionadas = uris
                    },
                    onImageRemoved = { uri ->
                        imagenesSeleccionadas = imagenesSeleccionadas.filter { it != uri }
                    },
                    maxImages = 5
                )

                if (imagenesSeleccionadas.isNotEmpty()) {
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        "${imagenesSeleccionadas.size} imagen(es) seleccionada(s)",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.primary
                    )
                }
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    if (titulo.isNotBlank() && contenido.isNotBlank()) {
                        isLoading = true

                        // Guardar imÃ¡genes
                        val imagePaths = imagenesSeleccionadas.mapNotNull { uri ->
                            ImageUtils.guardarImagenDesdeUri(
                                context,
                                uri,
                                "publicaciones"
                            )
                        }

                        // Confirmar creaciÃ³n
                        onConfirm(titulo, contenido, imagePaths)
                        isLoading = false
                    } else {
                        Toast.makeText(
                            context,
                            "Completa tÃ­tulo y contenido",
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                },
                enabled = !isLoading
            ) {
                if (isLoading) {
                    CircularProgressIndicator(
                        modifier = Modifier.size(16.dp),
                        strokeWidth = 2.dp
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                }
                Text(if (isLoading) "Publicando..." else "Publicar")
            }
        },
        dismissButton = {
            TextButton(
                onClick = onDismiss,
                enabled = !isLoading
            ) {
                Text("Cancelar")
            }
        }
    )
}

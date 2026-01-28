package com.example.myapplication.ui.screens

import android.widget.Toast
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import coil.compose.AsyncImage
import com.example.myapplication.data.remote.dto.ItemCarritoResponse
import com.example.myapplication.ui.viewmodel.CarritoViewModel
import com.example.myapplication.ui.viewmodel.UsuarioViewModel

/**
 * Pantalla del Carrito de Compras
 * Muestra productos agregados, permite modificar cantidades y procesar compra
 */
@Composable
fun CarritoScreen(
    navController: NavController,
    usuarioViewModel: UsuarioViewModel,
    carritoViewModel: CarritoViewModel
) {
    val usuarioActual by usuarioViewModel.usuarioActual.collectAsState()
    val itemsCarrito by carritoViewModel.itemsCarrito.collectAsState()
    val totalCarrito by carritoViewModel.totalCarrito.collectAsState()
    val context = LocalContext.current

    var mostrarDialogoCompra by remember { mutableStateOf(false) }

    LaunchedEffect(usuarioActual) {
        usuarioActual?.let { usuario ->
            carritoViewModel.cargarCarrito(usuario.id)
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        // Header
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column {
                Text(
                    text = "Mi Carrito",
                    fontSize = 28.sp,
                    fontWeight = FontWeight.Bold
                )
                Text(
                    text = "${itemsCarrito.size} productos",
                    fontSize = 14.sp,
                    color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f)
                )
            }

            if (itemsCarrito.isNotEmpty()) {
                IconButton(
                    onClick = {
                        usuarioActual?.let { usuario ->
                            carritoViewModel.vaciarCarrito(usuario.id) { success, message ->
                                Toast.makeText(context, message, Toast.LENGTH_SHORT).show()
                            }
                        }
                    }
                ) {
                    Icon(
                        imageVector = Icons.Default.DeleteForever,
                        contentDescription = "Vaciar carrito",
                        tint = MaterialTheme.colorScheme.error
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        if (itemsCarrito.isEmpty()) {
            // Carrito vacío
            Box(
                modifier = Modifier.fillMaxSize(),
                contentAlignment = Alignment.Center
            ) {
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Center
                ) {
                    Icon(
                        imageVector = Icons.Default.ShoppingCart,
                        contentDescription = "Carrito vacío",
                        modifier = Modifier.size(120.dp),
                        tint = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.3f)
                    )
                    Spacer(modifier = Modifier.height(16.dp))
                    Text(
                        text = "Tu carrito está vacío",
                        fontSize = 20.sp,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.5f)
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = "¡Explora nuestro catálogo!",
                        fontSize = 14.sp,
                        color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.4f)
                    )
                    Spacer(modifier = Modifier.height(24.dp))
                    Button(
                        onClick = { navController.navigate("catalogo") }
                    ) {
                        Icon(Icons.Default.Games, contentDescription = null)
                        Spacer(modifier = Modifier.width(8.dp))
                        Text("Ver Catálogo")
                    }
                }
            }
        } else {
            // Lista de items
            LazyColumn(
                modifier = Modifier.weight(1f),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                items(itemsCarrito) { item ->
                    ItemCarritoCard(
                        item = item,
                        onCantidadChanged = { nuevaCantidad ->
                            carritoViewModel.actualizarCantidad(item.id, nuevaCantidad) { _, _ -> }
                        },
                        onEliminar = {
                            carritoViewModel.eliminarItem(item) { success, message ->
                                Toast.makeText(context, message, Toast.LENGTH_SHORT).show()
                            }
                        }
                    )
                }

                // Espaciado para el footer
                item { Spacer(modifier = Modifier.height(100.dp)) }
            }

            // Footer con total y botón de compra
            Surface(
                modifier = Modifier.fillMaxWidth(),
                color = MaterialTheme.colorScheme.surface,
                shadowElevation = 8.dp
            ) {
                Column(
                    modifier = Modifier.padding(16.dp)
                ) {
                    // Resumen
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text("Subtotal:", fontSize = 16.sp)
                        Text(
                            formatearPrecioCLP(totalCarrito),
                            fontSize = 16.sp,
                            fontWeight = FontWeight.Medium
                        )
                    }

                    Spacer(modifier = Modifier.height(8.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text(
                            "TOTAL:",
                            fontSize = 20.sp,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.primary
                        )
                        Text(
                            formatearPrecioCLP(totalCarrito),
                            fontSize = 24.sp,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.primary
                        )
                    }

                    Spacer(modifier = Modifier.height(16.dp))

                    Button(
                        onClick = { mostrarDialogoCompra = true },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(56.dp),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = MaterialTheme.colorScheme.primary
                        )
                    ) {
                        Icon(Icons.Default.ShoppingCart, contentDescription = null)
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            "COMPRAR AHORA",
                            fontSize = 16.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }
            }
        }
    }

    // Diálogo de confirmación de compra
    if (mostrarDialogoCompra) {
        AlertDialog(
            onDismissRequest = { mostrarDialogoCompra = false },
            icon = { Icon(Icons.Default.ShoppingBag, contentDescription = null) },
            title = { Text("Confirmar Compra") },
            text = {
                Column {
                    Text("¿Deseas confirmar esta compra?")
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        "Total a pagar: ${formatearPrecioCLP(totalCarrito)}",
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.primary
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        "${itemsCarrito.size} productos",
                        fontSize = 14.sp,
                        color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f)
                    )
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        usuarioActual?.let { usuario ->
                            carritoViewModel.procesarCompra(usuario.id) { success, message ->
                                Toast.makeText(context, message, Toast.LENGTH_LONG).show()
                                if (success) {
                                    mostrarDialogoCompra = false
                                    navController.navigate("home")
                                }
                            }
                        }
                    }
                ) {
                    Text("Confirmar")
                }
            },
            dismissButton = {
                TextButton(onClick = { mostrarDialogoCompra = false }) {
                    Text("Cancelar")
                }
            }
        )
    }
}

@Composable
fun ItemCarritoCard(
    item: ItemCarritoResponse,
    onCantidadChanged: (Int) -> Unit,
    onEliminar: () -> Unit
) {
    var mostrarEliminar by remember { mutableStateOf(false) }

    Card(
        modifier = Modifier.fillMaxWidth(),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Row(
            modifier = Modifier.padding(12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Imagen del juego
            AsyncImage(
                model = item.imagenUrl,
                contentDescription = item.nombreJuego,
                modifier = Modifier
                    .size(80.dp)
                    .clip(RoundedCornerShape(8.dp)),
                contentScale = ContentScale.Crop
            )

            Spacer(modifier = Modifier.width(12.dp))

            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = item.nombreJuego,
                    fontWeight = FontWeight.Bold,
                    fontSize = 16.sp,
                    maxLines = 2
                )

                Spacer(modifier = Modifier.height(4.dp))

                Text(
                    text = formatearPrecioCLP(item.precioUnitario),
                    fontSize = 14.sp,
                    color = MaterialTheme.colorScheme.primary,
                    fontWeight = FontWeight.Medium
                )

                Spacer(modifier = Modifier.height(8.dp))

                // Controles de cantidad
                Row(
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    IconButton(
                        onClick = {
                            if (item.cantidad > 1) {
                                onCantidadChanged(item.cantidad - 1)
                            } else {
                                mostrarEliminar = true
                            }
                        },
                        modifier = Modifier.size(32.dp)
                    ) {
                        Icon(
                            imageVector = if (item.cantidad == 1) Icons.Default.Delete
                            else Icons.Default.Remove,
                            contentDescription = "Disminuir",
                            tint = if (item.cantidad == 1) MaterialTheme.colorScheme.error
                            else MaterialTheme.colorScheme.primary
                        )
                    }

                    Text(
                        text = item.cantidad.toString(),
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.padding(horizontal = 16.dp)
                    )

                    IconButton(
                        onClick = { onCantidadChanged(item.cantidad + 1) },
                        modifier = Modifier.size(32.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.Add,
                            contentDescription = "Aumentar",
                            tint = MaterialTheme.colorScheme.primary
                        )
                    }
                }
            }

            Column(
                horizontalAlignment = Alignment.End
            ) {
                Text(
                    text = formatearPrecioCLP(item.subtotal),
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.primary
                )

                Spacer(modifier = Modifier.height(8.dp))

                IconButton(
                    onClick = { mostrarEliminar = true }
                ) {
                    Icon(
                        imageVector = Icons.Default.Close,
                        contentDescription = "Eliminar",
                        tint = MaterialTheme.colorScheme.error
                    )
                }
            }
        }
    }

    // Diálogo de confirmación para eliminar
    if (mostrarEliminar) {
        AlertDialog(
            onDismissRequest = { mostrarEliminar = false },
            title = { Text("Eliminar producto") },
            text = { Text("¿Deseas eliminar ${item.nombreJuego} del carrito?") },
            confirmButton = {
                Button(
                    onClick = {
                        onEliminar()
                        mostrarEliminar = false
                    },
                    colors = ButtonDefaults.buttonColors(
                        containerColor = MaterialTheme.colorScheme.error
                    )
                ) {
                    Text("Eliminar")
                }
            },
            dismissButton = {
                TextButton(onClick = { mostrarEliminar = false }) {
                    Text("Cancelar")
                }
            }
        )
    }
}

fun formatearPrecioCLP(precio: Double): String {
    return "$${String.format("%,.0f", precio).replace(",", ".")}"
}
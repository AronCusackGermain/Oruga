package com.example.myapplication.ui.componentes

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.example.myapplication.data.remote.dto.UsuarioResponse

/**
 * Menú lateral deslizable (Drawer) - ACTUALIZADO con UsuarioResponse
 */
@Composable
fun DrawerMenu(
    usuario: UsuarioResponse?,
    navController: NavController,
    onCloseDrawer: () -> Unit,
    onLogout: () -> Unit,
    cantidadItemsCarrito: Int = 0
) {
    ModalDrawerSheet {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp)
        ) {
            // Cabecera del perfil
            if (usuario != null) {
                DrawerHeader(usuario)
                HorizontalDivider(modifier = Modifier.padding(vertical = 16.dp))
            }

            // Opciones del menú
            DrawerMenuItem(
                icon = Icons.Default.Home,
                text = "Inicio",
                onClick = {
                    navController.navigate("home")
                    onCloseDrawer()
                }
            )

            DrawerMenuItem(
                icon = Icons.Default.Games,
                text = "Catálogo de Juegos",
                onClick = {
                    navController.navigate("catalogo")
                    onCloseDrawer()
                }
            )

            // Carrito con badge
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable {
                        navController.navigate("carrito")
                        onCloseDrawer()
                    }
                    .padding(vertical = 12.dp, horizontal = 8.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(
                    imageVector = Icons.Default.ShoppingCart,
                    contentDescription = "Carrito",
                    tint = MaterialTheme.colorScheme.onSurface,
                    modifier = Modifier.size(24.dp)
                )

                Spacer(modifier = Modifier.width(16.dp))

                Text(
                    text = "Mi Carrito",
                    fontSize = 16.sp,
                    color = MaterialTheme.colorScheme.onSurface,
                    modifier = Modifier.weight(1f)
                )

                if (cantidadItemsCarrito > 0) {
                    Badge(
                        containerColor = MaterialTheme.colorScheme.error
                    ) {
                        Text(
                            text = cantidadItemsCarrito.toString(),
                            fontSize = 12.sp,
                            color = MaterialTheme.colorScheme.onError
                        )
                    }
                }
            }

            DrawerMenuItem(
                icon = Icons.Default.Group,
                text = "Comunidad",
                onClick = {
                    navController.navigate("comunidad")
                    onCloseDrawer()
                }
            )

            DrawerMenuItem(
                icon = Icons.Default.Chat,
                text = "Chat Grupal",
                onClick = {
                    navController.navigate("chat_grupal")
                    onCloseDrawer()
                }
            )

            DrawerMenuItem(
                icon = Icons.Default.Message,
                text = "Chats Privados",
                onClick = {
                    navController.navigate("chats_privados")
                    onCloseDrawer()
                }
            )

            DrawerMenuItem(
                icon = Icons.Default.Person,
                text = "Mi Perfil",
                onClick = {
                    navController.navigate("perfil")
                    onCloseDrawer()
                }
            )

            DrawerMenuItem(
                icon = Icons.Default.People,
                text = "Miembros",
                onClick = {
                    navController.navigate("miembros")
                    onCloseDrawer()
                }
            )

            if (usuario?.esModerador == true) {
                HorizontalDivider(modifier = Modifier.padding(vertical = 8.dp))

                DrawerMenuItem(
                    icon = Icons.Default.Shield,
                    text = "Panel Moderador",
                    onClick = {
                        navController.navigate("panel_moderador")
                        onCloseDrawer()
                    }
                )
            }

            Spacer(modifier = Modifier.weight(1f))

            // Botón de cerrar sesión
            DrawerMenuItem(
                icon = Icons.Default.ExitToApp,
                text = "Cerrar Sesión",
                onClick = {
                    onLogout()
                    onCloseDrawer()
                }
            )
        }
    }
}

@Composable
fun DrawerHeader(usuario: UsuarioResponse) {
    Column(
        horizontalAlignment = Alignment.Start,
        modifier = Modifier.fillMaxWidth()
    ) {
        // Avatar
        Box(
            modifier = Modifier
                .size(64.dp)
                .clip(CircleShape)
                .background(MaterialTheme.colorScheme.primary),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = Icons.Default.Person,
                contentDescription = "Avatar",
                tint = MaterialTheme.colorScheme.onPrimary,
                modifier = Modifier.size(40.dp)
            )
        }

        Spacer(modifier = Modifier.height(12.dp))

        // Nombre de usuario
        Text(
            text = usuario.nombreUsuario,
            fontSize = 18.sp,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.onSurface
        )

        // Email
        Text(
            text = usuario.email,
            fontSize = 14.sp,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )

        // Badge de moderador si aplica
        if (usuario.esModerador) {
            Spacer(modifier = Modifier.height(8.dp))
            Surface(
                color = MaterialTheme.colorScheme.primaryContainer,
                shape = MaterialTheme.shapes.small
            ) {
                Row(
                    modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        imageVector = Icons.Default.Shield,
                        contentDescription = "Moderador",
                        tint = MaterialTheme.colorScheme.onPrimaryContainer,
                        modifier = Modifier.size(16.dp)
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(
                        text = "Moderador",
                        fontSize = 12.sp,
                        color = MaterialTheme.colorScheme.onPrimaryContainer,
                        fontWeight = FontWeight.Medium
                    )
                }
            }
        }
    }
}

@Composable
fun DrawerMenuItem(
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    text: String,
    onClick: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick)
            .padding(vertical = 12.dp, horizontal = 8.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(
            imageVector = icon,
            contentDescription = text,
            tint = MaterialTheme.colorScheme.onSurface,
            modifier = Modifier.size(24.dp)
        )

        Spacer(modifier = Modifier.width(16.dp))

        Text(
            text = text,
            fontSize = 16.sp,
            color = MaterialTheme.colorScheme.onSurface
        )
    }
}

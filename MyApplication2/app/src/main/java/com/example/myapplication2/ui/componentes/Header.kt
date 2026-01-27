package com.example.myapplication.ui.componentes

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController

/**
 * Componente Header reutilizable
 * Barra superior con logo, tÃ­tulo y botones de navegaciÃ³n
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun Header(
    titulo: String,
    mostrarHome: Boolean = true,
    mostrarMenu: Boolean = true,
    mostrarNotificaciones: Boolean = false,
    cantidadNotificaciones: Int = 0,
    navController: NavController? = null,
    onMenuClick: () -> Unit = {}
) {
    TopAppBar(
        title = {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.Start
            ) {
                // Logo/Icono de Oruga
                Icon(
                    imageVector = Icons.Default.BugReport,
                    contentDescription = "Logo Oruga",
                    tint = MaterialTheme.colorScheme.primary,
                    modifier = Modifier.size(32.dp)
                )

                Spacer(modifier = Modifier.width(8.dp))

                Text(
                    text = titulo,
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onSurface
                )
            }
        },
        navigationIcon = {
            if (mostrarHome && navController != null) {
                IconButton(onClick = { navController.navigate("home") }) {
                    Icon(
                        imageVector = Icons.Default.Home,
                        contentDescription = "Inicio",
                        tint = MaterialTheme.colorScheme.primary
                    )
                }
            }
        },
        actions = {
            // Notificaciones
            if (mostrarNotificaciones) {
                BadgedBox(
                    badge = {
                        if (cantidadNotificaciones > 0) {
                            Badge {
                                Text(
                                    text = cantidadNotificaciones.toString(),
                                    fontSize = 10.sp
                                )
                            }
                        }
                    }
                ) {
                    IconButton(onClick = { navController?.navigate("notificaciones") }) {
                        Icon(
                            imageVector = Icons.Default.Notifications,
                            contentDescription = "Notificaciones"
                        )
                    }
                }
            }

            // BotÃ³n de menÃº hamburguesa
            if (mostrarMenu) {
                IconButton(onClick = onMenuClick) {
                    Icon(
                        imageVector = Icons.Default.Menu,
                        contentDescription = "MenÃº",
                        tint = MaterialTheme.colorScheme.onSurface
                    )
                }
            }
        },
        colors = TopAppBarDefaults.topAppBarColors(
            containerColor = MaterialTheme.colorScheme.surface,
            titleContentColor = MaterialTheme.colorScheme.onSurface
        )
    )
}

/**
 * Barra de navegaciÃ³n inferior
 */
@Composable
fun BottomNavBar(
    navController: NavController,
    currentRoute: String?
) {
    NavigationBar(
        containerColor = MaterialTheme.colorScheme.surface,
        contentColor = MaterialTheme.colorScheme.onSurface
    ) {
        NavigationBarItem(
            icon = { Icon(Icons.Default.Home, contentDescription = "Inicio") },
            label = { Text("Inicio") },
            selected = currentRoute == "home",
            onClick = { navController.navigate("home") }
        )

        NavigationBarItem(
            icon = { Icon(Icons.Default.Games, contentDescription = "CatÃ¡logo") },
            label = { Text("Juegos") },
            selected = currentRoute == "catalogo",
            onClick = { navController.navigate("catalogo") }
        )

        NavigationBarItem(
            icon = { Icon(Icons.Default.Group, contentDescription = "Comunidad") },
            label = { Text("Comunidad") },
            selected = currentRoute == "comunidad",
            onClick = { navController.navigate("comunidad") }
        )

        NavigationBarItem(
            icon = { Icon(Icons.Default.Chat, contentDescription = "Chat") },
            label = { Text("Chat") },
            selected = currentRoute == "chat",
            onClick = { navController.navigate("chat") }
        )

        NavigationBarItem(
            icon = { Icon(Icons.Default.Person, contentDescription = "Perfil") },
            label = { Text("Perfil") },
            selected = currentRoute == "perfil",
            onClick = { navController.navigate("perfil") }
        )
    }
}

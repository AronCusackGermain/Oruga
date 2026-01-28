package com.example.myapplication

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.example.myapplication.data.remote.RetrofitClient
import com.example.myapplication.domain.repository.*
import com.example.myapplication.ui.componentes.*
import com.example.myapplication.ui.screens.*
import com.example.myapplication.ui.theme.OrugaTheme
import com.example.myapplication.ui.viewmodel.*
import kotlinx.coroutines.launch
import androidx.lifecycle.viewmodel.compose.viewModel

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // 🔑 INICIALIZAR RetrofitClient
        RetrofitClient.init(applicationContext)

        setContent {
            OrugaTheme {
                OrugaApp()
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun OrugaApp() {
    val navController = rememberNavController()
    val drawerState = rememberDrawerState(initialValue = DrawerValue.Closed)
    val scope = rememberCoroutineScope()

    // CREAR REPOSITORIOS
    val usuarioRepository = remember { UsuarioRepository() }
    val publicacionRepository = remember { PublicacionRepository() }
    val mensajeRepository = remember { MensajeRepository() }
    val carritoRepository = remember { CarritoRepository() }
    val moderacionRepository = remember { ModeracionRepository() }
    val comentarioRepository = remember { ComentarioRepository() }

    // CREAR VIEWMODELS CON FACTORIES
    val usuarioViewModel: UsuarioViewModel = viewModel(
        factory = UsuarioViewModelFactory(usuarioRepository)
    )
    val publicacionViewModel: PublicacionViewModel = viewModel(
        factory = PublicacionViewModelFactory(publicacionRepository)
    )
    val mensajeViewModel: MensajeViewModel = viewModel(
        factory = MensajeViewModelFactory(mensajeRepository)
    )
    val carritoViewModel: CarritoViewModel = viewModel(
        factory = CarritoViewModelFactory(carritoRepository)
    )
    val moderacionViewModel: ModeracionViewModel = viewModel(
        factory = ModeracionViewModelFactory(moderacionRepository)
    )
    val comentarioViewModel: ComentarioViewModel = viewModel(
        factory = ComentarioViewModelFactory(comentarioRepository)
    )

    // ESTADOS
    val usuarioActual by usuarioViewModel.usuarioActual.collectAsState()
    val authResponse by usuarioViewModel.authResponse.collectAsState()
    val cantidadItemsCarrito by carritoViewModel.cantidadItems.collectAsState()
    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute = navBackStackEntry?.destination?.route

    // Cargar datos iniciales cuando hay usuario autenticado
    LaunchedEffect(authResponse) {
        authResponse?.let {
            usuarioViewModel.cargarUsuarios()
            publicacionViewModel.cargarPublicaciones()
            carritoViewModel.cargarCarrito(it.id)
        }
    }

    val rutasSinNavegacion = listOf("splash", "login", "registro")
    val mostrarNavegacion = currentRoute !in rutasSinNavegacion

    ModalNavigationDrawer(
        drawerState = drawerState,
        drawerContent = {
            if (mostrarNavegacion && usuarioActual != null) {
                DrawerMenu(
                    usuario = usuarioActual,
                    navController = navController,
                    onCloseDrawer = { scope.launch { drawerState.close() } },
                    onLogout = {
                        usuarioViewModel.logout {
                            navController.navigate("login") {
                                popUpTo(0) { inclusive = true }
                            }
                        }
                    },
                    cantidadItemsCarrito = cantidadItemsCarrito.toInt()
                )
            }
        },
        gesturesEnabled = mostrarNavegacion
    ) {
        Scaffold(
            topBar = {
                if (mostrarNavegacion) {
                    Header(
                        titulo = when {
                            currentRoute == "home" -> "Oruga"
                            currentRoute == "catalogo" -> "Catálogo"
                            currentRoute == "comunidad" -> "Comunidad"
                            currentRoute == "chat_grupal" -> "Chat Grupal"
                            currentRoute == "perfil" -> "Mi Perfil"
                            currentRoute == "miembros" -> "Miembros"
                            currentRoute == "carrito" -> "Carrito (${cantidadItemsCarrito})"
                            currentRoute == "panel_moderador" -> "Panel Moderación"
                            currentRoute?.startsWith("detalle_juego") == true -> "Detalles"
                            currentRoute?.startsWith("detalle_publicacion") == true -> "Publicación"
                            else -> "Oruga"
                        },
                        mostrarHome = currentRoute != "home",
                        mostrarMenu = true,
                        navController = navController,
                        onMenuClick = {
                            scope.launch {
                                if (drawerState.isClosed) drawerState.open()
                                else drawerState.close()
                            }
                        }
                    )
                }
            },
            bottomBar = {
                if (mostrarNavegacion) {
                    BottomNavBar(
                        navController = navController,
                        currentRoute = currentRoute
                    )
                }
            }
        ) { innerPadding ->
            NavHost(
                navController = navController,
                startDestination = "splash",
                modifier = Modifier.padding(innerPadding)
            ) {
                // SPLASH
                composable("splash") {
                    SplashScreen(navController)
                }

                // LOGIN
                composable("login") {
                    LoginScreen(navController, usuarioViewModel)
                }

                // REGISTRO
                composable("registro") {
                    RegisterScreen(navController, usuarioViewModel)
                }

                // HOME
                composable("home") {
                    HomeScreen(
                        navController = navController,
                        usuarioViewModel = usuarioViewModel,
                        publicacionViewModel = publicacionViewModel
                    )
                }

                // CATÁLOGO
                composable("catalogo") {
                    CatalogoScreen(navController)
                }

                // DETALLE DE JUEGO
                composable(
                    route = "detalle_juego/{juegoId}",
                    arguments = listOf(navArgument("juegoId") { type = NavType.IntType })
                ) { backStackEntry ->
                    val juegoId = backStackEntry.arguments?.getInt("juegoId") ?: 0
                    DetalleJuegoScreen(
                        navController = navController,
                        juegoId = juegoId,
                        usuarioViewModel = usuarioViewModel,
                        carritoViewModel = carritoViewModel
                    )
                }

                // CARRITO
                composable("carrito") {
                    CarritoScreen(
                        navController = navController,
                        usuarioViewModel = usuarioViewModel,
                        carritoViewModel = carritoViewModel
                    )
                }

                // COMUNIDAD
                composable("comunidad") {
                    ComunidadScreen(
                        navController = navController,
                        usuarioViewModel = usuarioViewModel,
                        publicacionViewModel = publicacionViewModel
                    )
                }

                // DETALLE PUBLICACION (NUEVO: Corrigiendo error de navegación)
                composable(
                    route = "detalle_publicacion/{publicacionId}",
                    arguments = listOf(navArgument("publicacionId") { type = NavType.LongType })
                ) { backStackEntry ->
                    val publicacionId = backStackEntry.arguments?.getLong("publicacionId") ?: 0L
                    DetallePublicacionScreen(
                        navController = navController,
                        publicacionId = publicacionId,
                        usuarioViewModel = usuarioViewModel,
                        publicacionViewModel = publicacionViewModel,
                        comentarioViewModel = comentarioViewModel
                    )
                }

                // CHAT GRUPAL
                composable("chat_grupal") {
                    ChatGrupalScreen(
                        usuarioViewModel = usuarioViewModel,
                        mensajeViewModel = mensajeViewModel
                    )
                }

                // CHATS PRIVADOS
                composable("chats_privados") {
                    ChatsPrivadosScreen(
                        navController = navController,
                        usuarioViewModel = usuarioViewModel,
                        mensajeViewModel = mensajeViewModel
                    )
                }

                // CHAT PRIVADO INDIVIDUAL
                composable(
                    route = "chat_privado/{usuarioId}",
                    arguments = listOf(navArgument("usuarioId") { type = NavType.IntType })
                ) { backStackEntry ->
                    val otroUsuarioId = backStackEntry.arguments?.getInt("usuarioId") ?: 0
                    ChatPrivadoScreen(
                        navController = navController,
                        usuarioViewModel = usuarioViewModel,
                        mensajeViewModel = mensajeViewModel,
                        otroUsuarioId = otroUsuarioId
                    )
                }

                // MIEMBROS
                composable("miembros") {
                    MiembrosScreen(
                        navController = navController,
                        usuarioViewModel = usuarioViewModel
                    )
                }

                // PERFIL
                composable("perfil") {
                    PerfilScreen(
                        navController = navController,
                        usuarioViewModel = usuarioViewModel
                    )
                }

                // PANEL MODERADOR
                composable("panel_moderador") {
                    if (usuarioActual?.esModerador == true) {
                        PanelModeradorScreen(
                            usuarioViewModel = usuarioViewModel,
                            publicacionViewModel = publicacionViewModel,
                            moderacionViewModel = moderacionViewModel
                        )
                    }
                }
            }
        }
    }
}

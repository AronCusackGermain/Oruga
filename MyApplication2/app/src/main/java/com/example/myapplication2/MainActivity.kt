package com.example.myapplication

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.example.myapplication.data.db.AppDatabase
import com.example.myapplication.domain.repository.*
import com.example.myapplication.ui.componentes.*
import com.example.myapplication.ui.screens.*
import com.example.myapplication.ui.theme.OrugaTheme
import com.example.myapplication.ui.viewmodel.*
import kotlinx.coroutines.launch

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val database = AppDatabase.getDatabase(applicationContext)

        setContent {
            OrugaTheme {
                OrugaApp(database)
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun OrugaApp(database: AppDatabase) {
    val navController = rememberNavController()
    val drawerState = rememberDrawerState(initialValue = DrawerValue.Closed)
    val scope = rememberCoroutineScope()

    // CREAR REPOSITORIOS
    val usuarioRepository = remember { UsuarioRepository(database.usuarioDao()) }
    val publicacionRepository = remember { PublicacionRepository(database.publicacionDao()) }
    val mensajeRepository = remember { MensajeRepository(database.mensajeDao()) }
    val moderacionRepository = remember {
        ModeracionRepository(
            database.usuarioDao(),
            database.publicacionDao(),
            database.mensajeDao(),
            database.reporteDao()
        )
    }
    val comentarioRepository = remember {
        ComentarioRepository(
            database.comentarioDao(),
            database.publicacionDao()
        )
    }
    val carritoRepository = remember { CarritoRepository(database.carritoDao()) }

    // CREAR VIEWMODELS
    val usuarioViewModel: UsuarioViewModel = viewModel(
        factory = UsuarioViewModelFactory(usuarioRepository)
    )
    val publicacionViewModel: PublicacionViewModel = viewModel(
        factory = PublicacionViewModelFactory(publicacionRepository)
    )
    val mensajeViewModel: MensajeViewModel = viewModel(
        factory = MensajeViewModelFactory(mensajeRepository)
    )
    val moderacionViewModel: ModeracionViewModel = viewModel(
        factory = ModeracionViewModelFactory(moderacionRepository)
    )
    val comentarioViewModel: ComentarioViewModel = viewModel(
        factory = ComentarioViewModelFactory(comentarioRepository)
    )
    val carritoViewModel: CarritoViewModel = viewModel(
        factory = CarritoViewModelFactory(carritoRepository)
    )

    // ESTADOS
    val usuarioActual by usuarioViewModel.usuarioActual.collectAsState()
    val cantidadItemsCarrito by carritoViewModel.cantidadItems.collectAsState()
    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute = navBackStackEntry?.destination?.route

    val rutasSinNavegacion = listOf("splash", "login", "registro")
    val mostrarNavegacion = currentRoute !in rutasSinNavegacion

    ModalNavigationDrawer(
        drawerState = drawerState,
        drawerContent = {
            if (mostrarNavegacion) {
                DrawerMenu(
                    usuario = usuarioActual,
                    navController = navController,
                    cantidadItemsCarrito = cantidadItemsCarrito,
                    onCloseDrawer = { scope.launch { drawerState.close() } },
                    onLogout = {
                        usuarioViewModel.logout {
                            navController.navigate("login") {
                                popUpTo(0) { inclusive = true }
                            }
                        }
                    }
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
                            currentRoute == "catalogo" -> "CatÃ¡logo"
                            currentRoute == "comunidad" -> "Comunidad"
                            currentRoute == "chat_grupal" -> "Chat Grupal"
                            currentRoute == "perfil" -> "Mi Perfil"
                            currentRoute == "miembros" -> "Miembros"
                            currentRoute == "carrito" -> "Carrito (${cantidadItemsCarrito})"
                            currentRoute?.startsWith("detalle_juego") == true -> "Detalles"
                            currentRoute?.startsWith("detalle_publicacion") == true -> "PublicaciÃ³n"
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

                // CATÃLOGO
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

                // DETALLE DE PUBLICACIÃ“N CON COMENTARIOS
                composable(
                    route = "detalle_publicacion/{publicacionId}",
                    arguments = listOf(navArgument("publicacionId") { type = NavType.IntType })
                ) { backStackEntry ->
                    val publicacionId = backStackEntry.arguments?.getInt("publicacionId") ?: 0
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

                // PERFIL
                composable("perfil") {
                    PerfilScreen(
                        navController = navController,
                        usuarioViewModel = usuarioViewModel
                    )
                }

                // MIEMBROS
                composable("miembros") {
                    MiembrosScreen(
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

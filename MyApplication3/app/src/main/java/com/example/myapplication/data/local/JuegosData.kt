package com.example.myapplication.data.local

import com.example.myapplication.R
import com.example.myapplication.data.models.Juego

/**
 * Catálogo de juegos con precios en CLP (Pesos Chilenos)
 * Tasa aproximada: 1 USD = 950 CLP
 */
object JuegosData {

    val listaJuegos = listOf(
        Juego(
            id = 1,
            nombre = "The Witcher 3",
            descripcion = "Un RPG de mundo abierto donde encarnas a Geralt de Rivia, un cazador de monstruos. Explora un vasto mundo lleno de decisiones morales complejas.",
            genero = "RPG, Acción",
            precio = 37990.0, // 39.99 USD → CLP
            imagenUrl = R.drawable.juego_witcher3,
            plataformas = "PC, PS5, Xbox, Switch",
            desarrollador = "CD Projekt Red",
            fechaLanzamiento = "Mayo 2015",
            calificacion = 4.9f
        ),
        Juego(
            id = 2,
            nombre = "Elden Ring",
            descripcion = "Un juego de rol de acción desarrollado por FromSoftware. Explora un mundo oscuro y desafiante creado en colaboración con George R.R. Martin.",
            genero = "RPG, Souls-like",
            precio = 56990.0, // 59.99 USD → CLP
            imagenUrl = R.drawable.juego_eldenring,
            plataformas = "PC, PS5, Xbox",
            desarrollador = "FromSoftware",
            fechaLanzamiento = "Febrero 2022",
            calificacion = 4.8f
        ),
        Juego(
            id = 3,
            nombre = "Hollow Knight",
            descripcion = "Un metroidvania 2D con un mundo interconectado. Explora cavernas antiguas, lucha contra criaturas corrompidas y descubre secretos ocultos.",
            genero = "Metroidvania, Acción",
            precio = 14240.0, // 14.99 USD → CLP
            imagenUrl = R.drawable.juego_hollowknight,
            plataformas = "PC, PS4, Xbox, Switch",
            desarrollador = "Team Cherry",
            fechaLanzamiento = "Febrero 2017",
            calificacion = 4.7f
        ),
        Juego(
            id = 4,
            nombre = "Stardew Valley",
            descripcion = "Simulador de granja donde construyes tu propio rancho, cultivas, crías animales y te relacionas con los habitantes del pueblo.",
            genero = "Simulación, RPG",
            precio = 14240.0, // 14.99 USD → CLP
            imagenUrl = R.drawable.juego_stardew,
            plataformas = "PC, PS4, Xbox, Switch, Mobile",
            desarrollador = "ConcernedApe",
            fechaLanzamiento = "Febrero 2016",
            calificacion = 4.8f
        ),
        Juego(
            id = 5,
            nombre = "Hades",
            descripcion = "Un roguelike de acción donde intentas escapar del inframundo. Combate dinámico, historia rica y rejugabilidad infinita.",
            genero = "Roguelike, Acción",
            precio = 23750.0, // 24.99 USD → CLP
            imagenUrl = R.drawable.juego_hades,
            plataformas = "PC, PS5, Xbox, Switch",
            desarrollador = "Supergiant Games",
            fechaLanzamiento = "Septiembre 2020",
            calificacion = 4.9f
        ),
        Juego(
            id = 6,
            nombre = "Celeste",
            descripcion = "Un platformer desafiante sobre escalar una montaña. Historia emotiva sobre salud mental y superación personal.",
            genero = "Plataformas, Indie",
            precio = 18990.0, // 19.99 USD → CLP
            imagenUrl = R.drawable.juego_celeste,
            plataformas = "PC, PS4, Xbox, Switch",
            desarrollador = "Maddy Makes Games",
            fechaLanzamiento = "Enero 2018",
            calificacion = 4.8f
        ),
        Juego(
            id = 7,
            nombre = "Baldur's Gate 3",
            descripcion = "RPG táctico basado en D&D 5e. Crea tu personaje y vive una aventura épica con decisiones que afectan la historia.",
            genero = "RPG, Táctico",
            precio = 56990.0, // 59.99 USD → CLP
            imagenUrl = R.drawable.juego_baldurs,
            plataformas = "PC, PS5",
            desarrollador = "Larian Studios",
            fechaLanzamiento = "Agosto 2023",
            calificacion = 4.9f
        ),
        Juego(
            id = 8,
            nombre = "Terraria",
            descripcion = "Aventura sandbox 2D de exploración, construcción y combate. Infinitas posibilidades de creación y exploración.",
            genero = "Sandbox, Acción",
            precio = 9490.0, // 9.99 USD → CLP
            imagenUrl = R.drawable.juego_terraria,
            plataformas = "PC, PS4, Xbox, Switch, Mobile",
            desarrollador = "Re-Logic",
            fechaLanzamiento = "Mayo 2011",
            calificacion = 4.7f
        ),
        Juego(
            id = 9,
            nombre = "Dead Cells",
            descripcion = "Roguelite de acción 2D con combate fluido. Muere, aprende y vuelve más fuerte en este desafiante juego.",
            genero = "Roguelite, Metroidvania",
            precio = 23750.0, // 24.99 USD → CLP
            imagenUrl = R.drawable.juego_deadcells,
            plataformas = "PC, PS4, Xbox, Switch",
            desarrollador = "Motion Twin",
            fechaLanzamiento = "Agosto 2018",
            calificacion = 4.6f
        ),
        Juego(
            id = 10,
            nombre = "Ori and the Blind Forest",
            descripcion = "Plataformas metroidvania con hermosos gráficos pintados a mano. Historia emotiva y jugabilidad precisa.",
            genero = "Metroidvania, Plataformas",
            precio = 18990.0, // 19.99 USD → CLP
            imagenUrl = R.drawable.juego_ori,
            plataformas = "PC, Xbox, Switch",
            desarrollador = "Moon Studios",
            fechaLanzamiento = "Marzo 2015",
            calificacion = 4.8f
        )
    )

    /**
     * Formatea precio a formato chileno
     */
    fun formatearPrecioCLP(precio: Double): String {
        return "$${String.format("%,.0f", precio).replace(",", ".")}"
    }
}
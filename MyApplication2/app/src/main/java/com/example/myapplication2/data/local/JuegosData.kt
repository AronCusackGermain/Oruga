package com.example.myapplication.data.local

import com.example.myapplication.R
import com.example.myapplication.data.models.Juego

/**
 * Objeto que contiene la lista local de videojuegos
 * CatÃ¡logo de 10 juegos para el foro Oruga
 */
object JuegosData {

    val listaJuegos = listOf(
        Juego(
            id = 1,
            nombre = "The Witcher 3",
            descripcion = "Un RPG de mundo abierto donde encarnas a Geralt de Rivia, un cazador de monstruos. Explora un vasto mundo lleno de decisiones morales complejas.",
            genero = "RPG, AcciÃ³n",
            precio = 39.99,
            imagenUrl = R.drawable.juego_witcher3,
            plataformas = "PC, PS5, Xbox, Switch",
            desarrollador = "CD Projekt Red",
            fechaLanzamiento = "Mayo 2015",
            calificacion = 4.9f
        ),
        Juego(
            id = 2,
            nombre = "Elden Ring",
            descripcion = "Un juego de rol de acciÃ³n desarrollado por FromSoftware. Explora un mundo oscuro y desafiante creado en colaboraciÃ³n con George R.R. Martin.",
            genero = "RPG, Souls-like",
            precio = 59.99,
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
            genero = "Metroidvania, AcciÃ³n",
            precio = 14.99,
            imagenUrl = R.drawable.juego_hollowknight,
            plataformas = "PC, PS4, Xbox, Switch",
            desarrollador = "Team Cherry",
            fechaLanzamiento = "Febrero 2017",
            calificacion = 4.7f
        ),
        Juego(
            id = 4,
            nombre = "Stardew Valley",
            descripcion = "Simulador de granja donde construyes tu propio rancho, cultivas, crÃ­as animales y te relacionas con los habitantes del pueblo.",
            genero = "SimulaciÃ³n, RPG",
            precio = 14.99,
            imagenUrl = R.drawable.juego_stardew,
            plataformas = "PC, PS4, Xbox, Switch, Mobile",
            desarrollador = "ConcernedApe",
            fechaLanzamiento = "Febrero 2016",
            calificacion = 4.8f
        ),
        Juego(
            id = 5,
            nombre = "Hades",
            descripcion = "Un roguelike de acciÃ³n donde intentas escapar del inframundo. Combate dinÃ¡mico, historia rica y rejugabilidad infinita.",
            genero = "Roguelike, AcciÃ³n",
            precio = 24.99,
            imagenUrl = R.drawable.juego_hades,
            plataformas = "PC, PS5, Xbox, Switch",
            desarrollador = "Supergiant Games",
            fechaLanzamiento = "Septiembre 2020",
            calificacion = 4.9f
        ),
        Juego(
            id = 6,
            nombre = "Celeste",
            descripcion = "Un platformer desafiante sobre escalar una montaÃ±a. Historia emotiva sobre salud mental y superaciÃ³n personal.",
            genero = "Plataformas, Indie",
            precio = 19.99,
            imagenUrl = R.drawable.juego_celeste,
            plataformas = "PC, PS4, Xbox, Switch",
            desarrollador = "Maddy Makes Games",
            fechaLanzamiento = "Enero 2018",
            calificacion = 4.8f
        ),
        Juego(
            id = 7,
            nombre = "Baldur's Gate 3",
            descripcion = "RPG tÃ¡ctico basado en D&D 5e. Crea tu personaje y vive una aventura Ã©pica con decisiones que afectan la historia.",
            genero = "RPG, TÃ¡ctico",
            precio = 59.99,
            imagenUrl = R.drawable.juego_baldurs,
            plataformas = "PC, PS5",
            desarrollador = "Larian Studios",
            fechaLanzamiento = "Agosto 2023",
            calificacion = 4.9f
        ),
        Juego(
            id = 8,
            nombre = "Terraria",
            descripcion = "Aventura sandbox 2D de exploraciÃ³n, construcciÃ³n y combate. Infinitas posibilidades de creaciÃ³n y exploraciÃ³n.",
            genero = "Sandbox, AcciÃ³n",
            precio = 9.99,
            imagenUrl = R.drawable.juego_terraria,
            plataformas = "PC, PS4, Xbox, Switch, Mobile",
            desarrollador = "Re-Logic",
            fechaLanzamiento = "Mayo 2011",
            calificacion = 4.7f
        ),
        Juego(
            id = 9,
            nombre = "Dead Cells",
            descripcion = "Roguelite de acciÃ³n 2D con combate fluido. Muere, aprende y vuelve mÃ¡s fuerte en este desafiante juego.",
            genero = "Roguelite, Metroidvania",
            precio = 24.99,
            imagenUrl = R.drawable.juego_deadcells,
            plataformas = "PC, PS4, Xbox, Switch",
            desarrollador = "Motion Twin",
            fechaLanzamiento = "Agosto 2018",
            calificacion = 4.6f
        ),
        Juego(
            id = 10,
            nombre = "Ori and the Blind Forest",
            descripcion = "Plataformas metroidvania con hermosos grÃ¡ficos pintados a mano. Historia emotiva y jugabilidad precisa.",
            genero = "Metroidvania, Plataformas",
            precio = 19.99,
            imagenUrl = R.drawable.juego_ori,
            plataformas = "PC, Xbox, Switch",
            desarrollador = "Moon Studios",
            fechaLanzamiento = "Marzo 2015",
            calificacion = 4.8f
        )
    )
}

package com.oruga.gaming.config;

import com.oruga.gaming.entity.Juego;
import com.oruga.gaming.repository.Juegorepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

@Component
public class DataInitializer implements CommandLineRunner {

    private final Juegorepository juegoRepository;

    public DataInitializer(Juegorepository juegoRepository) {
        this.juegoRepository = juegoRepository;
    }

    @Override
    public void run(String... args) {
        if (juegoRepository.count() == 0) {
            inicializarJuegos();
        }
    }

    private void inicializarJuegos() {
        // The Witcher 3
        juegoRepository.save(Juego.builder()
                .nombre("The Witcher 3")
                .descripcion("Un RPG de mundo abierto donde encarnas a Geralt de Rivia, un cazador de monstruos.")
                .genero("RPG, Acción")
                .precio(37990.0)
                .stock(50)
                .imagenUrl("https://images.igdb.com/igdb/image/upload/t_cover_big/co1wyy.jpg")
                .plataformas("PC, PS5, Xbox, Switch")
                .desarrollador("CD Projekt Red")
                .fechaLanzamiento("Mayo 2015")
                .calificacion(4.9)
                .activo(true)
                .build());

        // Elden Ring
        juegoRepository.save(Juego.builder()
                .nombre("Elden Ring")
                .descripcion("Un juego de rol de acción desarrollado por FromSoftware.")
                .genero("RPG, Souls-like")
                .precio(56990.0)
                .stock(30)
                .imagenUrl("https://images.igdb.com/igdb/image/upload/t_cover_big/co4jni.jpg")
                .plataformas("PC, PS5, Xbox")
                .desarrollador("FromSoftware")
                .fechaLanzamiento("Febrero 2022")
                .calificacion(4.8)
                .activo(true)
                .build());

        // Hollow Knight
        juegoRepository.save(Juego.builder()
                .nombre("Hollow Knight")
                .descripcion("Un metroidvania 2D con un mundo interconectado.")
                .genero("Metroidvania, Acción")
                .precio(14240.0)
                .stock(100)
                .imagenUrl("https://images.igdb.com/igdb/image/upload/t_cover_big/co1rgi.jpg")
                .plataformas("PC, PS4, Xbox, Switch")
                .desarrollador("Team Cherry")
                .fechaLanzamiento("Febrero 2017")
                .calificacion(4.7)
                .activo(true)
                .build());

        // Stardew Valley
        juegoRepository.save(Juego.builder()
                .nombre("Stardew Valley")
                .descripcion("Simulador de granja donde construyes tu propio rancho.")
                .genero("Simulación, RPG")
                .precio(14240.0)
                .stock(200)
                .imagenUrl("https://images.igdb.com/igdb/image/upload/t_cover_big/co1xd7.jpg")
                .plataformas("PC, PS4, Xbox, Switch, Mobile")
                .desarrollador("ConcernedApe")
                .fechaLanzamiento("Febrero 2016")
                .calificacion(4.8)
                .activo(true)
                .build());

        // Hades
        juegoRepository.save(Juego.builder()
                .nombre("Hades")
                .descripcion("Un roguelike de acción donde intentas escapar del inframundo.")
                .genero("Roguelike, Acción")
                .precio(23750.0)
                .stock(75)
                .imagenUrl("https://images.igdb.com/igdb/image/upload/t_cover_big/co2i0j.jpg")
                .plataformas("PC, PS5, Xbox, Switch")
                .desarrollador("Supergiant Games")
                .fechaLanzamiento("Septiembre 2020")
                .calificacion(4.9)
                .activo(true)
                .build());

        System.out.println("✅ Base de datos inicializada con juegos de prueba");
    }
}
package com.cine.demo.config;

import com.cine.demo.model.*;
import com.cine.demo.model.enums.*;
import com.cine.demo.repository.*;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Component
@RequiredArgsConstructor
public class DataLoader implements ApplicationRunner {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final TheaterRepository theaterRepository;
    private final MovieRepository movieRepository;
    private final ScreeningRepository screeningRepository;
    private final MerchandiseRepository merchandiseRepository;

    @Override
    @Transactional
    public void run(ApplicationArguments args) {
        seedUsers();
        List<Theater> theaters = seedTheaters();
        List<Movie> movies = seedMovies();
        seedScreenings(movies, theaters);
        seedMerchandise();
    }

    private void seedUsers() {
        String encodedPassword = passwordEncoder.encode("lumen2024");

        List<User> initialUsers = List.of(
            User.builder().name("Administrador Sistema").username("admin1")      .email("admin@lumen.es")       .password(encodedPassword).dateOfBirth(LocalDate.of(1985, 1, 1)).role(Role.ADMIN)       .status("active").build(),
            User.builder().name("Supervisor Cine")      .username("supervisor1") .email("supervisor@lumen.es")  .password(encodedPassword).dateOfBirth(LocalDate.of(1988, 5,15)).role(Role.SUPERVISOR)  .status("active").build(),
            User.builder().name("Operador Cine")        .username("operador1")   .email("operador@lumen.es")    .password(encodedPassword).dateOfBirth(LocalDate.of(1990, 3,20)).role(Role.OPERATOR)    .status("active").build(),
            User.builder().name("Taquillero Cine")      .username("taquillero1") .email("taquillero@lumen.es")  .password(encodedPassword).dateOfBirth(LocalDate.of(1992, 7,10)).role(Role.TICKET)      .status("active").build(),
            User.builder().name("Técnico Cine")         .username("tecnico1")    .email("tecnico@lumen.es")     .password(encodedPassword).dateOfBirth(LocalDate.of(1993,11,25)).role(Role.MAINTENANCE) .status("active").build(),
            User.builder().name("Solo Lectura")         .username("readonly1")   .email("readonly@lumen.es")    .password(encodedPassword).dateOfBirth(LocalDate.of(1995, 8,30)).role(Role.READONLY)    .status("active").build()
        );

        for (User user : initialUsers) {
            if (userRepository.findByUsername(user.getUsername()).isEmpty()) {
                userRepository.save(user);
            }
        }
    }

    private List<Theater> seedTheaters() {
        if (theaterRepository.count() > 0) {
            return theaterRepository.findAll();
        }

        List<Theater> theaters = new ArrayList<>();
        String[][] theaterDefs = {
            {"Sala 1 - IMAX",  "120"},
            {"Sala 2 - VIP",   "60"},
            {"Sala 3",         "100"},
            {"Sala 4",         "80"},
        };

        for (String[] def : theaterDefs) {
            Theater theater = Theater.builder()
                    .name(def[0])
                    .capacity(Integer.parseInt(def[1]))
                    .status("active")
                    .build();
            theater = theaterRepository.save(theater);
            addSeats(theater, Integer.parseInt(def[1]));
            theaters.add(theater);
        }
        return theaters;
    }

    private void addSeats(Theater theater, int capacity) {
        String[] rows = {"A","B","C","D","E","F","G","H","I","J"};
        int seatsPerRow = capacity / rows.length;
        List<Seat> seats = new ArrayList<>();
        for (String row : rows) {
            for (int n = 1; n <= seatsPerRow; n++) {
                SeatType type = row.equals("A") || row.equals("B") ? SeatType.VIP : SeatType.STANDARD;
                seats.add(Seat.builder().theater(theater).row(row).number(n).type(type).build());
            }
        }
        theater.getSeats().addAll(seats);
        theaterRepository.save(theater);
    }

    private List<Movie> seedMovies() {
        if (movieRepository.count() > 0) {
            return movieRepository.findAll();
        }

        return movieRepository.saveAll(List.of(
            Movie.builder().title("Dune: Parte Dos").description("La épica continuación de la saga galáctica.").director("Denis Villeneuve").year(2024).genre("Ciencia Ficción").language("Español").format("IMAX").durationMin(166).ageRating(AgeRating.TWELVE).active(true).build(),
            Movie.builder().title("Oppenheimer").description("La historia del padre de la bomba atómica.").director("Christopher Nolan").year(2023).genre("Drama").language("Español").format("2D").durationMin(180).ageRating(AgeRating.SIXTEEN).active(true).build(),
            Movie.builder().title("El Rey León").description("Clásico animado de Disney.").director("Roger Allers").year(1994).genre("Animación").language("Español").format("2D").durationMin(88).ageRating(AgeRating.ALL).active(true).build(),
            Movie.builder().title("Spider-Man: No Way Home").description("Peter Parker enfrenta a sus mayores enemigos.").director("Jon Watts").year(2021).genre("Acción").language("Español").format("3D").durationMin(148).ageRating(AgeRating.TWELVE).active(true).build(),
            Movie.builder().title("Avatar: El Camino del Agua").description("Jake Sully y su familia luchan por sobrevivir en Pandora.").director("James Cameron").year(2022).genre("Ciencia Ficción").language("Español").format("3D").durationMin(192).ageRating(AgeRating.TWELVE).active(true).build(),
            Movie.builder().title("Beetlejuice Beetlejuice").description("El regreso del fantasma con más estilo.").director("Tim Burton").year(2024).genre("Comedia").language("Español").format("2D").durationMin(104).ageRating(AgeRating.TWELVE).active(true).build()
        ));
    }

    private void seedMerchandise() {
        if (merchandiseRepository.count() > 0) return;

        merchandiseRepository.saveAll(List.of(
            Merchandise.builder().name("Palomitas Grandes").description("Palomitas de maíz tamaño grande").category(MerchandiseCategory.FOOD).price(5.50).stock(100).active(true).build(),
            Merchandise.builder().name("Refresco").description("Refresco 500ml").category(MerchandiseCategory.DRINK).price(3.00).stock(150).active(true).build(),
            Merchandise.builder().name("Agua").description("Botella de agua 500ml").category(MerchandiseCategory.DRINK).price(2.00).stock(200).active(true).build(),
            Merchandise.builder().name("Nachos").description("Nachos con salsa").category(MerchandiseCategory.FOOD).price(4.50).stock(80).active(true).build(),
            Merchandise.builder().name("Camiseta Lumen Cinema").description("Camiseta oficial del cine").category(MerchandiseCategory.CLOTHING).price(19.99).stock(50).active(true).build(),
            Merchandise.builder().name("Taza Lumen Cinema").description("Taza coleccionable").category(MerchandiseCategory.COLLECTIBLES).price(12.99).stock(30).active(true).build(),
            Merchandise.builder().name("Poster Dune").description("Poster oficial de Dune Parte 2").category(MerchandiseCategory.POSTERS).price(8.99).stock(40).active(true).build(),
            Merchandise.builder().name("Combo Familiar").description("2 palomitas grandes + 2 refrescos").category(MerchandiseCategory.FOOD).price(14.99).stock(60).active(true).build()
        ));
    }

    private void seedScreenings(List<Movie> movies, List<Theater> theaters) {
        if (screeningRepository.count() > 0) return;

        LocalDateTime base = LocalDateTime.now().withHour(16).withMinute(0).withSecond(0).withNano(0);

        List<Screening> screenings = new ArrayList<>();
        for (int day = 0; day < 7; day++) {
            LocalDateTime date = base.plusDays(day);
            for (int i = 0; i < movies.size() && i < theaters.size(); i++) {
                Theater t = theaters.get(i % theaters.size());
                screenings.add(Screening.builder()
                        .movie(movies.get(i))
                        .theater(t)
                        .dateTime(date.plusHours(i * 2L))
                        .basePrice(new BigDecimal(i == 1 ? "14.00" : "9.50"))
                        .availableSeats(t.getCapacity())
                        .status(ScreeningStatus.SCHEDULED)
                        .build());
            }
        }
        screeningRepository.saveAll(screenings);
    }
}
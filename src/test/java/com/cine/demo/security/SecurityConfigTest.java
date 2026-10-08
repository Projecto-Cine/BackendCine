package com.cine.demo.security;

import com.cine.demo.controller.ClientsController;
import com.cine.demo.controller.MovieController;
import com.cine.demo.controller.ScreeningController;
import com.cine.demo.service.CloudinaryService;
import com.cine.demo.service.MovieService;
import com.cine.demo.service.PurchaseService;
import com.cine.demo.service.ScreeningService;
import com.cine.demo.service.UserService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

/**
 * Comprueba la cadena de autorizacion real, no solo que exista configuracion.
 *
 * La version anterior de este fichero afirmaba unicamente que la clase llevara
 * la anotacion de method security y que se pudiera instanciar. Eso pasaba igual
 * cuando la cadena terminaba en anyRequest().permitAll(), es decir cuando no
 * protegia nada. Lo que importa es quien entra sin credenciales y quien no.
 */
@WebMvcTest(controllers = {MovieController.class, ScreeningController.class, ClientsController.class})
@Import(SecurityConfig.class)
class SecurityConfigTest {

    @Autowired private MockMvc mockMvc;

    @MockitoBean private JwtUtil jwtUtil;
    @MockitoBean private MovieService movieService;
    @MockitoBean private ScreeningService screeningService;
    @MockitoBean private UserService userService;
    @MockitoBean private PurchaseService purchaseService;
    @MockitoBean private CloudinaryService cloudinaryService;

    // --- Catalogo publico: la tienda debe funcionar sin cuenta -------------

    @Test
    void movieCatalogue_isPublic() throws Exception {
        mockMvc.perform(get("/api/movies")).andExpect(status().isOk());
    }

    @Test
    void activeMovies_arePublic() throws Exception {
        mockMvc.perform(get("/api/movies/active")).andExpect(status().isOk());
    }

    @Test
    void screeningList_isPublic() throws Exception {
        mockMvc.perform(get("/api/screenings")).andExpect(status().isOk());
    }

    @Test
    void upcomingScreenings_arePublic() throws Exception {
        mockMvc.perform(get("/api/screenings/upcoming")).andExpect(status().isOk());
    }

    @Test
    void seatMapOfAScreening_isPublic() throws Exception {
        mockMvc.perform(get("/api/screenings/1/seats")).andExpect(status().isOk());
    }

    // --- Todo lo demas exige sesion ----------------------------------------

    @Test
    void clientData_requiresAuthentication() throws Exception {
        mockMvc.perform(get("/api/clients")).andExpect(status().isUnauthorized());
    }

    @Test
    void deletingAMovie_requiresAuthentication() throws Exception {
        mockMvc.perform(delete("/api/movies/1")).andExpect(status().isUnauthorized());
    }

    @Test
    void purchasesOfAScreening_requireAuthentication() throws Exception {
        mockMvc.perform(get("/api/screenings/1/purchases")).andExpect(status().isUnauthorized());
    }

    /**
     * El cambio de fondo del bloque: el criterio por defecto pasa de permitir a
     * exigir. Una ruta nueva que nadie recuerde anotar queda cerrada, no
     * abierta.
     */
    @Test
    void anUnknownRoute_defaultsToRequiringAuthentication() throws Exception {
        mockMvc.perform(get("/api/una-ruta-que-no-existe"))
                .andExpect(status().isUnauthorized());
    }

    @Test
    void methodSecurityIsEnabled_soControllerAnnotationsApply() {
        assertThat(SecurityConfig.class.isAnnotationPresent(EnableMethodSecurity.class)).isTrue();
    }
}

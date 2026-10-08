package com.cine.demo.security;

import com.cine.demo.controller.ClientsController;
import com.cine.demo.controller.DashboardController;
import com.cine.demo.controller.EmployeeController;
import com.cine.demo.controller.IncidentController;
import com.cine.demo.controller.MovieController;
import com.cine.demo.controller.PaymentController;
import com.cine.demo.controller.PurchaseController;
import com.cine.demo.controller.ReportController;
import com.cine.demo.controller.ShiftController;
import com.cine.demo.controller.UserController;
import com.cine.demo.exception.GlobalExceptionHandler;
import com.cine.demo.security.access.OwnershipGuard;
import com.cine.demo.service.CloudinaryService;
import com.cine.demo.service.DashboardService;
import com.cine.demo.service.EmployeeService;
import com.cine.demo.service.IncidentService;
import com.cine.demo.service.MovieService;
import com.cine.demo.service.PaymentService;
import com.cine.demo.service.PurchaseService;
import com.cine.demo.service.ReportService;
import com.cine.demo.service.ShiftService;
import com.cine.demo.service.UserService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

/**
 * Matriz de autorizacion de la API.
 *
 * Este fichero existe porque la suite anterior no probaba el acceso en ningun
 * punto: los tests de controlador usan slices de @WebMvcTest sin method
 * security, de modo que @PreAuthorize no se evalua y todo responde 200 aunque
 * la politica estuviera mal. Pasaban igual cuando la configuracion terminaba en
 * anyRequest().permitAll() y dieciocho de veinte controladores no tenian
 * ninguna anotacion.
 *
 * Aqui se activa method security de forma explicita y se afirma el 403, que es
 * la parte que de verdad protege los datos. Cada caso denegado corresponde a un
 * acceso que antes era posible.
 */
@WebMvcTest(controllers = {
        ClientsController.class,
        UserController.class,
        EmployeeController.class,
        PaymentController.class,
        PurchaseController.class,
        DashboardController.class,
        ReportController.class,
        ShiftController.class,
        MovieController.class,
        IncidentController.class,
})
@Import({AuthorizationMatrixTest.MethodSecurityEnabled.class, GlobalExceptionHandler.class})
class AuthorizationMatrixTest {

    @TestConfiguration
    @EnableMethodSecurity
    static class MethodSecurityEnabled {
    }

    @Autowired private MockMvc mockMvc;

    @MockitoBean(name = "ownership") private OwnershipGuard ownership;

    @MockitoBean private UserService userService;
    @MockitoBean private EmployeeService employeeService;
    @MockitoBean private PaymentService paymentService;
    @MockitoBean private PurchaseService purchaseService;
    @MockitoBean private DashboardService dashboardService;
    @MockitoBean private ReportService reportService;
    @MockitoBean private ShiftService shiftService;
    @MockitoBean private MovieService movieService;
    @MockitoBean private IncidentService incidentService;
    @MockitoBean private CloudinaryService cloudinaryService;

    // ---------------------------------------------------------------------
    // Datos personales de clientes. Antes cualquier sesion valida los leia.
    // ---------------------------------------------------------------------

    @Test
    @WithMockUser(authorities = "CLIENT")
    void client_cannotListEveryClient() throws Exception {
        mockMvc.perform(get("/api/clients")).andExpect(status().isForbidden());
    }

    @Test
    @WithMockUser(authorities = "CAJERO")
    void cashier_cannotListEveryClient() throws Exception {
        mockMvc.perform(get("/api/clients")).andExpect(status().isForbidden());
    }

    @Test
    @WithMockUser(authorities = "GERENCIA")
    void management_canListClients() throws Exception {
        mockMvc.perform(get("/api/clients")).andExpect(status().isOk());
    }

    @Test
    @WithMockUser(authorities = "CLIENT")
    void client_cannotDeleteAnotherClient() throws Exception {
        mockMvc.perform(delete("/api/clients/7").with(csrf()))
                .andExpect(status().isForbidden());
    }

    @Test
    @WithMockUser(authorities = "CLIENT")
    void client_cannotLookUpUsersByEmail() throws Exception {
        // Enumeracion de cuentas: confirmaba si un email estaba registrado.
        mockMvc.perform(get("/api/users/by-email").param("email", "ana@test.com"))
                .andExpect(status().isForbidden());
    }

    @Test
    @WithMockUser(authorities = "CLIENT")
    void client_cannotSearchUsers() throws Exception {
        mockMvc.perform(get("/api/users/search").param("q", "ana"))
                .andExpect(status().isForbidden());
    }

    // ---------------------------------------------------------------------
    // Propiedad: el rol no distingue a un cliente de otro.
    // ---------------------------------------------------------------------

    @Test
    @WithMockUser(authorities = "CLIENT")
    void client_cannotReadAnotherUsersProfile() throws Exception {
        when(ownership.isSelf(anyLong())).thenReturn(false);

        mockMvc.perform(get("/api/users/99")).andExpect(status().isForbidden());
    }

    @Test
    @WithMockUser(authorities = "CLIENT")
    void client_canReadOwnProfile() throws Exception {
        when(ownership.isSelf(5L)).thenReturn(true);

        mockMvc.perform(get("/api/users/5")).andExpect(status().isOk());
    }

    @Test
    @WithMockUser(authorities = "CLIENT")
    void client_cannotReadAnotherUsersPurchase() throws Exception {
        when(ownership.ownsPurchase(anyLong())).thenReturn(false);

        mockMvc.perform(get("/api/purchases/42")).andExpect(status().isForbidden());
    }

    @Test
    @WithMockUser(authorities = "CLIENT")
    void client_canReadOwnPurchase() throws Exception {
        when(ownership.ownsPurchase(42L)).thenReturn(true);

        mockMvc.perform(get("/api/purchases/42")).andExpect(status().isOk());
    }

    @Test
    @WithMockUser(authorities = "CLIENT")
    void client_cannotListAnotherUsersPurchases() throws Exception {
        when(ownership.isSelf(anyLong())).thenReturn(false);

        mockMvc.perform(get("/api/purchases/user/99")).andExpect(status().isForbidden());
    }

    // ---------------------------------------------------------------------
    // Gestion de personal y del negocio: solo gerencia.
    // ---------------------------------------------------------------------

    @Test
    @WithMockUser(authorities = "CAJERO")
    void cashier_cannotListEmployees() throws Exception {
        mockMvc.perform(get("/api/employees")).andExpect(status().isForbidden());
    }

    @Test
    @WithMockUser(authorities = "CLIENT")
    void client_cannotListEmployees() throws Exception {
        mockMvc.perform(get("/api/employees")).andExpect(status().isForbidden());
    }

    @Test
    @WithMockUser(authorities = "GERENCIA")
    void management_canListEmployees() throws Exception {
        mockMvc.perform(get("/api/employees")).andExpect(status().isOk());
    }

    @Test
    @WithMockUser(authorities = "CAJERO")
    void cashier_cannotSeeTheDashboard() throws Exception {
        mockMvc.perform(get("/api/dashboard")).andExpect(status().isForbidden());
    }

    @Test
    @WithMockUser(authorities = "CAJERO")
    void cashier_cannotSeeReports() throws Exception {
        mockMvc.perform(get("/api/reports/sales-week")).andExpect(status().isForbidden());
    }

    @Test
    @WithMockUser(authorities = "CLIENT")
    void client_cannotDeleteMovies() throws Exception {
        mockMvc.perform(delete("/api/movies/3").with(csrf()))
                .andExpect(status().isForbidden());
    }

    // ---------------------------------------------------------------------
    // Dinero: devoluciones e historico de facturacion.
    // ---------------------------------------------------------------------

    @Test
    @WithMockUser(authorities = "CAJERO")
    void cashier_cannotIssueRefunds() throws Exception {
        mockMvc.perform(post("/api/payments/refund")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"purchaseId\":1,\"reason\":\"prueba\"}"))
                .andExpect(status().isForbidden());
    }

    @Test
    @WithMockUser(authorities = "CLIENT")
    void client_cannotIssueRefunds() throws Exception {
        mockMvc.perform(post("/api/payments/refund")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"purchaseId\":1,\"reason\":\"prueba\"}"))
                .andExpect(status().isForbidden());
    }

    @Test
    @WithMockUser(authorities = "CAJERO")
    void cashier_cannotReadTheBillingHistory() throws Exception {
        mockMvc.perform(get("/api/payments/history")).andExpect(status().isForbidden());
    }

    @Test
    @WithMockUser(authorities = "CLIENT")
    void client_cannotListEveryPurchase() throws Exception {
        mockMvc.perform(get("/api/purchases")).andExpect(status().isForbidden());
    }

    // ---------------------------------------------------------------------
    // Turnos: los consulta toda la plantilla, los edita gerencia.
    // ---------------------------------------------------------------------

    @Test
    @WithMockUser(authorities = "LIMPIEZA")
    void cleaningStaff_canReadTheirShifts() throws Exception {
        mockMvc.perform(get("/api/shifts")).andExpect(status().isOk());
    }

    @Test
    @WithMockUser(authorities = "MANTENIMIENTO")
    void maintenanceStaff_canReadTheirShifts() throws Exception {
        mockMvc.perform(get("/api/shifts")).andExpect(status().isOk());
    }

    @Test
    @WithMockUser(authorities = "LIMPIEZA")
    void cleaningStaff_cannotDeleteShifts() throws Exception {
        mockMvc.perform(delete("/api/shifts/1").with(csrf()))
                .andExpect(status().isForbidden());
    }

    @Test
    @WithMockUser(authorities = "CLIENT")
    void client_cannotReadShifts() throws Exception {
        mockMvc.perform(get("/api/shifts")).andExpect(status().isForbidden());
    }

    // ---------------------------------------------------------------------
    // Incidencias: mantenimiento y gerencia.
    // ---------------------------------------------------------------------

    @Test
    @WithMockUser(authorities = "MANTENIMIENTO")
    void maintenanceStaff_canReadIncidents() throws Exception {
        mockMvc.perform(get("/api/incidents")).andExpect(status().isOk());
    }

    @Test
    @WithMockUser(authorities = "CAJERO")
    void cashier_cannotReadIncidents() throws Exception {
        mockMvc.perform(get("/api/incidents")).andExpect(status().isForbidden());
    }

    @Test
    @WithMockUser(authorities = "LIMPIEZA")
    void cleaningStaff_cannotReadIncidents() throws Exception {
        mockMvc.perform(get("/api/incidents")).andExpect(status().isForbidden());
    }

    /** Sin la llamada a csrf() las peticiones mutantes fallarian por otro motivo. */
    private static org.springframework.test.web.servlet.request.RequestPostProcessor csrf() {
        return org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf();
    }
}

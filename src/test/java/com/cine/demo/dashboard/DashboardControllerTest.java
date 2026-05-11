package com.cine.demo.dashboard;

import com.cine.demo.controller.DashboardController;
import com.cine.demo.exception.GlobalExceptionHandler;
import com.cine.demo.service.DashboardService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(DashboardController.class)
@Import(GlobalExceptionHandler.class)
class DashboardControllerTest {

    @Autowired private MockMvc mockMvc;
    @MockitoBean private DashboardService dashboardService;

    @Test
    void getDashboard_returns200_inSkeleton() throws Exception {
        mockMvc.perform(get("/api/dashboard"))
                .andExpect(status().isOk());
    }
}

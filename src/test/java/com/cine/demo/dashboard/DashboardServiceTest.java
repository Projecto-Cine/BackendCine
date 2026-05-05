package com.cine.demo.dashboard;

import com.cine.demo.service.impl.DashboardServiceImpl;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class DashboardServiceTest {

    private final DashboardServiceImpl service = new DashboardServiceImpl();

    /**
     * El servicio Dashboard todavía es un esqueleto: getDashboardData
     * devuelve null hasta que se implemente la lógica de KPIs reales.
     * Este test fija ese contrato.
     */
    @Test
    void getDashboardData_returnsNull_inCurrentSkeleton() {
        assertThat(service.getDashboardData()).isNull();
    }
}

package com.vnh.services.impl;

import com.vnh.repositories.StatsRepository;
import java.util.Collections;
import java.util.List;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class StatsServiceImplTest {

    @Mock private StatsRepository statsRepo;
    @InjectMocks private StatsServiceImpl service;

    @Test
    void delegatesDashboardStatsToRepository() {
        List<Object[]> rows = Collections.singletonList(new Object[]{"A", 1L});
        when(statsRepo.countPatientsByGender()).thenReturn(rows);
        when(statsRepo.countPatientsByAgeGroup()).thenReturn(rows);
        when(statsRepo.countPatientsBySpecialty()).thenReturn(rows);
        when(statsRepo.topUsedServices()).thenReturn(rows);
        when(statsRepo.popularDiseases()).thenReturn(rows);
        when(statsRepo.revenueByTime(2026, "month")).thenReturn(rows);

        assertSame(rows, service.countPatientsByGender());
        assertSame(rows, service.countPatientsByAgeGroup());
        assertSame(rows, service.countPatientsBySpecialty());
        assertSame(rows, service.countServicesUsed());
        assertSame(rows, service.countPopularDiseases());
        assertSame(rows, service.getRevenueByTime("month", 2026));
    }
}

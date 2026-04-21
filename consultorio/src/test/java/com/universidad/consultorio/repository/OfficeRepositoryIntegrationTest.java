package com.universidad.consultorio.repository;

import com.universidad.consultorio.entity.Office;
import com.universidad.consultorio.enums.OfficeStatus;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.ActiveProfiles;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
@ActiveProfiles("test")
class OfficeRepositoryIntegrationTest {

    @Autowired private OfficeRepository officeRepository;

    @BeforeEach
    void setUp() {
        officeRepository.deleteAll();
        officeRepository.save(Office.builder().name("C-01").status(OfficeStatus.ACTIVE).build());
        officeRepository.save(Office.builder().name("C-02").status(OfficeStatus.INACTIVE).build());
        officeRepository.save(Office.builder().name("C-03").status(OfficeStatus.MAINTENANCE).build());
    }

    @Test
    @DisplayName("findByStatus ACTIVE - only active offices returned")
    void findByStatus_active() {
        List<Office> result = officeRepository.findByStatus(OfficeStatus.ACTIVE);
        assertThat(result).hasSize(1);
        assertThat(result.get(0).getName()).isEqualTo("C-01");
    }

    @Test
    @DisplayName("findByStatus MAINTENANCE - only maintenance offices returned")
    void findByStatus_maintenance() {
        List<Office> result = officeRepository.findByStatus(OfficeStatus.MAINTENANCE);
        assertThat(result).hasSize(1);
    }
}

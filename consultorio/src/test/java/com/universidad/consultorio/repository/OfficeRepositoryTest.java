
package com.universidad.consultorio.repository;

import com.universidad.consultorio.entity.Office;
import com.universidad.consultorio.enums.OfficeStatus;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.data.jpa.test.autoconfigure.DataJpaTest;
import org.springframework.test.context.ActiveProfiles;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
@ActiveProfiles("test")
class OfficeRepositoryTest {

    @Autowired private OfficeRepository officeRepository;

    private Office office;

    @BeforeEach
    void setUp() {
        office = officeRepository.save(Office.builder()
                .name("Consultorio 101")
                .location("Piso 1")
                .floor("101")
                .status(OfficeStatus.ACTIVE)
                .build());
    }

    @Test
    @DisplayName("findByStatus - encuentra consultorios por estado")
    void shouldFindByStatus() {
        var inactiveOffice = officeRepository.save(Office.builder()
                .name("Consultorio 102")
                .location("Piso 1")
                .floor("102")
                .status(OfficeStatus.INACTIVE)
                .build());

        var activeOffices = officeRepository.findByStatus(OfficeStatus.ACTIVE);
        var inactiveOffices = officeRepository.findByStatus(OfficeStatus.INACTIVE);

        assertThat(activeOffices).hasSize(1);
        assertThat(activeOffices.get(0).getName()).isEqualTo("Consultorio 101");
        assertThat(inactiveOffices).hasSize(1);
        assertThat(inactiveOffices.get(0).getName()).isEqualTo("Consultorio 102");
    }

    @Test
    @DisplayName("findByStatus - retorna lista vacía si no hay consultorios con ese estado")
    void shouldReturnEmptyWhenNoOfficesWithStatus() {
        var result = officeRepository.findByStatus(OfficeStatus.MAINTENANCE);

        assertThat(result).isEmpty();
    }

    @Test
    @DisplayName("save - guarda un consultorio correctamente")
    void shouldSaveOffice() {
        var newOffice = Office.builder()
                .name("Consultorio 200")
                .location("Piso 2")
                .floor("200")
                .status(OfficeStatus.ACTIVE)
                .build();

        var saved = officeRepository.save(newOffice);

        assertThat(saved.getId()).isNotNull();
        assertThat(saved.getName()).isEqualTo("Consultorio 200");
        assertThat(saved.getStatus()).isEqualTo(OfficeStatus.ACTIVE);
    }

    @Test
    @DisplayName("findById - encuentra consultorio por ID")
    void shouldFindById() {
        var found = officeRepository.findById(office.getId());

        assertThat(found).isPresent();
        assertThat(found.get().getName()).isEqualTo("Consultorio 101");
    }
}
package com.universidad.consultorio.repository;

import com.universidad.consultorio.entity.Office;
import com.universidad.consultorio.enums.OfficeStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface OfficeRepository extends JpaRepository<Office, Long> {
    List<Office> findByStatus(OfficeStatus status);
}

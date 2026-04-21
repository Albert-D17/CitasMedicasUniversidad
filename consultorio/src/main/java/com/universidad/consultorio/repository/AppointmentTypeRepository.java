package com.universidad.consultorio.repository;

import com.universidad.consultorio.entity.AppointmentType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface AppointmentTypeRepository extends JpaRepository<AppointmentType, Long> {
}

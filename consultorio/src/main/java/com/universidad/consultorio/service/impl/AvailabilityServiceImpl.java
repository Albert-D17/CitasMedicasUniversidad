package com.universidad.consultorio.service.impl;

import com.universidad.consultorio.dto.response.AvailabilitySlotResponse;
import com.universidad.consultorio.entity.Appointment;
import com.universidad.consultorio.entity.AppointmentType;
import com.universidad.consultorio.entity.DoctorSchedule;
import com.universidad.consultorio.exception.ResourceNotFoundException;
import com.universidad.consultorio.repository.AppointmentRepository;
import com.universidad.consultorio.repository.AppointmentTypeRepository;
import com.universidad.consultorio.repository.DoctorRepository;
import com.universidad.consultorio.repository.DoctorScheduleRepository;
import com.universidad.consultorio.service.AvailabilityService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class AvailabilityServiceImpl implements AvailabilityService {

    private final DoctorRepository doctorRepository;
    private final DoctorScheduleRepository doctorScheduleRepository;
    private final AppointmentRepository appointmentRepository;
    private final AppointmentTypeRepository appointmentTypeRepository;

    @Override
    @Transactional(readOnly = true)
    public List<AvailabilitySlotResponse> getAvailableSlots(Long doctorId, LocalDate date, Long appointmentTypeId) {
        if (!doctorRepository.existsById(doctorId)) {
            throw new ResourceNotFoundException("Doctor not found with id: " + doctorId);
        }

        AppointmentType appointmentType = appointmentTypeRepository.findById(appointmentTypeId)
                .orElseThrow(() -> new ResourceNotFoundException("Appointment type not found: " + appointmentTypeId));

        List<DoctorSchedule> schedules = doctorScheduleRepository
                .findByDoctorIdAndDayOfWeek(doctorId, date.getDayOfWeek());

        if (schedules.isEmpty()) {
            return List.of();
        }

        // Obtener citas ya confirmadas o programadas del doctor en esa fecha
        LocalDateTime dayStart = date.atStartOfDay();
        LocalDateTime dayEnd   = date.atTime(LocalTime.MAX);
        List<Appointment> existing = appointmentRepository
                .findActiveDoctorAppointmentsInRange(doctorId, dayStart, dayEnd);

        int duration = appointmentType.getDurationMinutes();
        List<AvailabilitySlotResponse> slots = new ArrayList<>();

        for (DoctorSchedule schedule : schedules) {
            LocalDateTime cursor = date.atTime(schedule.getStartTime());
            LocalDateTime scheduleEnd = date.atTime(schedule.getEndTime());

            while (!cursor.plusMinutes(duration).isAfter(scheduleEnd)) {
                LocalDateTime slotEnd = cursor.plusMinutes(duration);
                final LocalDateTime slotStart = cursor;

                // El slot es válido si no solapa con ninguna cita existente
                boolean overlaps = existing.stream().anyMatch(a ->
                        slotStart.isBefore(a.getEndAt()) && slotEnd.isAfter(a.getStartAt())
                );

                if (!overlaps) {
                    slots.add(AvailabilitySlotResponse.builder()
                            .startAt(slotStart)
                            .endAt(slotEnd)
                            .build());
                }

                cursor = cursor.plusMinutes(duration);
            }
        }

        return slots;
    }
}

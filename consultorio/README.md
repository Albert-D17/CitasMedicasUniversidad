# Plataforma de Reservas de Consultorios Médicos Universitarios

API REST para la gestión de citas médicas universitarias, desarrollada con Java 21, Spring Boot 3.3 y PostgreSQL.

## Stack tecnológico

- **Java 21** · **Spring Boot 3.3** · **Spring Data JPA**
- **PostgreSQL** (base de datos principal)
- **Testcontainers** (pruebas de integración de repositorios)
- **JUnit 5** + **Mockito** (pruebas unitarias de servicios y controladores)
- **Lombok** (reducción de boilerplate)

## Requisitos previos

- Java 21
- Maven 3.9+
- PostgreSQL 15+ corriendo localmente
- Docker (para Testcontainers en las pruebas)

## Configuración y ejecución

1. Crear la base de datos:
```sql
CREATE DATABASE consultorio_db;
```

2. Ajustar credenciales en `src/main/resources/application.properties`:
```properties
spring.datasource.url=jdbc:postgresql://localhost:5432/consultorio_db
spring.datasource.username=postgres
spring.datasource.password=postgres
```

3. Compilar y ejecutar:
```bash
mvn clean spring-boot:run
```

4. La API queda disponible en `http://localhost:8080`

## Ejecutar pruebas

```bash
mvn test
```

Las pruebas de repositorio utilizan Testcontainers y levantan PostgreSQL automáticamente en Docker. Las pruebas de servicio y controlador son unitarias y no requieren base de datos.

## Arquitectura por capas

```
controller   →  recibe HTTP, valida DTOs, delega al service
service      →  aplica reglas de negocio, orquesta repositorios
repository   →  acceso a datos JPA (query methods + JPQL)
mapper       →  convierte entidades ↔ DTOs
entity       →  modelo de dominio JPA
dto          →  contratos de entrada/salida de la API
exception    →  excepciones de dominio + manejador global
```

## Reglas de negocio principales

### Creación de citas
- El paciente y el doctor deben existir y estar activos.
- El consultorio debe existir y estar en estado `ACTIVE`.
- No se aceptan citas en fechas pasadas.
- La cita debe caer dentro del horario laboral configurado para el doctor en ese día.
- `endAt` **no** es enviado por el cliente; el servicio lo calcula usando la duración del tipo de cita.
- No puede haber traslape de horario para el doctor, el consultorio ni el paciente.
- Toda cita nueva inicia en estado `SCHEDULED`.

### Transiciones de estado

| Estado inicial | Acción       | Estado final | Restricciones adicionales                             |
|----------------|-------------|--------------|-------------------------------------------------------|
| SCHEDULED      | confirm     | CONFIRMED    | —                                                     |
| SCHEDULED/CONFIRMED | cancel | CANCELLED  | Requiere motivo obligatorio                           |
| CONFIRMED      | complete    | COMPLETED    | Solo si la hora actual ≥ `startAt`                   |
| CONFIRMED      | no-show     | NO_SHOW      | Solo si la hora actual ≥ `startAt`                   |

Estados terminales (`COMPLETED`, `NO_SHOW`, `CANCELLED`) no admiten transiciones adicionales.

### Disponibilidad
- Los slots disponibles se generan a partir del horario del doctor, la duración del tipo de cita y las citas no canceladas ya existentes.
- Solo se devuelven bloques completos y libres.

## Endpoints

### Pacientes
| Método | URL | Descripción |
|--------|-----|-------------|
| POST | /api/patients | Registrar paciente |
| GET | /api/patients | Listar pacientes |
| GET | /api/patients/{id} | Obtener paciente |
| PUT | /api/patients/{id} | Actualizar paciente |

### Doctores
| Método | URL | Descripción |
|--------|-----|-------------|
| POST | /api/doctors | Registrar doctor |
| GET | /api/doctors | Listar doctores (filtro opcional `?specialtyId=`) |
| GET | /api/doctors/{id} | Obtener doctor |
| PUT | /api/doctors/{id} | Actualizar doctor |

### Especialidades
| Método | URL | Descripción |
|--------|-----|-------------|
| POST | /api/specialties | Crear especialidad |
| GET | /api/specialties | Listar especialidades |

### Consultorios
| Método | URL | Descripción |
|--------|-----|-------------|
| POST | /api/offices | Crear consultorio |
| GET | /api/offices | Listar consultorios |
| PUT | /api/offices/{id} | Actualizar consultorio |

### Tipos de cita
| Método | URL | Descripción |
|--------|-----|-------------|
| POST | /api/appointment-types | Crear tipo de cita |
| GET | /api/appointment-types | Listar tipos de cita |

### Horarios de doctor
| Método | URL | Descripción |
|--------|-----|-------------|
| POST | /api/doctors/{doctorId}/schedules | Añadir horario |
| GET | /api/doctors/{doctorId}/schedules | Ver horarios del doctor |

### Citas
| Método | URL | Descripción |
|--------|-----|-------------|
| POST | /api/appointments | Crear cita |
| GET | /api/appointments | Listar citas |
| GET | /api/appointments/{id} | Obtener cita |
| PUT | /api/appointments/{id}/confirm | Confirmar cita |
| PUT | /api/appointments/{id}/cancel | Cancelar cita |
| PUT | /api/appointments/{id}/complete | Completar cita |
| PUT | /api/appointments/{id}/no-show | Marcar no asistida |

### Disponibilidad y reportes
| Método | URL | Descripción |
|--------|-----|-------------|
| GET | /api/availability/doctors/{doctorId}?date=YYYY-MM-DD&appointmentTypeId= | Slots disponibles |
| GET | /api/reports/office-occupancy?from=&to= | Ocupación de consultorios |
| GET | /api/reports/doctor-productivity?from=&to= | Productividad de doctores |
| GET | /api/reports/no-show-patients?from=&to= | Pacientes con inasistencias |

## Códigos de respuesta HTTP

| Código | Situación |
|--------|-----------|
| 200 | OK — consulta o actualización exitosa |
| 201 | Created — recurso creado |
| 400 | Bad Request — validación de campos o regla de negocio violada |
| 404 | Not Found — recurso no existe |
| 409 | Conflict — traslape de horario o duplicado |
| 500 | Internal Server Error |

## Decisiones de diseño

- **`endAt` calculado en servidor**: evita inconsistencias entre cliente y duración real del tipo de cita.
- **Enums de estado**: `AppointmentStatus`, `OfficeStatus` y `PatientStatus` almacenados como `STRING` en PostgreSQL para legibilidad de la base de datos.
- **Mappers manuales**: se usaron mappers simples en lugar de MapStruct para transparencia pedagógica.
- **GlobalExceptionHandler**: centraliza el manejo de errores y produce respuestas JSON consistentes en todos los endpoints.
- **Testcontainers con perfil `test`**: los tests de repositorio usan `application-test.properties` con el driver JDBC de Testcontainers, sin necesidad de base de datos local.

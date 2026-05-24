# Peluqueria Backend

Backend del sistema de reserva de turnos para una peluqueria con varios profesionales. Construido con **Spring Boot 3 + Java 21 + MySQL 8**.

## Stack

- Java 21
- Spring Boot 3.3.x (Web, Data JPA, Security, Validation)
- MySQL 8 (`mysql-connector-j`)
- JWT (`jjwt` 0.12.x)
- springdoc-openapi (Swagger UI)
- Lombok
- JUnit 5 + Mockito (tests del calculo de slots)
- Maven (con wrapper `./mvnw`)

## Arquitectura

Paquetes por feature, capas estrictas `controller -> service (interfaz + impl) -> repository`. Las entidades JPA nunca salen del backend: se mapean a DTOs request/response.

```
com.mab.peluqueria
 |- config        (security, cors, openapi, JWT, data seeder)
 |- common        (exceptions, error response, exception handler global)
 |- auth          (login admin + JWT, usuario)
 |- servicio      (catalogo de servicios)
 |- profesional   (profesionales y los servicios que ofrecen)
 |- horario       (HorarioLaboral - bandas horarias semanales)
 |- cliente       (clientes identificados por telefono)
 |- turno         (DisponibilidadService + TurnoService - corazon del sistema)
 |- notificacion  (interfaz + mock de WhatsApp)
```

## Requisitos

- **JDK 21** instalado y `JAVA_HOME` configurado.
- **MySQL 8** corriendo localmente (o un MySQL remoto al que apuntes via variables de entorno).
- No necesitas instalar Maven: usa el wrapper `./mvnw` (Linux/Mac) o `mvnw.cmd` (Windows).

## Setup de MySQL local

```sql
CREATE DATABASE peluqueria CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;
-- Opcional: crear un usuario dedicado.
-- Si usas root, el dev profile ya esta listo con root/root por defecto.
```

El profile `dev` apunta a `localhost:3306/peluqueria` y crea la BD si no existe (`createDatabaseIfNotExist=true`).

## Variables de entorno (opcionales)

Tienen defaults razonables para desarrollo. Para `prod` son obligatorias.

| Variable | Default (dev) | Descripcion |
|---|---|---|
| `SPRING_PROFILES_ACTIVE` | `dev` | Profile activo |
| `DB_HOST` | `localhost` | Host de MySQL |
| `DB_PORT` | `3306` | Puerto de MySQL |
| `DB_NAME` | `peluqueria` | Base de datos |
| `DB_USER` | `root` | Usuario |
| `DB_PASSWORD` | `root` | Password |
| `APP_JWT_SECRET` | placeholder | Secreto HMAC para firmar JWTs (>= 256 bits) |
| `APP_JWT_EXPIRATION_MS` | `86400000` | Expiracion del token (24h) |
| `APP_CORS_ALLOWED_ORIGINS` | `http://localhost:4200,...` | Origenes permitidos por CORS, separados por coma |
| `APP_SEED` | `true` en dev | Si `true`, carga datos demo al arrancar (solo si la BD esta vacia) |
| `NOTIFICACION_PROVIDER` | `mock` | Provider de notificaciones. Hoy solo `mock` esta implementado |

## Como correrlo

### Windows (PowerShell)

```powershell
.\mvnw.cmd spring-boot:run
```

### Linux / Mac

```bash
./mvnw spring-boot:run
```

La primera vez tarda unos minutos descargando dependencias. Despues levanta en `http://localhost:8080`.

## Endpoints principales

### Publicos (no requieren JWT)

| Metodo | Path | Descripcion |
|---|---|---|
| `GET` | `/api/servicios/publicos` | Lista los servicios activos |
| `GET` | `/api/profesionales/publicos` | Lista profesionales activos con sus servicios |
| `GET` | `/api/turnos/disponibilidad?profesionalId=X&servicioId=Y&fecha=YYYY-MM-DD` | Slots disponibles |
| `POST` | `/api/turnos` | Reserva un turno |
| `GET` | `/api/turnos/cliente?telefono=+5491155...` | Turnos futuros del cliente |
| `PATCH` | `/api/turnos/{id}/cancelar?codigo=XXXXX` | Cancela usando el codigo |
| `POST` | `/api/auth/login` | Login admin -> devuelve JWT |

### Admin (requieren `Authorization: Bearer <jwt>`)

| Metodo | Path | Descripcion |
|---|---|---|
| `GET POST PUT DELETE` | `/api/servicios[...]` | CRUD de servicios |
| `GET POST PUT DELETE` | `/api/profesionales[...]` | CRUD de profesionales |
| `GET POST PUT DELETE` | `/api/horarios[...]` | CRUD de horarios laborales |
| `GET` | `/api/turnos?profesionalId=&fecha=&estado=` | Agenda con filtros |
| `PATCH` | `/api/turnos/{id}/estado` | Cambia el estado del turno |

### Swagger UI

`http://localhost:8080/swagger-ui.html`

## Credenciales demo

Cuando `app.seed=true` (default en dev), se crea un admin:

- **email**: `admin@peluqueria.com`
- **password**: `admin1234`

Y 3 profesionales, 4 servicios, horarios y algunos turnos de ejemplo en los proximos dias laborales.

## Tests

```powershell
.\mvnw.cmd test
```

El test principal es `DisponibilidadServiceImplTest`: cubre el calculo de slots disponibles (sin horario, dia completo, slots ocupados, hoy con horas pasadas, multiples bloques, profesional/servicio invalidos, etc).

## Build

```powershell
.\mvnw.cmd clean package
```

Genera `target/peluqueria-backend-0.0.1-SNAPSHOT.jar` ejecutable:

```powershell
java -jar target/peluqueria-backend-0.0.1-SNAPSHOT.jar
```

## Notas de produccion

- Setea `SPRING_PROFILES_ACTIVE=prod` y todas las variables de entorno (DB y JWT son obligatorias).
- `app.seed` queda en `false` en prod (no se cargan datos demo).
- `ddl-auto: validate` en prod: la BD debe estar migrada antes (idealmente con Flyway/Liquibase en una iteracion futura).
- Cambia `APP_CORS_ALLOWED_ORIGINS` para incluir el dominio real del frontend.
- Usa un `APP_JWT_SECRET` largo y aleatorio (>= 32 bytes).

## Proximos pasos (out of scope de este entregable)

- Integracion real con WhatsApp via Meta API o Twilio (la abstraccion ya esta lista; basta una nueva impl con otro `notificacion.provider`).
- Migraciones con Flyway.
- Tests de integracion con `@SpringBootTest` + H2.
- Rate limiting en endpoints publicos de reserva.

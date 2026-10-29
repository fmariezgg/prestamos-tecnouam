# Préstamos TecnoUAM

Aplicación web para gestionar el préstamo de equipos tecnológicos (examen integrador, Servicios Web, 2.º corte).
Backend **Spring Boot + PostgreSQL**, seguridad **JWT** con roles `USER`/`ADMIN` y frontend **React + Axios**.

## Arquitectura

```
Navegador (React :5173)
   │  Axios (interceptor añade  Authorization: Bearer <token>)
   ▼
Spring Boot :8080
   JwtAuthFilter ──► valida token ──► SecurityConfig (roles por ruta)
        │
        ▼
   Controller ──► Service (reglas de negocio, @Transactional) ──► Repository (JPA) ──► PostgreSQL (Docker)
        ▲
   DTOs + validación          GlobalExceptionHandler → cuerpo de error uniforme
```

## Requisitos

- Java 17
- Node.js 20.19+ / 22.12+ (probado con Node 26) y npm
- Docker (solo para PostgreSQL)

## Puesta en marcha

```bash
# 1. Variables de entorno (el archivo .env NO se versiona)
cp .env.example .env          # edita .env y define valores reales

# 2. Base de datos
docker compose up -d          # PostgreSQL en el puerto DB_PORT (5432 por defecto)

# 3. Backend (carga ../.env, aplica migraciones Flyway y crea las cuentas)
backend/run.sh                # API en http://localhost:8080

# 4. Frontend
cd frontend
cp .env.example .env          # VITE_API_URL=http://localhost:8080/api
npm install
npm run dev                   # http://localhost:5173
```

El esquema y los datos mínimos se crean automáticamente con Flyway
(`backend/src/main/resources/db/migration`). Copias independientes: `backend/sql/schema.sql` y `backend/sql/seed.sql`.

## Variables de entorno (solo nombres, sin valores)

| Variable | Dónde | Para qué |
|---|---|---|
| `DB_NAME`, `DB_USER`, `DB_PASSWORD`, `DB_PORT` | `.env` raíz | Credenciales y puerto de PostgreSQL (Docker y backend) |
| `JWT_SECRET` | `.env` raíz | Clave de firma de los tokens (mínimo 32 caracteres) |
| `ADMIN_PASSWORD` | `.env` raíz | Contraseña de la cuenta `admin`, creada al arrancar |
| `USER_PASSWORD` | `.env` raíz | Contraseña de la cuenta `user`, creada al arrancar |
| `DB_URL` (opcional) | entorno | URL JDBC; por defecto `jdbc:postgresql://localhost:5432/tecnouam`. Si cambias `DB_PORT`, ajústala |
| `CORS_ORIGINS` (opcional) | entorno | Orígenes permitidos; por defecto `http://localhost:5173` |
| `VITE_API_URL` | `frontend/.env` | URL base de la API que usa Axios |

## Cuentas de prueba

| Usuario | Rol | Contraseña |
|---|---|---|
| `admin` | `ADMIN` | la definida en `ADMIN_PASSWORD` |
| `user` | `USER` | la definida en `USER_PASSWORD` |

## Endpoints

Todos devuelven JSON. Excepto el login, requieren `Authorization: Bearer <token>`.

| Método y ruta | Rol | Éxito | Errores posibles |
|---|---|---|---|
| `POST /api/auth/login` | público | 200 `{token, tokenType, expiresIn, username, rol}` | 400 datos inválidos, 401 credenciales incorrectas |
| `GET /api/equipos` | USER, ADMIN | 200 | 401 |
| `GET /api/equipos/{id}` | USER, ADMIN | 200 | 401, 404 |
| `POST /api/equipos` | ADMIN | 201 | 400, 401, 403, 409 código duplicado |
| `PUT /api/equipos/{id}` | ADMIN | 200 | 400, 401, 403, 404, 409 |
| `DELETE /api/equipos/{id}` | ADMIN | 204 | 401, 403, 404, 409 préstamo activo |
| `GET /api/prestamos` | USER, ADMIN | 200 | 401 |
| `POST /api/prestamos` | ADMIN | 201 | 400 fechas inválidas, 401, 403, 404 equipo, 409 equipo ya prestado |
| `PUT /api/prestamos/{id}/devolver` | ADMIN | 200 | 401, 403, 404, 409 ya devuelto |

Cuerpo de error: `{timestamp, status, error, message, path, fieldErrors?}`.

## Reglas de negocio

- Un equipo `PRESTADO` no puede prestarse de nuevo (409).
- Registrar un préstamo pone el equipo en `PRESTADO`; devolverlo lo vuelve a `DISPONIBLE` y guarda `fechaDevolucionReal`. Ambos cambios ocurren en una sola transacción.
- La fecha prevista de devolución no puede ser anterior a la fecha del préstamo (400).
- `codigoPatrimonial` es único (409).
- Un equipo con préstamo `ACTIVO` no se puede eliminar (409). Si solo tiene préstamos `DEVUELTO`, se elimina junto con ese historial.
- Solo `ADMIN` crea, modifica, elimina, presta y devuelve; `USER` solo consulta.

## Flujo JWT

1. `POST /api/auth/login` valida usuario y contraseña (BCrypt) y devuelve un token firmado con `JWT_SECRET` (rol y expiración dentro del token).
2. React guarda la sesión en `sessionStorage`; un interceptor de Axios añade `Authorization: Bearer <token>` a cada petición.
3. `JwtAuthFilter` valida el token en cada petición y `SecurityConfig` decide por rol.
4. El servidor es *stateless*: no guarda sesiones.

**Almacenamiento del token:** `sessionStorage` se borra al cerrar la pestaña y no viaja solo con cada petición (evita CSRF), pero cualquier script de la página puede leerlo, así que un XSS lo expondría. Una cookie `HttpOnly` mitiga eso, pero exige configurar CSRF y cookies entre orígenes; se eligió `sessionStorage` por simplicidad en este alcance.

## 401 vs 403

- **401 Unauthorized**: no estás autenticado (sin token, token inválido/expirado o login incorrecto). En React, un 401 te devuelve al login.
- **403 Forbidden**: estás autenticado, pero tu rol no permite la acción (por ejemplo `USER` haciendo `POST /api/equipos`).

## Pruebas

```bash
# Backend (23 pruebas MockMvc sobre H2, no necesitan Docker)
cd backend && ./mvnw test

# Frontend (Vitest + Testing Library) y lint
cd frontend && npm test && npm run lint

# Postman: importa postman/TecnoUAM.postman_collection.json y
# postman/TecnoUAM.postman_environment.json; rellena adminPassword y userPassword
# en el environment y ejecuta la colección en orden (Collection Runner).
# Con newman (el backend debe estar en marcha):
npx newman run postman/TecnoUAM.postman_collection.json \
  -e postman/TecnoUAM.postman_environment.json \
  --env-var "adminPassword=<ADMIN_PASSWORD>" --env-var "userPassword=<USER_PASSWORD>"
```

La colección cubre los escenarios del Reto 4 (carpetas 0 a 9: auth, alta, consulta, actualización, préstamo, duplicado, devolución, eliminación, seguridad 401/403, validación 400/404) y es repetible: genera códigos patrimoniales únicos y limpia lo que crea.
Última ejecución verificada con newman: 28 peticiones, 43 aserciones, 0 fallos (`evidencias/newman-resultado.txt`).

Para el Reto 3 (React): crea o presta un equipo desde Postman y pulsa **Actualizar** (o recarga) en la interfaz; los datos vienen siempre de la API.

## Estructura

```
backend/   Spring Boot (controller, service, repository, model, dto, security, exception, mapper)
frontend/  React (src/components, src/services, src/hooks, src/utils)
postman/   Colección y environment (sin secretos)
evidencias/ Resultado de la ejecución con newman
odd/       Registro de tareas del trabajo
docker-compose.yml  PostgreSQL
```

## Organización del equipo

| Integrante | Nombre | Responsabilidad principal |
|---|---|---|
| 1 | | Entidades, relaciones JPA y base de datos |
| 2 | | CRUD, controladores y servicios |
| 3 | | Spring Security, JWT y roles |
| 4 | | React, Axios y visualización |
| 5 | | Pruebas Postman, integración y evidencias |

## Guía rápida para la defensa (dónde vive cada regla)

Rutas bajo `backend/src/main/java/com/uam/tecnouam/prestamos/`.

| Tema | Ubicación |
|---|---|
| Relación JPA Prestamo → Equipo | `model/Prestamo.java`, `model/Equipo.java` |
| Estado `PRESTADO`/`DISPONIBLE` y rechazo de duplicado (409) | `service/PrestamoService.registrar` (bloqueo `findByIdForUpdate`) |
| Devolución | `service/PrestamoService.devolver` |
| Bloqueo de eliminación con préstamo activo | `service/EquipoService.eliminar` |
| Validación de fechas | `dto/PrestamoRequest` + `dto/validation/FechasPrestamoValidator` |
| Códigos HTTP y cuerpo de error | `exception/GlobalExceptionHandler` |
| Roles por ruta | `security/SecurityConfig` |
| Generar/validar JWT | `security/JwtService`, `security/JwtAuthFilter` |
| 401 y 403 en JSON | `security/RestAuthenticationEntryPoint`, `security/RestAccessDeniedHandler` |
| Consulta Axios con token | `frontend/src/services/apiClient.js` (interceptores) y `equipoService.js` |
| Estados de carga, vacío y error | `frontend/src/hooks/useFetch.js`, `components/StateMessages.jsx`, `EquiposPage.jsx` |
| Controles solo ADMIN en la UI | `frontend/src/App.jsx` (`isAdmin`); el backend siempre lo hace cumplir |

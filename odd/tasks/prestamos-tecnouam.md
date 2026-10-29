# Feature: prestamos-tecnouam (Examen integrador, 2do corte)

Objective: Spring Boot + PostgreSQL (Docker) + JWT + React app for loaning tech equipment, per `02-Instrucciones/Servicios WEB ver2.pdf`.
Authorized scope: new dir `04-Entregables/prestamos-tecnouam` only. Layered arch, DTOs, validation, CRUD.
TDD: not configured; ordinary functional checks (mvn test, npm build, curl against real API). Runner: `./mvnw test`.
Delivery: ask-on-risk; commits on feat/prestamos-fullstack; no push without user approval.

## Tasks
- [x] T1 Infra: docker-compose Postgres, .env.example, .gitignore (inline)
- [x] T2 Backend: entities Equipo/Prestamo, repos, services, controllers, DTOs, validation, exception handler, business rules (delegated writer) - c57e82c
- [x] T3 Security: JWT login, stateless, USER/ADMIN roles, 401/403 handlers (delegated writer) - c57e82c
- [x] T4 SQL schema + seed (schema.sql / data) + backend tests - 328641a (tests, 23 green on H2), 4984387 (backend/sql); curl matrix verified against Dockerized Postgres
- [ ] T5 React frontend: Axios, useState/useEffect, login, table, loading/empty/error states (delegated writer)
- [ ] T6 Postman collection + README + evidence
- [ ] T7 Run end-to-end verification (all 9 Reto-4 scenarios) and publish repo (needs user OK)

## Route log
T1 inline; T2-T4 delegated writer (mandatory writer trigger); T5-T6 delegated writer.

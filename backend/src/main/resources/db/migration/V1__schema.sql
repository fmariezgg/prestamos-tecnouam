-- Schema for the TecnoUAM equipment loan system (portable PostgreSQL / H2).

CREATE TABLE usuarios (
    id            BIGINT GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
    username      VARCHAR(50)  NOT NULL,
    password_hash VARCHAR(100) NOT NULL,
    rol           VARCHAR(10)  NOT NULL,
    CONSTRAINT uk_usuarios_username UNIQUE (username),
    CONSTRAINT ck_usuarios_rol CHECK (rol IN ('ADMIN', 'USER'))
);

CREATE TABLE equipos (
    id                 BIGINT GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
    codigo_patrimonial VARCHAR(50)  NOT NULL,
    nombre             VARCHAR(120) NOT NULL,
    tipo               VARCHAR(50)  NOT NULL,
    estado             VARCHAR(20)  NOT NULL,
    CONSTRAINT uk_equipos_codigo_patrimonial UNIQUE (codigo_patrimonial),
    CONSTRAINT ck_equipos_estado CHECK (estado IN ('DISPONIBLE', 'PRESTADO'))
);

CREATE TABLE prestamos (
    id                        BIGINT GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
    equipo_id                 BIGINT       NOT NULL,
    nombre_solicitante        VARCHAR(120) NOT NULL,
    fecha_prestamo            DATE         NOT NULL,
    fecha_prevista_devolucion DATE         NOT NULL,
    fecha_devolucion_real     DATE,
    estado                    VARCHAR(20)  NOT NULL,
    CONSTRAINT fk_prestamos_equipo FOREIGN KEY (equipo_id) REFERENCES equipos (id),
    CONSTRAINT ck_prestamos_estado CHECK (estado IN ('ACTIVO', 'DEVUELTO')),
    CONSTRAINT ck_prestamos_fechas CHECK (fecha_prevista_devolucion >= fecha_prestamo)
);

CREATE INDEX idx_prestamos_equipo ON prestamos (equipo_id);
CREATE INDEX idx_prestamos_estado ON prestamos (estado);

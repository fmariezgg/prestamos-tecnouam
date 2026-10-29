-- Sample data. Application users (admin, user) are created at startup by
-- DataInitializer so that no password or hash is stored in this repository.

INSERT INTO equipos (codigo_patrimonial, nombre, tipo, estado) VALUES
    ('LAP-001', 'Laptop Dell Latitude 5440', 'LAPTOP', 'DISPONIBLE'),
    ('LAP-002', 'Laptop Lenovo ThinkPad E14', 'LAPTOP', 'PRESTADO'),
    ('LAP-003', 'Laptop HP ProBook 450', 'LAPTOP', 'DISPONIBLE'),
    ('PRO-001', 'Proyector Epson PowerLite X49', 'PROYECTOR', 'DISPONIBLE'),
    ('PRO-002', 'Proyector BenQ MS535', 'PROYECTOR', 'DISPONIBLE'),
    ('TAB-001', 'Tableta Samsung Galaxy Tab A9', 'TABLETA', 'DISPONIBLE'),
    ('TAB-002', 'Tableta Apple iPad 10a gen', 'TABLETA', 'DISPONIBLE');

-- One returned loan (history) and one active loan consistent with LAP-002 = PRESTADO.
INSERT INTO prestamos (equipo_id, nombre_solicitante, fecha_prestamo, fecha_prevista_devolucion, fecha_devolucion_real, estado)
SELECT id, 'Maria Lopez', DATE '2026-10-01', DATE '2026-10-05', DATE '2026-10-04', 'DEVUELTO'
FROM equipos WHERE codigo_patrimonial = 'PRO-001';

INSERT INTO prestamos (equipo_id, nombre_solicitante, fecha_prestamo, fecha_prevista_devolucion, fecha_devolucion_real, estado)
SELECT id, 'Carlos Ramirez', DATE '2026-10-20', DATE '2026-11-03', NULL, 'ACTIVO'
FROM equipos WHERE codigo_patrimonial = 'LAP-002';

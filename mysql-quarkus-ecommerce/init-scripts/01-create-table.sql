-- Este script se ejecuta automáticamente al iniciar el contenedor
USE ecommerce_system;

CREATE TABLE usuarios (
    id_user INT AUTO_INCREMENT PRIMARY KEY,
    nombre VARCHAR(100) NOT NULL,
    correo VARCHAR(100) NOT NULL UNIQUE,
    contrasena VARCHAR(255) NOT NULL,
    direccion VARCHAR(255)
);

-- Insertar algunos datos de prueba para que no esté vacía
INSERT INTO usuarios (nombre, correo, contrasena, direccion) VALUES 
('Juan Diego', 'juan.diego@example.com', 'itq2026', 'Santiago de Querétaro'),
('Admin Sistema', 'admin@ecommerce.com', 'admin123', 'Campus Querétaro');
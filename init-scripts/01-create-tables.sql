-- Base de datos del sistema e-commerce
USE ecommerce_db;

-- Tabla de usuarios
CREATE TABLE IF NOT EXISTS usuarios (
    id_user     BIGINT AUTO_INCREMENT PRIMARY KEY,
    nombre      VARCHAR(100) NOT NULL,
    correo      VARCHAR(150) NOT NULL UNIQUE,
    contrasena  VARCHAR(255) NOT NULL,
    direccion   VARCHAR(255),
    created_at  TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at  TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP
);

-- Tabla de productos
CREATE TABLE IF NOT EXISTS productos (
    id_producto  BIGINT AUTO_INCREMENT PRIMARY KEY,
    nombre       VARCHAR(150) NOT NULL,
    descripcion  TEXT,
    precio       DECIMAL(10,2) NOT NULL,
    stock        INT NOT NULL DEFAULT 0,
    created_at   TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at   TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP
);

-- Tabla de carrito
CREATE TABLE IF NOT EXISTS carrito (
    id_carrito  BIGINT AUTO_INCREMENT PRIMARY KEY,
    id_user     BIGINT NOT NULL,
    id_producto BIGINT NOT NULL,
    cantidad    INT NOT NULL DEFAULT 1,
    total       DECIMAL(10,2),
    fecha       TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (id_user)     REFERENCES usuarios(id_user),
    FOREIGN KEY (id_producto) REFERENCES productos(id_producto)
);

-- Tabla de pedidos
CREATE TABLE IF NOT EXISTS pedidos (
    id_pedido     BIGINT AUTO_INCREMENT PRIMARY KEY,
    id_user       BIGINT NOT NULL,
    estado_pedido ENUM('PENDIENTE','CONFIRMADO','ENVIADO','ENTREGADO','CANCELADO') DEFAULT 'PENDIENTE',
    fecha         TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at    TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    FOREIGN KEY (id_user) REFERENCES usuarios(id_user)
);

-- Tabla de detalles de pedido
CREATE TABLE IF NOT EXISTS detalles_pedido (
    id_detalle   BIGINT AUTO_INCREMENT PRIMARY KEY,
    id_pedido    BIGINT NOT NULL,
    id_producto  BIGINT NOT NULL,
    cantidad     INT NOT NULL,
    precio_total DECIMAL(10,2) NOT NULL,
    FOREIGN KEY (id_pedido)   REFERENCES pedidos(id_pedido),
    FOREIGN KEY (id_producto) REFERENCES productos(id_producto)
);

-- Tabla de pagos
CREATE TABLE IF NOT EXISTS pagos (
    id_pago     BIGINT AUTO_INCREMENT PRIMARY KEY,
    id_pedido   BIGINT NOT NULL UNIQUE,
    metodo_pago VARCHAR(50) NOT NULL,
    estado      ENUM('PENDIENTE','PAGADO','FALLIDO','REEMBOLSADO') DEFAULT 'PENDIENTE',
    created_at  TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at  TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    FOREIGN KEY (id_pedido) REFERENCES pedidos(id_pedido)
);

-- Índices para mejorar rendimiento
CREATE INDEX idx_carrito_user    ON carrito(id_user);
CREATE INDEX idx_pedidos_user    ON pedidos(id_user);
CREATE INDEX idx_detalles_pedido ON detalles_pedido(id_pedido);

-- Tabla de secuencias requerida por Hibernate para generación de IDs
CREATE TABLE IF NOT EXISTS hibernate_sequence (
    next_val BIGINT
) ENGINE=InnoDB;

INSERT INTO hibernate_sequence (next_val) VALUES (10);

-- Datos de ejemplo
INSERT INTO usuarios (nombre, correo, contrasena, direccion) VALUES
('Juan Diego', 'juan@example.com', 'hashed_password_1', 'Calle Principal 123'),
('Maria Lopez', 'maria@example.com', 'hashed_password_2', 'Av. Reforma 456');

INSERT INTO productos (nombre, descripcion, precio, stock) VALUES
('Mochila', 'Mochila resistente para uso diario', 299.99, 50),
('Laptop', 'Laptop de alto rendimiento', 15999.00, 10);

INSERT INTO pedidos (id_user, estado_pedido) VALUES
(1, 'CONFIRMADO'),
(2, 'PENDIENTE');

INSERT INTO detalles_pedido (id_pedido, id_producto, cantidad, precio_total) VALUES
(1, 1, 2, 599.98),
(2, 2, 1, 15999.00);

INSERT INTO pagos (id_pedido, metodo_pago, estado) VALUES
(1, 'TARJETA', 'PAGADO'),
(2, 'EFECTIVO', 'PENDIENTE');

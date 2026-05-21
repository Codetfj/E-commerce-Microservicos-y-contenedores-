# E-Commerce Microservices Platform

Plataforma de comercio electrónico construida sobre una **arquitectura de microservicios**, diseñada para gestionar de forma modular e independiente las operaciones de un sistema de ventas en línea: usuarios, productos, carrito de compras, pedidos y pagos, todo expuesto mediante **APIs REST**.

---

## Tabla de Contenidos

- [Descripción General](#descripción-general)
- [Arquitectura](#arquitectura)
- [Tecnologías](#tecnologías)
- [Estructura del Proyecto](#estructura-del-proyecto)
- [Base de Datos](#base-de-datos)
- [API - Endpoints](#api---endpoints)
- [Requisitos Previos](#requisitos-previos)
- [Instalación y Ejecución](#instalación-y-ejecución)
- [Roles del Sistema](#roles-del-sistema)
- [Estados del Sistema](#estados-del-sistema)
- [Equipo](#equipo)
- [Licencia](#licencia)

---

## Descripción General

Este proyecto implementa los principios de una arquitectura de microservicios aplicados a un e-commerce. Cada módulo de negocio es un servicio independiente con responsabilidades bien delimitadas, lo que permite:

- Desarrollar, escalar y mantener cada servicio de forma autónoma
- Facilitar el trabajo colaborativo por equipos
- Desplegar el sistema en contenedores Docker de manera uniforme
- Documentar y probar cada API por separado

El sistema cubre el flujo completo de un e-commerce: desde el registro de usuarios hasta el procesamiento del pago de un pedido.

---

## Arquitectura

El sistema se compone de los siguientes microservicios:

```
┌─────────────────────────────────────────────────────────────────┐
│                        CLIENTE / FRONTEND                        │
└───────────────────────────────┬─────────────────────────────────┘
                                │ HTTP REST
        ┌───────────────────────┼───────────────────────┐
        │                       │                       │
┌───────▼──────┐   ┌────────────▼───────┐   ┌──────────▼───────┐
│  Auth Service │   │  Product Service   │   │   Cart Service   │
│  /users       │   │  /products         │   │   /cart          │
└───────────────┘   └────────────────────┘   └──────────────────┘
        │                       │                       │
        └───────────────────────┼───────────────────────┘
                                │
               ┌────────────────┼────────────────┐
               │                │                │
    ┌──────────▼──────┐  ┌──────▼───────┐  ┌────▼──────────────┐
    │  Order Service  │  │ Order Items  │  │  Payment Service  │
    │  /orders        │  │ /orders/{id} │  │  /orders/{id}/    │
    │                 │  │ /items       │  │  payment          │
    └─────────────────┘  └──────────────┘  └───────────────────┘
                                │
                    ┌───────────▼───────────┐
                    │   MySQL 8.0 (Docker)  │
                    │   ecommerce_db        │
                    └───────────────────────┘
```

### Flujo principal del sistema

1. El usuario se registra o inicia sesión mediante **Auth Service**
2. Consulta el catálogo de productos en **Product Service**
3. Agrega productos al carrito mediante **Cart Service**
4. Genera un pedido a través de **Order Service**
5. Los detalles de cada artículo del pedido son gestionados por **Order Items Service**
6. El pago es procesado por **Payment Service**

---

## Tecnologías

| Capa | Tecnología |
|---|---|
| Framework backend | Quarkus 3.33.1 |
| Lenguaje | Java 25 |
| API Design | OpenAPI 3 (OAS) |
| ORM | Hibernate ORM Panache |
| Base de datos | MySQL 8.0 |
| Contenerización | Docker / Docker Compose |
| Build tool | Maven |
| Validación | Jakarta Validation |
| Observabilidad | SmallRye Health + Micrometer Prometheus |
| Serialización | Jackson |
| Control de versiones | Git / GitHub |

---

## Estructura del Proyecto

```
E-commerce-Microservicos/
│
├── Quarkus/
│   └── api-rest/                          <- API REST principal (Quarkus)
│       ├── pom.xml                        <- Dependencias Maven
│       └── src/
│           ├── main/
│           │   ├── java/com/ecommerce/api/
│           │   │   ├── model/             <- Modelos del dominio
│           │   │   │   ├── Cart.java
│           │   │   │   ├── CartProduct.java
│           │   │   │   ├── Order.java
│           │   │   │   ├── OrderItem.java
│           │   │   │   ├── Products.java
│           │   │   │   ├── Users.java
│           │   │   │   └── ...
│           │   │   ├── resource/          <- Endpoints REST
│           │   │   │   ├── CartResource.java
│           │   │   │   ├── OrderResource.java
│           │   │   │   ├── PaymentResource.java
│           │   │   │   ├── ProductResource.java
│           │   │   │   └── UserResource.java
│           │   │   └── service/           <- Lógica de negocio
│           │   │       ├── CartService.java
│           │   │       ├── OrderService.java
│           │   │       ├── PaymentService.java
│           │   │       ├── ProductService.java
│           │   │       └── UserService.java
│           │   └── resources/
│           │       ├── application.properties   <- Configuración
│           │       └── api/oas-e-commerce-v1.yaml
│           └── test/
│
├── services/
│   └── auth-service/                      <- Servicio de autenticación
│
├── init-scripts/
│   └── 01-create-tables.sql               <- Script de inicialización de BD
│
├── docs/
│   ├── arquitectura.md                    <- Documentación de arquitectura
│   ├── api.md                             <- Documentación de endpoints
│   └── base-datos.md                      <- Documentación de base de datos
│
├── diagrams/                              <- Diagramas del sistema
├── mysql-data/                            <- Volumen persistente MySQL (local)
├── docker-compose.yml                     <- Orquestación con bind mount
├── docker-compose-volumes.yml             <- Orquestación con named volumes
├── .gitignore
├── LICENSE
└── README.md
```

---

## Base de Datos

La base de datos se llama `ecommerce_db` y se inicializa automáticamente al levantar el contenedor MySQL. Las tablas se crean desde `init-scripts/01-create-tables.sql`.

### Diagrama de tablas

```
usuarios                    productos
─────────────               ─────────────────
id_user (PK)                id_producto (PK)
nombre                      nombre
correo (UNIQUE)             descripcion
contrasena                  precio
direccion                   stock
created_at                  created_at
updated_at                  updated_at

carrito                     pedidos
──────────────              ───────────────────
id_carrito (PK)             id_pedido (PK)
id_user (FK)                id_user (FK)
id_producto (FK)            estado_pedido (ENUM)
cantidad                    fecha
total                       updated_at
fecha
                            detalles_pedido
pagos                       ──────────────────
──────────────              id_detalle (PK)
id_pago (PK)                id_pedido (FK)
id_pedido (FK, UNIQUE)      id_producto (FK)
metodo_pago                 cantidad
estado (ENUM)               precio_total
created_at
updated_at
```

### Estados de pedido
`PENDIENTE` · `CONFIRMADO` · `ENVIADO` · `ENTREGADO` · `CANCELADO`

### Estados de pago
`PENDIENTE` · `PAGADO` · `FALLIDO` · `REEMBOLSADO`

---

## API - Endpoints

La especificación completa está definida en `src/main/resources/api/oas-e-commerce-v1.yaml`.

### Usuarios `/users`
| Método | Endpoint | Descripción | Rol |
|---|---|---|---|
| `POST` | `/users` | Registrar nuevo usuario | Público |
| `POST` | `/users/login` | Iniciar sesión | Público |
| `GET` | `/users` | Listar todos los usuarios | Admin |
| `GET` | `/users/{id}` | Obtener usuario por ID | Admin / Propietario |
| `PATCH` | `/users/{id}` | Actualizar usuario | Admin / Propietario |
| `DELETE` | `/users/{id}` | Eliminar usuario | Admin / Propietario |

### Productos `/products`
| Método | Endpoint | Descripción | Rol |
|---|---|---|---|
| `GET` | `/products` | Listar productos | Público |
| `GET` | `/products/{id}` | Obtener producto | Público |
| `POST` | `/products` | Crear producto | Admin |
| `PATCH` | `/products/{id}` | Actualizar producto | Admin |
| `DELETE` | `/products/{id}` | Eliminar producto | Admin |

### Carrito `/cart`
| Método | Endpoint | Descripción |
|---|---|---|
| `GET` | `/cart` | Ver carrito del usuario |
| `POST` | `/cart/items` | Agregar producto al carrito |
| `PATCH` | `/cart/items/{itemId}` | Modificar cantidad |
| `DELETE` | `/cart/items/{itemId}` | Eliminar producto del carrito |
| `DELETE` | `/cart` | Vaciar carrito |

### Pedidos `/orders`
| Método | Endpoint | Descripción |
|---|---|---|
| `GET` | `/orders` | Listar pedidos |
| `GET` | `/orders/{id}` | Obtener pedido |
| `POST` | `/orders` | Crear pedido |
| `PATCH` | `/orders/{id}` | Actualizar estado |
| `DELETE` | `/orders/{id}` | Cancelar pedido |
| `GET` | `/orders/{id}/items` | Ver items del pedido |
| `GET` | `/orders/{id}/items/{itemId}` | Ver item específico |
| `POST` | `/orders/{id}/items` | Agregar item al pedido |
| `PATCH` | `/orders/{id}/items/{itemId}` | Modificar item |
| `DELETE` | `/orders/{id}/items/{itemId}` | Eliminar item |

### Pagos `/orders/{id}/payment`
| Método | Endpoint | Descripción |
|---|---|---|
| `GET` | `/orders/{id}/payment` | Consultar pago |
| `POST` | `/orders/{id}/payment` | Procesar pago |
| `PATCH` | `/orders/{id}/payment` | Actualizar estado del pago |
| `DELETE` | `/orders/{id}/payment` | Revertir pago |

---

## Requisitos Previos

- [Docker](https://www.docker.com/) v20+ y Docker Compose v2+
- [Java 25](https://openjdk.org/)
- [Maven 3.9+](https://maven.apache.org/) (o usar el wrapper `./mvnw` incluido)

Verificar instalación:
```bash
docker --version
docker compose version
java --version
./mvnw --version
```

---

## Instalación y Ejecución

### 1. Clonar el repositorio

```bash
git clone https://github.com/Celestial182521/E-commerce-Microservicos-y-contenedores-.git
cd E-commerce-Microservicos-y-contenedores-
```

### 2. Levantar la base de datos MySQL

```bash
docker compose up -d
```

Esto crea automáticamente:
- El contenedor `mysql-ecommerce` en el puerto `3306`
- La base de datos `ecommerce_db`
- Todas las tablas y datos de ejemplo definidos en `init-scripts/01-create-tables.sql`

Verificar que el contenedor está corriendo:
```bash
docker ps
docker logs mysql-ecommerce
```

### 3. Ejecutar la API con Quarkus

```bash
cd Quarkus/api-rest
./mvnw quarkus:dev
```

La API estará disponible en:

| URL | Descripción |
|---|---|
| `http://localhost:8080` | API principal |
| `http://localhost:8080/q/swagger-ui` | Documentación interactiva Swagger |
| `http://localhost:8080/q/health` | Health check |
| `http://localhost:8080/metrics` | Métricas Prometheus |

### 4. Detener el proyecto

```bash
# Detener Quarkus
Ctrl + C

# Detener y eliminar contenedores
docker compose down
```

### Opción alternativa — Named Volumes (recomendado para producción)

```bash
docker compose -f docker-compose-volumes.yml up -d
```

---

## Roles del Sistema

### Admin
- Acceso completo a usuarios, productos, pedidos y pagos
- Puede crear, editar y eliminar cualquier recurso
- Consulta global de todos los pedidos del sistema

### User
- Registro e inicio de sesión
- Consulta y gestión de su propio perfil
- Consulta de productos disponibles
- Gestión de su carrito de compras
- Creación y seguimiento de sus pedidos
- Procesamiento de pago de sus pedidos

---

## Documentación

La documentación detallada del sistema se encuentra en la carpeta `/docs`:

- [`docs/arquitectura.md`](docs/arquitectura.md) — Diseño y descripción de los microservicios
- [`docs/api.md`](docs/api.md) — Referencia de endpoints
- [`docs/base-datos.md`](docs/base-datos.md) — Modelo de datos y relaciones

---

## Equipo

| Nombre |
|---|
| Ángel Mauricio Flores Olivarez |
| Avitud Cruz Habid Hazel |
| Juan Diego Trejo Fuentes |

---

## Licencia

Este proyecto está bajo la licencia [MIT](LICENSE).

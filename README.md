# SBJ Hexagonal: CRUD con arquitectura hexagonal

API REST de gestión de usuarios con **arquitectura hexagonal** (puertos y adaptadores), Spring Boot 4.0.1, Java 21 y MySQL 8, documentada con Swagger/OpenAPI.

## Características

- Arquitectura hexagonal: el dominio no depende de Spring, de JPA ni de HTTP.
- CRUD completo de usuarios con validación (Jakarta Bean Validation).
- Respuestas de error uniformes: 400, 404, 409 y 500.
- Documentación interactiva con Swagger UI.
- MySQL 8 con JPA/Hibernate; Docker Compose con la base de datos y datos de prueba.
- Pruebas unitarias del dominio, servicio, controlador, mapper y adaptador (JUnit 5 y Mockito).

## Requisitos

- JDK 21 y Maven 3.9 o superior.
- Docker y Docker Compose (opcional, recomendado).
- MySQL 8 si ejecutas sin Docker.

## Compilar y ejecutar

```bash
# Con Docker Compose (base de datos + aplicación)
docker compose up --build

# En local (necesita MySQL)
mvn spring-boot:run
```

## Arquitectura

```text
        HTTP                     ┌───────────────────────────────┐
  ──────────────►  UserController │          DOMINIO              │
 (adaptador de entrada)  ──────► │  UserServicePort (puerto in)  │
                                 │  UserService (casos de uso)   │
                                 │  UserRepositoryPort (puerto   │  ──► UserRepositoryAdapter ──► MySQL
                                 │  out)         User (entidad)  │      (adaptador de salida)
                                 └───────────────────────────────┘
```

- **Puertos de entrada** (`domain/port/in`): lo que ofrece la aplicación.
- **Puertos de salida** (`domain/port/out`): lo que necesita de la persistencia.
- **Adaptadores** (`infrastructure/adapter`): el controlador REST (entrada) y el repositorio JPA (salida).

## API

Con el contexto `/api`, Swagger UI está en <http://localhost:8080/api/swagger-ui.html> y la definición en <http://localhost:8080/api/v3/api-docs>.

| Método | Ruta | Descripción | Respuestas |
|--------|------|-------------|------------|
| `POST` | `/api/v1/users` | Crea un usuario | 201, 400, 409 |
| `GET` | `/api/v1/users` | Lista los usuarios | 200 |
| `GET` | `/api/v1/users/{id}` | Obtiene un usuario | 200, 404 |
| `PUT` | `/api/v1/users/{id}` | Actualiza un usuario | 200, 400, 404, 409 |
| `DELETE` | `/api/v1/users/{id}` | Elimina un usuario | 204, 404 |

Ejemplo:

```bash
curl -X POST http://localhost:8080/api/v1/users \
  -H "Content-Type: application/json" \
  -d '{"name":"Juan Pérez","email":"juan@example.com","phone":"+525512345678"}'
```

Reglas de validación: `name` 2-50 caracteres (obligatorio), `email` válido de hasta 100 caracteres y único (obligatorio), `phone` de hasta 15 caracteres (opcional).

## Base de datos

Variables de entorno (valores por defecto entre paréntesis): `DB_USERNAME` (`root`) y `DB_PASSWORD` (`password`).
La tabla `users` la crea Hibernate (`ddl-auto=update`) o el script `docker/mysql/init/01-init.sql` al usar Docker Compose, que además carga 10 usuarios de prueba.

## Pruebas

```bash
mvn test
mvn test -Dtest=UserServiceTest
```

> Con Lombok, compila con JDK 21 (versiones de Lombok anteriores a la 1.18.40 no funcionan con JDK 25).

## Estructura

```text
src/main/java/com/juniverse/hexagonal/
├── application/service/        # casos de uso
├── domain/
│   ├── entity/                 # User
│   ├── exception/              # excepciones del dominio
│   └── port/{in,out}/          # puertos
└── infrastructure/
    ├── adapter/in/             # controlador, DTOs, mapper y manejo de errores
    ├── adapter/out/            # repositorio JPA
    └── config/                 # JPA y OpenAPI
```

## Licencia

Código de ejemplo de uso libre con fines educativos.

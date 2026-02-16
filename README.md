# Coupon Domain

API de gerenciamento de cupons de desconto, implementada com **Arquitetura Hexagonal** e **Domain-Driven Design (DDD)**.

## Tecnologias

- Java 21
- Spring Boot 3.4.3
- PostgreSQL 17 (homologação) / H2 (desenvolvimento)
- JaCoCo (cobertura de testes)
- Springdoc OpenAPI (Swagger)
- Docker Compose

## Como executar

### Desenvolvimento (H2 em memória)

```bash
./mvnw spring-boot:run
```

A aplicação sobe no perfil `dev` por padrão com banco H2 em memória.

### Homologação (PostgreSQL via Docker)

```bash
docker compose up -d
SPRING_PROFILES_ACTIVE=hom ./mvnw spring-boot:run
```

## Como executar os testes

```bash
./mvnw test
```

O relatório de cobertura JaCoCo é gerado em `target/site/jacoco/index.html`.

## Swagger

Com a aplicação rodando, acesse:

- **Swagger UI**: http://localhost:8080/swagger-ui.html
- **OpenAPI JSON**: http://localhost:8080/v3/api-docs

## Endpoints

| Método | Endpoint           | Descrição                | Status |
|--------|--------------------|--------------------------|--------|
| POST   | /cupons            | Criar cupom              | 201    |
| GET    | /cupons/{codigo}   | Buscar cupom por código  | 200    |
| GET    | /cupons            | Listar todos os cupons   | 200    |
| DELETE | /cupons/{codigo}   | Excluir cupom (soft)     | 204    |

## Estrutura de Pacotes

```
domain
  ├── model          (Aggregate Root e Value Objects)
  ├── exception      (Exceções de domínio)
  └── port           (Ports de saída - interfaces)

application
  ├── port/in        (Ports de entrada - use cases)
  └── usecase        (Implementações dos use cases)

adapter
  ├── in/web         (Controller, DTOs, Exception Handler)
  └── out/persistence (JPA Entity, Repository, Mapper, Adapter)

config               (Configurações Spring)
```

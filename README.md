# ecommerceWebsite

Microservice-based e-commerce backend. Spring Boot + PostgreSQL + Amazon SQS.

---

## Architecture

```mermaid
graph TD
    Client["Client (Browser / Mobile)"]

    subgraph API_Gateway["API Gateway (Phase 3)"]
        GW[Gateway]
    end

    subgraph Services
        AUTH[auth_service]
        NORMAL[normal_service]
        ADMIN[admin_service]
        ANALYTICS[analytic_service]
        EMAIL[email_service\nfuture]
    end

    subgraph Data
        PG[(PostgreSQL\nRDS)]
    end

    subgraph Messaging
        SQS[Amazon SQS]
    end

    Client --> GW
    GW --> AUTH
    GW --> NORMAL
    GW --> ADMIN

    AUTH --> PG
    NORMAL --> PG
    ADMIN --> PG

    AUTH --> SQS
    NORMAL --> SQS
    ADMIN --> SQS

    SQS --> ANALYTICS
    SQS --> EMAIL

    ANALYTICS --> PG

    classDef client    fill:#4A90D9,stroke:#2C5F8A,color:#fff
    classDef gateway   fill:#7B68EE,stroke:#4B3BC0,color:#fff
    classDef service   fill:#3DAA6E,stroke:#1F7A47,color:#fff
    classDef future    fill:#888,stroke:#555,color:#fff,stroke-dasharray:5 5
    classDef db        fill:#E07B39,stroke:#A04D18,color:#fff
    classDef queue     fill:#D4A017,stroke:#9A7010,color:#fff

    class Client client
    class GW gateway
    class AUTH,NORMAL,ADMIN,ANALYTICS service
    class EMAIL future
    class PG db
    class SQS queue
```

---

## Services

### auth_service
- Login, logout, session management
- Two-factor / OTP verification
- JWT token generation and refresh
- Touches only: `users` table

### normal_service
- Browse and search products
- Cart operations (add / remove / update)
- Order placement and confirmation
- General user-facing flows

### admin_service
- Seller registration and product listing
- Super-admin approval workflow for new products
- Stock tracking and inventory edits

### analytic_service
- Consumes events from all services via SQS
- Logs API requests to DB
- Planned: GraphDB-based user activity recommendations

### email_service *(future)*
- SQS consumer
- OTP delivery and order notification emails

---

## Stack

| Layer | Tech |
|---|---|
| Framework | Spring Boot |
| ORM | Hibernate / JPA |
| Database | PostgreSQL (Amazon RDS) |
| Messaging | Amazon SQS |
| Auth | Spring Security + JWT |

---

## Roadmap

- [x] **Phase 1** — DB schema, REST endpoints, entity mappings
- [ ] **Phase 2** — Spring Security, SQS integration, email_service
- [ ] **Phase 3** — API Gateway
- [ ] **Phase 4** — Cloud deployment (AWS)

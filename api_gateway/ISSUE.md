# API Gateway — Routing Not Working (Root Cause & Fix)

## Symptoms

- `POST localhost:8080/auth/signup/user` returned `403 Forbidden`
- After partial fix, returned `404 Not Found`
- Direct call to `POST localhost:8081/auth/signup/user` worked fine

---

## Root Causes (in order of discovery)

### 1. Wrong route property prefix

**File:** `application.properties`

The Spring Cloud Gateway MVC (WebMVC mode) uses the property prefix:
```
spring.cloud.gateway.server.webmvc.routes
```

The config was using the wrong prefix:
```properties
# WRONG — silently ignored, no routes ever loaded
spring.cloud.gateway.mvc.routes[0].id=auth_service
spring.cloud.gateway.mvc.routes[0].uri=lb://AUTH_SERVICE
spring.cloud.gateway.mvc.routes[0].predicates[0]=Path=/auth/**
```

Because the prefix was wrong, Spring never registered any routes. Every request fell through to Spring MVC's `DispatcherServlet`, which returned `404`.

**Fix:**
```properties
# CORRECT
spring.cloud.gateway.server.webmvc.routes[0].id=auth_service
spring.cloud.gateway.server.webmvc.routes[0].uri=lb://AUTH_SERVICE
spring.cloud.gateway.server.webmvc.routes[0].predicates[0]=Path=/auth/**
```

---

### 2. Security path mismatch

**File:** `SecurityConfig.java`

`permitAll()` was set for `/api/auth/**` but the route predicate and actual request path used `/auth/**` (no `/api/` prefix). Spring Security blocked the request before the gateway could route it.

```java
// WRONG
.requestMatchers("/api/auth/**").permitAll()

// CORRECT
.requestMatchers("/auth/**", "/error").permitAll()
```

> `/error` also needs `permitAll()` — Spring Boot's error handler internally dispatches `GET /error` when something fails. Without it, any gateway error produces a masked `403` instead of the real error message.

---

### 3. Default session creation (cosmetic / noise)

Spring Security was issuing `JSESSIONID` cookies (form-login default). For a stateless JWT gateway this is wrong and adds noise to logs.

**Fix added to `SecurityConfig`:**
```java
.httpBasic(AbstractHttpConfigurer::disable)
.formLogin(AbstractHttpConfigurer::disable)
.sessionManagement(s -> s.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
```

---

## How It Was Diagnosed

| Step | Signal | What it revealed |
|------|--------|-----------------|
| `curl -v` | `403` response | Spring Security blocking |
| `logging.level.org.springframework.security=DEBUG` | `Secured POST /auth/signup/user` then `Securing GET /error` → `Http403ForbiddenEntryPoint` | Security passed the original request; `/error` was blocked, masking the real error |
| Added `/error` to `permitAll()` | `404 Not Found` with `"path": "/auth/signup/user"` | Security no longer the issue; gateway had no matching route |
| Inspected gateway jar metadata | `spring.cloud.gateway.server.webmvc.routes` is the real property key | Wrong prefix in `application.properties` — routes never loaded |

---

## Final Working Config

```properties
spring.cloud.gateway.server.webmvc.routes[0].id=auth_service
spring.cloud.gateway.server.webmvc.routes[0].uri=lb://AUTH_SERVICE
spring.cloud.gateway.server.webmvc.routes[0].predicates[0]=Path=/auth/**
```

```java
.requestMatchers("/auth/**", "/error").permitAll()
```

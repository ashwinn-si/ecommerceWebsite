# API Gateway — Changes Needed

## 1. Security Fixes

### 1.1 `SecurityConfig.java:27` — Filter registered as Class, not bean (breaks at runtime)

```java
// WRONG
.addFilterBefore(JwtFilter.class, UsernamePasswordAuthenticationFilter.class);

// RIGHT — inject JwtService via constructor and pass filter instance
.addFilterBefore(new JwtFilter(jwtService), UsernamePasswordAuthenticationFilter.class);
```

Full fix:

```java
@Configuration
@EnableWebSecurity
public class SecurityConfig {

    private final JwtService jwtService;

    public SecurityConfig(JwtService jwtService) {
        this.jwtService = jwtService;
    }

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
            .csrf(csrf -> csrf.disable())
            .authorizeHttpRequests(auth -> auth
                .requestMatchers("/api/auth/**").permitAll()
                .requestMatchers("/api/user/**").hasAuthority("ROLE_USER")
                .requestMatchers("/api/seller/**").hasAuthority("ROLE_SELLER")
                .requestMatchers("/api/admin/**").hasAuthority("ROLE_ADMIN")
                .anyRequest().authenticated()
            )
            .addFilterBefore(new JwtFilter(jwtService), UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }
}
```

---

### 1.2 `JwtFilter.java:46-63` — Role determined by URL, not token claim + NPE risk

**Problems:**
- A seller hitting `/api/user/**` gets parsed as `UserJwtData` — wrong role assigned
- Both `parseUserToken` and `parseSellerToken` return `null` on exception, but code dereferences without null check → NPE

**Fix — add `role` claim to JWT at auth service issue time, then read it in filter:**

In `auth_service` when issuing tokens, add:
```java
.claim("role", "USER")   // or "SELLER" or "ADMIN"
```

Then rewrite `JwtFilter.doFilterInternal`:

```java
@Override
protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
        throws ServletException, IOException {

    String header = request.getHeader("Authorization");
    if (header == null || !header.startsWith("Bearer ")) {
        filterChain.doFilter(request, response);
        return;
    }

    String token = header.substring(7);

    if (!jwtService.isValidToken(token)) {
        response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
        response.getWriter().write("Unauthorized: Invalid or expired token");
        return;
    }

    String role = jwtService.extractRole(token); // read "role" claim
    Long id = jwtService.extractId(token);

    if (role == null || id == null) {
        response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
        response.getWriter().write("Unauthorized: Malformed token");
        return;
    }

    UsernamePasswordAuthenticationToken auth = new UsernamePasswordAuthenticationToken(
        id, null, List.of(new SimpleGrantedAuthority("ROLE_" + role))
    );
    SecurityContextHolder.getContext().setAuthentication(auth);

    filterChain.doFilter(request, response);
}
```

Add these methods to `JwtService`:

```java
public String extractRole(String jwtToken) {
    try {
        Claims claims = Jwts.parser().verifyWith(SECRET_KEY).build()
            .parseSignedClaims(jwtToken).getPayload();
        return claims.get("role", String.class);
    } catch (Exception e) {
        return null;
    }
}

public Long extractId(String jwtToken) {
    try {
        Claims claims = Jwts.parser().verifyWith(SECRET_KEY).build()
            .parseSignedClaims(jwtToken).getPayload();
        return claims.get("id", Long.class);
    } catch (Exception e) {
        return null;
    }
}
```

---

### 1.3 `JwtService.java:15` — Hardcoded JWT secret

```java
// WRONG — secret committed to git
private final String SECRET_STRING = "your_super_secret_key_that_is_long_enough_for_sha_256";
```

Move to `application.properties`:

```properties
jwt.secret=your_super_secret_key_that_is_long_enough_for_sha_256
```

Inject in `JwtService`:

```java
@Service
public class JwtService {

    private final SecretKey SECRET_KEY;

    public JwtService(@Value("${jwt.secret}") String secret) {
        this.SECRET_KEY = Keys.hmacShaKeyFor(secret.getBytes());
    }

    // ... rest unchanged
}
```

> Same secret must be used in `auth_service`. Externalize both via env var or config server.

---

## 2. Eureka Routing Setup

### 2.1 `pom.xml` — Add Eureka client dependency

```xml
<dependency>
    <groupId>org.springframework.cloud</groupId>
    <artifactId>spring-cloud-starter-netflix-eureka-client</artifactId>
</dependency>
```

---

### 2.2 `ApiGatewayApplication.java` — Enable discovery client

```java
@SpringBootApplication
@EnableDiscoveryClient
public class ApiGatewayApplication {
    public static void main(String[] args) {
        SpringApplication.run(ApiGatewayApplication.class, args);
    }
}
```

---

### 2.3 `application.properties` — Configure Eureka + routes

```properties
spring.application.name=api-gateway
server.port=8080

# JWT secret (externalized from code)
jwt.secret=your_super_secret_key_that_is_long_enough_for_sha_256

# Eureka
eureka.client.service-url.defaultZone=http://localhost:8761/eureka/

# Routes
# lb:// tells gateway to resolve service name from Eureka and load-balance
spring.cloud.gateway.mvc.routes[0].id=auth-service
spring.cloud.gateway.mvc.routes[0].uri=lb://AUTH-SERVICE
spring.cloud.gateway.mvc.routes[0].predicates[0]=Path=/auth/**

spring.cloud.gateway.mvc.routes[1].id=user-service
spring.cloud.gateway.mvc.routes[1].uri=lb://USER-SERVICE
spring.cloud.gateway.mvc.routes[1].predicates[0]=Path=/user/**

spring.cloud.gateway.mvc.routes[2].id=seller-service
spring.cloud.gateway.mvc.routes[2].uri=lb://SELLER-SERVICE
spring.cloud.gateway.mvc.routes[2].predicates[0]=Path=/seller/**

spring.cloud.gateway.mvc.routes[3].id=admin-service
spring.cloud.gateway.mvc.routes[3].uri=lb://ADMIN-SERVICE
spring.cloud.gateway.mvc.routes[3].predicates[0]=Path=/admin/**
```

> Service names in `lb://` must match `spring.application.name` in each downstream service (case-insensitive).

---

## Summary

| # | File | Issue | Severity |
|---|------|--------|----------|
| 1 | `SecurityConfig.java:27` | Filter passed as `Class` not bean instance | Breaks at runtime |
| 2 | `JwtFilter.java:46-63` | Role derived from URL, not token claim | Auth bypass risk |
| 3 | `JwtFilter.java:47,53` | No null check after parse — NPE on bad token | Runtime crash |
| 4 | `JwtService.java:15` | Hardcoded JWT secret in source | Security vulnerability |
| 5 | `pom.xml` | Missing Eureka client dependency | No service discovery |
| 6 | `ApiGatewayApplication.java` | Missing `@EnableDiscoveryClient` | No service discovery |
| 7 | `application.properties` | No routes configured | Gateway forwards nothing |

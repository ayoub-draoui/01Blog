# Spring Security with JWT - Complete Step-by-Step Guide

## Table of Contents
1. [Overview - What Are We Building?](#overview)
2. [Prerequisites](#prerequisites)
3. [Step 1: Understanding the Big Picture](#step-1-understanding-the-big-picture)
4. [Step 2: Setting Up Dependencies](#step-2-setting-up-dependencies)
5. [Step 3: Creating Your User Model](#step-3-creating-your-user-model)
6. [Step 4: CustomUserDetails - Wrapping Your User](#step-4-customuserdetails)
7. [Step 5: CustomUserDetailsService - Loading Users](#step-5-customuserdetailsservice)
8. [Step 6: JWT Configuration Properties](#step-6-jwt-configuration-properties)
9. [Step 7: JwtUtil - The Token Factory](#step-7-jwtutil)
10. [Step 8: JwtFilter - Checking Every Request](#step-8-jwtfilter)
11. [Step 9: SecurityConfig - Tying It All Together](#step-9-securityconfig)
12. [Step 10: Creating Your Auth Controller](#step-10-auth-controller)
13. [Testing Your Security](#testing-your-security)
14. [Common Issues and Solutions](#common-issues)

---

## Overview - What Are We Building?

Think of your application like a nightclub:

- **Spring Security** = The entire security system of the club
- **JWT (JSON Web Token)** = A wristband you get after showing your ID at the door
- **Authentication** = Proving who you are (showing ID at the door)
- **Authorization** = What you're allowed to do (VIP wristband vs regular wristband)

### The Flow:
1. User sends username/password (shows ID)
2. Server verifies credentials and gives back a JWT token (wristband)
3. User includes JWT in every future request (shows wristband)
4. Server checks the JWT to know who you are and what you can do

---

## Prerequisites

You need:
- Spring Boot project (2.7+ or 3.x)
- Basic understanding of Java and Spring
- A User entity/model in your database
- A UserRepository to access users

---

## Step 1: Understanding the Big Picture

### The 6 Key Components:

```
┌─────────────────────────────────────────────────────────────┐
│                        YOUR APPLICATION                      │
├─────────────────────────────────────────────────────────────┤
│                                                               │
│  1. SecurityConfig                                           │
│     └─> The master configuration (sets all rules)           │
│                                                               │
│  2. CustomUserDetails                                        │
│     └─> Wrapper around your User entity                     │
│                                                               │
│  3. CustomUserDetailsService                                 │
│     └─> Loads users from database                           │
│                                                               │
│  4. JwtProperty                                              │
│     └─> Reads JWT settings from application.properties      │
│                                                               │
│  5. JwtUtil                                                  │
│     └─> Creates and validates JWT tokens                    │
│                                                               │
│  6. JwtFilter                                                │
│     └─> Intercepts EVERY request to check for JWT           │
│                                                               │
└─────────────────────────────────────────────────────────────┘
```

### How They Work Together:

```
Request comes in → JwtFilter intercepts it
                    ↓
                Does it have a JWT token?
                    ↓
                Yes → JwtUtil validates it
                    ↓
                Valid? → CustomUserDetailsService loads the user
                    ↓
                SecurityConfig checks: "Is this user allowed here?"
                    ↓
                Yes → Request proceeds to your controller
```

---

## Step 2: Setting Up Dependencies

Add these to your `pom.xml` (Maven) or `build.gradle` (Gradle):

### Maven (pom.xml):
```xml
<dependencies>
    <!-- Spring Security -->
    <dependency>
        <groupId>org.springframework.boot</groupId>
        <artifactId>spring-boot-starter-security</artifactId>
    </dependency>
    
    <!-- JWT Library -->
    <dependency>
        <groupId>io.jsonwebtoken</groupId>
        <artifactId>jjwt-api</artifactId>
        <version>0.12.3</version>
    </dependency>
    <dependency>
        <groupId>io.jsonwebtoken</groupId>
        <artifactId>jjwt-impl</artifactId>
        <version>0.12.3</version>
        <scope>runtime</scope>
    </dependency>
    <dependency>
        <groupId>io.jsonwebtoken</groupId>
        <artifactId>jjwt-jackson</artifactId>
        <version>0.12.3</version>
        <scope>runtime</scope>
    </dependency>
</dependencies>
```

### Gradle (build.gradle):
```gradle
dependencies {
    implementation 'org.springframework.boot:spring-boot-starter-security'
    implementation 'io.jsonwebtoken:jjwt-api:0.12.3'
    runtimeOnly 'io.jsonwebtoken:jjwt-impl:0.12.3'
    runtimeOnly 'io.jsonwebtoken:jjwt-jackson:0.12.3'
}
```

---

## Step 3: Creating Your User Model

You probably already have this, but here's what it should look like:

```java
package com.yourapp.model;

import jakarta.persistence.*;
import lombok.Data;

@Entity
@Table(name = "users")
@Data
public class User {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @Column(unique = true, nullable = false)
    private String username;
    
    @Column(unique = true, nullable = false)
    private String email;
    
    @Column(nullable = false)
    private String password;  // This should be BCrypt encrypted!
    
    @Enumerated(EnumType.STRING)
    private Role role;  // ADMIN, USER, etc.
}
```

And your Role enum:

```java
package com.yourapp.model;

public enum Role {
    USER,
    ADMIN
}
```

Your UserRepository:

```java
package com.yourapp.repository;

import com.yourapp.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Optional;

public interface UserRepository extends JpaRepository<User, Long> {
    
    // Find by username OR email (either one works for login)
    @Query("SELECT u FROM User u WHERE u.username = :usernameOrEmail OR u.email = :usernameOrEmail")
    Optional<User> findByUsernameOrEmail(@Param("usernameOrEmail") String usernameOrEmail);
}
```

---

## Step 4: CustomUserDetails - Wrapping Your User

**WHY DO WE NEED THIS?**

Spring Security doesn't know about YOUR User class. It only understands the `UserDetails` interface. So we create an adapter (wrapper) that translates your User into something Spring Security understands.

```java
package com.yourapp.security;

import com.yourapp.model.User;
import lombok.AllArgsConstructor;
import lombok.Data;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Collection;
import java.util.List;

@Data
@AllArgsConstructor
public class CustomUserDetails implements UserDetails {
    
    private User user;  // Your actual User entity
    
    // Get the user's ID (useful later)
    public Long getId() {
        return user.getId();
    }
    
    // REQUIRED: Return user's roles/permissions
    // Spring Security uses this to check authorization
    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        // Convert your Role enum to Spring Security's format
        // "ROLE_" prefix is REQUIRED by Spring Security
        return List.of(new SimpleGrantedAuthority("ROLE_" + user.getRole().name()));
    }
    
    // REQUIRED: Return the encrypted password
    @Override
    public String getPassword() {
        return user.getPassword();
    }
    
    // REQUIRED: Return the username
    @Override
    public String getUsername() {
        return user.getUsername();
    }
    
    // REQUIRED: Is the account expired?
    @Override
    public boolean isAccountNonExpired() {
        return true;  // You can add logic here if needed
    }
    
    // REQUIRED: Is the account locked?
    @Override
    public boolean isAccountNonLocked() {
        return true;  // You can add logic here if needed
    }
    
    // REQUIRED: Are the credentials expired?
    @Override
    public boolean isCredentialsNonExpired() {
        return true;  // You can add logic here if needed
    }
    
    // REQUIRED: Is the account enabled?
    @Override
    public boolean isEnabled() {
        return true;  // You can add logic here if needed
    }
}
```

**KEY POINTS:**
- This class wraps your User entity
- It implements Spring Security's `UserDetails` interface
- The `ROLE_` prefix in `getAuthorities()` is CRITICAL - Spring Security requires it
- All the `isXxxNonExpired()` methods can return true for now (you can add logic later)

---

## Step 5: CustomUserDetailsService - Loading Users

**WHAT DOES THIS DO?**

This class tells Spring Security HOW to load a user from your database. Spring Security will call this whenever it needs to look up a user.

```java
package com.yourapp.security;

import com.yourapp.model.User;
import com.yourapp.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service
public class CustomUserDetailsService implements UserDetailsService {
    
    @Autowired
    private UserRepository userRepository;
    
    // REQUIRED: This method is called by Spring Security to load a user
    @Override
    public UserDetails loadUserByUsername(String usernameOrEmail) throws UsernameNotFoundException {
        
        // 1. Try to find the user in the database
        User user = userRepository.findByUsernameOrEmail(usernameOrEmail)
            .orElseThrow(() -> new UsernameNotFoundException(
                "User not found with username or email: " + usernameOrEmail
            ));
        
        // 2. Wrap the User in CustomUserDetails and return it
        return new CustomUserDetails(user);
    }
}
```

**WHAT HAPPENS HERE:**
1. Spring Security passes in a username/email
2. We look it up in the database
3. If found, we wrap it in `CustomUserDetails` and return it
4. If not found, we throw an exception

---

## Step 6: JWT Configuration Properties

**WHY DO WE NEED THIS?**

We need a secret key to sign our JWT tokens. We don't want to hardcode it in our Java code (that's insecure). Instead, we put it in `application.properties` and read it with this class.

### First, add to `application.properties`:

```properties
# JWT Configuration
jwt.secret=YourSuperSecretKeyThatShouldBeAtLeast256BitsLongForHS256Algorithm
jwt.expiration=86400000

# Note: 86400000 milliseconds = 24 hours
```

**IMPORTANT:** In production, use a strong random secret key. You can generate one using:
```bash
openssl rand -base64 64
```

### Then create the property class:

```java
package com.yourapp.security;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;
import lombok.Data;

@Configuration
@ConfigurationProperties(prefix = "jwt")  // Reads properties starting with "jwt."
@Data
public class JwtProperty {
    
    private String secret;  // Automatically populated from jwt.secret
    
    // Convert the secret to bytes (required by JWT library)
    public byte[] getSecretBytes() {
        return secret.getBytes();
    }
}
```

**WHAT HAPPENS:**
- `@ConfigurationProperties(prefix = "jwt")` tells Spring to read all properties starting with "jwt."
- The `secret` field is automatically populated from `jwt.secret` in application.properties
- We provide a helper method to convert it to bytes

---

## Step 7: JwtUtil - The Token Factory

**THIS IS THE HEART OF JWT!**

This class does 3 main things:
1. **Creates** JWT tokens (when user logs in)
2. **Validates** JWT tokens (on every request)
3. **Extracts** information from tokens (username, role, etc.)

```java
package com.yourapp.security;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;

@Component
public class JwtUtil {
    
    @Autowired
    private JwtProperty jwtProperty;
    
    // ==================== 1. CREATE THE SIGNING KEY ====================
    
    /**
     * Creates a secure key from our secret string.
     * This key is used to sign and verify tokens.
     */
    private SecretKey getSigningKey() {
        return Keys.hmacShaKeyFor(jwtProperty.getSecretBytes());
    }
    
    // ==================== 2. GENERATE TOKEN ====================
    
    /**
     * Creates a new JWT token for a user.
     * Called when user logs in successfully.
     * 
     * @param userDetails The authenticated user
     * @return A JWT token string
     */
    public String generateToken(CustomUserDetails userDetails) {
        
        // 1. Create claims (extra info to store in the token)
        Map<String, Object> claims = new HashMap<>();
        
        // Store the user's role in the token
        claims.put("role", userDetails.getAuthorities()
            .iterator()
            .next()
            .getAuthority());
        
        // 2. Set expiration time (24 hours from now)
        long expirationTime = 24 * 60 * 60 * 1000;  // 24 hours in milliseconds
        
        // 3. Build and return the token
        return Jwts.builder()
            .setClaims(claims)                    // Add our custom data
            .setSubject(userDetails.getUsername()) // Who this token is for
            .setIssuedAt(new Date())              // When it was created
            .setExpiration(new Date(System.currentTimeMillis() + expirationTime))
            .signWith(getSigningKey(), SignatureAlgorithm.HS256)  // Sign it
            .compact();                           // Convert to string
    }
    
    // ==================== 3. VALIDATE TOKEN ====================
    
    /**
     * Checks if a token is valid.
     * Called on every request.
     * 
     * @param token The JWT token to validate
     * @param username The username to verify against
     * @return true if token is valid, false otherwise
     */
    public boolean validateToken(String token, String username) {
        try {
            final String extractedUsername = extractUsername(token);
            
            // Token is valid if:
            // 1. Username matches
            // 2. Token is not expired
            return extractedUsername.equals(username) && !isTokenExpired(token);
            
        } catch (Exception e) {
            // If any error occurs, token is invalid
            return false;
        }
    }
    
    // ==================== 4. EXTRACT INFORMATION ====================
    
    /**
     * Extracts the username from a token.
     */
    public String extractUsername(String token) {
        return extractClaim(token, Claims::getSubject);
    }
    
    /**
     * Extracts the role from a token.
     */
    public String extractRole(String token) {
        return extractAllClaims(token).get("role", String.class);
    }
    
    /**
     * Extracts the expiration date from a token.
     */
    public Date extractExpiration(String token) {
        return extractClaim(token, Claims::getExpiration);
    }
    
    /**
     * Checks if a token has expired.
     */
    public boolean isTokenExpired(String token) {
        return extractExpiration(token).before(new Date());
    }
    
    // ==================== HELPER METHODS ====================
    
    /**
     * Generic method to extract any claim from a token.
     */
    public <T> T extractClaim(String token, Function<Claims, T> claimsResolver) {
        final Claims claims = extractAllClaims(token);
        return claimsResolver.apply(claims);
    }
    
    /**
     * Parses the token and extracts all claims.
     * This is where the token signature is verified.
     */
    private Claims extractAllClaims(String token) {
        return Jwts.parserBuilder()
            .setSigningKey(getSigningKey())  // Use our secret key
            .build()
            .parseClaimsJws(token)           // Parse and verify signature
            .getBody();                      // Get the claims
    }
}
```

**UNDERSTANDING THE TOKEN STRUCTURE:**

A JWT has 3 parts separated by dots: `xxxxx.yyyyy.zzzzz`

```
Header.Payload.Signature

Header:    Algorithm and token type
Payload:   Your data (username, role, expiration, etc.)
Signature: Proof that the token hasn't been tampered with
```

**KEY POINTS:**
- `generateToken()` creates a new token when user logs in
- `validateToken()` checks if a token is valid and not expired
- `extractUsername()` gets the username from the token
- The signature ensures the token hasn't been modified

---

## Step 8: JwtFilter - Checking Every Request

**THIS IS THE SECURITY GUARD!**

This filter intercepts EVERY incoming request and checks if it has a valid JWT token.

```java
package com.yourapp.security;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

@Component
public class JwtFilter extends OncePerRequestFilter {
    
    @Autowired
    private JwtUtil jwtUtil;
    
    @Autowired
    private CustomUserDetailsService userDetailsService;
    
    /**
     * This method runs ONCE for EVERY request.
     * It checks if the request has a valid JWT token.
     */
    @Override
    protected void doFilterInternal(
            HttpServletRequest request,
            HttpServletResponse response,
            FilterChain filterChain) throws ServletException, IOException {
        
        // ========== STEP 1: Extract token from request header ==========
        
        final String authHeader = request.getHeader("Authorization");
        String username = null;
        String token = null;
        
        // Check if Authorization header exists and starts with "Bearer "
        if (authHeader != null && authHeader.startsWith("Bearer ")) {
            
            // Extract token (remove "Bearer " prefix)
            token = authHeader.substring(7);
            
            // Extract username from token
            try {
                username = jwtUtil.extractUsername(token);
            } catch (Exception e) {
                // If token is malformed, username will be null
                System.out.println("Error extracting username from token: " + e.getMessage());
            }
        }
        
        // ========== STEP 2: Validate token and authenticate user ==========
        
        // If we have a username AND user is not already authenticated
        if (username != null && SecurityContextHolder.getContext().getAuthentication() == null) {
            
            // Load user details from database
            UserDetails userDetails = userDetailsService.loadUserByUsername(username);
            
            // Validate the token
            if (jwtUtil.validateToken(token, userDetails.getUsername())) {
                
                // Create authentication object
                UsernamePasswordAuthenticationToken authToken = 
                    new UsernamePasswordAuthenticationToken(
                        userDetails,              // Principal (the user)
                        null,                     // Credentials (not needed for JWT)
                        userDetails.getAuthorities()  // Authorities (roles)
                    );
                
                // Add request details
                authToken.setDetails(
                    new WebAuthenticationDetailsSource().buildDetails(request)
                );
                
                // Set authentication in Spring Security context
                // This tells Spring Security: "This user is authenticated!"
                SecurityContextHolder.getContext().setAuthentication(authToken);
            }
        }
        
        // ========== STEP 3: Continue with the request ==========
        
        // Pass the request to the next filter in the chain
        filterChain.doFilter(request, response);
    }
}
```

**WHAT HAPPENS HERE (Step by Step):**

```
1. Request arrives: GET /api/posts
   Header: Authorization: Bearer eyJhbGciOiJIUzI1NiJ9...

2. Filter extracts token from "Authorization" header
   Token: eyJhbGciOiJIUzI1NiJ9...

3. Filter extracts username from token
   Username: john_doe

4. Filter validates token
   ✓ Signature is correct
   ✓ Not expired
   ✓ Username matches

5. Filter loads user from database
   User: { id: 1, username: "john_doe", role: "USER" }

6. Filter creates authentication object and puts it in SecurityContext
   SecurityContext now knows: "This is john_doe with role USER"

7. Request continues to controller
   Your controller can now access the authenticated user!
```

**KEY POINTS:**
- Runs BEFORE your controllers
- Checks for "Authorization: Bearer <token>" header
- Validates the token
- If valid, tells Spring Security the user is authenticated
- If invalid, request continues but user is NOT authenticated

---

## Step 9: SecurityConfig - Tying It All Together

**THIS IS THE MASTER CONTROL PANEL!**

This class configures everything:
- Which URLs require authentication
- Which roles can access which endpoints
- CORS settings
- Session management
- Password encryption

```java
package com.yourapp.security;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import java.util.Arrays;

@Configuration
@EnableWebSecurity  // Enable Spring Security
public class SecurityConfig {
    
    private final JwtFilter jwtFilter;
    private final CustomUserDetailsService userDetailsService;
    
    // Constructor injection (recommended over @Autowired)
    public SecurityConfig(JwtFilter jwtFilter, CustomUserDetailsService userDetailsService) {
        this.jwtFilter = jwtFilter;
        this.userDetailsService = userDetailsService;
    }
    
    // ==================== CORS CONFIGURATION ====================
    
    /**
     * Configures Cross-Origin Resource Sharing (CORS).
     * Allows your frontend (React, Angular, etc.) to call your API.
     */
    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration configuration = new CorsConfiguration();
        
        // Which origins (domains) can access your API
        configuration.setAllowedOrigins(Arrays.asList(
            "http://localhost:3000",      // React default
            "http://localhost:4200",      // Angular default
            "http://127.0.0.1:4200"
        ));
        
        // Which HTTP methods are allowed
        configuration.setAllowedMethods(Arrays.asList(
            "GET", "POST", "PUT", "DELETE", "OPTIONS", "PATCH"
        ));
        
        // Which headers are allowed
        configuration.setAllowedHeaders(Arrays.asList("*"));
        
        // Allow sending credentials (cookies, authorization headers)
        configuration.setAllowCredentials(true);
        
        // Expose the Authorization header to the frontend
        configuration.setExposedHeaders(Arrays.asList("Authorization"));
        
        // How long the browser can cache CORS preflight requests
        configuration.setMaxAge(3600L);
        
        // Apply this configuration to all paths
        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", configuration);
        return source;
    }
    
    // ==================== SECURITY FILTER CHAIN ====================
    
    /**
     * The main security configuration.
     * This is where you define ALL your security rules.
     */
    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
            // Enable CORS
            .cors(cors -> cors.configurationSource(corsConfigurationSource()))
            
            // Disable CSRF (not needed for JWT-based APIs)
            .csrf(csrf -> csrf.disable())
            
            // ========== AUTHORIZATION RULES ==========
            .authorizeHttpRequests(auth -> auth
                
                // PUBLIC ENDPOINTS (no authentication required)
                .requestMatchers("/auth/**").permitAll()         // Login, register
                .requestMatchers("/public/**").permitAll()       // Public content
                
                // ADMIN ONLY ENDPOINTS
                .requestMatchers("/admin/**").hasRole("ADMIN")
                
                // USER OR ADMIN ENDPOINTS
                .requestMatchers("/posts/**").hasAnyRole("USER", "ADMIN")
                .requestMatchers("/users/**").hasAnyRole("USER", "ADMIN")
                
                // ALL OTHER ENDPOINTS require authentication
                .anyRequest().authenticated()
            )
            
            // ========== SESSION MANAGEMENT ==========
            // STATELESS = Don't create sessions (we use JWT instead)
            .sessionManagement(session -> 
                session.sessionCreationPolicy(SessionCreationPolicy.STATELESS)
            );
        
        // Tell Spring Security how to load users
        http.userDetailsService(userDetailsService);
        
        // Add our JWT filter BEFORE the default authentication filter
        http.addFilterBefore(jwtFilter, UsernamePasswordAuthenticationFilter.class);
        
        return http.build();
    }
    
    // ==================== PASSWORD ENCODER ====================
    
    /**
     * BCrypt password encoder.
     * NEVER store plain text passwords!
     */
    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }
    
    // ==================== AUTHENTICATION MANAGER ====================
    
    /**
     * Authentication manager.
     * Used to authenticate users (check username/password).
     */
    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration config) 
            throws Exception {
        return config.getAuthenticationManager();
    }
}
```

**UNDERSTANDING THE AUTHORIZATION RULES:**

```java
.requestMatchers("/auth/**").permitAll()
```
- **Pattern:** `/auth/**` means `/auth/login`, `/auth/register`, etc.
- **Rule:** `permitAll()` = Anyone can access (no login needed)

```java
.requestMatchers("/admin/**").hasRole("ADMIN")
```
- **Pattern:** `/admin/**` means `/admin/users`, `/admin/settings`, etc.
- **Rule:** `hasRole("ADMIN")` = Only users with ADMIN role can access

```java
.requestMatchers("/posts/**").hasAnyRole("USER", "ADMIN")
```
- **Pattern:** `/posts/**` means `/posts/create`, `/posts/123`, etc.
- **Rule:** `hasAnyRole("USER", "ADMIN")` = USER or ADMIN can access

```java
.anyRequest().authenticated()
```
- **Pattern:** Any URL not matched above
- **Rule:** `authenticated()` = Must be logged in (any role)

**IMPORTANT NOTES:**

1. **Order matters!** More specific rules should come BEFORE general rules.
2. **Role names:** Spring Security automatically adds "ROLE_" prefix. So `hasRole("ADMIN")` checks for "ROLE_ADMIN".
3. **CSRF disabled:** We disable CSRF for JWT-based APIs (it's for traditional session-based apps).
4. **STATELESS:** We don't use sessions because JWT tokens are self-contained.

---

## Step 10: Creating Your Auth Controller

Now let's create the login endpoint where users get their JWT token!

```java
package com.yourapp.controller;

import com.yourapp.security.CustomUserDetails;
import com.yourapp.security.JwtUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/auth")
public class AuthController {
    
    @Autowired
    private AuthenticationManager authenticationManager;
    
    @Autowired
    private JwtUtil jwtUtil;
    
    /**
     * Login endpoint.
     * User sends username/password, receives JWT token.
     */
    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody LoginRequest loginRequest) {
        
        try {
            // 1. Attempt to authenticate the user
            Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                    loginRequest.getUsername(),
                    loginRequest.getPassword()
                )
            );
            
            // 2. If we get here, authentication was successful
            CustomUserDetails userDetails = (CustomUserDetails) authentication.getPrincipal();
            
            // 3. Generate JWT token
            String token = jwtUtil.generateToken(userDetails);
            
            // 4. Return the token to the client
            return ResponseEntity.ok(new LoginResponse(
                token,
                userDetails.getUsername(),
                userDetails.getAuthorities().iterator().next().getAuthority()
            ));
            
        } catch (AuthenticationException e) {
            // Authentication failed (wrong username/password)
            return ResponseEntity.status(401).body("Invalid username or password");
        }
    }
}

// ==================== REQUEST/RESPONSE CLASSES ====================

/**
 * Login request from client.
 */
class LoginRequest {
    private String username;
    private String password;
    
    // Getters and setters
    public String getUsername() { return username; }
    public void setUsername(String username) { this.username = username; }
    public String getPassword() { return password; }
    public void setPassword(String password) { this.password = password; }
}

/**
 * Login response to client.
 */
class LoginResponse {
    private String token;
    private String username;
    private String role;
    
    public LoginResponse(String token, String username, String role) {
        this.token = token;
        this.username = username;
        this.role = role;
    }
    
    // Getters
    public String getToken() { return token; }
    public String getUsername() { return username; }
    public String getRole() { return role; }
}
```

**WHAT HAPPENS DURING LOGIN:**

```
1. User sends POST request to /auth/login
   Body: { "username": "john", "password": "secret123" }

2. AuthenticationManager checks credentials
   - Calls CustomUserDetailsService.loadUserByUsername("john")
   - Compares hashed passwords using BCrypt
   - If match → authentication successful
   - If no match → throws AuthenticationException

3. If successful, we get the authenticated user

4. JwtUtil generates a token for this user

5. We send back the token to the client
   Response: { "token": "eyJhbGc...", "username": "john", "role": "ROLE_USER" }

6. Client saves the token (in localStorage, cookie, etc.)

7. Client includes token in all future requests
   Header: Authorization: Bearer eyJhbGc...
```

---

## Testing Your Security

### 1. Register a User (if you don't have one):

```bash
POST /auth/register
Content-Type: application/json

{
  "username": "john",
  "email": "john@example.com",
  "password": "password123"
}
```

**IMPORTANT:** Make sure to hash the password using BCrypt before saving!

```java
@PostMapping("/register")
public ResponseEntity<?> register(@RequestBody RegisterRequest request) {
    User user = new User();
    user.setUsername(request.getUsername());
    user.setEmail(request.getEmail());
    
    // HASH THE PASSWORD!
    user.setPassword(passwordEncoder.encode(request.getPassword()));
    
    user.setRole(Role.USER);
    userRepository.save(user);
    
    return ResponseEntity.ok("User registered successfully");
}
```

### 2. Login to Get Token:

```bash
POST /auth/login
Content-Type: application/json

{
  "username": "john",
  "password": "password123"
}
```

**Response:**
```json
{
  "token": "eyJhbGciOiJIUzI1NiJ9.eyJyb2xlIjoiUk9MRV9VU0VSIiwic3ViIjoiam9obiIsImlhdCI6MTcwNjU1MDAwMCwiZXhwIjoxNzA2NjM2NDAwfQ.abc123...",
  "username": "john",
  "role": "ROLE_USER"
}
```

### 3. Use Token to Access Protected Endpoint:

```bash
GET /posts/123
Authorization: Bearer eyJhbGciOiJIUzI1NiJ9.eyJyb2xlIjoiUk9MRV9VU0VSIiwic3ViIjoiam9obiIsImlhdCI6MTcwNjU1MDAwMCwiZXhwIjoxNzA2NjM2NDAwfQ.abc123...
```

**If token is valid:** You get the data
**If token is invalid/expired:** 401 Unauthorized

### 4. Access User Info in Your Controller:

```java
@GetMapping("/profile")
public ResponseEntity<?> getProfile() {
    // Get the authenticated user
    Authentication auth = SecurityContextHolder.getContext().getAuthentication();
    CustomUserDetails userDetails = (CustomUserDetails) auth.getPrincipal();
    
    // Access user info
    Long userId = userDetails.getId();
    String username = userDetails.getUsername();
    
    return ResponseEntity.ok("Hello, " + username);
}
```

---

## Common Issues and Solutions

### Issue 1: "Access Denied" even with valid token

**Cause:** Role prefix mismatch

**Solution:** Make sure you're adding "ROLE_" prefix in `CustomUserDetails.getAuthorities()`:

```java
// WRONG
return List.of(new SimpleGrantedAuthority(user.getRole().name()));

// CORRECT
return List.of(new SimpleGrantedAuthority("ROLE_" + user.getRole().name()));
```

### Issue 2: CORS errors from frontend

**Cause:** Frontend domain not in allowed origins

**Solution:** Add your frontend URL to `corsConfigurationSource()`:

```java
configuration.setAllowedOrigins(Arrays.asList(
    "http://localhost:3000",  // Add your frontend URL here
    "https://myapp.com"
));
```

### Issue 3: Token expired

**Cause:** Token expiration time is too short

**Solution:** Increase expiration time in `JwtUtil.generateToken()`:

```java
// 24 hours
long expirationTime = 24 * 60 * 60 * 1000;

// 7 days
long expirationTime = 7 * 24 * 60 * 60 * 1000;
```

### Issue 4: "Invalid token" error

**Common causes:**
1. Token is malformed (missing "Bearer " prefix)
2. Secret key changed (tokens signed with old key are invalid)
3. Token expired
4. Token was modified

**Solution:** Check token format in frontend:

```javascript
// WRONG
headers: { 'Authorization': token }

// CORRECT
headers: { 'Authorization': `Bearer ${token}` }
```

### Issue 5: Password not matching

**Cause:** Password not hashed or wrong encoder

**Solution:** Always hash passwords when saving:

```java
user.setPassword(passwordEncoder.encode(plainPassword));
```

### Issue 6: Can't access any endpoint (even public ones)

**Cause:** Filter order is wrong or JwtFilter is too strict

**Solution:** Make sure JwtFilter doesn't reject requests to public endpoints. The filter should ONLY authenticate if a token is present, not require it for all endpoints.

---

## Summary - The Complete Flow

```
┌─────────────────────────────────────────────────────────────────┐
│                         REGISTRATION                             │
├─────────────────────────────────────────────────────────────────┤
│  User → POST /auth/register                                     │
│       → Controller hashes password with BCrypt                  │
│       → Saves user to database                                  │
└─────────────────────────────────────────────────────────────────┘

┌─────────────────────────────────────────────────────────────────┐
│                            LOGIN                                 │
├─────────────────────────────────────────────────────────────────┤
│  User → POST /auth/login { username, password }                │
│       → AuthenticationManager verifies credentials              │
│       → JwtUtil generates token                                 │
│       → Client receives token                                   │
│       → Client saves token (localStorage, etc.)                 │
└─────────────────────────────────────────────────────────────────┘

┌─────────────────────────────────────────────────────────────────┐
│                      ACCESSING PROTECTED ENDPOINT               │
├─────────────────────────────────────────────────────────────────┤
│  User → GET /posts/123                                          │
│       → Header: Authorization: Bearer <token>                   │
│       ↓                                                          │
│  JwtFilter intercepts request                                   │
│       → Extracts token from header                              │
│       → JwtUtil validates token                                 │
│       → CustomUserDetailsService loads user                     │
│       → Sets authentication in SecurityContext                  │
│       ↓                                                          │
│  SecurityConfig checks authorization                            │
│       → Does user have required role?                           │
│       → YES → Request proceeds to controller                    │
│       → NO → 403 Forbidden                                      │
│       ↓                                                          │
│  Controller processes request                                   │
│       → Can access user info from SecurityContext               │
│       → Returns response                                        │
└─────────────────────────────────────────────────────────────────┘
```

---

## Next Steps

Now that you understand the basics, you can:

1. **Add refresh tokens** (for when access tokens expire)
2. **Add email verification** (send confirmation emails)
3. **Add password reset** (forgot password functionality)
4. **Add OAuth2** (login with Google, GitHub, etc.)
5. **Add rate limiting** (prevent brute force attacks)
6. **Add account lockout** (after too many failed login attempts)
7. **Add audit logging** (track who did what)

---

## Questions to Test Your Understanding

1. What is the purpose of `CustomUserDetails`?
2. When does `JwtFilter` run?
3. What does `STATELESS` session management mean?
4. Why do we add "ROLE_" prefix to roles?
5. What's the difference between authentication and authorization?
6. What happens if a token expires?
7. Why do we hash passwords?

**Answers:**
1. It wraps your User entity so Spring Security can understand it
2. On EVERY incoming request, before it reaches your controller
3. We don't create server-side sessions; JWT tokens are self-contained
4. Spring Security requires it for role-based authorization
5. Authentication = proving who you are; Authorization = what you're allowed to do
6. JwtFilter will reject it and user must login again to get a new token
7. So if the database is compromised, passwords can't be read in plain text

---

**Good luck! You've got this! 🚀**

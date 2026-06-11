# Auth Core

Auth Core — це бібліотека для Spring Boot, яка надає готову реалізацію session-based автентифікації користувачів через Spring Security.

Бібліотека реалізує:

* Login API
* Logout API
* Current User API
* Збереження SecurityContext у HttpSession
* Інтеграцію зі Spring Security AuthenticationManager

Бібліотека не містить власної моделі користувача і не працює напряму з базою даних. Вона використовує стандартні механізми Spring Security та очікує, що основний застосунок надасть реалізацію UserDetailsService.

---

# Features

## Authentication

Підтримується автентифікація через:

```text
username + password
```

Після успішної автентифікації створюється:

```text
SecurityContext
```

який автоматично зберігається в:

```text
HttpSession
```

та асоціюється з cookie:

```text
JSESSIONID
```

---

# Endpoints

## Login

### Request

```http
POST /api/auth/login
Content-Type: application/json
```

```json
{
  "username": "name",
  "password": "1234"
}
```

### Response

```json
{
  "message": "Login successful",
  "username": "name",
  "authorities": [
    {
      "authority": "ROLE_USER"
    }
  ]
}
```

### Side Effects

При успішній автентифікації сервер повертає:

```http
Set-Cookie: JSESSIONID=...
```

Ця cookie використовується для подальшої авторизації.

---

## Current User

Повертає інформацію про поточного автентифікованого користувача.

### Request

```http
GET /api/auth/me
```

### Response

```json
{
  "username": "name",
  "authorities": [
    {
      "authority": "ROLE_USER"
    }
  ],
  "authenticated": true
}
```

### Authentication Required

```text
Yes
```

---

## Logout

Очищає SecurityContext та інвалідує поточну HTTP Session.

### Request

```http
POST /api/auth/logout
```

### Response

```json
{
  "message": "Logout successful"
}
```

### Side Effects

* SecurityContext очищається
* Session інвалідується
* Cookie JSESSIONID видаляється

---

# Installation

## Maven

```xml
<dependency>
    <groupId>org.example</groupId>
    <artifactId>auth-core</artifactId>
    <version>0.0.1-SNAPSHOT</version>
</dependency>
```

---

# Required Beans

Для роботи бібліотеки основний застосунок повинен надати такі Spring Beans.

## AuthenticationManager

```java
@Bean
public AuthenticationManager authenticationManager(
        AuthenticationConfiguration configuration
) throws Exception {
    return configuration.getAuthenticationManager();
}
```

## PasswordEncoder

```java
@Bean
public PasswordEncoder passwordEncoder() {
    return new BCryptPasswordEncoder();
}
```

## UserDetailsService

```java
@Service
public class AppUserDetailsService implements UserDetailsService {

    private final UserRepository userRepository;

    public AppUserDetailsService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Override
    public UserDetails loadUserByUsername(String username) {

        User user = userRepository.findByUsername(username)
                .orElseThrow(() ->
                        new UsernameNotFoundException(username));

        return new AppUserDetails(user);
    }
}
```

---

# Security Configuration

Мінімальна конфігурація Spring Security.

```java
@Bean
public SecurityFilterChain securityFilterChain(HttpSecurity http)
        throws Exception {

    return http
            .csrf(csrf -> csrf.disable())
            .authorizeHttpRequests(auth -> auth
                    .requestMatchers(
                            "/api/auth/login",
                            "/api/auth/register"
                    ).permitAll()

                    .requestMatchers(
                            "/api/auth/me",
                            "/api/auth/logout"
                    ).authenticated()

                    .requestMatchers("/api/private/**")
                    .hasRole("USER")

                    .anyRequest()
                    .authenticated()
            )
            .formLogin(form -> form.disable())
            .httpBasic(basic -> basic.disable())
            .build();
}
```

---



## Password Storage

Паролі повинні зберігатися у вигляді BCrypt hash.



---




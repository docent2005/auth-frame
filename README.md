# auth-core

Версія: 0.0.2-SNAPSHOT

auth-core — це бібліотека для Spring Boot, яка спрощує реалізацію автентифікації на основі JWT та Spring Security.

Основна мета бібліотеки — прибрати необхідність щоразу реалізовувати однакову логіку входу користувача, генерації JWT-токенів та їх перевірки. Бібліотека надає готові сервіси та фільтри, які можна підключити до будь-якого Spring Boot застосунку.

На даний момент бібліотека реалізує:

* endpoint для входу користувача;
* генерацію JWT access token;
* перевірку JWT токенів;
* автоматичне створення Authentication на основі JWT;
* JWT Authentication Filter;
* DTO для запитів та відповідей автентифікації;
* інтеграцію зі Spring Security.

Бібліотека не містить:

* сутностей користувача;
* сутностей ролей;
* репозиторіїв;
* логіки реєстрації;
* структури бази даних;
* бізнес-логіки застосунку.

Усі ці компоненти повинні бути реалізовані в основному проєкті.

---

ПІДКЛЮЧЕННЯ БІБЛІОТЕКИ

Додайте залежність:

<dependency>
    <groupId>org.example</groupId>
    <artifactId>auth-core</artifactId>
    <version>0.0.2-SNAPSHOT</version>
</dependency>

---

НАЛАШТУВАННЯ JWT

У application.properties необхідно вказати:

auth.jwt.secret=your-secret-key
auth.jwt.expiration-ms=86400000

auth.jwt.secret використовується для підписування JWT токенів.

auth.jwt.expiration-ms визначає час життя токена в мілісекундах.

Наприклад:

86400000 = 24 години.

---

ЩО ПОВИНЕН РЕАЛІЗУВАТИ ОСНОВНИЙ ПРОЄКТ

Для роботи бібліотеки необхідно надати реалізацію UserDetailsService.

Приклад:

@Service
public class AppUserDetailsService implements UserDetailsService {

```
@Override
public UserDetails loadUserByUsername(String username) {
    // пошук користувача в базі даних
}
```

}

Також необхідно створити PasswordEncoder:

@Bean
public PasswordEncoder passwordEncoder() {
return new BCryptPasswordEncoder();
}

І AuthenticationManager:

@Bean
public AuthenticationManager authenticationManager(
AuthenticationConfiguration configuration
) throws Exception {
return configuration.getAuthenticationManager();
}

---

НАЛАШТУВАННЯ SPRING SECURITY

Для роботи JWT необхідно використовувати stateless режим:

.sessionManagement(session ->
session.sessionCreationPolicy(SessionCreationPolicy.STATELESS)
)

JWT фільтр повинен бути доданий до Security Filter Chain:

.addFilterBefore(
jwtAuthenticationFilter,
UsernamePasswordAuthenticationFilter.class
)

Endpoint входу необхідно зробити доступним без автентифікації:

.requestMatchers("/api/auth/login")
.permitAll()

---

ВХІД У СИСТЕМУ

Бібліотека автоматично надає endpoint:

POST /api/auth/login

Приклад запиту:

{
"username": "john",
"password": "password"
}

Приклад відповіді:

{
"message": "Login successful",
"username": "john",
"authorities": [
{
"authority": "ROLE_USER"
}
],
"accessToken": "eyJ...",
"tokenType": "Bearer"
}

---

ВИКОРИСТАННЯ JWT

Після успішного входу клієнт повинен зберегти access token.

Для доступу до захищених endpoint токен необхідно передавати через HTTP-заголовок:

Authorization: Bearer <token>

Під час обробки запиту JwtAuthenticationFilter автоматично:

1. зчитує токен;
2. перевіряє його валідність;
3. отримує ім'я користувача та ролі;
4. створює Authentication;
5. записує Authentication у SecurityContextHolder.

Після цього Spring Security може виконувати авторизацію користувача.

---

АРХІТЕКТУРА

Схема входу:

Client
→ /api/auth/login
→ AuthController
→ AuthService
→ AuthenticationManager
→ UserDetailsService
→ Authentication
→ JwtService
→ JWT Token

Схема перевірки токена:

Client
→ Authorization: Bearer <token>
→ JwtAuthenticationFilter
→ JwtService
→ Authentication
→ SecurityContextHolder



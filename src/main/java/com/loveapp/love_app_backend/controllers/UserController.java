package com.loveapp.love_app_backend.controllers;

import com.loveapp.love_app_backend.modal.user.User;
import com.loveapp.love_app_backend.services.JwtService;
import com.loveapp.love_app_backend.services.UserService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;
import java.util.UUID;

@RestController
@RequestMapping("/api/users")
public class UserController {

    private final UserService service;
    private final JwtService jwtService;

    public UserController(UserService service, JwtService jwtService) {
        this.service = service;
        this.jwtService = jwtService;
    }

    // Protegida pelo SecurityConfig — só usuários autenticados
    @GetMapping
    public ResponseEntity<?> getAll() {
        return ResponseEntity.ok(
                service.getAllUsers().stream().map(u -> Map.of(
                        "id", u.getId(),
                        "username", u.getUsername(),
                        "email", u.getEmail()
                        // senha nunca é retornada
                )).toList()
        );
    }

    // Pública — cadastro
    @PostMapping
    public ResponseEntity<?> create(@RequestBody Map<String, String> body) {
        String username = body.get("username");
        String email = body.get("email");
        String password = body.get("password");

        User user = service.createUser(username, email, password);

        // Retorna token já no cadastro para logar automaticamente
        String token = jwtService.generateToken(user.getId(), user.getEmail());

        return ResponseEntity.ok(Map.of(
                "token", token,
                "id", user.getId(),
                "username", user.getUsername(),
                "email", user.getEmail()
        ));
    }

    // Protegida — só o próprio usuário deveria deletar sua conta (validar no service se necessário)
    @DeleteMapping("/{id}")
    public ResponseEntity<?> delete(@PathVariable UUID id) {
        service.deleteUser(id);
        return ResponseEntity.ok(Map.of("message", "Usuário deletado com sucesso"));
    }

    // Protegida
    @GetMapping("/{id}")
    public ResponseEntity<?> get(@PathVariable UUID id) {
        User user = service.getUser(id);
        return ResponseEntity.ok(Map.of(
                "id", user.getId(),
                "username", user.getUsername(),
                "email", user.getEmail()
                // senha nunca é retornada
        ));
    }

    // Pública — login retorna JWT
    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody Map<String, String> body) {
        String email = body.get("email");
        String password = body.get("password");

        User user = service.login(email, password);

        // Gera token JWT assinado
        String token = jwtService.generateToken(user.getId(), user.getEmail());

        return ResponseEntity.ok(Map.of(
                "token", token,
                "id", user.getId(),
                "username", user.getUsername(),
                "email", user.getEmail()
        ));
    }
}

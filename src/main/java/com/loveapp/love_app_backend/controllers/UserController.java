package com.loveapp.love_app_backend.controllers;

import com.loveapp.love_app_backend.modal.User;
import com.loveapp.love_app_backend.services.UserService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;
import java.util.UUID;

@RestController
@RequestMapping("/api/users")
public class UserController {

    private final UserService service;

    public UserController(UserService service) {
        this.service = service;
    }

    @GetMapping
    public ResponseEntity<List<User>> getAll() {
        List<User> users = service.getAllUsers();
        return ResponseEntity.ok(users);
    }


    // Criar usuário
    @PostMapping
    public ResponseEntity<User> create(@RequestBody Map<String, String> body){
        String username = body.get("username");
        String email = body.get("email");
        String password = body.get("password");

        User user = service.createUser(username, email, password);

        return ResponseEntity.ok(user);
    }

    // Excluir usuário
    @DeleteMapping("/{id}")
    public ResponseEntity<?> delete(@PathVariable UUID id){
        service.deleteUser(id);
        return ResponseEntity.ok(Map.of("message", "Usuário deletado com sucesso"));
    }

    // Buscar usuário
    @GetMapping("/{id}")
    public ResponseEntity<User> get(@PathVariable UUID id){
        return ResponseEntity.ok(service.getUser(id));
    }

    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody Map<String, String> body) {
        String email = body.get("email");
        String password = body.get("password");

        User user = service.login(email, password);

        // Aqui você pode retornar só o ID e nome do usuário ou tudo menos a senha
        return ResponseEntity.ok(Map.of(
                "id", user.getId(),
                "username", user.getUsername(),
                "email", user.getEmail()
        ));
    }
}

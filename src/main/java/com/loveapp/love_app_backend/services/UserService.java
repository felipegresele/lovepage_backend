package com.loveapp.love_app_backend.services;

import com.loveapp.love_app_backend.modal.User;
import com.loveapp.love_app_backend.repositories.UserRepository;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

@Service
public class UserService {

    private final UserRepository repository;
    private final BCryptPasswordEncoder passwordEncoder;

    public UserService(UserRepository repository) {
        this.repository = repository;
        this.passwordEncoder = new BCryptPasswordEncoder();
    }

    public List<User> getAllUsers() {
        return repository.findAll();
    }

    // Criar usuário
    public User createUser(String username, String email, String rawPassword){
        if(repository.findByEmail(email).isPresent()){
            throw new RuntimeException("Email já cadastrado");
        }
        if(repository.findByUsername(username).isPresent()){
            throw new RuntimeException("Nome de usuário já existe");
        }

        User user = User.builder()
                .username(username)
                .email(email)
                .password(passwordEncoder.encode(rawPassword))
                .build();

        return repository.save(user);
    }

    // Excluir usuário pelo ID
    public void deleteUser(UUID userId){
        User user = repository.findById(userId)
                .orElseThrow(() -> new RuntimeException("Usuário não encontrado"));
        repository.delete(user);
    }

    // Buscar usuário pelo ID
    public User getUser(UUID userId){
        return repository.findById(userId)
                .orElseThrow(() -> new RuntimeException("Usuário não encontrado"));
    }

    public User login(String email, String rawPassword) {
        User user = repository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("Email ou senha incorretos"));

        if (!passwordEncoder.matches(rawPassword, user.getPassword())) {
            throw new RuntimeException("Email ou senha incorretos");
        }

        return user;
    }
}

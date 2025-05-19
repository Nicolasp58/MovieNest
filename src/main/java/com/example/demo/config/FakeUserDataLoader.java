package com.example.demo.config;

import com.example.demo.models.User; // Asegúrate que la ruta coincida con la real
import com.example.demo.repositories.UserRepository;

import com.github.javafaker.Faker;
import jakarta.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.password.PasswordEncoder;

@Configuration
public class FakeUserDataLoader {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @PostConstruct
    public void init() {
        Faker faker = new Faker();

        for (int i = 0; i < 10; i++) {
            String username = faker.name().username();
            String rawPassword = faker.internet().password(8, 16);

            User user = new User();
            user.setUsername(username);
            user.setPassword(passwordEncoder.encode(rawPassword));

            userRepository.save(user);

            System.out.println("Usuario fake creado: " + username + " / " + rawPassword);
        }
    }
}

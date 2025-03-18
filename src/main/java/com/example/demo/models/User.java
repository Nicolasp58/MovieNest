package com.example.demo.models;

import jakarta.persistence.*;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "users")
public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String username;
    private String password;
    private String role; // Ahora es String en lugar de Role

    @ElementCollection
    @CollectionTable(name = "user_purchased_movies", joinColumns = @JoinColumn(name = "user_id"))
    @Column(name = "movie_id")
    private List<Long> purchasedMoviesIds = new ArrayList<>();

    public User() {}

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getRole() {
        return role;
    }

    public void setRole(String role) {
        this.role = role;
    }

    public List<Long> getPurchasedMoviesIds() {
        return purchasedMoviesIds;
    }

    public void addPurchasedMovie(Long movieId) {
        this.purchasedMoviesIds.add(movieId);
    }
}

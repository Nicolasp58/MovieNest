package com.example.demo.models;

import jakarta.persistence.*;

@Entity
@Table (name = "reviews")
public class Review {
    
    @Id
    @GeneratedValue (strategy = GenerationType.IDENTITY)
    private Long id;

    private String description;
    private Integer rating;

    @ManyToOne
    @JoinColumn (name = "movie_id")
    private Movie movie;

    public Review() {
    }

    public Review(String description, Movie movie) {
        this.description = description;
        this.movie = movie;
        this.rating = rating;
    }

    //Getters y Setters
    public Long getId() {
        return id;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Movie getMovie() {
        return movie;
    }

    public void setMovie(Movie movie) {
        this.movie = movie;
    }
    
    public Integer getRating() {
        return rating;
    }

    public void setRating(Integer rating) {
        this.rating = rating;
    }
}

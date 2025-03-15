package com.example.demo.controllers;

import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;

public class ReviewForm {

    @NotEmpty(message = "The review description is required")
    private String description;

    @NotNull(message = "The movie ID is required")
    private Long movieId;

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Long getMovieId() {
        return movieId;
    }

    public void setMovieId(Long movieId) {
        this.movieId = movieId;
    }
}

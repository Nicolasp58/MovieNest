package com.example.demo.controllers;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NegativeOrZero;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

public class MovieForm{

    @NotEmpty(message = "The movie name is required")
    private String name;

    @NotEmpty(message = "The movie desciption is required")
    private String description;

    @NotNull(message = "The price is required")
    @Positive(message = "Price must be greater than 0")
    private Double price;


    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Double getPrice() {
        return price;
    }

    public void setPrice(Double price) {
        this.price = price;
    }
}
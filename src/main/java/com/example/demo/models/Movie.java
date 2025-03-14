package com.example.demo.models;

import jakarta.persistence.*; 

import java.util.ArrayList; 
import java.util.List;

@Entity
@Table(name = "movies")
public class Movie{

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY) 
    private Long id; 

    private String name;
    private String description;
    private Integer price;

    //Constructor
    public Movie() {
    }

    public Movie(String name, String description, Integer price) {
        this.name = name;
        this.description = description;
        this.price = price;
    }

    //Getters y Setters
    public Long getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public String getDescription() {
        return description;
    }

    public Integer getPrice() {
        return price;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public void setPrice(Integer price) {
        this.price = price;
    }

}
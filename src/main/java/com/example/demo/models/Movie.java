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
    private String genre;
    private Integer price;

    @OneToMany (mappedBy = "movie", cascade = CascadeType.ALL)
    private List<Review> reviews = new ArrayList<>();

    public List<Review> getReviews() {
        return reviews;
    }

    public void setReviews(List<Review> reviews) {
        this.reviews = reviews;
    }

    //Constructor
    public Movie() {
    }

    public Movie(String name, String description, String genre, Integer price) {
        this.name = name;
        this.description = description;
        this.genre = genre;
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

    public String getGenre() {
        return genre;
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

    public void setGenre(String genre) {
        this.genre = genre;
    }

    public void setPrice(Integer price) {
        this.price = price;
    }

}

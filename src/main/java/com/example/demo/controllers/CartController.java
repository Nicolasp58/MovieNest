package com.example.demo.controllers;

import com.example.demo.models.Movie;
import com.example.demo.repositories.MovieRepository;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@Controller
@RequestMapping("/cart")
public class CartController {

    @Autowired
    private MovieRepository movieRepository;

    @GetMapping
    public String index(HttpSession session, Model model) {
        
        List<Movie> movies = movieRepository.findAll();
        
        // Obtener productos del carrito almacenados en la sesión
        Map<Long, Integer> cartMovieData = (Map<Long, Integer>) session.getAttribute("cart_movie_data");
        Map<Long, Movie> cartMovies = new HashMap<>();

        if (cartMovieData != null) {
            for (Long id : cartMovieData.keySet()) {
                Optional<Movie> movie = movieRepository.findById(id);
                movie.ifPresent(p -> cartMovies.put(id, p));
            }
        }

        model.addAttribute("title", "Cart - MovieNest");
        model.addAttribute("subtitle", "Shopping Cart");
        model.addAttribute("movies", movies);
        model.addAttribute("cartMovies", cartMovies);
        return "cart/index";
    }

    @PostMapping("/add/{id}")
    public String add(@PathVariable Long id, HttpSession session) {
        Optional<Movie> movie = movieRepository.findById(id);
        if (movie.isEmpty()) {
            return "redirect:/cart";
        }

        // Recuperar el carrito de la sesión o crear uno nuevo si es nulo
        Map<Long, Integer> cartMovieData = (Map<Long, Integer>) session.getAttribute("cart_movie_data");
        if (cartMovieData == null) {
            cartMovieData = new HashMap<>();
        }

        // Agregar producto al carrito (contador de cantidad)
        cartMovieData.put(id, cartMovieData.getOrDefault(id, 0) + 1);
        session.setAttribute("cart_movie_data", cartMovieData);
        return "redirect:/cart";
    }

    @GetMapping("/removeAll")
    public String removeAll(HttpSession session) {
        session.removeAttribute("cart_movie_data");
        return "redirect:/cart";
    }
}
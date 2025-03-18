package com.example.demo.controllers;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.RequestParam;

import com.example.demo.models.Movie;
import com.example.demo.models.Review;
import com.example.demo.repositories.MovieRepository; 
import com.example.demo.repositories.ReviewRepository;
import com.example.demo.models.User;
import com.example.demo.repositories.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.Optional;
import java.util.List;



@Controller
public class HomeController {

    @Autowired
    private MovieRepository movieRepository;

    @Autowired
    private ReviewRepository reviewRepository;

    @Autowired
    private UserRepository userRepository;


    @GetMapping("/")
    public String index() {
        // Verifica si el usuario ya ha iniciado sesión
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();

        if (auth == null || !auth.isAuthenticated() || auth.getPrincipal().equals("anonymousUser")) {
            return "redirect:/auth/register"; // Si no está autenticado, lo manda a registrarse
        }

        return "redirect:/home"; // Si ya está autenticado, lo manda a home
    }

    @GetMapping("/home")
    public String home(Model model) {
        model.addAttribute("title", "Welcome to MovieNest");
        model.addAttribute("subtitle", "Your ultimate movie experience!");
        return "home/index"; // Asegura que la vista home/index.html se renderice correctamente
    }

    @GetMapping("/about")
    public String about(Model model) {
        model.addAttribute("title", "About MovieNest");
        return "home/about";
    }

    @GetMapping("/profile")
    public String profile(Model model) {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
    if (auth == null || !auth.isAuthenticated() || auth.getPrincipal().equals("anonymousUser")) {
        return "redirect:/auth/login"; // Redirige al login si no está autenticado
    }

    String username = auth.getName();
    Optional<User> userOpt = userRepository.findByUsername(username);

    if (userOpt.isEmpty()) {
        return "redirect:/auth/login"; // Si no encuentra el usuario, lo manda a login
    }

    User user = userOpt.get();

    // Obtener las películas compradas por el usuario
    List<Movie> purchasedMovies = movieRepository.findAllById(user.getPurchasedMoviesIds());

    model.addAttribute("title", "Your Profile");
    model.addAttribute("username", username);
    model.addAttribute("purchasedMovies", purchasedMovies);

    return "profile/index";
    }

    @GetMapping("/movies/bought/{id}") 
    public String show(@PathVariable("id") Long id, Model model) { 
        Movie movie = movieRepository.findById(id) .orElseThrow(() -> new RuntimeException("Movie not found")); 
        model.addAttribute("title", movie.getName() + " - MovieNest"); 
        model.addAttribute("subtitle", movie.getName() + " - Movie information"); 
        model.addAttribute("movie", movie);
        return "profile/movies";    
    }

    @PostMapping("/movies/{id}/reviews")
    public String attachReview(
            @PathVariable("id") Long id, 
            @RequestParam("description") String description, 
            @RequestParam("rating") int rating) {
        
        Movie movie = movieRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Movie not found"));

        Review review = new Review();
        review.setMovie(movie);  // Asignar la película a la reseña
        review.setDescription(description);
        review.setRating(rating);

        reviewRepository.save(review);
        
        return "redirect:/movies/bought/" + id; 
    }

}

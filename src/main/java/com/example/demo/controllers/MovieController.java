package com.example.demo.controllers;

import com.example.demo.models.Movie;
import com.example.demo.repositories.MovieRepository; 
import org.springframework.beans.factory.annotation.Autowired; 
import org.springframework.stereotype.Controller; 
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping; 
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;

import java.util.List;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RequestBody;



@Controller
public class MovieController {

    @Autowired
    private MovieRepository movieRepository;

    @GetMapping("/movies")
    public String index(Model model) {
        List<Movie> movies = movieRepository.findAll(); 
        model.addAttribute("title", "Movies - MovieNest"); 
        model.addAttribute("subtitle", "List of movies"); 
        model.addAttribute("movies", movies);
        return "movie/index"; 
    }

    @GetMapping("/movies/{id}") 
    public String show(@PathVariable("id") Long id, Model model) { 
        Movie movie = movieRepository.findById(id) .orElseThrow(() -> new RuntimeException("Movie not found")); 
        model.addAttribute("title", movie.getName() + " - Online Store"); 
        model.addAttribute("subtitle", movie.getName() + " - Movie information"); 
        model.addAttribute("movie", movie);
        return "movie/show";    
    }
    
    @GetMapping("/movies/create")
    public String createMovieForm(Model model) { 
        model.addAttribute("movie", new Movie()); 
        return "movie/create"; 
    }

    @GetMapping("/movies/success")
    public String successPage() {
        return "movie/success";
    }

    @PostMapping("/movies") 
    public String save(Movie movie) {  
        if (movie.getName() == null || movie.getName().isEmpty() || movie.getDescription() == null || movie.getDescription().isEmpty() || movie.getPrice() == null) { 
            throw new RuntimeException("Name, Description and Price are required"); 
        } 
        movieRepository.save(movie); 
        return "redirect:/movies/success"; 
    }

    @PostMapping("/movies/{id}/delete")
    public String deleteMovie(@PathVariable("id") Long id) {
        movieRepository.deleteById(id);
        return "redirect:/movies";
    }
    
}
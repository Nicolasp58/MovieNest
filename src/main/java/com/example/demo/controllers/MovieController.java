package com.example.demo.controllers;

import com.example.demo.models.Movie;
import com.example.demo.repositories.MovieRepository; 
import com.example.demo.interfaces.ImageStorage;
import org.springframework.beans.factory.annotation.Autowired; 
import org.springframework.stereotype.Controller; 
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RequestBody;


@Controller
public class MovieController {

    @Autowired
    private MovieRepository movieRepository;

    @Autowired
    private ImageStorage imageStorage;

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
        model.addAttribute("title", movie.getName() + " - MovieNest"); 
        model.addAttribute("subtitle", movie.getName() + " - Movie information"); 
        model.addAttribute("movie", movie);
        return "movie/show";    
    }
    
    @GetMapping("/movies/create")
    public String createMovieForm(Model model) { 
        model.addAttribute("title", "Add Movie");
        model.addAttribute("movie", new Movie()); 
        return "movie/create"; 
    }

    @GetMapping("/movies/success")
    public String successPage() {
        return "movie/success";
    }

    @PostMapping("/movies") 
    public String save(@ModelAttribute Movie movie, @RequestParam("image") MultipartFile image) {  
        if (movie.getName() == null || movie.getName().isEmpty() || movie.getDescription() == null || movie.getDescription().isEmpty() || movie.getGenre() == null || movie.getGenre().isEmpty() || movie.getPrice() == null) { 
            throw new RuntimeException("Name, Description, Genre and Price are required"); 
        }
        if (!image.isEmpty()) {
            String filename = image.getOriginalFilename();
            imageStorage.store(image, filename);
            movie.setImagePath("uploads/" + filename);
            System.out.println("URL de imagen guardada: " + movie.getImagePath());
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
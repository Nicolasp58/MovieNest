package com.example.demo.controllers;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import com.example.demo.models.Movie;
import com.example.demo.repositories.MovieRepository;
import com.example.demo.interfaces.ImageStorage;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.math.BigDecimal;
import java.util.List;
import com.github.javafaker.Faker;

@Controller
public class MovieController {

    @Autowired
    private MovieRepository movieRepository;

    @Autowired
    private ImageStorage imageStorage;

  @GetMapping("/movies")
public String index(Model model, @RequestParam(defaultValue = "0") int page) {
    int pageSize = 6;
    Page<Movie> moviePage = movieRepository.findAll(PageRequest.of(page, pageSize));

    model.addAttribute("title", "Movies - MovieNest");
    model.addAttribute("subtitle", "List of movies");
    model.addAttribute("movies", moviePage.getContent());
    model.addAttribute("currentPage", page);
    model.addAttribute("totalPages", moviePage.getTotalPages());

    return "movie/index";
}


    @GetMapping("/movies/{id}")
    public String show(@PathVariable("id") Long id, Model model) {
        Movie movie = movieRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Movie not found"));
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
        if (movie.getName() == null || movie.getName().isEmpty() ||
            movie.getGenre() == null || movie.getGenre().isEmpty() ||
            movie.getDescription() == null || movie.getDescription().isEmpty() ||
            movie.getPrice() == null) {
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

    // ✅ Nuevo método para generar películas falsas
    @GetMapping("/movies/faker")
    public String generateFakeMovies() {
        Faker faker = new Faker();
        for (int i = 0; i < 10; i++) {
            Movie movie = new Movie();
            movie.setName(faker.book().title());
            movie.setDescription(faker.lorem().paragraph());
            movie.setGenre(faker.book().genre());
            movie.setPrice(faker.number().numberBetween(5, 20)); // 👈 Aquí corregido
            movie.setImagePath("uploads/default.jpg");
            movieRepository.save(movie);
        }
        return "redirect:/movies";
    }

}

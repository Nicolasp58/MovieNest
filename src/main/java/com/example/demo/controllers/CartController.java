package com.example.demo.controllers;

import com.example.demo.services.PaymentService;
import com.example.demo.models.Movie;
import com.example.demo.models.Payment;
import com.example.demo.models.User;
import com.example.demo.repositories.MovieRepository;
import com.example.demo.repositories.PaymentRepository;
import com.example.demo.repositories.UserRepository;

import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.security.core.Authentication;


import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@Controller
@RequestMapping("/cart")
public class CartController {
    @Autowired
    private PaymentService paymentService;

    @Autowired
    private MovieRepository movieRepository;

    @Autowired
    private PaymentRepository paymentRepository;

    @Autowired
    private UserRepository userRepository;

    @GetMapping
    public String index(HttpSession session, Model model) {
        List<Movie> movies = movieRepository.findAll();

        Map<Long, Integer> cartMovieData = (Map<Long, Integer>) session.getAttribute("cart_movie_data");
        Map<Long, Movie> cartMovies = new HashMap<>();
        double totalPrice = 0.0;

        if (cartMovieData != null) {
            for (Long id : cartMovieData.keySet()) {
                Optional<Movie> movie = movieRepository.findById(id);
                if (movie.isPresent()) {
                    cartMovies.put(id, movie.get());
                    totalPrice += movie.get().getPrice() * cartMovieData.get(id);
                }
            }
        }

        session.setAttribute("total_price", totalPrice);
        model.addAttribute("title", "Cart - MovieNest");
        model.addAttribute("subtitle", "Shopping Cart");
        model.addAttribute("movies", movies);
        model.addAttribute("cartMovies", cartMovies);
        model.addAttribute("totalPrice", totalPrice);
        return "cart/index";
    }

    @PostMapping("/add/{id}")
    public String add(@PathVariable Long id, HttpSession session) {
        Optional<Movie> movie = movieRepository.findById(id);
        if (movie.isEmpty()) {
            return "redirect:/cart";
        }

        Map<Long, Integer> cartMovieData = (Map<Long, Integer>) session.getAttribute("cart_movie_data");
        if (cartMovieData == null) {
            cartMovieData = new HashMap<>();
        }

        cartMovieData.put(id, cartMovieData.getOrDefault(id, 0) + 1);
        session.setAttribute("cart_movie_data", cartMovieData);
        return "redirect:/cart";
    }

    @GetMapping("/removeAll")
    public String removeAll(HttpSession session) {
        session.removeAttribute("cart_movie_data");
        return "redirect:/cart";
    }

    @PostMapping("/checkout")
public String checkout(@RequestParam String paymentMethod, HttpSession session) {
    Authentication auth = SecurityContextHolder.getContext().getAuthentication();

    if (auth == null || !auth.isAuthenticated() || auth.getPrincipal().equals("anonymousUser")) {
        return "redirect:/auth/login";
    }

    String username = auth.getName();
    Optional<User> userOpt = userRepository.findByUsername(username);

    if (userOpt.isEmpty()) {
        return "redirect:/cart";
    }

    User user = userOpt.get();
    Map<Long, Integer> cartMovieData = (Map<Long, Integer>) session.getAttribute("cart_movie_data");

    if (cartMovieData == null || cartMovieData.isEmpty()) {
        return "redirect:/cart";
    }

    for (Long movieId : cartMovieData.keySet()) {
        Optional<Movie> movieOpt = movieRepository.findById(movieId);
        if (movieOpt.isPresent()) {
            Movie movie = movieOpt.get();

            Payment payment = new Payment();
            payment.setTotalAmount(movie.getPrice() * cartMovieData.get(movieId));
            payment.setPaymentMethod(paymentMethod);
            payment.setMovie(movie);
            paymentService.savePayment(payment);

            if (!user.getPurchasedMoviesIds().contains(movieId)) {
                user.addPurchasedMovie(movieId);
            }
        }
    }

    // Guardamos los cambios en la base de datos
    userRepository.save(user);

    session.removeAttribute("cart_movie_data");
    session.removeAttribute("total_price");

    return "redirect:/cart/confirmation";
}

    @GetMapping("/confirmation")
    public String confirmationPage(Model model) {
        model.addAttribute("message", "Payment successful!");
        return "cart/confirmation";
    }
}

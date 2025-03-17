package com.example.demo.controllers;
import com.example.demo.services.PaymentService;
import com.example.demo.models.Movie;
import com.example.demo.models.Payment;
import com.example.demo.repositories.MovieRepository;
import com.example.demo.repositories.PaymentRepository;
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
    private PaymentService paymentService;

    @Autowired
    private MovieRepository movieRepository;

    @Autowired
    private PaymentRepository paymentRepository;

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
        Double totalPrice = (Double) session.getAttribute("total_price");
        if (totalPrice == null || totalPrice == 0.0) {
            return "redirect:/cart";
        }

        Payment payment = new Payment();
        payment.setTotalAmount(totalPrice);
        payment.setPaymentMethod(paymentMethod);
        paymentService.savePayment(payment);

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

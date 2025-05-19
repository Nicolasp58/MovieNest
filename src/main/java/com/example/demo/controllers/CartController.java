package com.example.demo.controllers;

import com.example.demo.services.PaymentService;
import com.example.demo.models.Movie;
import com.example.demo.models.Payment;
import com.example.demo.models.User;
import com.example.demo.repositories.MovieRepository;
import com.example.demo.repositories.PaymentRepository;
import com.example.demo.repositories.UserRepository;
import com.example.demo.util.PdfGenerator;

import jakarta.servlet.http.HttpSession;
import jakarta.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.security.core.Authentication;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.*;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

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
    public void checkout(@RequestParam String paymentMethod,
                         HttpSession session,
                         HttpServletResponse response) throws IOException {

        Authentication auth = SecurityContextHolder.getContext().getAuthentication();

        if (auth == null || !auth.isAuthenticated() || auth.getPrincipal().equals("anonymousUser")) {
            response.sendRedirect("/auth/login");
            return;
        }

        String username = auth.getName();
        Optional<User> userOpt = userRepository.findByUsername(username);

        if (userOpt.isEmpty()) {
            response.sendRedirect("/cart");
            return;
        }

        User user = userOpt.get();
        Map<Long, Integer> cartMovieData = (Map<Long, Integer>) session.getAttribute("cart_movie_data");

        if (cartMovieData == null || cartMovieData.isEmpty()) {
            response.sendRedirect("/cart");
            return;
        }

        double total = 0.0;

        for (Long movieId : cartMovieData.keySet()) {
            Optional<Movie> movieOpt = movieRepository.findById(movieId);
            if (movieOpt.isPresent()) {
                Movie movie = movieOpt.get();

                Payment payment = new Payment();
                double amount = movie.getPrice() * cartMovieData.get(movieId);
                payment.setTotalAmount(amount);
                payment.setPaymentMethod(paymentMethod);
                payment.setMovie(movie);
                paymentService.savePayment(payment);

                total += amount;

                if (!user.getPurchasedMoviesIds().contains(movieId)) {
                    user.addPurchasedMovie(movieId);
                }
            }
        }

        userRepository.save(user);
        session.removeAttribute("cart_movie_data");
        session.removeAttribute("total_price");

        // ✅ Generar PDF y Excel
        Map<String, byte[]> files = PdfGenerator.generatePaymentDocuments(username, paymentMethod, total);

        // ✅ Empaquetar ZIP y enviar como descarga
        response.setContentType("application/zip");
        response.setHeader("Content-Disposition", "attachment; filename=\"comprobantes.zip\"");

        try (ByteArrayOutputStream baos = new ByteArrayOutputStream();
             ZipOutputStream zipOut = new ZipOutputStream(baos)) {

            for (Map.Entry<String, byte[]> entry : files.entrySet()) {
                ZipEntry zipEntry = new ZipEntry(entry.getKey());
                zipOut.putNextEntry(zipEntry);
                zipOut.write(entry.getValue());
                zipOut.closeEntry();
            }

            zipOut.finish();
            response.getOutputStream().write(baos.toByteArray());
            response.getOutputStream().flush();
        }
    }

    @GetMapping("/confirmation")
    public String confirmationPage(Model model) {
        model.addAttribute("message", "Payment successful!");
        return "cart/confirmation";
    }
}
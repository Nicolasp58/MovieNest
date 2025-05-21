package com.example.demo.controllers;

import com.example.demo.services.CurrencyConverterService;
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
    
    @Autowired
    private CurrencyConverterService currencyConverterService; // Nuevo: inyección del servicio de conversión

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
    public String checkout(@RequestParam String paymentMethod,
                           HttpSession session) throws IOException {
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

        Map<Long, Integer> cartData = (Map<Long, Integer>) session.getAttribute("cart_movie_data");
        if (cartData == null || cartData.isEmpty()) {
            return "redirect:/cart";
        }

        double total = 0.0;
        for (Long movieId : cartData.keySet()) {
            Optional<Movie> movieOpt = movieRepository.findById(movieId);
            if (movieOpt.isPresent()) {
                Movie movie = movieOpt.get();
                double amount = movie.getPrice() * cartData.get(movieId);
                Payment payment = new Payment();
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
        
        // Obtener la moneda seleccionada por el usuario
        String selectedCurrency = (String) session.getAttribute("selectedCurrency");
        if (selectedCurrency == null) {
            selectedCurrency = "USD"; // Valor por defecto
        }
        
        // 1) Generar PDF y Excel con la moneda seleccionada
        Map<String, byte[]> files = PdfGenerator.generatePaymentDocuments(
            username, 
            paymentMethod, 
            total, 
            selectedCurrency, 
            currencyConverterService
        );

        // 2) Empaquetar en ZIP
        byte[] zipBytes;
        try (ByteArrayOutputStream baos = new ByteArrayOutputStream();
             ZipOutputStream zipOut = new ZipOutputStream(baos)) {
            for (Map.Entry<String, byte[]> entry : files.entrySet()) {
                ZipEntry zipEntry = new ZipEntry(entry.getKey());
                zipOut.putNextEntry(zipEntry);
                zipOut.write(entry.getValue());
                zipOut.closeEntry();
            }
            zipOut.finish();
            zipBytes = baos.toByteArray();
        }

        // 3) Guardar ZIP en sesión
        session.setAttribute("paymentZip", zipBytes);

        // 4) Redirigir a /cart/confirmation
        return "redirect:/cart/confirmation";
    }

    @GetMapping("/downloadZip")
    public void downloadZip(HttpSession session, HttpServletResponse response) throws IOException {
        byte[] zipBytes = (byte[]) session.getAttribute("paymentZip");
        if (zipBytes == null) {
            response.sendRedirect("/cart");
            return;
        }

        response.setContentType("application/zip");
        response.setHeader("Content-Disposition", "attachment; filename=\"comprobantes.zip\"");
        response.getOutputStream().write(zipBytes);
        response.getOutputStream().flush();

        // (Opcional) quitar de sesión
        session.removeAttribute("paymentZip");
    }

    @GetMapping("/confirmation")
    public String confirmationPage() {
        return "cart/confirmation";
    }
}
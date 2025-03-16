package com.example.demo.controllers;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;

@Controller
public class HomeController {

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
}

package com.example.demo.controllers;

import com.example.demo.services.CurrencyConverterService;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ModelAttribute;

@ControllerAdvice
public class GlobalModelAttributes {

    @Autowired
    private CurrencyConverterService currencyConverterService;

    @ModelAttribute
    public void addCurrencyAttributes(Model model, HttpSession session) {
        // Establecer USD como moneda predeterminada si no hay ninguna seleccionada
        String selectedCurrency = (String) session.getAttribute("selectedCurrency");
        if (selectedCurrency == null) {
            selectedCurrency = "USD";
            session.setAttribute("selectedCurrency", selectedCurrency);
        }
        
        model.addAttribute("currencyService", currencyConverterService);
        model.addAttribute("selectedCurrency", selectedCurrency);
    }
}
package com.example.demo.controllers;

import com.example.demo.services.CurrencyConverterService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.Map;

@Controller
public class CurrencyController {

    @Autowired
    private CurrencyConverterService currencyConverterService;

    @GetMapping("/api/currencies")
    @ResponseBody
    public Map<String, Double> getAvailableCurrencies() {
        return currencyConverterService.getAvailableCurrencies();
    }
    
    @GetMapping("/api/convert")
    @ResponseBody
    public double convertPrice(
            @RequestParam double amount,
            @RequestParam String from,
            @RequestParam String to) {
        return currencyConverterService.convertPrice(amount, from, to);
    }
    
    @GetMapping("/change-currency")
    public String changeCurrency(
            @RequestParam("currency") String currency,
            HttpServletRequest request,
            HttpServletResponse response) {

        // Guardar la moneda seleccionada en la sesión
        HttpSession session = request.getSession();
        session.setAttribute("selectedCurrency", currency);

        String referer = request.getHeader("Referer"); 
        return "redirect:" + (referer != null ? referer : "/");
    }
}
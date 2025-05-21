package com.example.demo.util;

import com.example.demo.services.CurrencyConverterService;
import org.springframework.stereotype.Component;

import java.text.NumberFormat;
import java.util.Currency;
import java.util.Locale;

@Component
public class CurrencyFormatter {
    
    private final CurrencyConverterService currencyConverterService;
    
    public CurrencyFormatter(CurrencyConverterService currencyConverterService) {
        this.currencyConverterService = currencyConverterService;
    }
    
    public String format(double amount, String fromCurrency, String toCurrency) {
        // Convertir el precio
        double convertedAmount = currencyConverterService.convertPrice(amount, fromCurrency, toCurrency);
        
        // Formatear según la moneda
        NumberFormat formatter = NumberFormat.getCurrencyInstance();
        formatter.setCurrency(Currency.getInstance(toCurrency));
        
        return formatter.format(convertedAmount);
    }
}
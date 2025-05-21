package com.example.demo.services;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.LinkedHashMap;
import java.util.Map;

@Service
public class CurrencyConverterService {

    private final RestTemplate restTemplate;
    private final String apiKey;
    
    // Caché de tasas de cambio para reducir llamadas a la API
    private Map<String, Double> ratesCache = new LinkedHashMap<>();
    private long lastFetchTime = 0;
    private static final long CACHE_DURATION = 3600000; // 1 hora en milisegundos

    public CurrencyConverterService(
            @Value("${currency.api.key}") String apiKey) {
        this.restTemplate = new RestTemplate();
        this.apiKey = apiKey;
    }

    public double convertPrice(double amount, String from, String to) {
        if (from.equals(to)) {
            return amount;
        }
        
        refreshRatesIfNeeded();
        
        // Si la moneda base es USD, usamos directamente la tasa
        if (from.equals("USD")) {
            return amount * ratesCache.getOrDefault(to, 1.0);
        } 
        // Si la moneda objetivo es USD, dividimos por la tasa de la moneda base
        else if (to.equals("USD")) {
            return amount / ratesCache.getOrDefault(from, 1.0);
        } 
        // Para otras conversiones, convertimos a USD primero y luego a la moneda objetivo
        else {
            double amountInUSD = amount / ratesCache.getOrDefault(from, 1.0);
            return amountInUSD * ratesCache.getOrDefault(to, 1.0);
        }
    }
    
    public Map<String, Double> getAvailableCurrencies() {
        refreshRatesIfNeeded();
        return ratesCache;
    }
    
    private void refreshRatesIfNeeded() {
        long currentTime = System.currentTimeMillis();
        
        // Si el caché está vacío o ha pasado el tiempo de expiración
        if (ratesCache.isEmpty() || (currentTime - lastFetchTime > CACHE_DURATION)) {
            fetchLatestRates();
            lastFetchTime = currentTime;
        }
    }
    
    private void fetchLatestRates() {
        String url = "https://v6.exchangerate-api.com/v6/" + apiKey + "/latest/USD";
        try {
            Map<String, Object> response = restTemplate.getForObject(url, Map.class);
            
            if (response != null && "success".equals(response.get("result"))) {
                Map<String, Double> rates = (Map<String, Double>) response.get("conversion_rates");
                
                // Guardar solo las monedas más comunes para no sobrecargar la interfaz
                ratesCache.clear();
                ratesCache.put("USD", 1.0); // USD es nuestra moneda base
                ratesCache.put("EUR", rates.get("EUR"));
                ratesCache.put("GBP", rates.get("GBP"));
                ratesCache.put("JPY", rates.get("JPY"));
                ratesCache.put("CNY", rates.get("CNY"));
                ratesCache.put("AUD", rates.get("AUD"));
                ratesCache.put("CAD", rates.get("CAD"));
                ratesCache.put("COP", rates.get("COP"));
                ratesCache.put("MXN", rates.get("MXN"));
                ratesCache.put("BRL", rates.get("BRL"));
            } else {
                throw new RuntimeException("Failed to fetch currency rates");
            }
        } catch (Exception e) {
            throw new RuntimeException("Error accessing currency API", e);
        }
    }
}
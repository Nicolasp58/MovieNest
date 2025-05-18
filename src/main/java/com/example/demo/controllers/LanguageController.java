package com.example.demo.controllers;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.LocaleResolver;
import org.springframework.web.servlet.support.RequestContextUtils;

import java.util.Locale;

@Controller
public class LanguageController {

    @GetMapping("/change-language")
    public String changeLanguage(
            @RequestParam("lang") String lang,
            HttpServletRequest request,
            HttpServletResponse response) {

        LocaleResolver localeResolver = RequestContextUtils.getLocaleResolver(request);
        if (localeResolver != null) {
            Locale newLocale = Locale.forLanguageTag(lang);
            localeResolver.setLocale(request, response, newLocale);
        }

        String referer = request.getHeader("Referer"); // para redirigir a la página anterior
        return "redirect:" + (referer != null ? referer : "/");
    }
}

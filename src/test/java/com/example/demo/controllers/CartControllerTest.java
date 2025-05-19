package com.example.demo.controllers;

import com.example.demo.models.Movie;
import com.example.demo.repositories.MovieRepository;
import com.example.demo.repositories.PaymentRepository;
import com.example.demo.repositories.UserRepository;
import com.example.demo.services.PaymentService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.mock.web.MockHttpSession;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

public class CartControllerTest {

    @Mock
    private PaymentService paymentService;

    @Mock
    private MovieRepository movieRepository;

    @Mock
    private PaymentRepository paymentRepository;

    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private CartController cartController;

    private MockMvc mockMvc;
    private MockHttpSession session;

    @BeforeEach
    public void setup() {
        MockitoAnnotations.openMocks(this);
        mockMvc = MockMvcBuilders.standaloneSetup(cartController).build();
        session = new MockHttpSession();
    }

    @Test
    public void testAddToCart() throws Exception {
        
        Movie testMovie = new Movie("Test Movie", "Description", "Action", 15);
        
        when(movieRepository.findById(1L)).thenReturn(Optional.of(testMovie));

        mockMvc.perform(post("/cart/add/1").session(session))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/cart"));

        Map<Long, Integer> cartData = (Map<Long, Integer>) session.getAttribute("cart_movie_data");
        assert cartData != null && cartData.containsKey(1L);
        assert cartData.get(1L) == 1;
    }
}
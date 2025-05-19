package com.example.demo.controllers;

import com.example.demo.models.Movie;
import com.example.demo.repositories.MovieRepository;
import com.example.demo.interfaces.ImageStorage;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.util.ArrayList;
import java.util.List;

import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

public class MovieControllerTest {

    @Mock
    private MovieRepository movieRepository;

    @Mock
    private ImageStorage imageStorage;

    @InjectMocks
    private MovieController movieController;

    private MockMvc mockMvc;

    @BeforeEach
    public void setup() {
        MockitoAnnotations.openMocks(this);
        mockMvc = MockMvcBuilders.standaloneSetup(movieController).build();
    }

    @Test
    public void testIndexMethod() throws Exception {

        List<Movie> movieList = new ArrayList<>();
        Movie movie1 = new Movie("Test Movie 1", "Description 1", "Action", 10);
        Movie movie2 = new Movie("Test Movie 2", "Description 2", "Comedy", 15);
        movieList.add(movie1);
        movieList.add(movie2);

        when(movieRepository.findAll()).thenReturn(movieList);

        mockMvc.perform(get("/movies"))
                .andExpect(status().isOk())
                .andExpect(view().name("movie/index"))
                .andExpect(model().attributeExists("movies"))
                .andExpect(model().attributeExists("title"));

        verify(movieRepository, times(1)).findAll();
    }
}
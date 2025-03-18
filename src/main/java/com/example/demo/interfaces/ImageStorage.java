package com.example.demo.interfaces;

import org.springframework.web.multipart.MultipartFile;

public interface ImageStorage {
    void store(MultipartFile file, String filename);
}
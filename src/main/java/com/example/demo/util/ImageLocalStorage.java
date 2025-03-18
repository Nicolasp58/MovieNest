package com.example.demo.util;

import com.example.demo.interfaces.ImageStorage;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

@Component
public class ImageLocalStorage implements ImageStorage {
    private static final String STORAGE_DIR = "src/main/resources/static/uploads/";

    @Override
    public void store(MultipartFile file, String filename) {
        if (file != null && !file.isEmpty()) {
            try {
                // Crear el directorio si no existe
                Path storageDir = Paths.get(STORAGE_DIR);
                if (!Files.exists(storageDir)) {
                Files.createDirectories(storageDir);
                }
                // Guardar la imagen 
                Path destinationFile = storageDir.resolve(filename).normalize().toAbsolutePath();
                System.out.println("Guardando imagen en: " + destinationFile);
                Files.copy(file.getInputStream(), destinationFile);
            } catch (IOException e) {
                e.printStackTrace();
                // Aquí se podría lanzar una excepción personalizada
            }
        }
    }
}
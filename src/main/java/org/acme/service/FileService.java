package org.acme.service;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

import jakarta.enterprise.context.ApplicationScoped;

@ApplicationScoped
public class FileService {

    public void createDirectoryWithTimestamp(String fileName,String content) throws IOException {
        // Obtener el directorio de trabajo actual
        Path currentDir = Paths.get("").toAbsolutePath();

        // Formatear la fecha y hora actual
        //DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd_HH-mm-ss");
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yy-HH:mm");
        String timestamp = LocalDateTime.now().format(formatter);

        String sanitizedTimestamp = timestamp.replace("/", "").replace(":", "").replace("-", "");

        // Crear el nuevo directorio con el timestamp
        Path newDir = currentDir.resolve("Logs_" + sanitizedTimestamp);
        Files.createDirectories(newDir);

        // Crear un archivo dentro del nuevo directorio
        Path filePath = newDir.resolve(fileName+".log");
        
        Files.writeString(filePath, content);
    }
}
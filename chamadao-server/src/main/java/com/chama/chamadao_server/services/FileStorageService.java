package com.chama.chamadao_server.services;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import jakarta.annotation.PostConstruct;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
public class FileStorageService {

    @Value("${project.image}")
    private String imageUploadDir;

    private Path fileStorageLocation;

    @PostConstruct
    public void init(){
        this.fileStorageLocation = Paths.get(imageUploadDir).toAbsolutePath().normalize();
        try{
            Files.createDirectories(this.fileStorageLocation);
        } catch (Exception e){
            throw new RuntimeException("Could not create the directory where the uploaded files will be stored", e);
        }
    }

    /**
     * Store a file in the file storage location
     * @param file the name of the file to store
     * @param prefix a prefix to add to the file name
     * @return the stored file name
     */
    public String storeFile(MultipartFile file, String prefix){
        if (file == null || file.isEmpty()) {
            throw new IllegalArgumentException("File is empty or null");
            
        }
        String contentType = file.getContentType();
        if (contentType == null || !contentType.startsWith("image/")) {
            throw new IllegalArgumentException("File is not an image");
        }

        if (file.getSize() > 5 * 1024 * 1024) { // 5MB
            throw new IllegalArgumentException("File is too large");
            
        }
        //get the original file name
        String originalFileName = file.getOriginalFilename();
        String extension = originalFileName != null ? originalFileName.substring(originalFileName.lastIndexOf('.')) : ".jpg";
        String newFileName = prefix + UUID.randomUUID().toString() + extension;
        Path targetLocation = this.fileStorageLocation.resolve(newFileName);

        try{
            Files.copy(file.getInputStream(), targetLocation, StandardCopyOption.REPLACE_EXISTING);
            log.info("File stored at: {}", newFileName);
            return newFileName;
        } catch(IOException ex){
            throw new RuntimeException("Could not store file " + newFileName + ". Please try again!", ex);
        }
    }

    /**
     * Load a file from the file storage location
     * @param fileName the name of the file to load
     * @return the path to the file
     */
    public Resource loadFileAsResource(String fileName){
        try{
            Path filePath = this.fileStorageLocation.resolve(fileName).normalize();
            Resource resource = new UrlResource(filePath.toUri());
            if (resource.exists()) {
                return resource;
            } else {
                throw new RuntimeException("File not found " + fileName);
            }
        } catch (Exception e){
            throw new RuntimeException("Could not load file " + fileName, e);
        }
    }

    /**
     * Determine content type of a file
     * @param filename the file to determine content type
     * @return the content type of the file
     */

     public String getContentType(String filename){
        if (filename == null || !filename.contains(".")) {
            return "application/octet-stream"; // Default content type
        }

        String extension = filename.substring(filename.lastIndexOf(".") + 1).toLowerCase();
        return switch (extension) {
            case "png" -> "image/png";
            case "jpg", "jpeg" -> "image/jpeg";
            case "gif" -> "image/gif";
            case "bmp" -> "image/bmp";
            case "webp" -> "image/webp";
            default -> "application/octet-stream";
        };
     }
    
}

package _blog.demo.service;


import _blog.demo.exceptions.ResourceNotFoundException;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.UUID;

import javax.management.RuntimeErrorException;

@Service
public class FileStorageService {
    @Value("${file.upload-dir:updoads}")
    private String uploadDir;
    public void init(){
        try{
            Path uploadPath = Paths.get(uploadDir);
            if (!Files.exists(uploadPath)){
                Files.createDirectories(uploadPath);
            }
        }catch (IOException e) {
            throw new RuntimeException("the file could not be created", e);
        }
    }

    public String storeFile(MultipartFile file , String type){
        init();
        if (file.isEmpty()){
            throw new IllegalArgumentException("the file is empty");
        }
        String contenType = file.getContentType();
        if (contenType == null){
             throw new IllegalArgumentException("unknown or unsupported format");

        }
        if (type.equals("IMAGE")) {
            if (!contenType.startsWith("image/")){
                throw new IllegalArgumentException("File must be an image");
            }
             if (file.getSize() > 5 * 1024 * 1024) {
                throw new IllegalArgumentException("Image file size must not exceed 5MB");
            }
        }else if (type.equals("VIDEO")) {
             if (!contenType.startsWith("video/")) {
                throw new IllegalArgumentException("File must be a video");
            }
             if (file.getSize() > 50 * 1024 * 1024) {
                throw new IllegalArgumentException("Video file size must not exceed 50MB");
            }
        }
        try {
             String originalFilename = file.getOriginalFilename();
             String extension = originalFilename != null && originalFilename.contains(".")
                    ? originalFilename.substring(originalFilename.lastIndexOf("."))
                   : "";
            String filename = UUID.randomUUID().toString() + extension;
            Path targetLocation = Paths.get(uploadDir).resolve(filename);
            Files.copy(file.getInputStream(),targetLocation, StandardCopyOption.REPLACE_EXISTING);
            return filename;
            

        }catch (IOException e) {
            throw new RuntimeException("Faild to stor the file try agin later",e );
        }   
    }
    public byte[] loadFile(String filename){
        try {
            Path filePath = Paths.get(uploadDir).resolve(filename).normalize();
            if (!Files.exists(filePath)) {
                throw new ResourceNotFoundException("File not found: " + filename);
            }
            return Files.readAllBytes(filePath);
            
        }catch (IOException e) {
            throw new RuntimeException("Failed to load file", e);
        }
    }
    public void deletFile(String filename){
        try{
            if (filename == null || filename.isEmpty()){
                return;
            }
            Path filePath = Paths.get(uploadDir).resolve(filename).resolve(filename).normalize();
            Files.delete(filePath);
        } catch (IOException e){
            throw new RuntimeException("failed to delette file try agiiian later",e);

        }
    }
    public String getFilePath(String name){
        return Paths.get(uploadDir).resolve(name).toString();
    }
}

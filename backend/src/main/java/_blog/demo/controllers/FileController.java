package _blog.demo.controllers;

import _blog.demo.service.FileStorageService;
import lombok.AllArgsConstructor;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/files")
@AllArgsConstructor
@CrossOrigin(origins = {"http://localhost:4200", "http://localhost:3000"})  
public class FileController {
    private FileStorageService fileStorageService;

    @GetMapping("/{filename:.+}")
    public ResponseEntity<Resource> downloadFile(@PathVariable String filename) {
        try {
            byte[] fileData = fileStorageService.loadFile(filename);
            
            ByteArrayResource resource = new ByteArrayResource(fileData);
            
            // Determine content type based on file extension
            String contentType = "application/octet-stream";
            String lowerFilename = filename.toLowerCase();
            
            if (lowerFilename.endsWith(".jpg") || lowerFilename.endsWith(".jpeg")) {
                contentType = "image/jpeg";
            } else if (lowerFilename.endsWith(".png")) {
                contentType = "image/png";
            } else if (lowerFilename.endsWith(".gif")) {
                contentType = "image/gif";
            } else if (lowerFilename.endsWith(".webp")) {
                contentType = "image/webp";
            } else if (lowerFilename.endsWith(".mp4")) {
                contentType = "video/mp4";
            } else if (lowerFilename.endsWith(".webm")) {
                contentType = "video/webm";
            } else if (lowerFilename.endsWith(".mov")) {
                contentType = "video/quicktime";
            }

            return ResponseEntity.ok()
                    .contentType(MediaType.parseMediaType(contentType))
                    .header(HttpHeaders.CONTENT_DISPOSITION, "inline; filename=\"" + filename + "\"")
                    .header(HttpHeaders.ACCESS_CONTROL_ALLOW_ORIGIN, "*")  
                    .body(resource);
                    
        } catch (Exception e) {
            System.err.println("Error loading file: " + filename);
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(null);
        }
    }
}
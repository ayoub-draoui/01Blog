package _blog.demo.service;

import _blog.demo.exceptions.ResourceNotFoundException;
import org.apache.tika.Tika;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.Arrays;
import java.util.List;
import java.util.UUID;

@Service
public class FileStorageService {
    
    @Value("${file.upload-dir:uploads}")
    private String uploadDir;
    
    private final Tika tika = new Tika();
    
    private static final List<String> ALLOWED_IMAGE_TYPES = Arrays.asList(
        "image/jpeg",
        "image/png",
        "image/gif",
        "image/webp",
        "image/bmp"
    );
    
    private static final List<String> ALLOWED_VIDEO_TYPES = Arrays.asList(
        "video/mp4",
        "video/webm",
        "video/quicktime",
        "video/x-msvideo",
        "video/mpeg"
    );
    
    public void init() {
        try {
            Path uploadPath = Paths.get(uploadDir);
            if (!Files.exists(uploadPath)) {
                Files.createDirectories(uploadPath);
            }
        } catch (IOException e) {
            throw new RuntimeException("The upload directory could not be created", e);
        }
    }

   
    public String storeFile(MultipartFile file, String type) {
        init();
        
        // Basic validation
        if (file.isEmpty()) {
            throw new IllegalArgumentException("The file is empty");
        }
        
        String detectedMimeType;
        try (InputStream inputStream = file.getInputStream()) {
            detectedMimeType = tika.detect(inputStream);
        } catch (IOException e) {
            throw new RuntimeException("Failed to detect file type", e);
        }
        
        if (detectedMimeType == null || detectedMimeType.isEmpty()) {
            throw new IllegalArgumentException("Unknown or unsupported file format");
        }
        
        // Validate based on expected type
        if (type.equals("IMAGE")) {
            if (!ALLOWED_IMAGE_TYPES.contains(detectedMimeType)) {
                throw new IllegalArgumentException(
                    "File must be a valid image. Detected type: " + detectedMimeType
                );
            }
            
            if (file.getSize() > 5 * 1024 * 1024) {
                throw new IllegalArgumentException("Image file size must not exceed 5MB");
            }
            
        } else if (type.equals("VIDEO")) {
            if (!ALLOWED_VIDEO_TYPES.contains(detectedMimeType)) {
                throw new IllegalArgumentException(
                    "File must be a valid video. Detected type: " + detectedMimeType
                );
            }
            
            if (file.getSize() > 50 * 1024 * 1024) {
                throw new IllegalArgumentException("Video file size must not exceed 50MB");
            }
            
        } else {
            throw new IllegalArgumentException("Invalid type specified. Must be IMAGE or VIDEO");
        }
        
        try {
            String originalFilename = file.getOriginalFilename();
            String extension = getExtensionFromMimeType(detectedMimeType);
            
            if (originalFilename != null && originalFilename.contains(".")) {
                String originalExtension = originalFilename.substring(originalFilename.lastIndexOf("."));
                if (isExtensionValidForMimeType(originalExtension, detectedMimeType)) {
                    extension = originalExtension;
                }
            }
            
            String filename = UUID.randomUUID().toString() + extension;
            Path targetLocation = Paths.get(uploadDir).resolve(filename);
            
            Files.copy(file.getInputStream(), targetLocation, StandardCopyOption.REPLACE_EXISTING);
            
            return filename;
            
        } catch (IOException e) {
            throw new RuntimeException("Failed to store the file. Please try again later", e);
        }
    }
 
    public byte[] loadFile(String filename) {
        try {
            Path filePath = Paths.get(uploadDir).resolve(filename).normalize();
            
            if (!Files.exists(filePath)) {
                throw new ResourceNotFoundException("File not found: " + filename);
            }
            
            return Files.readAllBytes(filePath);
            
        } catch (IOException e) {
            throw new RuntimeException("Failed to load file", e);
        }
    }

    
    public void deletFile(String filename) {
        try {
            if (filename == null || filename.isEmpty()) {
                return;
            }
            
            // Fixed: removed duplicate .resolve(filename)
            Path filePath = Paths.get(uploadDir).resolve(filename).normalize();
            
            if (Files.exists(filePath)) {
                Files.delete(filePath);
            }
            
        } catch (IOException e) {
            throw new RuntimeException("Failed to delete file. Please try again later", e);
        }
    }

   
    public String getFilePath(String name) {
        return Paths.get(uploadDir).resolve(name).toString();
    }
    
   
    private String getExtensionFromMimeType(String mimeType) {
        switch (mimeType) {
            // Images
            case "image/jpeg":
                return ".jpg";
            case "image/png":
                return ".png";
            case "image/gif":
                return ".gif";
            case "image/webp":
                return ".webp";
            case "image/bmp":
                return ".bmp";
            
            // Videos
            case "video/mp4":
                return ".mp4";
            case "video/webm":
                return ".webm";
            case "video/quicktime":
                return ".mov";
            case "video/x-msvideo":
                return ".avi";
            case "video/mpeg":
                return ".mpeg";
            
            default:
                return "";
        }
    }
    
    
    private boolean isExtensionValidForMimeType(String extension, String mimeType) {
        String normalizedExtension = extension.toLowerCase();
        
        switch (mimeType) {
            case "image/jpeg":
                return normalizedExtension.equals(".jpg") || normalizedExtension.equals(".jpeg");
            case "image/png":
                return normalizedExtension.equals(".png");
            case "image/gif":
                return normalizedExtension.equals(".gif");
            case "image/webp":
                return normalizedExtension.equals(".webp");
            case "image/bmp":
                return normalizedExtension.equals(".bmp");
            case "video/mp4":
                return normalizedExtension.equals(".mp4");
            case "video/webm":
                return normalizedExtension.equals(".webm");
            case "video/quicktime":
                return normalizedExtension.equals(".mov");
            case "video/x-msvideo":
                return normalizedExtension.equals(".avi");
            case "video/mpeg":
                return normalizedExtension.equals(".mpeg") || normalizedExtension.equals(".mpg");
            default:
                return false;
        }
    }
}
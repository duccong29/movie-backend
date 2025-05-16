package movies.utils;

import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.UUID;

@Component
public class FileUtils {
    /**
     * Generate a unique filename for a file
     *
     * @param originalFilename the original filename
     * @return a unique filename
     */
    public String generateUniqueFilename(String originalFilename) {
        String extension = "";
        if (originalFilename.contains(".")) {
            extension = originalFilename.substring(originalFilename.lastIndexOf("."));
        }
        return UUID.randomUUID().toString() + extension;
    }

    /**
     * Save a file to a directory
     *
     * @param file the file to save
     * @param targetPath the path to save the file to
     * @param filename the name to save the file as
     * @return the path where the file was saved
     * @throws IOException if an I/O error occurs
     */
    public Path saveFile(MultipartFile file, Path targetPath, String filename) throws IOException {
        // Create the directory if it doesn't exist
        if (!Files.exists(targetPath)) {
            Files.createDirectories(targetPath);
        }

        // Save the file
        Path targetLocation = targetPath.resolve(filename);
        Files.copy(file.getInputStream(), targetLocation, StandardCopyOption.REPLACE_EXISTING);

        return targetLocation;
    }

    /**
     * Delete a file
     *
     * @param filePath the path of the file to delete
     * @return true if the file was deleted, false otherwise
     */
    public boolean deleteFile(String filePath) {
        try {
            Path path = Paths.get(filePath);
            return Files.deleteIfExists(path);
        } catch (IOException e) {
            return false;
        }
    }

    /**
     * Check if a file exists
     *
     * @param filePath the path of the file to check
     * @return true if the file exists, false otherwise
     */
    public boolean fileExists(String filePath) {
        Path path = Paths.get(filePath);
        return Files.exists(path);
    }
}

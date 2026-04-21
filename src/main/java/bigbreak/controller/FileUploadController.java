package bigbreak.controller;

import bigbreak.ShootingDay;
import bigbreak.service.ScriptProcessingService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;

//React (using Vite) usually runs on 5173 port
@CrossOrigin(origins = "http://localhost:5173")
@RestController
@RequestMapping("/api")
public class FileUploadController {

    @Autowired
    ScriptProcessingService service;
    @PostMapping("/upload-script")
    public ResponseEntity<?> uploadScript(@RequestParam("file") MultipartFile file) throws IllegalAccessException {
        if (file.getOriginalFilename() == null || !file.getOriginalFilename().toLowerCase().endsWith(".txt")) {
            return ResponseEntity.badRequest().body("Only .txt files are allowed.");
        }
        try {
            String scriptText = new String(file.getBytes());
            var result = service.processScript(scriptText);
            return ResponseEntity.ok(result);
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(500).body("Failed to read file");
        }
    }
}

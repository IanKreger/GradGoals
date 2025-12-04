package com.gradgoals;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.nio.file.Files;
import java.nio.file.Path;
import java.util.*;

@RestController
@RequestMapping
@CrossOrigin(origins = "*")
public class ResourceController {

    // ------------------------
    // GET AVAILABLE RESOURCES
    // ------------------------
    @GetMapping
    public List<Map<String, String>> getResources() {
        ResourceManager manager = new ResourceManager();
        manager.loadDefaultResources();

        List<Map<String, String>> result = new ArrayList<>();
        for (ResourceManager.ResourceItem item : manager.getAllResources()) {
            Map<String, String> map = new HashMap<>();
            map.put("title", item.getTitle());
            map.put("type", item.getType());
            map.put("url", item.getUrl());
            result.add(map);
        }
        return result;
    }
}
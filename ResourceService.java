package com.example.website.core;

import org.springframework.core.io.Resource;

public interface ResourceService {
    Resource load(String name);
}

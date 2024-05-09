package com.github.wesleybritovlk.healthmanager.app;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api")
public class ApiController {
        @Value("${app.name}")
        private String name;
        @Value("${app.version}")
        private String version;
        @Value("${app.documentation}")
        private String documentation;
        @Value("${app.repository}")
        private String repository;

        public record Response(String name, String version, String documentation, String repository) {
        }

        @GetMapping
        public ResponseEntity<Object> getHomeApi() {
                return ResponseEntity.ok(new Response(name, version, documentation, repository));
        }
}

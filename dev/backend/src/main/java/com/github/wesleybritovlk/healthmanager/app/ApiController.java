package com.github.wesleybritovlk.healthmanager.app;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletRequest;

@Tag(name = "api", description = "Access API base info")
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

        @Schema(name = "ApiResponse", title = "ApiResponse")
        record Response(String name, 
                        @Schema(example = "0.0.0") String version, 
                        @Schema(example = "http://example.com") String documentation,
                        @Schema(example = "https://github.com") String repository) {
        }

        @GetMapping
        @Operation(summary = "Returns the basic info of the API")
        @ApiResponse(responseCode = "200", content = @Content(schema = @Schema(ref = "ApiResponse", implementation = Response.class)))
        public ResponseEntity<Object> getHomeApi(HttpServletRequest request) {
                String docFormat = "%s%s".formatted(request.getRequestURL(), documentation);
                return ResponseEntity.ok(new Response(name, version, docFormat, repository));
        }
}

package com.lprakapovich.blog.publicationservice.api;

import com.lprakapovich.blog.publicationservice.api.dto.PublicationDto;
import com.lprakapovich.blog.publicationservice.service.PublicationService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.net.URI;
import java.security.Principal;

@RestController
@RequestMapping("/publications")
@RequiredArgsConstructor
class PublicationRestEndpoint {

    private final PublicationService publicationService;

    @GetMapping()
    ResponseEntity<Void> getPublication(Principal principal) {

        String username = principal.getName();
        return ResponseEntity.ok().build();
    }

    @PostMapping
    ResponseEntity<URI> createPublication(@Valid @RequestBody PublicationDto publicationDto) {
        return ResponseEntity.ok().build();
    }
}

package com.lprakapovich.blog.publicationservice.api;

import com.lprakapovich.blog.publicationservice.api.dto.PublicationDto;
import com.lprakapovich.blog.publicationservice.service.PublicationService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;

@RestController
@RequestMapping("/publications")
@RequiredArgsConstructor
public class PublicationRestEndpoint {

    private final PublicationService publicationService;

    @GetMapping("/{id}")
    ResponseEntity<Void> getPublication(@RequestHeader("Authorizaion") String auth, @PathVariable Long id) {

        return ResponseEntity.ok().build();
    }

    @PostMapping
    ResponseEntity<Void> createPublication(@Valid @RequestBody PublicationDto publicationDto) {
        return ResponseEntity.ok().build();
    }
}

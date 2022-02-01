package com.lprakapovich.blog.publicationservice.api;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.lprakapovich.blog.publicationservice.model.Publication;
import com.lprakapovich.blog.publicationservice.model.Status;
import com.lprakapovich.blog.publicationservice.service.BlogService;
import com.lprakapovich.blog.publicationservice.service.PublicationService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.net.URI;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

import static com.lprakapovich.blog.publicationservice.util.BlogIdResolver.resolveBlogIdFromPrincipal;

@RestController
@RequestMapping("/publications")
@RequiredArgsConstructor
class PublicationRestEndpoint {

    private final PublicationService publicationService;
    private final BlogService blogService;
    private final ObjectMapper objectMapper;

    @PostMapping
    public ResponseEntity<URI> createPublication(@Valid @RequestBody PublicationDto publicationDto) {
        String blogId = resolveBlogId();
        Publication publication = map(publicationDto);
        long publicationId = publicationService.createPublication(publication, blogId);
        return ResponseEntity.created(URI.create(String.valueOf(publicationId))).build();
    }

    @GetMapping("/{id}")
    public ResponseEntity<PublicationDto> getPublication(@PathVariable long id) {
        Publication publication = publicationService.getById(id, resolveBlogId());
        PublicationDto publicationDto = map(publication);
        return ResponseEntity.ok(publicationDto);
    }

    @GetMapping
    public ResponseEntity<List<PublicationDto>> getPublications(@RequestParam(defaultValue = "0") int page,
                                                                @RequestParam(defaultValue = "3") int size,
                                                                @RequestParam(required = false) Status status,
                                                                @RequestParam(required = false) Long categoryId) {
        String blogId = resolveBlogId();
        PageRequest p = PageRequest.of(page, size, Sort.by("createdDateTime").descending());
        List<Publication> publications;

        if (Objects.nonNull(status)) {
            publications = publicationService.getAllByBlogIdAndStatus(blogId, status, p);
        } else if (Objects.nonNull(categoryId)) {
            publications = publicationService.getAllByBlogIdAndCategory(blogId, categoryId, p);
        } else {
            publications = publicationService.getAllByBlogId(blogId, p);
        }
        return ResponseEntity.ok(map(publications));
    }

    // TODO add sorting by publicationDate
    @GetMapping("/subscriptions")
    public ResponseEntity<List<PublicationDto>> getPublicationsFromSubscriptions(@RequestParam(defaultValue = "0") int page,
                                                                                 @RequestParam(defaultValue = "3") int size) {
        String blogId = resolveBlogId();
        List<Publication> publications = publicationService.getPublicationsFromSubscriptions(blogId, PageRequest.of(page, size));
        return ResponseEntity.ok(map(publications));
    }

    @DeleteMapping("{id}")
    public ResponseEntity<Void> deletePublication(@PathVariable long id) {
        String blogId = resolveBlogId();
        publicationService.deleteById(id, blogId);
        return ResponseEntity.noContent().build();
    }

    @PutMapping("/{id}/{categoryId}")
    public ResponseEntity<PublicationDto> assignPublicationToCategory(@PathVariable(value = "id") long publicationId, @PathVariable long categoryId) {
        String blogId = resolveBlogId();
        Publication updatedPublication = publicationService.assignPublicationToCategory(blogId, publicationId, categoryId);
        PublicationDto updatedPublicationDto = map(updatedPublication);
        return ResponseEntity.ok().body(updatedPublicationDto);
    }

    @DeleteMapping("/{id}/{categoryId}")
    public ResponseEntity<PublicationDto> unassignPublicationFromCategory(@PathVariable(value = "id") long publicationId, @PathVariable long categoryId) {
        String blogId = resolveBlogId();
        Publication updatedPublication = publicationService.unassignPublicationFromCategory(blogId, publicationId, categoryId);
        PublicationDto updatedPublicationDto = map(updatedPublication);
        return ResponseEntity.ok().body(updatedPublicationDto);
    }

    // TODO instead of resolving blogId each time, add interceptor to compare values from path and token
    private String resolveBlogId() {
        String blogId = resolveBlogIdFromPrincipal();
        blogService.validateExistence(blogId);
        return blogId;
    }

    private List<PublicationDto> map(List<Publication> publications) {
        return publications
                .stream()
                .map(p -> objectMapper.convertValue(p, PublicationDto.class))
                .collect(Collectors.toList());
    }

    private PublicationDto map(Publication publication) {
        return objectMapper.convertValue(publication, PublicationDto.class);
    }

    private Publication map(PublicationDto publicationDto) {
        return objectMapper.convertValue(publicationDto, Publication.class);
    }
}

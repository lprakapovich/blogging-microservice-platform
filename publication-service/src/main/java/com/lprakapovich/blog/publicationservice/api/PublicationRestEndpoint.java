package com.lprakapovich.blog.publicationservice.api;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.lprakapovich.blog.publicationservice.api.dto.CreatePublicationDto;
import com.lprakapovich.blog.publicationservice.api.dto.PublicationDto;
import com.lprakapovich.blog.publicationservice.api.dto.UpdatePublicationDto;
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

import static com.lprakapovich.blog.publicationservice.api.paging.PageableDefaultValues.DEFAULT_PAGE_NUMBER;
import static com.lprakapovich.blog.publicationservice.api.paging.PageableDefaultValues.DEFAULT_PAGE_SIZE;
import static com.lprakapovich.blog.publicationservice.util.BlogIdResolver.resolveUsernameFromPrincipal;

@RestController
@RequestMapping("/publication-service/uri/{blogId}/publications")
@RequiredArgsConstructor
class PublicationRestEndpoint {

    private final PublicationService publicationService;
    private final BlogService blogService;
    private final ObjectMapper objectMapper;

    private static final String CREATED_DATETIME_FIELD = "createdDateTime";

    @PostMapping
    public ResponseEntity<URI> createPublication(@PathVariable String blogId,
                                                 @Valid @RequestBody CreatePublicationDto publicationDto) {
        checkBlog(blogId);
        Publication publication = objectMapper.convertValue(publicationDto, Publication.class);
        long publicationId = publicationService.createPublication(publication, blogId);
        return ResponseEntity.created(URI.create(String.valueOf(publicationId))).build();
    }

    @PutMapping("/{id}")
    public ResponseEntity<Void> updatePublication(@PathVariable String blogId,
                                                  @PathVariable(value = "id") long publicationId,
                                                  @RequestBody @Valid UpdatePublicationDto publicationDto) {
        checkBlog(blogId);
        Publication updatedPublication = objectMapper.convertValue(publicationDto, Publication.class);
        publicationService.updatePublication(blogId, publicationId, updatedPublication);
        return ResponseEntity.ok().build();
    }

    @GetMapping
    public ResponseEntity<List<PublicationDto>> getPublications(@PathVariable String blogId,
                                                                @RequestParam(defaultValue = DEFAULT_PAGE_NUMBER) int page,
                                                                @RequestParam(defaultValue = DEFAULT_PAGE_SIZE) int size,
                                                                @RequestParam(required = false) Status status,
                                                                @RequestParam(required = false) Long categoryId) {

        PageRequest pageable = PageRequest.of(page, size, Sort.by(CREATED_DATETIME_FIELD).descending());
        List<Publication> publications;

        boolean categorySpecified = Objects.nonNull(categoryId);
        boolean statusSpecified = Objects.nonNull(status);
        boolean blogOwner = blogService.ownsBlog(blogId);

        if (categorySpecified) {
            publications = publicationService.getAllByBlogIdAndCategory(blogId, categoryId, pageable);
        } else if (statusSpecified) {
            Status allowedStatus = blogOwner ? status : Status.PUBLISHED;
            publications = publicationService.getAllByBlogIdAndStatus(blogId, allowedStatus, pageable);
        } else {
            publications = blogService.ownsBlog(blogId) ? publicationService.getAllByBlogId(blogId, pageable)
                    : publicationService.getAllByBlogIdAndStatus(blogId, Status.PUBLISHED, pageable);
        }
        return ResponseEntity.ok(map(publications));
    }
    
    @GetMapping("/{id}")
    public ResponseEntity<PublicationDto> getPublication(@PathVariable String blogId, @PathVariable long id) {
        Publication publication = blogService.ownsBlog(blogId) ?
                publicationService.getById(id, blogId) :
                publicationService.getByIdAndStatus(id, blogId, Status.PUBLISHED);
        return ResponseEntity.ok(map(publication));
    }

    @GetMapping("/subscriptions")
    public ResponseEntity<List<PublicationDto>> getPublicationsFromSubscriptions(@PathVariable String blogId,
                                                                                 @RequestParam(defaultValue = DEFAULT_PAGE_NUMBER) int page,
                                                                                 @RequestParam(defaultValue = DEFAULT_PAGE_SIZE) int size) {
        checkBlog(blogId);
        PageRequest pageable = PageRequest.of(page, size, Sort.by(CREATED_DATETIME_FIELD).descending());
        List<Publication> publications = publicationService.getPublicationsFromSubscriptions(blogId, pageable);
        return ResponseEntity.ok(map(publications));
    }

//    @GetMapping("/subscriptions/{subscriptionBlogId}")
//    public ResponseEntity<List<PublicationDto>> getPublicationsFromParticularSubscription(@PathVariable String blogId,
//                                                                                          @PathVariable String subscriptionBlogId,
//                                                                                          @RequestParam(defaultValue = DEFAULT_PAGE_NUMBER) int page,
//                                                                                          @RequestParam(defaultValue = DEFAULT_PAGE_SIZE) int size,
//                                                                                          @RequestParam(required = false) Long categoryId) {
//        checkBlog(blogId);
//        PageRequest p = PageRequest.of(page, size, Sort.by(CREATED_DATETIME_FIELD));
//        List<Publication> publications = Objects.nonNull(categoryId) ?
//                publicationService.getPublicationsFromSubscriptionByCategory(blogId, subscriptionBlogId, categoryId, p) :
//                publicationService.getPublicationsFromSubscription(blogId, subscriptionBlogId, p);
//        return ResponseEntity.ok(map(publications));
//    }
//
//    @GetMapping("/subscriptions/{subscriptionBlogId}/{publicationId}")
//    public ResponseEntity<PublicationDto> getPublicationFromSubscription(@PathVariable String blogId,
//                                                                         @PathVariable String subscriptionBlogId,
//                                                                         @PathVariable long publicationId) {
//        checkBlog(blogId);
//        Publication publication = publicationService.getPublicationFromSubscription(blogId, subscriptionBlogId, publicationId);
//        return ResponseEntity.ok(map(publication));
//    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deletePublication(@PathVariable String blogId, @PathVariable long id) {
        checkBlog(blogId);
        publicationService.deleteById(id, blogId);
        return ResponseEntity.noContent().build();
    }

    @PutMapping("/{id}/{categoryId}")
    public ResponseEntity<PublicationDto> assignPublicationToCategory(@PathVariable String blogId,
                                                                      @PathVariable(value = "id") long publicationId,
                                                                      @PathVariable long categoryId) {
        checkBlog(blogId);
        Publication updatedPublication = publicationService.assignPublicationToCategory(blogId, publicationId, categoryId);
        PublicationDto updatedPublicationDto = map(updatedPublication);
        return ResponseEntity.ok().body(updatedPublicationDto);
    }

    @DeleteMapping("/{id}/{categoryId}")
    public ResponseEntity<PublicationDto> unassignPublicationFromCategory(@PathVariable String blogId,
                                                                          @PathVariable(value = "id") long publicationId,
                                                                          @PathVariable long categoryId) {
        checkBlog(blogId);
        Publication updatedPublication = publicationService.unassignPublicationFromCategory(blogId, publicationId, categoryId);
        PublicationDto updatedPublicationDto = map(updatedPublication);
        return ResponseEntity.ok().body(updatedPublicationDto);
    }

    private void checkBlog(String blogId) {
        String authenticatedUsername = resolveUsernameFromPrincipal();
        blogService.validateExistence(blogId, authenticatedUsername);
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
}

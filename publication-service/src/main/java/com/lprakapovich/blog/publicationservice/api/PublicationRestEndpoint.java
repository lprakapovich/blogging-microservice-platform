package com.lprakapovich.blog.publicationservice.api;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.lprakapovich.blog.publicationservice.api.dto.CreatePublicationDto;
import com.lprakapovich.blog.publicationservice.api.dto.PublicationDto;
import com.lprakapovich.blog.publicationservice.api.dto.UpdatePublicationDto;
import com.lprakapovich.blog.publicationservice.model.Blog.BlogId;
import com.lprakapovich.blog.publicationservice.model.Publication;
import com.lprakapovich.blog.publicationservice.model.Status;
import com.lprakapovich.blog.publicationservice.service.BlogService;
import com.lprakapovich.blog.publicationservice.service.PublicationService;
import com.lprakapovich.blog.publicationservice.util.BlogOwnershipValidator;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.List;
import java.util.Objects;

import static com.lprakapovich.blog.publicationservice.api.dto.utils.DtoMappingUtils.map;
import static com.lprakapovich.blog.publicationservice.api.dto.utils.DtoMappingUtils.mapPublications;
import static com.lprakapovich.blog.publicationservice.api.paging.PageableDefaultValues.DEFAULT_PAGE_NUMBER;
import static com.lprakapovich.blog.publicationservice.api.paging.PageableDefaultValues.DEFAULT_PAGE_SIZE;
import static com.lprakapovich.blog.publicationservice.util.AuthenticatedUserResolver.resolveUsernameFromPrincipal;

@RestController
@RequestMapping("/publication-service/{blogId},{username}/publications")
@RequiredArgsConstructor
class PublicationRestEndpoint {

    private final PublicationService publicationService;
    private final BlogService blogService;
    private final ObjectMapper objectMapper;
    private final BlogOwnershipValidator blogOwnershipValidator;

    private static final String CREATED_DATETIME_FIELD = "createdDateTime";

    @PostMapping
    public ResponseEntity<PublicationDto> createPublication(@PathVariable String blogId,
                                                 @PathVariable String username,
                                                 @RequestBody CreatePublicationDto publicationDto) {
        BlogId id = new BlogId(blogId, username);
        blogOwnershipValidator.validate(id);
        Publication publication = objectMapper.convertValue(publicationDto, Publication.class);
        Publication created = publicationService.createPublication(publication, id);
        return ResponseEntity.ok(map(created));
    }

    @PutMapping("/{id}")
    public ResponseEntity<PublicationDto> updatePublication(@PathVariable String blogId,
                                                  @PathVariable String username,
                                                  @PathVariable(value = "id") long publicationId,
                                                  @RequestBody @Valid UpdatePublicationDto publicationDto) {
        BlogId id = new BlogId(blogId, username);
        blogOwnershipValidator.validate(id);
        Publication updatedPublication = objectMapper.convertValue(publicationDto, Publication.class);
        Publication updated = publicationService.updatePublication(id, publicationId, updatedPublication);
        return ResponseEntity.ok(map(updated));
    }

    @GetMapping
    public ResponseEntity<List<PublicationDto>> getPublications(@PathVariable String blogId,
                                                                @PathVariable String username,
                                                                @RequestParam(defaultValue = DEFAULT_PAGE_NUMBER) int page,
                                                                @RequestParam(defaultValue = DEFAULT_PAGE_SIZE) int size,
                                                                @RequestParam(required = false) Status status,
                                                                @RequestParam(required = false) Long categoryId) {

        PageRequest pageable = PageRequest.of(page, size, Sort.by(CREATED_DATETIME_FIELD).descending());
        List<Publication> publications;

        boolean isCategorySpecified = Objects.nonNull(categoryId) ;
        boolean isStatusSpecified = Objects.nonNull(status);
        boolean isOwner = blogService.isOwner(blogId);
        Status resolvedStatus = isOwner ? status : Status.PUBLISHED;
        BlogId id = new BlogId(blogId, username);

        if (isCategorySpecified && isStatusSpecified) {
            publications = publicationService.getAllByBLogIdAndCategoryAndStatus(id, categoryId, resolvedStatus, pageable);
        } else {
            if (isCategorySpecified) {
                publications = publicationService.getAllByBlogIdAndCategory(id, categoryId, pageable);
            } else if (isStatusSpecified) {
                publications = publicationService.getAllByBlogIdAndStatus(id, resolvedStatus, pageable);
            } else {
                publications = blogService.isOwner(blogId) ?
                        publicationService.getAllByBlogId(id, pageable) :
                        publicationService.getAllByBlogIdAndStatus(id, Status.PUBLISHED, pageable);
            }
        }

        return ResponseEntity.ok(mapPublications(publications));
    }

    @GetMapping("/{id}")
    public ResponseEntity<PublicationDto> getPublication(@PathVariable String blogId,
                                                         @PathVariable String username,
                                                         @PathVariable(name = "id") long publicationId) {

        BlogId id = new BlogId(blogId, username);
        Publication publication = blogService.isOwner(blogId) ?
                publicationService.getById(publicationId, id) :
                publicationService.getByIdAndStatus(publicationId, id, Status.PUBLISHED);
        return ResponseEntity.ok(map(publication));
    }


    @GetMapping("/subscriptions")
    public ResponseEntity<List<PublicationDto>> getPublicationsFromSubscriptions(@PathVariable String blogId,
                                                                                 @PathVariable String username,
                                                                                 @RequestParam(defaultValue = DEFAULT_PAGE_NUMBER) int page,
                                                                                 @RequestParam(defaultValue = DEFAULT_PAGE_SIZE) int size) {
        BlogId id = new BlogId(blogId, username);
        blogOwnershipValidator.validate(id);

        PageRequest pageable = PageRequest.of(page, size, Sort.by(CREATED_DATETIME_FIELD).descending());
        List<Publication> publications = publicationService.getPublicationsFromSubscriptions(id, pageable);
        return ResponseEntity.ok(mapPublications(publications));
    }

    @GetMapping("/search")
    public ResponseEntity<List<PublicationDto>> getPublicationsBySearchCriteria(@RequestParam(required = false) String criteria) {
        String authenticatedUser = resolveUsernameFromPrincipal();
        List<Publication> publications = publicationService.getPublicationsBySearchCriteria(criteria, authenticatedUser);
        return ResponseEntity.ok(mapPublications(publications));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deletePublication(@PathVariable String blogId,
                                                  @PathVariable String username,
                                                  @PathVariable(name = "id") long publicationId) {
        BlogId id = new BlogId(blogId, username);
        blogOwnershipValidator.validate(id);
        publicationService.deletePublication(publicationId, id);
        return ResponseEntity.noContent().build();
    }

    @PutMapping("/{id}/{categoryId}")
    public ResponseEntity<PublicationDto> assignPublicationToCategory(@PathVariable String blogId,
                                                                      @PathVariable String username,
                                                                      @PathVariable(value = "id") long publicationId,
                                                                      @PathVariable long categoryId) {
        BlogId id = new BlogId(blogId, username);
        blogOwnershipValidator.validate(id);

        Publication updatedPublication = publicationService.assignCategoryToPublication(id, publicationId, categoryId);
        PublicationDto updatedPublicationDto = map(updatedPublication);
        return ResponseEntity.ok().body(updatedPublicationDto);
    }

    @DeleteMapping("/{id}/{categoryId}")
    public ResponseEntity<PublicationDto> unassignPublicationFromCategory(@PathVariable String blogId,
                                                                          @PathVariable String username,
                                                                          @PathVariable(value = "id") long publicationId,
                                                                          @PathVariable long categoryId) {
        BlogId id = new BlogId(blogId, username);
        blogOwnershipValidator.validate(id);
        Publication updatedPublication = publicationService.unassignCategoryFromPublication(id, publicationId, categoryId);
        PublicationDto updatedPublicationDto = map(updatedPublication);
        return ResponseEntity.ok().body(updatedPublicationDto);
    }
}

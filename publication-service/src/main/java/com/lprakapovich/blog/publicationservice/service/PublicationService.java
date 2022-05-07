package com.lprakapovich.blog.publicationservice.service;

import com.lprakapovich.blog.publicationservice.exception.PublicationNotFoundException;
import com.lprakapovich.blog.publicationservice.model.Blog;
import com.lprakapovich.blog.publicationservice.model.Blog.BlogId;
import com.lprakapovich.blog.publicationservice.model.Category;
import com.lprakapovich.blog.publicationservice.model.Publication;
import com.lprakapovich.blog.publicationservice.model.Status;
import com.lprakapovich.blog.publicationservice.repository.PublicationRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

import static com.lprakapovich.blog.publicationservice.util.AuthenticatedUserResolver.resolvePrincipal;

@Service
@RequiredArgsConstructor
public class PublicationService {

    private final PublicationRepository publicationRepository;
    private final BlogService blogService;
    private final CategoryService categoryService;
    private final SubscriptionService subscriptionService;

    public Publication createPublication(Publication publication, BlogId blogId) {
        checkBlogExistence(blogId);
        checkNullableCategoryExistence(blogId, publication);
        Blog blog = blogService.getById(blogId);
        publication.setBlog(blog);
        return publicationRepository.save(publication);
    }

    public Publication updatePublication(BlogId blogId, long publicationId, Publication publication) {
        checkBlogExistence(blogId);
        checkPublicationExistence(blogId, publicationId);
        checkNullableCategoryExistence(blogId, publication);
        publicationRepository.findByIdAndBlog_Id(publicationId, blogId)
                .ifPresent(p -> {
                    p.setTitle(publication.getTitle());
                    p.setCategory(publication.getCategory());
                    p.setStatus(publication.getStatus());
                    p.setContent(publication.getContent());
                    publicationRepository.save(p);
                });
        return getById(publicationId, blogId);
    }


    public void deletePublication(long id, BlogId blogId) {
        checkBlogExistence(blogId);
        checkPublicationExistence(blogId, id);
        Publication publication = getById(id, blogId);
        publication.setCategory(null);
        publicationRepository.save(publication);
        publicationRepository.deleteById(id);
    }

    public Publication getById(long id, BlogId blogId) {
        checkBlogExistence(blogId);
        return publicationRepository.findByIdAndBlog_Id(id, blogId)
                .orElseThrow(PublicationNotFoundException::new);
    }

    public Publication getByIdAndStatus(long id, BlogId blogId, Status status) {
        checkBlogExistence(blogId);
        return publicationRepository.findByIdAndBlog_IdAndStatus(id, blogId, status)
                .orElseThrow(PublicationNotFoundException::new);
    }

    public List<Publication> getAllByBlogId(BlogId blogId, Pageable pageable) {
        checkBlogExistence(blogId);
        return publicationRepository.findAllByBlog_Id(blogId, pageable);
    }

    public List<Publication> getAllByBlogIdAndStatus(BlogId blogId, Status status, PageRequest pageable) {
        checkBlogExistence(blogId);
        return publicationRepository.findAllByBlog_IdAndStatus(blogId, status, pageable);
    }

    public List<Publication> getAllByBlogIdAndCategory(BlogId blogId, long categoryId, PageRequest pageable) {
        checkBlogExistence(blogId);
        checkCategoryExistence(blogId, categoryId);
        return publicationRepository.findAllByBlog_IdAndCategoryId(blogId, categoryId, pageable);
    }

    public List<Publication> getAllByBLogIdAndCategoryAndStatus(BlogId blogId, long categoryId, Status status, Pageable pageable) {
        checkBlogExistence(blogId);
        checkCategoryExistence(blogId, categoryId);
        return publicationRepository.findAllByBlog_IdAndCategory_IdAndStatus(blogId, categoryId, status, pageable);
    }

    public List<Publication> getPublicationsFromSubscriptions(BlogId blogId, Pageable pageable) {
        checkBlogExistence(blogId);
        List<BlogId> subscriptionsIds = subscriptionService.getAllBlogSubscriptions(blogId)
                .stream()
                .map(subscription -> subscription.getId().getSubscription())
                .collect(Collectors.toList());
        return publicationRepository.findAllByBlog_IdInAndStatus(subscriptionsIds, Status.PUBLISHED, pageable);
    }

    public List<Publication> getPublicationsBySearchCriteria(String search, Pageable pageable) {
        String authenticatedUser = resolvePrincipal();
        return publicationRepository.findByStatusAndTitleContainsAndBlog_Id_UsernameNot(Status.PUBLISHED, search, authenticatedUser, pageable);
    }

    public Publication assignCategoryToPublication(BlogId blogId, long publicationId, long categoryId) {
        checkBlogExistence(blogId);
        checkCategoryExistence(blogId, categoryId);
        Publication publication = getById(publicationId, blogId);
        Category category = categoryService.getById(categoryId);
        publication.setCategory(category);
        return publication;
    }

    public Publication unassignCategoryFromPublication(BlogId blogId, long publicationId, long categoryId) {
        checkBlogExistence(blogId);
        checkCategoryExistence(blogId, categoryId);
        Publication publication = getById(publicationId, blogId);
        Category category = categoryService.getById(categoryId);
        if (Objects.equals(category, publication.getCategory())) {
            publication.setCategory(null);
        }
        return publication;
    }

    private void checkPublicationExistence(BlogId blogId, long id) {
        if (!publicationRepository.existsByIdAndBlog_Id(id, blogId)) {
            throw new PublicationNotFoundException();
        }
    }

    private void checkBlogExistence(BlogId blogId) {
        blogService.checkExistence(blogId);
    }

    private void checkCategoryExistence(BlogId blogId, long categoryId) {
        categoryService.checkExistence(categoryId, blogId);
    }

    private void checkNullableCategoryExistence(BlogId blogId, Publication publication) {
        if (publication.getCategory() != null) {
            checkCategoryExistence(blogId, publication.getCategory().getId());
        }
    }
}

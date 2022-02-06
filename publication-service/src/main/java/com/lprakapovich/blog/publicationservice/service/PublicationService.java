package com.lprakapovich.blog.publicationservice.service;

import com.lprakapovich.blog.publicationservice.exception.PublicationNotFoundException;
import com.lprakapovich.blog.publicationservice.model.Blog;
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

@Service
@RequiredArgsConstructor
public class PublicationService {

    private final PublicationRepository publicationRepository;
    private final BlogService blogService;
    private final CategoryService categoryService;
    private final SubscriptionService subscriptionService;

    /**
     * 
     * @param publication publication to create
     * @param blogId blogId of authenticated user
     * @return id of the created publication 
     */
    public long createPublication(Publication publication, String blogId) {
        checkCategory(blogId, publication.getCategory().getId());
        Blog blog = blogService.getById(blogId);
        publication.setBlog(blog);
        Publication createdPublication = publicationRepository.save(publication);
        return createdPublication.getId();
    }

    /**
     * 
     * @param blogId blogId of authenticated user
     * @param publicationId id of publication to update
     * @param publication updated publication
     */
    public void updatePublication(String blogId, long publicationId, Publication publication) {
        checkPublication(blogId, publicationId);
        checkCategory(blogId, publication.getCategory().getId());
        publicationRepository.findByIdAndBlog_Id(publicationId, blogId).ifPresent(p -> {
            p.setHeader(publication.getHeader());
            p.setSubHeader(publication.getSubHeader());
            p.setCategory(publication.getCategory());
            p.setStatus(publication.getStatus());
            p.setContent(publication.getContent());
            publicationRepository.save(p);
        });
    }

    /**
     *
     * @param id id of publication to fetch
     * @param blogId blogId of authenticated user
     * @return publication
     */
    public Publication getById(long id, String blogId) {
        return publicationRepository.findByIdAndBlog_Id(id, blogId).orElseThrow(PublicationNotFoundException::new);
    }

    /**
     * 
     * @param blogId blogId of authenticated user
     * @param pageable pageable request
     * @return publications of authenticated user
     */
    public List<Publication> getAllByBlogId(String blogId, Pageable pageable) {
        return publicationRepository.findAllByBlog_Id(blogId, pageable);
    }

    /**
     * 
     * @param id id of publication to delete
     * @param blogId blogId of authenticated user
     */
    public void deleteById(long id, String blogId) {
        checkPublication(blogId, id);
        publicationRepository.deleteById(id);
    }

    /**
     * 
     * @param blogId blogId of authenticated user
     * @param status publication status - draft, published, hidden
     * @param pageable pageable request
     * @return user publications with a particular status
     */
    public List<Publication> getAllByBlogIdAndStatus(String blogId, Status status, PageRequest pageable) {
        return publicationRepository.findAllByBlog_IdAndStatus(blogId, status, pageable);
    }

    /**
     *
     * @param blogId blogId of authenticated user
     * @param categoryId id of the category
     * @param pageable pageable request
     * @return user publications with a particular category
     */
    public List<Publication> getAllByBlogIdAndCategory(String blogId, long categoryId, PageRequest pageable) {
        checkCategory(blogId, categoryId);
        return publicationRepository.findAllByBlog_IdAndCategory_Id(blogId, categoryId, pageable);
    }

    /**
     *
     * @param blogId blogId of authenticated user
     * @param pageable pageable parameter
     * @return publications of all user subscriptions
     */
    public List<Publication> getPublicationsFromSubscriptions(String blogId, Pageable pageable) {
        List<String> subscriptionsIds = subscriptionService.getAllBlogSubscriptions(blogId)
                .stream()
                .map(subscription -> subscription.getId().getBlogId())
                .collect(Collectors.toList());
        return publicationRepository.findAllByBlog_IdInAndStatus(subscriptionsIds, Status.PUBLISHED, pageable);
    }

    /**
     * 
     * @param blogId blogId of authenticated user
     * @param subscribedBlogId blogId an authenticated user is subscribed to
     * @param pageable pageable parameter
     * @return all publications from a particular user subscription
     */
    public List<Publication> getPublicationsFromSubscription(String blogId, String subscribedBlogId, PageRequest pageable) {
        checkSubscription(blogId, subscribedBlogId);
        return publicationRepository.findAllByBlog_IdAndStatus(subscribedBlogId, Status.PUBLISHED, pageable);
    }

    /**
     *
     * @param blogId blogId of authenticated user
     * @param subscribedBlogId blogId an authenticated user is subscribed to
     * @param categoryId id of category of a subscribed blog
     * @param pageable pageable request
     * @return publications of a subscribed blog filtered by category
     */
    public List<Publication> getPublicationsFromSubscriptionByCategory(String blogId, String subscribedBlogId, Long categoryId, PageRequest pageable) {
        checkSubscription(blogId, subscribedBlogId);
        checkCategory(subscribedBlogId, categoryId);
        return publicationRepository.findAllByBlog_IdAndCategory_IdAndStatus(subscribedBlogId, categoryId, Status.PUBLISHED, pageable);
    }

    /**
     * 
     * @param blogId blogId of authenticated user
     * @param subscribedBlogId blogId an authenticated user is subscribed to
     * @param publicationId targeted publication
     * @return targeted publication of a subscribed blog
     */
    public Publication getPublicationFromSubscription(String blogId, String subscribedBlogId, long publicationId) {
        checkSubscription(blogId, subscribedBlogId);
        return publicationRepository.findByIdAndBlog_IdAndStatus(publicationId, subscribedBlogId, Status.PUBLISHED)
                .orElseThrow(PublicationNotFoundException::new);
    }

    /**
     *
     * @param blogId blogId of authenticated user
     * @param publicationId id of publication to which a category is assigned
     * @param categoryId id of category to be assigned
     * @return updated publication
     */
    public Publication assignPublicationToCategory(String blogId, long publicationId, long categoryId) {
        checkCategory(blogId, categoryId);
        Publication publication = getById(publicationId, blogId);
        Category category = categoryService.getById(categoryId, blogId);
        publication.setCategory(category);
        return publication;
    }

    /**
     *
     * @param blogId blogId of authenticated user
     * @param publicationId id of publication from which a category is unassigned
     * @param categoryId id of category to be unassigned
     * @return updated publication
     */
    public Publication unassignPublicationFromCategory(String blogId, long publicationId, long categoryId) {
        checkCategory(blogId, categoryId);
        Publication publication = getById(publicationId, blogId);
        Category category = categoryService.getById(categoryId, blogId);
        if (Objects.equals(category, publication.getCategory())) {
            publication.setCategory(null);
        }
        return publication;
    }

    /**
     * 
     * @param id publicationId
     * @param blogId blogId of authenticated user
     */
    private void checkPublication(String blogId, long id) {
        if (!publicationRepository.existsByIdAndBlog_Id(id, blogId)) {
            throw new PublicationNotFoundException();
        }
    }

    /**
     * 
     * @param blogId blogId of authenticated user
     * @param subscribedBlogId blogId that user is subscribed to
     */
    private void checkSubscription(String blogId, String subscribedBlogId) {
        subscriptionService.checkSubscription(subscribedBlogId, blogId);
    }

    /**
     *
     * @param blogId blogId of authenticated user
     * @param categoryId id of the category created by the user
     */
    private void checkCategory(String blogId, long categoryId) {
        categoryService.checkCategory(categoryId, blogId);
    }
}

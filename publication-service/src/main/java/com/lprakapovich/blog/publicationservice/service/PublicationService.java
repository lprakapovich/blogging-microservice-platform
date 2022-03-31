package com.lprakapovich.blog.publicationservice.service;

import com.lprakapovich.blog.publicationservice.exception.PublicationNotFoundException;
import com.lprakapovich.blog.publicationservice.model.*;
import com.lprakapovich.blog.publicationservice.model.Blog.BlogId;
import com.lprakapovich.blog.publicationservice.model.Subscription.SubscriptionId;
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

    public long createPublication(Publication publication, BlogId blogId) {
        checkNullableCategoryExistence(blogId, publication);
        Blog blog = blogService.getById(blogId);
        publication.setBlog(blog);
        Publication createdPublication = publicationRepository.save(publication);
        return createdPublication.getId();
    }

    public void updatePublication(BlogId blogId, long publicationId, Publication publication) {
        checkExistence(blogId, publicationId);
        checkNullableCategoryExistence(blogId, publication);
        publicationRepository.findByIdAndBlog_Id(publicationId, blogId)
                .ifPresent(p -> {
                    p.setHeader(publication.getHeader());
                    p.setSubHeader(publication.getSubHeader());
                    p.setCategory(publication.getCategory());
                    p.setStatus(publication.getStatus());
                    p.setContent(publication.getContent());
                    publicationRepository.save(p);
                });
    }


    public void deletePublication(long id, BlogId blogId) {
        checkExistence(blogId, id);
        publicationRepository.deleteById(id);
    }

    public Publication getById(long id, BlogId blogId) {
        return publicationRepository.findByIdAndBlog_Id(id, blogId)
                .orElseThrow(PublicationNotFoundException::new);
    }

    public Publication getByIdAndStatus(long id, BlogId blogId, Status status) {
        return publicationRepository.findByIdAndBlog_IdAndStatus(id, blogId, status)
                .orElseThrow(PublicationNotFoundException::new);
    }

    public List<Publication> getAllByBlogId(BlogId blogId, Pageable pageable) {
        return publicationRepository.findAllByBlog_Id(blogId, pageable);
    }

    public List<Publication> getAllByBlogIdAndStatus(BlogId blogId, Status status, PageRequest pageable) {
        return publicationRepository.findAllByBlog_IdAndStatus(blogId, status, pageable);
    }

    public List<Publication> getAllByBlogIdAndCategory(BlogId blogId, long categoryId, PageRequest pageable) {
        checkCategoryExistence(blogId, categoryId);
        return publicationRepository.findAllByBlog_IdAndCategoryId(blogId, categoryId, pageable);
    }

    public List<Publication> getPublicationsFromSubscriptions(BlogId blogId, Pageable pageable) {
        List<BlogId> subscriptionsIds = subscriptionService.getAllBlogSubscriptions(blogId)
                .stream()
                .map(subscription -> subscription.getId().getSubscription())
                .collect(Collectors.toList());
        return publicationRepository.findAllByBlog_IdInAndStatus(subscriptionsIds, Status.PUBLISHED, pageable);
    }

    public List<Publication> getPublicationsFromSubscription(BlogId subscriber, BlogId subscription, PageRequest pageable) {
        checkSubscriptionExistence(subscriber, subscription);
        return publicationRepository.findAllByBlog_IdAndStatus(subscription, Status.PUBLISHED, pageable);
    }

//    /**
//     *
//     * @param subscriber blogId
//     * @param subscription blogId an authenticated user is subscribed to
//     * @param categoryId id of category of a subscribed blog
//     * @param pageable pageable request
//     * @return publications of a subscribed blog filtered by category
//     */
//    public List<Publication> getPublicationsFromSubscriptionByCategory(BlogId subscriber, BlogId subscription, long categoryId, PageRequest pageable) {
//        checkSubscription(subscriber, subscription);
//        checkCategory(subscription, categoryId);
//        return publicationRepository.findAllByBlog_IdAndCategory_IdAndStatus(subscription, categoryId, Status.PUBLISHED, pageable);
//    }
//
//    /**
//     *
//     * @param blogId blogId
//     * @param subscribedBlogId blogId an authenticated user is subscribed to
//     * @param publicationId targeted publication
//     * @return targeted publication of a subscribed blog
//     */
//    public Publication getPublicationFromSubscription(String blogId, String subscribedBlogId, long publicationId) {
//        checkSubscription(blogId, subscribedBlogId);
//        return publicationRepository.findByIdAndBlog_IdAndStatus(publicationId, subscribedBlogId, Status.PUBLISHED)
//                .orElseThrow(PublicationNotFoundException::new);
//    }

    public List<Publication> getPublicationsBySearchCriteria(String search, String authenticatedUser) {
        return publicationRepository.findByStatusAndHeaderContainsAndBlog_Id_UsernameNot(Status.PUBLISHED, search, authenticatedUser);
    }

    public Publication assignCategoryToPublication(BlogId blogId, long publicationId, long categoryId) {
        checkCategoryExistence(blogId, categoryId);
        Publication publication = getById(publicationId, blogId);
        Category category = categoryService.getById(categoryId);
        publication.setCategory(category);
        return publication;
    }

    public Publication unassignCategoryFromPublication(BlogId blogId, long publicationId, long categoryId) {
        checkCategoryExistence(blogId, categoryId);
        Publication publication = getById(publicationId, blogId);
        Category category = categoryService.getById(categoryId);
        if (Objects.equals(category, publication.getCategory())) {
            publication.setCategory(null);
        }
        return publication;
    }

    private void checkExistence(BlogId blogId, long id) {
        if (!publicationRepository.existsByIdAndBlog_Id(id, blogId)) {
            throw new PublicationNotFoundException();
        }
    }

    private void checkSubscriptionExistence(BlogId subscriber, BlogId subscription) {
        subscriptionService.checkExistence(new SubscriptionId(subscriber, subscription));
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

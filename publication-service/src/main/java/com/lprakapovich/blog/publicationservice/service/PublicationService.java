package com.lprakapovich.blog.publicationservice.service;

import com.lprakapovich.blog.publicationservice.exception.PublicationNotFoundException;
import com.lprakapovich.blog.publicationservice.model.*;
import com.lprakapovich.blog.publicationservice.repository.PublicationRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class PublicationService {

    private final PublicationRepository publicationRepository;

    private final BlogService blogService;
    private final CategoryService categoryService;
    private final SubscriptionService subscriptionService;

    public Publication getById(long id, String blogId) {
        return publicationRepository.findByIdAndBlog_Id(id, blogId).orElseThrow(PublicationNotFoundException::new);
    }

    public List<Publication> getByCategoryId(long categoryId, String blogId) {
        return publicationRepository.findByCategory_IdAndBlog_Id(categoryId, blogId);
    }

    public long createPublication(Publication publication, String blogId) {
        Blog blog = blogService.getById(blogId);
        publication.setBlog(blog);
        Publication saved = publicationRepository.save(publication);
        return saved.getId();
    }

    public List<Publication> getAllByBlogId(String blogId) {
        return publicationRepository.findByBlog_Id(blogId);
    }

    public List<Publication> getAllByBlogId(String blogId, Pageable pageable) {
        return publicationRepository.findAllByBlog_Id(blogId, pageable);
    }

    @Transactional
    public void deleteById(long id, String blogId) {
        validateExistence(id, blogId);
        publicationRepository.deleteById(id);
    }

    @Transactional
    public Publication assignPublicationToCategory(String blogId, long publicationId, long categoryId) {
        Publication publication = getById(publicationId, blogId);
        Category category = categoryService.getById(categoryId, blogId);
        publication.setCategory(category);
        return publication;
    }

    @Transactional
    public Publication unassignPublicationFromCategory(String blogId, long publicationId, long categoryId) {
        Publication publication = getById(publicationId, blogId);
        Category category = categoryService.getById(categoryId, blogId);
        if (publication.getCategory() != null && publication.getCategory().equals(category)) {
            publication.setCategory(null);
        }
        return publication;
    }

    private void validateExistence(long id, String blogId) {
        if (!publicationRepository.existsByIdAndBlog_Id(id, blogId)) {
            throw new PublicationNotFoundException();
        }
    }

    // add status handling
    public List<Publication> getPublicationsFromSubscriptions(String blogId, PageRequest p) {
        List<String> subscriptionsIds = subscriptionService.getAllSubscriptions(blogId)
                .stream()
                .map(subscription -> subscription.getId().getBlogId())
                .collect(Collectors.toList());
        return publicationRepository.findAllByBlog_IdIn(subscriptionsIds, p);
    }
}

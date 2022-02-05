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
        // todo:  check category existence
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
        validateByPublicationIdAndBlogId(id, blogId);
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

    private void validateByPublicationIdAndBlogId(long id, String blogId) {
        if (!publicationRepository.existsByIdAndBlog_Id(id, blogId)) {
            throw new PublicationNotFoundException();
        }
    }

    public List<Publication> getPublicationsFromSubscriptions(String blogId, PageRequest p) {
        List<String> subscriptionsIds = subscriptionService.getAllSubscriptions(blogId)
                .stream()
                .map(subscription -> subscription.getId().getBlogId())
                .collect(Collectors.toList());
        return publicationRepository.findAllByBlog_IdIn(subscriptionsIds, p);
    }

    public List<Publication> getAllByBlogIdAndStatus(String blogId, Status status, PageRequest p) {
        return publicationRepository.findAllByBlog_IdAndStatus(blogId, status, p);
    }

    public List<Publication> getAllByBlogIdAndCategory(String blogId, long categoryId, PageRequest p) {
        return publicationRepository.findAllByBlog_IdAndCategory_Id(blogId, categoryId, p);
    }

    public void updatePublication(String blogId, long publicationId, Publication publication) {
        // todo:  check category existence
        validateByPublicationIdAndBlogId(publicationId, blogId);
        publicationRepository.findByIdAndBlog_Id(publicationId, blogId).ifPresent(p -> {
            p.setHeader(publication.getHeader());
            p.setSubHeader(publication.getSubHeader());
            p.setCategory(publication.getCategory());
            p.setStatus(publication.getStatus());
            p.setContent(publication.getContent());
            publicationRepository.save(p);
        });
    }
}

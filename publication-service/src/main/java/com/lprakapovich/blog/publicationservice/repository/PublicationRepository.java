package com.lprakapovich.blog.publicationservice.repository;

import com.lprakapovich.blog.publicationservice.model.Blog.BlogId;
import com.lprakapovich.blog.publicationservice.model.Publication;
import com.lprakapovich.blog.publicationservice.model.Status;
import org.springframework.data.domain.Pageable;
import org.springframework.data.repository.PagingAndSortingRepository;

import java.util.List;
import java.util.Optional;

public interface PublicationRepository extends PagingAndSortingRepository<Publication, Long> {

    Optional<Publication> findByIdAndBlog_Id(long publicationId, BlogId blogId);

    Optional<Publication> findByIdAndBlog_IdAndStatus(long publicationId, BlogId blogId, Status status);

    List<Publication> findAllByBlog_Id(BlogId blogId, Pageable pageable);

    List<Publication> findAllByBlog_IdInAndStatus(List<BlogId> ids, Status status, Pageable pageable);

    List<Publication> findAllByBlog_IdAndStatus(BlogId blogId, Status status, Pageable p);

    List<Publication> findAllByBlog_IdAndCategoryId(BlogId blogId, long categoryId, Pageable pageable);

    List<Publication> findAllByBlog_IdAndCategory_IdAndStatus(BlogId blogId, long categoryId, Status status, Pageable pageable);

    List<Publication> findByCategory_IdAndBlog_Id(long categoryId, BlogId blogId);

    List<Publication> findByStatusAndTitleContainsAndBlog_Id_UsernameNot(Status status, String search, String username);

    boolean existsByIdAndBlog_Id(long publicationId, BlogId blogId);

    void deleteAllByBlog_Id(BlogId blogId);
}

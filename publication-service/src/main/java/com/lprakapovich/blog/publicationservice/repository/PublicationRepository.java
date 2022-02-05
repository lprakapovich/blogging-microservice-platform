package com.lprakapovich.blog.publicationservice.repository;

import com.lprakapovich.blog.publicationservice.model.Publication;
import com.lprakapovich.blog.publicationservice.model.Status;
import org.springframework.data.domain.Pageable;
import org.springframework.data.repository.PagingAndSortingRepository;

import java.util.List;
import java.util.Optional;

public interface PublicationRepository extends PagingAndSortingRepository<Publication, Long> {

    Optional<Publication> findByIdAndBlog_Id(Long publicationId, String blogId);

    List<Publication> findByBlog_Id(String id);

    List<Publication> findAllByBlog_Id(String id, Pageable pageable);

    // todo:  add sorting
    List<Publication> findAllByBlog_IdInAndStatus(List<String> ids, Status status, Pageable pageable);

    List<Publication> findAllByBlog_IdAndStatus(String blogId, Status status, Pageable p);

    List<Publication> findAllByBlog_IdAndCategory_Id(String id, long categoryId, Pageable pageable);

    List<Publication> findAllByBlog_IdIn(List<String> ids, Pageable pageable);

    List<Publication> findByCategory_IdAndBlog_Id(long categoryId, String blogId);

    boolean existsByIdAndBlog_Id(long publicationId, String blogId);
}

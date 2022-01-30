package com.lprakapovich.blog.publicationservice.repository;

import com.lprakapovich.blog.publicationservice.model.Publication;
import org.springframework.data.repository.PagingAndSortingRepository;

import java.util.Optional;

public interface PublicationRepository extends PagingAndSortingRepository<Publication, Long> {

    Optional<Publication> findById(Long id);

//    List<Publication> findByBlog_Id(String id);
//
//    List<Publication> findByBlog_Id(String id, Pageable pageable);
//
//    List<Publication> findByCategory_Name(String category);
//
//    List<Publication> findByCategory_Name(String category, Pageable pageable);
//
//    List<Publication> findByBlog_IdAndPublicationStatus(String id, Status status);
//
//    List<Publication> findByBlog_IdAndPublicationStatus(String id, Status status, Pageable pageable);
//
//    List<Publication> findByPublicationStatus(Status status);
}

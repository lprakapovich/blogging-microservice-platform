package com.lprakapovich.blog.publicationservice.repository;

import com.lprakapovich.blog.publicationservice.model.Blog;
import org.springframework.data.repository.PagingAndSortingRepository;

import java.util.Optional;

public interface BlogRepository extends PagingAndSortingRepository<Blog, String> {

    Optional<Blog> getByIdAndUsername(String id, String username);

    boolean existsByIdAndUsername(String id, String username);
}

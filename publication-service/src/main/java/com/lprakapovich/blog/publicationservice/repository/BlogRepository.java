package com.lprakapovich.blog.publicationservice.repository;

import com.lprakapovich.blog.publicationservice.domain.Blog;
import org.springframework.data.repository.PagingAndSortingRepository;

public interface BlogRepository extends PagingAndSortingRepository<Blog, Long> {
}

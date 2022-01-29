package com.lprakapovich.blog.publicationservice.repository;

import com.lprakapovich.blog.publicationservice.domain.Category;
import org.springframework.data.repository.PagingAndSortingRepository;

public interface CategoryRepository  extends PagingAndSortingRepository<Category, Long> {
}

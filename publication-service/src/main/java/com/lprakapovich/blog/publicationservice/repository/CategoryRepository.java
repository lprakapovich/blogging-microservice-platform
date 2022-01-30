package com.lprakapovich.blog.publicationservice.repository;

import com.lprakapovich.blog.publicationservice.model.Category;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.data.repository.query.Param;

public interface CategoryRepository extends PagingAndSortingRepository<Category, Long> {

    @Modifying
    @Query(value = "update categories c set name = :name where c.id = :id and c.blog_id = :blogId", nativeQuery = true)
    void updateCategoryName(@Param(value = "name") String name, @Param(value = "id") long id, @Param(value = "blogId") String blogId);
}

package com.lprakapovich.blog.publicationservice.repository;

import com.lprakapovich.blog.publicationservice.model.Category;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface CategoryRepository extends PagingAndSortingRepository<Category, Long> {

    @Modifying
    @Query(value = "update categories c set name = :name where c.id = :id and c.blog_id = :blogId", nativeQuery = true)
    void updateCategoryName(@Param(value = "name") String name, @Param(value = "id") long id, @Param(value = "blogId") String blogId);

    @Query(value = "select * from categories c where c.blog_id = :blogId", nativeQuery = true)
    List<Category> findByBlogId(@Param(value = "blogId") String blogId);

    @Query(value = "select * from categories c where c.blog_id = :blogId and c.id = :id", nativeQuery = true)
    Optional<Category> findByIdAndBlogId(@Param(value = "id") long id, @Param(value = "blogId") String blogId);

    @Query(value = "select exists(select * from categories c where c.blog_id = :blogId and c.name = :name)", nativeQuery = true)
    boolean existsByNameAndBlogId(@Param(value = "name") String name, @Param(value = "blogId") String blogId);
}

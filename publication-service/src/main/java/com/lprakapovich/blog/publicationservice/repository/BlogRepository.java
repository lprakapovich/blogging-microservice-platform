package com.lprakapovich.blog.publicationservice.repository;

import com.lprakapovich.blog.publicationservice.model.Blog;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface BlogRepository extends PagingAndSortingRepository<Blog, Blog.BlogId> {

    List<Blog> findById_Username(String username);

    @Query(value = "select * from blogs b where (b.blog_id like %:criteria% or b.display_name like %:criteria% or b.description like %:criteria%)" +
            " and b.username <> :principal", nativeQuery = true)
    List<Blog> findByCriteriaExcludingUsername(@Param(value = "criteria") String criteria,
                                               @Param(value = "principal") String principal);
}

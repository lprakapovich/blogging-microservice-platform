package com.lprakapovich.blog.publicationservice.repository;

import com.lprakapovich.blog.publicationservice.model.Blog;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.data.repository.query.Param;

public interface BlogRepository extends PagingAndSortingRepository<Blog, String> {

    @Query(value = "update blogs b set first_name = :firstName, last_name = :lastName where b.id = :id",nativeQuery = true)
    Blog updateBlogData(@Param(value = "id") String id, @Param(value = "firstName") String firstName, @Param(value = "lastName") String lastName);
}

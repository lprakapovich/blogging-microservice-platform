package com.lprakapovich.blog.publicationservice.service;

import com.lprakapovich.blog.publicationservice.exception.BlogNotFoundException;
import com.lprakapovich.blog.publicationservice.exception.DuplicatedBlogException;
import com.lprakapovich.blog.publicationservice.model.Blog;
import com.lprakapovich.blog.publicationservice.repository.BlogRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

import static com.lprakapovich.blog.publicationservice.util.BlogIdResolver.resolveUsernameFromPrincipal;

@Service
@RequiredArgsConstructor
public class BlogService {

    private final BlogRepository blogRepository;

    public void createBlog(Blog blog) {
        if (blogRepository.existsById(blog.getId())) {
            throw new DuplicatedBlogException();
        }
        blogRepository.save(blog);
    }

    public Blog getById(String id) {
        return blogRepository.findById(id).orElseThrow(BlogNotFoundException::new);
    }

    public Blog getByIdAndUsername(String id, String username) {
        return blogRepository.getByIdAndUsername(id, username).orElseThrow(BlogNotFoundException::new);
    }

    public List<Blog> getAll() {
        ArrayList<Blog> blogs = new ArrayList<>();
        blogRepository.findAll().forEach(blogs::add);
        return blogs;
    }

    public Blog updateBlog(String id, Blog updatedBlog) {
        Blog blog = getById(id);
        blog.setName(updatedBlog.getName());
        blog.setDescription(updatedBlog.getDescription());
        return blogRepository.save(blog);
    }

    public boolean ownsBlog(String id) {
        String authenticatedUser = resolveUsernameFromPrincipal();
        return blogRepository.existsByIdAndUsername(id, authenticatedUser);
    }

    public boolean existsById(String id) {
        return blogRepository.existsById(id);
    }

    public boolean existsByIdAndUsername(String id, String username) {
        return blogRepository.existsByIdAndUsername(id, username);
    }

    public void validateBlogOwnership(String id) {
        String authenticatedUser = resolveUsernameFromPrincipal();
        validateExistence(id, authenticatedUser);
    }

    public void validateExistence(String id) {
        if (!blogRepository.existsById(id)) {
            throw new BlogNotFoundException();
        }
    }

    public void validateExistence(String id, String username) {
        if (!blogRepository.existsByIdAndUsername(id, username)) {
            throw new BlogNotFoundException();
        }
    }
}

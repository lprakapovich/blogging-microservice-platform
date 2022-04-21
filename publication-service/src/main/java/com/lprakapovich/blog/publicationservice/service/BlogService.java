package com.lprakapovich.blog.publicationservice.service;

import com.lprakapovich.blog.publicationservice.exception.BlogNotFoundException;
import com.lprakapovich.blog.publicationservice.exception.DuplicatedBlogIdException;
import com.lprakapovich.blog.publicationservice.model.Blog;
import com.lprakapovich.blog.publicationservice.model.Blog.BlogId;
import com.lprakapovich.blog.publicationservice.repository.BlogRepository;
import com.lprakapovich.blog.publicationservice.repository.CategoryRepository;
import com.lprakapovich.blog.publicationservice.repository.PublicationRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.util.List;

import static com.lprakapovich.blog.publicationservice.util.AuthenticatedUserResolver.resolveUsernameFromPrincipal;

@Service
@RequiredArgsConstructor
public class BlogService {

    private final BlogRepository blogRepository;
    private final CategoryRepository categoryRepository;
    private final PublicationRepository publicationRepository;

    public BlogId createBlog(Blog blog) {
        if (blogRepository.existsById(blog.getId())) {
            throw new DuplicatedBlogIdException();
        }
       return blogRepository.save(blog).getId();
    }

    public Blog updateBlog(BlogId id, Blog updatedBlog) {
        Blog blog = getById(id);
        blog.setDisplayName(updatedBlog.getDisplayName());
        blog.setDescription(updatedBlog.getDescription());
        return blogRepository.save(blog);
    }

    @Transactional
    public void deleteBlog(BlogId blogId) {
        checkExistence(blogId);
        publicationRepository.deleteAllByBlog_Id(blogId);
        categoryRepository.deleteAllByBlogIdAndUsername(blogId.getId(), blogId.getUsername());
        blogRepository.deleteById(blogId);
    }

    public Blog getById(BlogId id) {
        return blogRepository.findById(id).orElseThrow(BlogNotFoundException::new);
    }

    public List<Blog> getAllByUsername() {
        String authenticatedUser = resolveUsernameFromPrincipal();
        return blogRepository.findById_Username(authenticatedUser);
    }

    public boolean exists(String id) {
        String authenticatedUser = resolveUsernameFromPrincipal();
        BlogId blogId = new BlogId(id, authenticatedUser);
        return blogRepository.existsById(blogId);
    }

    public boolean isOwner(String id) {
        String authenticatedUser = resolveUsernameFromPrincipal();
        return blogRepository.existsById(new BlogId(id, authenticatedUser));
    }

    public void checkExistence(BlogId id) {
        if (!blogRepository.existsById(id)) {
            throw new BlogNotFoundException();
        }
    }

    public List<Blog> getAllBySearchCriteria(String search, String authenticatedUser) {
        return blogRepository.findByDescriptionContainsOrId_IdContainsAndId_UsernameNot(search, search, authenticatedUser);
    }
}

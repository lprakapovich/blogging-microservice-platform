package com.lprakapovich.blog.publicationservice.service;

import com.lprakapovich.blog.publicationservice.exception.BlogNotFoundException;
import com.lprakapovich.blog.publicationservice.exception.DuplicatedBlogIdException;
import com.lprakapovich.blog.publicationservice.model.Blog;
import com.lprakapovich.blog.publicationservice.model.Blog.BlogId;
import com.lprakapovich.blog.publicationservice.model.Status;
import com.lprakapovich.blog.publicationservice.repository.BlogRepository;
import com.lprakapovich.blog.publicationservice.repository.CategoryRepository;
import com.lprakapovich.blog.publicationservice.repository.PublicationRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.util.List;

import static com.lprakapovich.blog.publicationservice.util.AuthenticatedUserResolver.resolvePrincipal;

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
        String authenticatedUser = resolvePrincipal();
        return blogRepository.findById_Username(authenticatedUser);
    }

    public boolean existsById(String id) {
        String authenticatedUser = resolvePrincipal();
        BlogId blogId = new BlogId(id, authenticatedUser);
        return blogRepository.existsById(blogId);
    }

    public boolean isOwner(String blogId) {
        String authenticatedUser = resolvePrincipal();
        return blogRepository.existsById(new BlogId(blogId, authenticatedUser));
    }

    public void checkExistence(BlogId blogId) {
        if (!blogRepository.existsById(blogId)) {
            throw new BlogNotFoundException();
        }
    }

    public List<Blog> getAllBySearchCriteria(String criteria, int page, int pageSize) {
        String authenticatedUser = resolvePrincipal();
        int offset = page * pageSize;
        return blogRepository.findByCriteriaExcludingUsernameAndPageable(criteria, authenticatedUser, pageSize, offset);
    }

    public int getNumberOfBlogPublications(BlogId blogId) {
        return isOwner(blogId.getId()) ?
                publicationRepository.findByBlog_Id(blogId).size() :
                publicationRepository.findByBlog_IdAndStatus(blogId, Status.PUBLISHED).size();
    }
}

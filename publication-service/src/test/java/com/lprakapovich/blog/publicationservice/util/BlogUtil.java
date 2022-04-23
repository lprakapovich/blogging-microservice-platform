package com.lprakapovich.blog.publicationservice.util;

import com.lprakapovich.blog.publicationservice.model.Blog;
import com.lprakapovich.blog.publicationservice.model.Blog.BlogId;

import static com.lprakapovich.blog.publicationservice.util.AuthenticationMockUtils.DEFAULT_PRINCIPAL;

public class BlogUtil {

    public static final String BLOG_ID = "blogId";

    public static BlogId getDefaultBlogId() {
        return new BlogId(BLOG_ID, DEFAULT_PRINCIPAL);
    }

    public static Blog getDefaultBlog() {
        return Blog.builder().id(getDefaultBlogId()).build();
    }

    public static BlogId getBlogId(String blogId, String username) {
        return new BlogId(blogId, username);
    }

    public static Blog getBlog(String blogId, String username) {
        return Blog.builder().id(getBlogId(blogId, username)).build();
    }
}

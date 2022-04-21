package com.lprakapovich.blog.publicationservice.util;

import com.lprakapovich.blog.publicationservice.model.Blog;
import com.lprakapovich.blog.publicationservice.model.Blog.BlogId;

public class BlogUtil {

    public static final String BLOG_ID = "blogId";
    public static final String USERNAME = "username";

    public static BlogId getBlogId(String blogId, String username) {
        return new BlogId(blogId, username);
    }

    public static BlogId getDefaultBlogId() {
        return new BlogId(BLOG_ID, USERNAME);
    }

    public static Blog getDefaultBlog() {
        Blog blog = new Blog();
        blog.setId(getDefaultBlogId());
        return blog;
    }

}

package com.lprakapovich.blog.publicationservice.api.dto.utils;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.lprakapovich.blog.publicationservice.api.dto.BlogDto;
import com.lprakapovich.blog.publicationservice.api.dto.CategoryDto;
import com.lprakapovich.blog.publicationservice.api.dto.PublicationDto;
import com.lprakapovich.blog.publicationservice.model.Blog;
import com.lprakapovich.blog.publicationservice.model.Category;
import com.lprakapovich.blog.publicationservice.model.Publication;

import java.util.List;
import java.util.stream.Collectors;

// todo refactor to generics
public class DtoMappingUtils {

    private DtoMappingUtils() {}

    private static final ObjectMapper mapper = new ObjectMapper();

    public static BlogDto map(Blog blog) {
        return mapper.convertValue(blog, BlogDto.class);
    }

    public static List<BlogDto> mapBlogs(List<Blog> blogs) {
        return blogs.stream()
                .map(b -> mapper.convertValue(b, BlogDto.class))
                .collect(Collectors.toList());
    }

    public static List<CategoryDto> mapCategories(List<Category> categories) {
        return categories
                .stream()
                .map(c -> mapper.convertValue(c, CategoryDto.class))
                .collect(Collectors.toList());
    }

    public static CategoryDto map(Category category) {
        return mapper.convertValue(category, CategoryDto.class);
    }

    public static List<PublicationDto> mapPublications(List<Publication> publications) {
        return publications
                .stream()
                .map(p -> mapper.convertValue(p, PublicationDto.class))
                .collect(Collectors.toList());
    }

    public static PublicationDto map(Publication publication) {
        return mapper.convertValue(publication, PublicationDto.class);
    }
}

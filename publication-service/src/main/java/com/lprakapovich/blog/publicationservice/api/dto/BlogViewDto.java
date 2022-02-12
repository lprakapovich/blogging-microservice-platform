package com.lprakapovich.blog.publicationservice.api.dto;

import com.lprakapovich.blog.publicationservice.model.Category;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class BlogViewDto {

    private String id;
    private String name;
    private String description;
    private List<Category> categories;
    private int numberOfSubscriptions;
    private int numberOfSubscribers;
}

package com.lprakapovich.blog.publicationservice.api;

import com.lprakapovich.blog.publicationservice.api.dto.CategoryDto;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;

@Controller
@RequestMapping("/categories")
@RequiredArgsConstructor
public class CategoryRestEndpoint {

    @PostMapping
    public void createCategory(@Valid @RequestBody CategoryDto categoryDto) {

    }

    @DeleteMapping("/{id}")
    public void deleteCategory(@PathVariable String id) {

    }

    @PutMapping("/id")
    public void updateCategory(@PathVariable String id, @Valid @RequestBody CategoryDto categoryDto) {

    }
}

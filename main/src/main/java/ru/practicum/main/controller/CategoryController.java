package ru.practicum.main.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import org.springframework.http.HttpStatus;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.main.dto.CategoryCreateDTO;
import ru.practicum.main.dto.CategoryDTO;
import ru.practicum.main.service.CategoryService;

import java.util.List;

@Slf4j
@RequiredArgsConstructor
@RestController
@Validated
public class CategoryController {
    private final CategoryService categoryService;

    @PostMapping("/admin/categories")
    @ResponseStatus(HttpStatus.CREATED)
    public CategoryDTO createCategory(@Valid @RequestBody CategoryCreateDTO categoryCreateDTO) {
        log.debug("createCategory: {}", categoryCreateDTO);
        return categoryService.create(categoryCreateDTO);
    }

    @PatchMapping("/admin/categories/{id}")
    public CategoryDTO upadateCategory(@Valid @RequestBody CategoryCreateDTO categoryCreateDTO,
                                       @PathVariable Long id) {
        log.debug("getCategory: id={}, name={}", id, categoryCreateDTO.getName());
        return categoryService.upadateCategory(id, categoryCreateDTO);
    }

    @DeleteMapping("/admin/categories/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteCategory(@PathVariable Long id) {
        log.debug("deleteCategory: {}", id);
        categoryService.deleteCategory(id);
    }

    @GetMapping("/categories/{id}")
    public CategoryDTO getCategory(@PathVariable Long id) {
        log.debug("getCategory: {}", id);
        return categoryService.getCategory(id);
    }

    @GetMapping("/categories")
    public List<CategoryDTO> getListCategories(@RequestParam(required = false, defaultValue = "0") Long from,
                                               @RequestParam(required = false, defaultValue = "10") Long size) {
        log.debug("getListCategories from={}, size={}", from, size);
        return categoryService.getListCategories(from, size);
    }
}

package ru.practicum.main.mapper;

import org.springframework.stereotype.Service;
import ru.practicum.main.db.entity.Category;
import ru.practicum.main.dto.CategoryCreateDTO;
import ru.practicum.main.dto.CategoryDTO;

@Service
public class CategoryMapper {
    public CategoryDTO toCategoryDTO(Category category) {
        return new CategoryDTO(category.getId(), category.getName());
    }

    public Category toCategory(CategoryCreateDTO categoryCreateDTO, Long id) {
        return new Category(id, categoryCreateDTO.getName());
    }
}

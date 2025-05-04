package ru.practicum.main.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.main.db.CategoryRepository;
import ru.practicum.main.db.entity.Category;
import ru.practicum.main.dto.CategoryCreateDTO;
import ru.practicum.main.dto.CategoryDTO;
import ru.practicum.main.exception.NotFoundException;
import ru.practicum.main.mapper.CategoryMapper;

import java.util.List;

@RequiredArgsConstructor
@Service
public class CategoryService {
    private final CategoryMapper categoryMapper;
    private final CategoryRepository categoryRepository;

    public Category getCategoryById(Long id) {
        return categoryRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Category with id=" + id + " was not found"));
    }

    @Transactional
    public CategoryDTO create(CategoryCreateDTO categoryCreateDTO) {
        Category category = categoryMapper.toCategory(categoryCreateDTO, null);
        return categoryMapper.toCategoryDTO(categoryRepository.save(category));
    }

    @Transactional
    public void deleteCategory(Long id) {
        Long count = categoryRepository.removeById(id);
        if (count == 0) {
            throw new NotFoundException("Category with id=" + id + " was not found");
        }
    }

    @Transactional
    public CategoryDTO upadateCategory(Long id, CategoryCreateDTO categoryCreateDTO) {
        Category category = categoryMapper.toCategory(categoryCreateDTO, id);
        return categoryMapper.toCategoryDTO(categoryRepository.save(category));
    }

    public CategoryDTO getCategory(Long id) {
        return categoryMapper.toCategoryDTO(getCategoryById(id));
    }

    public List<CategoryDTO> getListCategories(Long from, Long size) {
        return categoryRepository.getListCategories(from, size).stream()
                .map(categoryMapper::toCategoryDTO)
                .toList();
    }
}

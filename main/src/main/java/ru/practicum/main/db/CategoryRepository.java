package ru.practicum.main.db;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import ru.practicum.main.db.entity.Category;

import java.util.List;

public interface CategoryRepository extends JpaRepository<Category, Long> {
    Long removeById(Long id);

    @Query(
            nativeQuery = true,
            value = """
                    select *
                        from categories
                    limit :size offset :from
                    """
    )
    List<Category> getListCategories(Long from, Long size);
}

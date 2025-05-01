package ru.practicum.main.db;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import ru.practicum.main.db.entity.Category;
import ru.practicum.main.dto.CategTest;

import java.util.List;

@Repository
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

    @Query(
            nativeQuery = true,
            value = """
                    select name
                        from categories
                    """
    )
    List<CategTest> getListCategories1();
}

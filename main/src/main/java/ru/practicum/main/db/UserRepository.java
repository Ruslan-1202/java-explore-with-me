package ru.practicum.main.db;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import ru.practicum.main.db.entity.User;

import java.util.List;

public interface UserRepository extends JpaRepository<User, Long> {

    @Query(
            nativeQuery = true,
            value = """
                    select *
                        from users
                        where (id in (:ids) or :ids is null)
                    limit :size offset :from
                    """
    )
    List<User> getUsers(List<Long> ids, Long from, Long size);
}

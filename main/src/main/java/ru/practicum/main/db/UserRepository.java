package ru.practicum.main.db;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import ru.practicum.main.db.entity.User;

import java.util.List;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {

    @Query(
            nativeQuery = true,
            value = """
                    select *
                        from users
                        where (:ids is null or id in (:ids))
                    limit :size offset :from
                    """
    )
    List<User> getUsers(List<Long> ids, Long from, Long size);
}

package movies.repository;

import movies.entity.Genre;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface GenreRepository extends JpaRepository<Genre, String> {
    boolean existsByName(String name);
    Optional<Genre> findByName(String name);
}

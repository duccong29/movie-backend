package movies.repository;

import movies.entity.Genre;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface GenreRepository extends JpaRepository<Genre, String> {
    boolean existsByNameIgnoreCase(String name);
    Optional<Genre> findByName(String name);
}

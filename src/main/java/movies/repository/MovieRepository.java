package movies.repository;

import movies.entity.Movie;
import org.springframework.data.jpa.repository.JpaRepository;

public interface MovieRepository extends JpaRepository<Movie, String> {
    boolean existsByTitle(String title);
}

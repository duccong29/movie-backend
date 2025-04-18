package movies.repository;

import movies.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface UserRepository extends JpaRepository<User, String> {
    Optional<User> findByEmail(String user);
    boolean existsByEmail(String email);
    boolean existsByUsername(String username);

}

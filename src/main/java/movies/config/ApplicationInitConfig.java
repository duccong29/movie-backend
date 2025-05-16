package movies.config;

import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.experimental.NonFinal;
import lombok.extern.slf4j.Slf4j;
import movies.constant.PredefinedRole;
import movies.entity.Genre;
import movies.entity.Role;
import movies.entity.User;
import movies.repository.GenreRepository;
import movies.repository.RoleRepository;
import movies.repository.UserRepository;
import org.springframework.boot.ApplicationRunner;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.List;
import java.util.Set;
import java.util.HashSet;

@Configuration
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@Slf4j
public class ApplicationInitConfig {
    PasswordEncoder passwordEncoder;
    UserRepository userRepository;
    RoleRepository roleRepository;
    GenreRepository genreRepository;

    @NonFinal
    static final String ADMIN_USER_EMAIL = "admin@gmail.com";
    @NonFinal
    static final String ADMIN_USER_NAME = "admin";
    @NonFinal
    static final String ADMIN_PASSWORD = "admin";

    @Bean
    @ConditionalOnProperty(
            prefix = "spring",
            value = "datasource.driverClassName",
            havingValue = "com.mysql.cj.jdbc.Driver")
    ApplicationRunner applicationRunner() {
        log.info("Initializing application.....");
        return args -> {
            Role userRole = getOrCreateRole(PredefinedRole.USER_ROLE);
            Role adminRole = getOrCreateRole(PredefinedRole.ADMIN_ROLE);

            if (userRepository.findByEmail(ADMIN_USER_EMAIL).isEmpty()) {
                Set<Role> adminRoles = new HashSet<>();
                adminRoles.add(adminRole);
                adminRoles.add(userRole);

                User adminUser = User.builder()
                        .email(ADMIN_USER_EMAIL)
                        .username(ADMIN_USER_NAME)
                        .password(passwordEncoder.encode(ADMIN_PASSWORD))
                        .roles(adminRoles)
                        .enabled(true)
                        .build();

                userRepository.save(adminUser);
                log.warn("Admin user created with default password: admin, please change it");
            }

            createDefaultGenres();

            log.info("Application initialization completed");
        };
    }

    private Role getOrCreateRole(String roleName) {
        return roleRepository.findByName(roleName)
                .orElseGet(() -> {
                    log.info("Creating new role: {}", roleName);
                    return roleRepository.save(Role.builder().name(roleName).build());
                });
    }

    private void createDefaultGenres() {
        List<String> defaultGenres = List.of(
                "Lãng Mạn", "Hành Động", "Khoa Học", "Kinh Dị", "Bí Ẩn",
                "Chiếu Rạp", "Hoạt Hình", "Hoàng Cung"
        );

        defaultGenres.forEach(name -> {
            genreRepository.findByName(name).orElseGet(() -> {
                log.info("Creating genre: {}", name);
                return genreRepository.save(Genre.builder().name(name).build());
            });
        });
    }
}

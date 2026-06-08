package com.gmail.ia.reader.infraestructure.config.init;

import lombok.RequiredArgsConstructor;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.core.annotation.Order;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

@Order(1)
@Component
@RequiredArgsConstructor
@ConditionalOnProperty(
        name = "app.db.init.enabled",
        havingValue = "true",
        matchIfMissing = false
)
public class DatabaseInitializer implements CommandLineRunner {

    private final JdbcTemplate jdbcTemplate;

    private final PasswordEncoder passwordEncoder;

    @Override
    public void run(String... args) {

        insertRolesIfNotExists();
        insertAdminIfNotExists();
    }

    private void insertRolesIfNotExists() {

        Integer countRole = jdbcTemplate.queryForObject(
                "SELECT COUNT(*) FROM role",
                Integer.class
        );

        if (countRole != null && countRole == 0) {

            jdbcTemplate.update("""
                    INSERT INTO role (id, name)
                    VALUES
                    (nextval('role_id_seq'), 'ROLE_ADMIN'),
                    (nextval('role_id_seq'), 'ROLE_USER')
                    """);
        }
    }

    private void insertAdminIfNotExists() {

        Integer countUser = jdbcTemplate.queryForObject(
                """
                SELECT COUNT(*)
                FROM user_account
                WHERE email = ?
                """,
                Integer.class,
                "corjuela1030@cue.edu.co"
        );

        if (countUser != null && countUser == 0) {

            insertUserWithRole(
                    "Christián Felippe Orjuela Forero",
                    "corjuela1030@cue.edu.co",
                    "ChristianFelippe",
                    "99",
                    "ROLE_ADMIN"
            );
        }
    }

    private void insertUserWithRole(
            String name,
            String email,
            String username,
            String rawPassword,
            String roleName) {

        Long userId = jdbcTemplate.queryForObject(
                """
                INSERT INTO user_account
                (
                    id,
                    name,
                    email,
                    username,
                    password_hash,
                    is_active,
                    created_at
                )
                VALUES
                (
                    nextval('user_account_id_seq'),
                    ?, ?, ?, ?, TRUE, NOW()
                )
                RETURNING id
                """,
                Long.class,
                name,
                email,
                username,
                passwordEncoder.encode(rawPassword)
        );

        Long roleId = jdbcTemplate.queryForObject(
                """
                SELECT id
                FROM role
                WHERE name = ?
                """,
                Long.class,
                roleName
        );

        jdbcTemplate.update(
                """
                INSERT INTO user_account_role
                (
                    user_id,
                    role_id
                )
                VALUES (?, ?)
                """,
                userId,
                roleId
        );
    }

}

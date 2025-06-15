package com.jameskavazy.dartscoreboard.user;

import org.springframework.jdbc.core.simple.JdbcClient;
import org.springframework.stereotype.Repository;
import org.springframework.util.Assert;

import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public class UserRepository {

    private final JdbcClient jdbcClient;

    public UserRepository(JdbcClient jdbcClient){
        this.jdbcClient = jdbcClient;
    }

    public Optional<User> findByEmail(String email) {
        String query = """
                SELECT *
                FROM users
                WHERE email = :email
                """;

        return jdbcClient.sql(query).param("email", email).query(User.class).optional();
    }

    public User create(User user) {
        int updated = jdbcClient.sql("INSERT INTO users(user_id, email, username) values(?, ?, ?)")
                .params(List.of(UUID.randomUUID().toString(), user.email(), user.username()))
                .update();
        Assert.state(updated == 1, "Failed to create user " + user.email());
        return user;
    }

    // TODO update username...
}

package com.jameskavazy.dartscoreboard.user;

import org.springframework.jdbc.core.simple.JdbcClient;
import org.springframework.stereotype.Repository;
import org.springframework.util.Assert;

import java.util.Optional;

@Repository
public class UserRepository {

    private final JdbcClient jdbcClient;

    public UserRepository(JdbcClient jdbcClient){
        this.jdbcClient = jdbcClient;
    }

    public Optional<User> findByEmail(String email) {
        String query = """
                SELECT email
                FROM users
                WHERE email = :email
                """;

        return jdbcClient.sql(query).param("email", email).query(User.class).optional();
    }

    public User create(User user) {
        int updated = jdbcClient.sql("INSERT INTO users(email) values(?)").params(user.email()).update();
        Assert.state(updated == 1, "Failed to create user " + user.email());
        return user;
    }
}

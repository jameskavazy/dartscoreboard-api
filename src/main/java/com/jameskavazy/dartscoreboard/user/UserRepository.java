package com.jameskavazy.dartscoreboard.user;

import org.springframework.jdbc.core.simple.JdbcClient;
import org.springframework.stereotype.Repository;
import org.springframework.util.Assert;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public class UserRepository {

    private final JdbcClient jdbcClient;

    public UserRepository(JdbcClient jdbcClient){
        this.jdbcClient = jdbcClient;
    }

    public Optional<User> findByUsername(String username) {
        String query = """
                SELECT *
                FROM users
                WHERE username = :username
                """;

        return jdbcClient.sql(query).param("username", username).query(User.class).optional();
    }

    public User create(User user) {
        int updated = jdbcClient.sql("INSERT INTO users(user_id, username, screen_name) values(?, ?, ?)")
                .params(List.of(UUID.randomUUID().toString(), user.username(), user.screenName()))
                .update();
        Assert.state(updated == 1, "Failed to create user " + user.username());
        return user;
    }

    public String userIdFromScreenName(String screenName) {
        return jdbcClient.sql("""
                SELECT user_id
                FROM users
                WHERE screen_name = :username
                """)
                .param("username", screenName)
                .query(String.class)
                .single();
    }

    public void updateScreenName(String username, String newScreenName){
        int updated = jdbcClient.sql("""
                        UPDATE users
                        SET screen_name = :newScreenName
                        WHERE username = :username
                        """)
                .param("username", username)
                .param("newScreenName", newScreenName)
                .update();
        Assert.state(updated == 1, "Could not update screen name");
    }

    public String userIdFromUsername(String username) {
        return jdbcClient.sql("""
                SELECT user_id
                FROM users
                WHERE username = :username
                """)
                .param("username", username)
                .query(String.class)
                .single();
    }
}

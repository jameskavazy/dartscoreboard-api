package com.jameskavazy.dartscoreboard.match;

import com.jameskavazy.dartscoreboard.user.User;
import com.jameskavazy.dartscoreboard.user.UserPrincipal;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Primary;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.provisioning.InMemoryUserDetailsManager;
import org.springframework.security.provisioning.UserDetailsManager;

import java.util.List;

@TestConfiguration
@Primary
public class SpringSecurityUserDetailsTestConfig {

    @Bean
    public UserDetailsService userDetailsService(){
        UserDetails userDetails = new UserPrincipal(testUser());

        return new UserDetailsManager() {

            private final InMemoryUserDetailsManager inMemoryUserDetailsManager = new InMemoryUserDetailsManager(List.of(userDetails));
            @Override
            public void createUser(UserDetails user) {
                this.inMemoryUserDetailsManager.createUser(user);
            }

            @Override
            public void updateUser(UserDetails user) {
                this.inMemoryUserDetailsManager.updateUser(user);
            }

            @Override
            public void deleteUser(String username) {
                this.inMemoryUserDetailsManager.deleteUser(username);
            }

            @Override
            public void changePassword(String oldPassword, String newPassword) {
                this.inMemoryUserDetailsManager.changePassword(oldPassword, newPassword);
            }

            @Override
            public boolean userExists(String username) {
                return this.inMemoryUserDetailsManager.userExists(username);
            }

            @Override
            public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
                return new UserPrincipal(testUser());
            }
        };
    }

    @Bean
    public User testUser(){
        return new User("user-1", "valid@username.com", "valid@username.com");
    }
}

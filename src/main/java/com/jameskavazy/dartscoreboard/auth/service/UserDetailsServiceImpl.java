package com.jameskavazy.dartscoreboard.auth.service;


import com.jameskavazy.dartscoreboard.user.User;
import com.jameskavazy.dartscoreboard.user.UserPrincipal;
import com.jameskavazy.dartscoreboard.user.UserRepository;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service
public class UserDetailsServiceImpl implements UserDetailsService {

    private final UserRepository userRepository;

    public UserDetailsServiceImpl(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        User user = userRepository.findByUsername(email)
                .orElseThrow(() -> new UsernameNotFoundException("Could not find " + email));
        return new UserPrincipal(user);
    }
}

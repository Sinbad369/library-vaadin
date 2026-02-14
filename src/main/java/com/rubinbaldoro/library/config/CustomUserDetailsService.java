package com.rubinbaldoro.library.config;

import com.rubinbaldoro.library.entity.Student;
import com.rubinbaldoro.library.repository.StudentRepository;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.Collections;

@Service
public class CustomUserDetailsService implements UserDetailsService {

    private final StudentRepository studentRepository;

    public CustomUserDetailsService(StudentRepository studentRepository) {
        this.studentRepository = studentRepository;
    }

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        // 1. Find the student in the DB
        Student student = studentRepository.findByLogin(username)
                .orElseThrow(() -> new UsernameNotFoundException("User not found with login: " + username));

        // 2. Safely check if enabled is true (handles nulls from old test data gracefully)
        boolean isAccountDisabled = !Boolean.TRUE.equals(student.getEnabled());

        // 3. Return a Spring Security User object
        return User.withUsername(student.getLogin())
                .password(student.getPassword()) // MUST be encrypted in DB
                .authorities(Collections.singletonList(new SimpleGrantedAuthority(student.getRole())))
                .disabled(isAccountDisabled) // Account is disabled if not verified
                .build();
    }
}
package com.example.portfolioappprog5resit.service;

import com.example.portfolioappprog5resit.domain.AppUser;
import com.example.portfolioappprog5resit.repository.AppUserRepository;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
public class AppUserServiceImpl implements AppUserService {
    private final AppUserRepository repo;
    private final PasswordEncoder encoder;

    public AppUserServiceImpl(AppUserRepository repo, PasswordEncoder encoder) {
        this.repo = repo; this.encoder = encoder;
    }

    @Override
    public void createNewUser(String username, String rawPassword) {
        if (repo.findByUsername(username).isPresent()) return;
        AppUser u = new AppUser();
        u.setUsername(username);
        u.setPasswordHash(encoder.encode(rawPassword));
        u.setRole("ROLE_USER");
        repo.save(u);
    }
}

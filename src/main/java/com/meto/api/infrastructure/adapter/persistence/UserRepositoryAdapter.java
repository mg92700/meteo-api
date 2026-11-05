package com.meto.api.infrastructure.adapter.persistence;

import com.meto.api.application.port.UserRepositoryPort;
import com.meto.api.domain.model.User;
import com.meto.api.infrastructure.adapter.persistence.entity.UserEntity;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class UserRepositoryAdapter implements UserRepositoryPort {

    private final UserJpaRepository jpaRepository;

    @Override
    public User findByEmail(String email) {
        UserEntity entity = jpaRepository.findByEmail(email);
        if (entity == null) return null;
        User user = new User();
        user.setId(entity.getId());
        user.setEmail(entity.getEmail());
        user.setPassword(entity.getPassword());
        user.setFirstname(entity.getFirstname());
        user.setLastname(entity.getLastname());
        user.setRole(entity.getRole());
        return user;
    }
}

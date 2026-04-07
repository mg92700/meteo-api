package com.meto.api.infrastructure.adapter.persistence;



import com.meto.api.infrastructure.adapter.persistence.entity.UserEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface UserJpaRepository extends JpaRepository<UserEntity, Long > {

    UserEntity findByEmail(String email);
}


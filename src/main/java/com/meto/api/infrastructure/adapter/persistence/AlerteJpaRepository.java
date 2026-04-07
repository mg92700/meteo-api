package com.meto.api.infrastructure.adapter.persistence;

import com.meto.api.infrastructure.adapter.persistence.entity.AlerteEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface AlerteJpaRepository extends JpaRepository<AlerteEntity,Long> {
    List<AlerteEntity> findByVilleAndActiveTrue(String ville);

}

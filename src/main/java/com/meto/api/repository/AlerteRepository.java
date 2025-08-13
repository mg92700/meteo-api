package com.meto.api.repository;

import com.meto.api.model.AlerteEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface AlerteRepository extends JpaRepository<AlerteEntity,Long> {
    List<AlerteEntity> findByVilleAndActiveTrue(String ville);

}

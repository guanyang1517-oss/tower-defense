package com.mygame.manage.repository;

import com.mygame.manage.entity.EnemyTypeEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface EnemyTypeRepository extends JpaRepository<EnemyTypeEntity, Long> {

    Optional<EnemyTypeEntity> findByEnemyCode(String enemyCode);
}

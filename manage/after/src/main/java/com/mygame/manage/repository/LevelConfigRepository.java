package com.mygame.manage.repository;

import com.mygame.manage.entity.LevelConfigEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface LevelConfigRepository extends JpaRepository<LevelConfigEntity, Long> {

    Optional<LevelConfigEntity> findByLevelCode(String levelCode);
}

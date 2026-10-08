package com.mygame.manage.repository;

import com.mygame.manage.entity.GameConfigEntity;
import org.springframework.data.jpa.repository.JpaRepository;

public interface GameConfigRepository extends JpaRepository<GameConfigEntity, Long> {
}

package com.mygame.manage.service;

import com.mygame.manage.entity.EnemyTypeEntity;
import com.mygame.manage.entity.LevelConfigEntity;

import java.util.List;
import java.util.Map;

public interface RuntimeConfigService {

    Object runtimeGameConfig();

    List<EnemyTypeEntity> runtimeEnemyTypes();

    List<LevelConfigEntity> runtimeLevels();

    Map<String, Object> runtimeLevelWaves(String levelCode);
}

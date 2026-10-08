package com.mygame.manage.service;

import com.mygame.manage.entity.LevelConfigEntity;

import java.util.List;

public interface LevelConfigService {

    List<LevelConfigEntity> list();

    LevelConfigEntity getById(Long id);

    LevelConfigEntity getByCode(String levelCode);

    LevelConfigEntity create(LevelConfigEntity request);

    LevelConfigEntity update(Long id, LevelConfigEntity request);

    void delete(Long id);
}

package com.mygame.manage.service;

import com.mygame.manage.entity.EnemyTypeEntity;

import java.util.List;

public interface EnemyTypeService {

    List<EnemyTypeEntity> list();

    EnemyTypeEntity getById(Long id);

    EnemyTypeEntity create(EnemyTypeEntity request);

    EnemyTypeEntity update(Long id, EnemyTypeEntity request);

    void delete(Long id);
}

package com.mygame.manage.service;

import com.mygame.manage.entity.GameConfigEntity;

public interface GameConfigService {

    GameConfigEntity getConfig();

    GameConfigEntity update(GameConfigEntity request);
}

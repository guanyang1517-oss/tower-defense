package com.mygame.manage.service.impl;

import com.mygame.manage.common.ApiException;
import com.mygame.manage.common.ValidationMessage;
import com.mygame.manage.entity.GameConfigEntity;
import com.mygame.manage.repository.GameConfigRepository;
import com.mygame.manage.service.GameConfigService;
import org.springframework.stereotype.Service;

@Service
public class GameConfigServiceImpl implements GameConfigService {

    private static final Long DEFAULT_ID = 1L;

    private final GameConfigRepository gameConfigRepository;

    public GameConfigServiceImpl(GameConfigRepository gameConfigRepository) {
        this.gameConfigRepository = gameConfigRepository;
    }

    @Override
    public GameConfigEntity getConfig() {
        return gameConfigRepository.findById(DEFAULT_ID).orElseGet(this::createDefaultConfig);
    }

    @Override
    public GameConfigEntity update(GameConfigEntity request) {
        validate(request);
        GameConfigEntity entity = getConfig();
        entity.setInitialMoney(request.getInitialMoney());
        entity.setInitialHealth(request.getInitialHealth());
        entity.setKillReward(request.getKillReward());
        entity.setTowerCost(request.getTowerCost());
        entity.setTowerRange(request.getTowerRange());
        entity.setBlockerCost(request.getBlockerCost());
        entity.setBlockerMaxHealth(request.getBlockerMaxHealth());
        entity.setBlockerReturnDamage(request.getBlockerReturnDamage());
        entity.setEnemyBaseSpeed(request.getEnemyBaseSpeed());
        entity.setEnemyBaseHealth(request.getEnemyBaseHealth());
        entity.setEnemyAttackDamage(request.getEnemyAttackDamage());
        return gameConfigRepository.save(entity);
    }

    private GameConfigEntity createDefaultConfig() {
        GameConfigEntity entity = new GameConfigEntity();
        entity.setId(DEFAULT_ID);
        entity.setInitialMoney(600);
        entity.setInitialHealth(10);
        entity.setKillReward(50);
        entity.setTowerCost(100);
        entity.setTowerRange(250F);
        entity.setBlockerCost(150);
        entity.setBlockerMaxHealth(1500F);
        entity.setBlockerReturnDamage(1F);
        entity.setEnemyBaseSpeed(150F);
        entity.setEnemyBaseHealth(10);
        entity.setEnemyAttackDamage(50F);
        return gameConfigRepository.save(entity);
    }

    private void validate(GameConfigEntity request) {
        requireNonNegative(request.getInitialMoney(), "initialMoney");
        requireNonNegative(request.getInitialHealth(), "initialHealth");
        requireNonNegative(request.getKillReward(), "killReward");
        requireNonNegative(request.getTowerCost(), "towerCost");
        requirePositive(request.getTowerRange(), "towerRange");
        requireNonNegative(request.getBlockerCost(), "blockerCost");
        requirePositive(request.getBlockerMaxHealth(), "blockerMaxHealth");
        requireNonNegative(request.getBlockerReturnDamage(), "blockerReturnDamage");
        requirePositive(request.getEnemyBaseSpeed(), "enemyBaseSpeed");
        requirePositive(request.getEnemyBaseHealth(), "enemyBaseHealth");
        requireNonNegative(request.getEnemyAttackDamage(), "enemyAttackDamage");
    }

    private void requireNonNegative(Number value, String fieldName) {
        if (value == null || value.doubleValue() < 0) {
            throw new ApiException(ValidationMessage.nonNegative(fieldName));
        }
    }

    private void requirePositive(Number value, String fieldName) {
        if (value == null || value.doubleValue() <= 0) {
            throw new ApiException(ValidationMessage.positive(fieldName));
        }
    }
}

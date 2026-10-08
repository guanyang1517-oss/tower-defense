package com.mygame.manage.service.impl;

import com.mygame.manage.common.ApiException;
import com.mygame.manage.common.ValidationMessage;
import com.mygame.manage.entity.EnemyTypeEntity;
import com.mygame.manage.repository.EnemyTypeRepository;
import com.mygame.manage.service.EnemyTypeService;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class EnemyTypeServiceImpl implements EnemyTypeService {

    private final EnemyTypeRepository enemyTypeRepository;

    public EnemyTypeServiceImpl(EnemyTypeRepository enemyTypeRepository) {
        this.enemyTypeRepository = enemyTypeRepository;
    }

    @Override
    public List<EnemyTypeEntity> list() {
        return enemyTypeRepository.findAll();
    }

    @Override
    public EnemyTypeEntity getById(Long id) {
        return enemyTypeRepository.findById(id).orElseThrow(() -> new ApiException("怪物类型不存在"));
    }

    @Override
    public EnemyTypeEntity create(EnemyTypeEntity request) {
        validate(request, null);
        request.setId(null);
        return enemyTypeRepository.save(request);
    }

    @Override
    public EnemyTypeEntity update(Long id, EnemyTypeEntity request) {
        validate(request, id);
        EnemyTypeEntity entity = getById(id);
        entity.setEnemyCode(request.getEnemyCode());
        entity.setEnemyName(request.getEnemyName());
        entity.setTexturePath(request.getTexturePath());
        entity.setAttackTexturePath(request.getAttackTexturePath());
        entity.setHealth(request.getHealth());
        entity.setSpeed(request.getSpeed());
        entity.setAttackDamage(request.getAttackDamage());
        entity.setReward(request.getReward());
        entity.setEnabled(request.getEnabled());
        return enemyTypeRepository.save(entity);
    }

    @Override
    public void delete(Long id) {
        enemyTypeRepository.deleteById(id);
    }

    private void validate(EnemyTypeEntity request, Long currentId) {
        requireText(request.getEnemyCode(), "enemyCode");
        requireText(request.getEnemyName(), "enemyName");
        requireText(request.getTexturePath(), "texturePath");
        requirePositive(request.getHealth(), "health");
        requirePositive(request.getSpeed(), "speed");
        requireNonNegative(request.getAttackDamage(), "attackDamage");
        requireNonNegative(request.getReward(), "reward");
        if (request.getEnabled() == null) {
            throw new ApiException(ValidationMessage.required("enabled"));
        }
        enemyTypeRepository.findByEnemyCode(request.getEnemyCode()).ifPresent(found -> {
            if (currentId == null || !currentId.equals(found.getId())) {
                throw new ApiException("怪物编码已存在");
            }
        });
    }

    private void requireText(String value, String fieldName) {
        if (value == null || value.trim().isEmpty()) {
            throw new ApiException(ValidationMessage.required(fieldName));
        }
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

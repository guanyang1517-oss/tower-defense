package com.mygame.manage.service.impl;

import com.mygame.manage.common.ApiException;
import com.mygame.manage.common.ValidationMessage;
import com.mygame.manage.entity.LevelConfigEntity;
import com.mygame.manage.entity.WaveConfigEntity;
import com.mygame.manage.repository.LevelConfigRepository;
import com.mygame.manage.repository.WaveConfigRepository;
import com.mygame.manage.repository.WaveSpawnConfigRepository;
import com.mygame.manage.service.LevelConfigService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class LevelConfigServiceImpl implements LevelConfigService {

    private final LevelConfigRepository levelConfigRepository;
    private final WaveConfigRepository waveConfigRepository;
    private final WaveSpawnConfigRepository waveSpawnConfigRepository;

    public LevelConfigServiceImpl(LevelConfigRepository levelConfigRepository,
                                  WaveConfigRepository waveConfigRepository,
                                  WaveSpawnConfigRepository waveSpawnConfigRepository) {
        this.levelConfigRepository = levelConfigRepository;
        this.waveConfigRepository = waveConfigRepository;
        this.waveSpawnConfigRepository = waveSpawnConfigRepository;
    }

    @Override
    public List<LevelConfigEntity> list() {
        return levelConfigRepository.findAll();
    }

    @Override
    public LevelConfigEntity getById(Long id) {
        return levelConfigRepository.findById(id).orElseThrow(() -> new ApiException("关卡不存在"));
    }

    @Override
    public LevelConfigEntity getByCode(String levelCode) {
        return levelConfigRepository.findByLevelCode(levelCode).orElseThrow(() -> new ApiException("关卡不存在"));
    }

    @Override
    public LevelConfigEntity create(LevelConfigEntity request) {
        validate(request, null);
        request.setId(null);
        return levelConfigRepository.save(request);
    }

    @Override
    public LevelConfigEntity update(Long id, LevelConfigEntity request) {
        validate(request, id);
        LevelConfigEntity entity = getById(id);
        entity.setLevelCode(request.getLevelCode());
        entity.setLevelName(request.getLevelName());
        entity.setMapPath(request.getMapPath());
        entity.setMaxWave(request.getMaxWave());
        entity.setEnabled(request.getEnabled());
        entity.setRemark(request.getRemark());
        return levelConfigRepository.save(entity);
    }

    @Override
    @Transactional
    public void delete(Long id) {
        List<WaveConfigEntity> waves = waveConfigRepository.findByLevelIdOrderByWaveNumberAsc(id);
        for (WaveConfigEntity wave : waves) {
            waveSpawnConfigRepository.deleteByWaveId(wave.getId());
        }
        waveConfigRepository.deleteAll(waves);
        levelConfigRepository.deleteById(id);
    }

    private void validate(LevelConfigEntity request, Long currentId) {
        requireText(request.getLevelCode(), "levelCode");
        requireText(request.getLevelName(), "levelName");
        requireText(request.getMapPath(), "mapPath");
        requirePositive(request.getMaxWave(), "maxWave");
        if (request.getEnabled() == null) {
            throw new ApiException(ValidationMessage.required("enabled"));
        }
        levelConfigRepository.findByLevelCode(request.getLevelCode()).ifPresent(found -> {
            if (currentId == null || !currentId.equals(found.getId())) {
                throw new ApiException("关卡编码已存在");
            }
        });
    }

    private void requireText(String value, String fieldName) {
        if (value == null || value.trim().isEmpty()) {
            throw new ApiException(ValidationMessage.required(fieldName));
        }
    }

    private void requirePositive(Number value, String fieldName) {
        if (value == null || value.doubleValue() <= 0) {
            throw new ApiException(ValidationMessage.positive(fieldName));
        }
    }
}

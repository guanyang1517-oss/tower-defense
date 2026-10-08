package com.mygame.manage.controller;

import com.mygame.manage.common.ApiResponse;
import com.mygame.manage.entity.WaveConfigEntity;
import com.mygame.manage.entity.WaveSpawnConfigEntity;
import com.mygame.manage.service.WaveConfigService;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api")
public class WaveConfigController {

    private final WaveConfigService waveConfigService;

    public WaveConfigController(WaveConfigService waveConfigService) {
        this.waveConfigService = waveConfigService;
    }

    @GetMapping("/levels/{levelId}/waves")
    public ApiResponse<List<WaveConfigEntity>> list(@PathVariable Long levelId) {
        return ApiResponse.ok(waveConfigService.listByLevelId(levelId));
    }

    @PostMapping("/levels/{levelId}/waves")
    public ApiResponse<WaveConfigEntity> create(@PathVariable Long levelId, @RequestBody WaveConfigEntity request) {
        return ApiResponse.ok(waveConfigService.create(levelId, request));
    }

    @PutMapping("/waves/{id}")
    public ApiResponse<WaveConfigEntity> update(@PathVariable Long id, @RequestBody WaveConfigEntity request) {
        return ApiResponse.ok(waveConfigService.update(id, request));
    }

    @DeleteMapping("/waves/{id}")
    public ApiResponse<Void> delete(@PathVariable Long id) {
        waveConfigService.delete(id);
        return ApiResponse.ok(null);
    }

    @GetMapping("/waves/{waveId}/spawns")
    public ApiResponse<List<WaveSpawnConfigEntity>> listSpawns(@PathVariable Long waveId) {
        return ApiResponse.ok(waveConfigService.listSpawns(waveId));
    }

    @PostMapping("/waves/{waveId}/spawns")
    public ApiResponse<WaveSpawnConfigEntity> createSpawn(@PathVariable Long waveId, @RequestBody WaveSpawnConfigEntity request) {
        return ApiResponse.ok(waveConfigService.createSpawn(waveId, request));
    }

    @PutMapping("/spawns/{id}")
    public ApiResponse<WaveSpawnConfigEntity> updateSpawn(@PathVariable Long id, @RequestBody WaveSpawnConfigEntity request) {
        return ApiResponse.ok(waveConfigService.updateSpawn(id, request));
    }

    @DeleteMapping("/spawns/{id}")
    public ApiResponse<Void> deleteSpawn(@PathVariable Long id) {
        waveConfigService.deleteSpawn(id);
        return ApiResponse.ok(null);
    }
}

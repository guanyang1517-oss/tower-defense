package com.mygame.manage.controller;

import com.mygame.manage.common.ApiResponse;
import com.mygame.manage.entity.LevelConfigEntity;
import com.mygame.manage.service.LevelConfigService;
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
@RequestMapping("/api/levels")
public class LevelConfigController {

    private final LevelConfigService levelConfigService;

    public LevelConfigController(LevelConfigService levelConfigService) {
        this.levelConfigService = levelConfigService;
    }

    @GetMapping
    public ApiResponse<List<LevelConfigEntity>> list() {
        return ApiResponse.ok(levelConfigService.list());
    }

    @GetMapping("/{id}")
    public ApiResponse<LevelConfigEntity> detail(@PathVariable Long id) {
        return ApiResponse.ok(levelConfigService.getById(id));
    }

    @PostMapping
    public ApiResponse<LevelConfigEntity> create(@RequestBody LevelConfigEntity request) {
        return ApiResponse.ok(levelConfigService.create(request));
    }

    @PutMapping("/{id}")
    public ApiResponse<LevelConfigEntity> update(@PathVariable Long id, @RequestBody LevelConfigEntity request) {
        return ApiResponse.ok(levelConfigService.update(id, request));
    }

    @DeleteMapping("/{id}")
    public ApiResponse<Void> delete(@PathVariable Long id) {
        levelConfigService.delete(id);
        return ApiResponse.ok(null);
    }
}

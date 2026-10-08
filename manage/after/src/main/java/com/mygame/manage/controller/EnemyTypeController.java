package com.mygame.manage.controller;

import com.mygame.manage.common.ApiResponse;
import com.mygame.manage.entity.EnemyTypeEntity;
import com.mygame.manage.service.EnemyTypeService;
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
@RequestMapping("/api/enemy-types")
public class EnemyTypeController {

    private final EnemyTypeService enemyTypeService;

    public EnemyTypeController(EnemyTypeService enemyTypeService) {
        this.enemyTypeService = enemyTypeService;
    }

    @GetMapping
    public ApiResponse<List<EnemyTypeEntity>> list() {
        return ApiResponse.ok(enemyTypeService.list());
    }

    @GetMapping("/{id}")
    public ApiResponse<EnemyTypeEntity> detail(@PathVariable Long id) {
        return ApiResponse.ok(enemyTypeService.getById(id));
    }

    @PostMapping
    public ApiResponse<EnemyTypeEntity> create(@RequestBody EnemyTypeEntity request) {
        return ApiResponse.ok(enemyTypeService.create(request));
    }

    @PutMapping("/{id}")
    public ApiResponse<EnemyTypeEntity> update(@PathVariable Long id, @RequestBody EnemyTypeEntity request) {
        return ApiResponse.ok(enemyTypeService.update(id, request));
    }

    @DeleteMapping("/{id}")
    public ApiResponse<Void> delete(@PathVariable Long id) {
        enemyTypeService.delete(id);
        return ApiResponse.ok(null);
    }
}

package com.mygame.manage.controller;

import com.mygame.manage.common.ApiResponse;
import com.mygame.manage.entity.GameConfigEntity;
import com.mygame.manage.service.GameConfigService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/game-config")
public class GameConfigController {

    private final GameConfigService gameConfigService;

    public GameConfigController(GameConfigService gameConfigService) {
        this.gameConfigService = gameConfigService;
    }

    @GetMapping
    public ApiResponse<GameConfigEntity> getConfig() {
        return ApiResponse.ok(gameConfigService.getConfig());
    }

    @PutMapping
    public ApiResponse<GameConfigEntity> update(@RequestBody GameConfigEntity request) {
        return ApiResponse.ok(gameConfigService.update(request));
    }
}

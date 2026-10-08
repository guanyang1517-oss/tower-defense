package com.mygame.manage.controller;

import com.mygame.manage.common.ApiResponse;
import com.mygame.manage.service.RuntimeConfigService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * 游戏运行时配置接口。
 *
 * <p>该控制器面向游戏客户端提供已启用的配置数据，包括全局配置、怪物类型、
 * 关卡列表以及指定关卡的波次与刷怪明细。</p>
 */
@RestController
@RequestMapping("/api/runtime")
public class RuntimeController {

    private final RuntimeConfigService runtimeConfigService;

    public RuntimeController(RuntimeConfigService runtimeConfigService) {
        this.runtimeConfigService = runtimeConfigService;
    }

    /**
     * 获取游戏全局运行配置。
     *
     * @return 全局数值配置，如初始金币、生命值等客户端启动所需参数
     */
    @GetMapping("/game-config")
    public ApiResponse<Object> gameConfig() {
        return ApiResponse.ok(runtimeConfigService.runtimeGameConfig());
    }

    /**
     * 获取启用状态的怪物类型列表。
     *
     * @return 客户端生成敌人所需的怪物类型、属性和资源配置
     */
    @GetMapping("/enemy-types")
    public ApiResponse<Object> enemyTypes() {
        return ApiResponse.ok(runtimeConfigService.runtimeEnemyTypes());
    }

    /**
     * 获取启用状态的关卡配置列表。
     *
     * @return 客户端关卡选择与关卡加载所需的关卡基础信息
     */
    @GetMapping("/levels")
    public ApiResponse<Object> levels() {
        return ApiResponse.ok(runtimeConfigService.runtimeLevels());
    }

    /**
     * 获取指定关卡的波次与刷怪明细。
     *
     * @param levelCode 关卡编码
     * @return 指定关卡下启用的波次配置和刷怪配置
     */
    @GetMapping("/levels/{levelCode}/waves")
    public ApiResponse<Object> waves(@PathVariable String levelCode) {
        return ApiResponse.ok(runtimeConfigService.runtimeLevelWaves(levelCode));
    }
}

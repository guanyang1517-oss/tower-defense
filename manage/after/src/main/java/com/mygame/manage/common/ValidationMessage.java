package com.mygame.manage.common;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

public final class ValidationMessage {

    private static final Map<String, String> FIELD_LABELS;

    static {
        Map<String, String> labels = new HashMap<String, String>();
        labels.put("initialMoney", "初始金币");
        labels.put("initialHealth", "初始生命值");
        labels.put("killReward", "击杀奖励");
        labels.put("towerCost", "防御塔价格");
        labels.put("towerRange", "防御塔射程");
        labels.put("blockerCost", "路障价格");
        labels.put("blockerMaxHealth", "路障最大血量");
        labels.put("blockerReturnDamage", "路障反伤");
        labels.put("enemyBaseSpeed", "敌人基础速度");
        labels.put("enemyBaseHealth", "敌人基础血量");
        labels.put("enemyAttackDamage", "敌人基础攻击");
        labels.put("enemyCode", "怪物编码");
        labels.put("enemyName", "怪物名称");
        labels.put("texturePath", "贴图路径");
        labels.put("attackTexturePath", "攻击贴图");
        labels.put("health", "血量");
        labels.put("speed", "速度");
        labels.put("attackDamage", "攻击");
        labels.put("reward", "奖励");
        labels.put("enabled", "启用状态");
        labels.put("levelCode", "关卡编码");
        labels.put("levelName", "关卡名称");
        labels.put("mapPath", "地图路径");
        labels.put("maxWave", "总波次");
        labels.put("remark", "备注");
        labels.put("waveNumber", "波次编号");
        labels.put("breakTime", "波间间隔");
        labels.put("spawnCount", "生成数量");
        labels.put("spawnInterval", "生成间隔");
        labels.put("pathIndex", "路径索引");
        labels.put("spawnOrder", "生成顺序");
        labels.put("enemyTypeId", "怪物类型");
        FIELD_LABELS = Collections.unmodifiableMap(labels);
    }

    private ValidationMessage() {
    }

    public static String required(String fieldName) {
        return fieldLabel(fieldName) + "不能为空";
    }

    public static String positive(String fieldName) {
        return fieldLabel(fieldName) + "必须大于 0";
    }

    public static String nonNegative(String fieldName) {
        return fieldLabel(fieldName) + "不能小于 0";
    }

    public static String fieldLabel(String fieldName) {
        String label = FIELD_LABELS.get(fieldName);
        return label == null ? fieldName : label;
    }
}

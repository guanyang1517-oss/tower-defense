package com.mygame.td.config;

import com.mygame.td.service.RemoteConfigService;

public class GameConfig {

    public static final int DEFAULT_INITIAL_MONEY = 600;
    public static final int DEFAULT_INITIAL_HEALTH = 10;
    public static final int DEFAULT_KILL_REWARD = 50;
    public static final int DEFAULT_TOWER_COST = 100;
    public static final float DEFAULT_TOWER_RANGE = 250f;
    public static final int DEFAULT_BLOCKER_COST = 150;
    public static final float DEFAULT_BLOCKER_MAX_HEALTH = 1500f;
    public static final float DEFAULT_BLOCKER_RETURN_DAMAGE = 1f;
    public static final float DEFAULT_ENEMY_BASE_SPEED = 150f;
    public static final int DEFAULT_ENEMY_BASE_HEALTH = 10;
    public static final float DEFAULT_ENEMY_ATTACK_DAMAGE = 50f;

    public static int INITIAL_MONEY = DEFAULT_INITIAL_MONEY;
    public static int INITIAL_HEALTH = DEFAULT_INITIAL_HEALTH;
    public static int KILL_REWARD = DEFAULT_KILL_REWARD;
    public static int TOWER_COST = DEFAULT_TOWER_COST;
    public static float TOWER_RANGE = DEFAULT_TOWER_RANGE;
    public static int BLOCKER_COST = DEFAULT_BLOCKER_COST;
    public static float BLOCKER_MAX_HEALTH = DEFAULT_BLOCKER_MAX_HEALTH;
    public static float BLOCKER_RETURN_DAMAGE = DEFAULT_BLOCKER_RETURN_DAMAGE;
    public static float ENEMY_BASE_SPEED = DEFAULT_ENEMY_BASE_SPEED;
    public static int ENEMY_BASE_HEALTH = DEFAULT_ENEMY_BASE_HEALTH;
    public static float ENEMY_ATTACK_DAMAGE = DEFAULT_ENEMY_ATTACK_DAMAGE;

    public static void resetDefaults() {
        INITIAL_MONEY = DEFAULT_INITIAL_MONEY;
        INITIAL_HEALTH = DEFAULT_INITIAL_HEALTH;
        KILL_REWARD = DEFAULT_KILL_REWARD;
        TOWER_COST = DEFAULT_TOWER_COST;
        TOWER_RANGE = DEFAULT_TOWER_RANGE;
        BLOCKER_COST = DEFAULT_BLOCKER_COST;
        BLOCKER_MAX_HEALTH = DEFAULT_BLOCKER_MAX_HEALTH;
        BLOCKER_RETURN_DAMAGE = DEFAULT_BLOCKER_RETURN_DAMAGE;
        ENEMY_BASE_SPEED = DEFAULT_ENEMY_BASE_SPEED;
        ENEMY_BASE_HEALTH = DEFAULT_ENEMY_BASE_HEALTH;
        ENEMY_ATTACK_DAMAGE = DEFAULT_ENEMY_ATTACK_DAMAGE;
    }

    public static void applyRemote(RemoteConfigService.GameBalanceData data) {
        if (data == null) {
            return;
        }

        INITIAL_MONEY = data.initialMoney;
        INITIAL_HEALTH = data.initialHealth;
        KILL_REWARD = data.killReward;
        TOWER_COST = data.towerCost;
        TOWER_RANGE = data.towerRange;
        BLOCKER_COST = data.blockerCost;
        BLOCKER_MAX_HEALTH = data.blockerMaxHealth;
        BLOCKER_RETURN_DAMAGE = data.blockerReturnDamage;
        ENEMY_BASE_SPEED = data.enemyBaseSpeed;
        ENEMY_BASE_HEALTH = data.enemyBaseHealth;
        ENEMY_ATTACK_DAMAGE = data.enemyAttackDamage;
    }
}

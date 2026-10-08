package com.mygame.manage.entity;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.PrePersist;
import javax.persistence.PreUpdate;
import javax.persistence.Table;
import java.time.LocalDateTime;

@Entity
@Table(name = "game_config")
public class GameConfigEntity {

    @Id
    private Long id;

    @Column(nullable = false)
    private Integer initialMoney;

    @Column(nullable = false)
    private Integer initialHealth;

    @Column(nullable = false)
    private Integer killReward;

    @Column(nullable = false)
    private Integer towerCost;

    @Column(nullable = false)
    private Float towerRange;

    @Column(nullable = false)
    private Integer blockerCost;

    @Column(nullable = false)
    private Float blockerMaxHealth;

    @Column(nullable = false)
    private Float blockerReturnDamage;

    @Column(nullable = false)
    private Float enemyBaseSpeed;

    @Column(nullable = false)
    private Integer enemyBaseHealth;

    @Column(nullable = false)
    private Float enemyAttackDamage;

    @Column(nullable = false)
    private LocalDateTime updateTime;

    @PrePersist
    @PreUpdate
    public void touch() {
        this.updateTime = LocalDateTime.now();
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Integer getInitialMoney() {
        return initialMoney;
    }

    public void setInitialMoney(Integer initialMoney) {
        this.initialMoney = initialMoney;
    }

    public Integer getInitialHealth() {
        return initialHealth;
    }

    public void setInitialHealth(Integer initialHealth) {
        this.initialHealth = initialHealth;
    }

    public Integer getKillReward() {
        return killReward;
    }

    public void setKillReward(Integer killReward) {
        this.killReward = killReward;
    }

    public Integer getTowerCost() {
        return towerCost;
    }

    public void setTowerCost(Integer towerCost) {
        this.towerCost = towerCost;
    }

    public Float getTowerRange() {
        return towerRange;
    }

    public void setTowerRange(Float towerRange) {
        this.towerRange = towerRange;
    }

    public Integer getBlockerCost() {
        return blockerCost;
    }

    public void setBlockerCost(Integer blockerCost) {
        this.blockerCost = blockerCost;
    }

    public Float getBlockerMaxHealth() {
        return blockerMaxHealth;
    }

    public void setBlockerMaxHealth(Float blockerMaxHealth) {
        this.blockerMaxHealth = blockerMaxHealth;
    }

    public Float getBlockerReturnDamage() {
        return blockerReturnDamage;
    }

    public void setBlockerReturnDamage(Float blockerReturnDamage) {
        this.blockerReturnDamage = blockerReturnDamage;
    }

    public Float getEnemyBaseSpeed() {
        return enemyBaseSpeed;
    }

    public void setEnemyBaseSpeed(Float enemyBaseSpeed) {
        this.enemyBaseSpeed = enemyBaseSpeed;
    }

    public Integer getEnemyBaseHealth() {
        return enemyBaseHealth;
    }

    public void setEnemyBaseHealth(Integer enemyBaseHealth) {
        this.enemyBaseHealth = enemyBaseHealth;
    }

    public Float getEnemyAttackDamage() {
        return enemyAttackDamage;
    }

    public void setEnemyAttackDamage(Float enemyAttackDamage) {
        this.enemyAttackDamage = enemyAttackDamage;
    }

    public LocalDateTime getUpdateTime() {
        return updateTime;
    }

    public void setUpdateTime(LocalDateTime updateTime) {
        this.updateTime = updateTime;
    }
}

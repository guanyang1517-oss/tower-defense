package com.mygame.manage.entity;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.PrePersist;
import javax.persistence.PreUpdate;
import javax.persistence.Table;
import java.time.LocalDateTime;

@Entity
@Table(name = "enemy_type")
public class EnemyTypeEntity extends IdEntity {

    @Column(nullable = false, unique = true, length = 64)
    private String enemyCode;

    @Column(nullable = false, length = 64)
    private String enemyName;

    @Column(nullable = false, length = 255)
    private String texturePath;

    @Column(length = 255)
    private String attackTexturePath;

    @Column(nullable = false)
    private Integer health;

    @Column(nullable = false)
    private Float speed;

    @Column(nullable = false)
    private Float attackDamage;

    @Column(nullable = false)
    private Integer reward;

    @Column(nullable = false)
    private Boolean enabled;

    @Column(nullable = false)
    private LocalDateTime createTime;

    @Column(nullable = false)
    private LocalDateTime updateTime;

    @PrePersist
    public void prePersist() {
        LocalDateTime now = LocalDateTime.now();
        this.createTime = now;
        this.updateTime = now;
    }

    @PreUpdate
    public void preUpdate() {
        this.updateTime = LocalDateTime.now();
    }

    public String getEnemyCode() {
        return enemyCode;
    }

    public void setEnemyCode(String enemyCode) {
        this.enemyCode = enemyCode;
    }

    public String getEnemyName() {
        return enemyName;
    }

    public void setEnemyName(String enemyName) {
        this.enemyName = enemyName;
    }

    public String getTexturePath() {
        return texturePath;
    }

    public void setTexturePath(String texturePath) {
        this.texturePath = texturePath;
    }

    public String getAttackTexturePath() {
        return attackTexturePath;
    }

    public void setAttackTexturePath(String attackTexturePath) {
        this.attackTexturePath = attackTexturePath;
    }

    public Integer getHealth() {
        return health;
    }

    public void setHealth(Integer health) {
        this.health = health;
    }

    public Float getSpeed() {
        return speed;
    }

    public void setSpeed(Float speed) {
        this.speed = speed;
    }

    public Float getAttackDamage() {
        return attackDamage;
    }

    public void setAttackDamage(Float attackDamage) {
        this.attackDamage = attackDamage;
    }

    public Integer getReward() {
        return reward;
    }

    public void setReward(Integer reward) {
        this.reward = reward;
    }

    public Boolean getEnabled() {
        return enabled;
    }

    public void setEnabled(Boolean enabled) {
        this.enabled = enabled;
    }

    public LocalDateTime getCreateTime() {
        return createTime;
    }

    public void setCreateTime(LocalDateTime createTime) {
        this.createTime = createTime;
    }

    public LocalDateTime getUpdateTime() {
        return updateTime;
    }

    public void setUpdateTime(LocalDateTime updateTime) {
        this.updateTime = updateTime;
    }
}

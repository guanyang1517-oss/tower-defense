package com.mygame.manage.entity;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.PrePersist;
import javax.persistence.PreUpdate;
import javax.persistence.Table;
import java.time.LocalDateTime;

@Entity
@Table(name = "wave_spawn_config")
public class WaveSpawnConfigEntity extends IdEntity {

    @Column(nullable = false)
    private Long waveId;

    @Column(nullable = false)
    private Long enemyTypeId;

    @Column(nullable = false)
    private Integer spawnCount;

    @Column(nullable = false)
    private Float spawnInterval;

    @Column(nullable = false)
    private Integer pathIndex;

    @Column(nullable = false)
    private Integer spawnOrder;

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

    public Long getWaveId() {
        return waveId;
    }

    public void setWaveId(Long waveId) {
        this.waveId = waveId;
    }

    public Long getEnemyTypeId() {
        return enemyTypeId;
    }

    public void setEnemyTypeId(Long enemyTypeId) {
        this.enemyTypeId = enemyTypeId;
    }

    public Integer getSpawnCount() {
        return spawnCount;
    }

    public void setSpawnCount(Integer spawnCount) {
        this.spawnCount = spawnCount;
    }

    public Float getSpawnInterval() {
        return spawnInterval;
    }

    public void setSpawnInterval(Float spawnInterval) {
        this.spawnInterval = spawnInterval;
    }

    public Integer getPathIndex() {
        return pathIndex;
    }

    public void setPathIndex(Integer pathIndex) {
        this.pathIndex = pathIndex;
    }

    public Integer getSpawnOrder() {
        return spawnOrder;
    }

    public void setSpawnOrder(Integer spawnOrder) {
        this.spawnOrder = spawnOrder;
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

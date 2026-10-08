package com.mygame.manage.entity;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.PrePersist;
import javax.persistence.PreUpdate;
import javax.persistence.Table;
import java.time.LocalDateTime;

@Entity
@Table(name = "wave_config")
public class WaveConfigEntity extends IdEntity {

    @Column(nullable = false)
    private Long levelId;

    @Column(nullable = false)
    private Integer waveNumber;

    @Column(nullable = false)
    private Float breakTime;

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

    public Long getLevelId() {
        return levelId;
    }

    public void setLevelId(Long levelId) {
        this.levelId = levelId;
    }

    public Integer getWaveNumber() {
        return waveNumber;
    }

    public void setWaveNumber(Integer waveNumber) {
        this.waveNumber = waveNumber;
    }

    public Float getBreakTime() {
        return breakTime;
    }

    public void setBreakTime(Float breakTime) {
        this.breakTime = breakTime;
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

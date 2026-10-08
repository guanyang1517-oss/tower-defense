package com.mygame.td.entity;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.Array;
import com.mygame.td.config.GameConfig;
import com.mygame.td.service.RemoteConfigService;

public class Enemy implements IDamageable {

    public Vector2 position;

    private Texture staticImg;
    private Animation<TextureRegion> attackAnim;
    private float stateTime = 0f;
    private Array<Vector2> path;
    private int currentPointIndex = 0;
    private float speed = GameConfig.ENEMY_BASE_SPEED;
    public boolean arrived = false;
    public boolean isAlive = true;
    public int health = GameConfig.ENEMY_BASE_HEALTH;
    public boolean isBlocked = false;
    public Blocker blockedBy = null;
    private float attackDamage = GameConfig.ENEMY_ATTACK_DAMAGE;
    private float attackInterval = 1.0f;
    private float attackTimer = 0f;
    private int reward = GameConfig.KILL_REWARD;

    public Enemy(Texture staticImg, Texture animSheet, Array<Vector2> path) {
        this(staticImg, animSheet, path, null);
    }

    public Enemy(Texture staticImg, Texture animSheet, Array<Vector2> path, RemoteConfigService.EnemyTypeData enemyTypeData) {
        this.staticImg = staticImg;
        this.path = path;

        if (enemyTypeData != null) {
            this.speed = enemyTypeData.speed;
            this.health = enemyTypeData.health;
            this.attackDamage = enemyTypeData.attackDamage;
            this.reward = enemyTypeData.reward;
        }

        if (animSheet != null) {
            try {
                int frameCols = 4;
                int frameWidth = animSheet.getWidth() / frameCols;
                int frameHeight = animSheet.getHeight();

                TextureRegion[][] tmp = TextureRegion.split(animSheet, frameWidth, frameHeight);
                Array<TextureRegion> frames = new Array<>();

                if (tmp.length > 0) {
                    for (int j = 0; j < tmp[0].length; j++) {
                        frames.add(tmp[0][j]);
                    }
                }

                attackAnim = new Animation<>(0.1f, frames, Animation.PlayMode.LOOP);
            } catch (Exception e) {
                attackAnim = null;
            }
        }

        if (path != null && path.size > 0) {
            this.position = new Vector2(path.get(0).x, path.get(0).y);
            this.currentPointIndex = 0;
        } else {
            this.position = new Vector2(0, 0);
        }
    }

    public void update(float delta) {
        if (isBlocked) {
            if (blockedBy == null || !blockedBy.isAlive()) {
                isBlocked = false;
                blockedBy = null;
                attackTimer = 0;
                stateTime = 0;
            } else {
                stateTime += delta;
                attackTimer += delta;

                if (attackTimer >= attackInterval) {
                    attackTimer = 0;
                    blockedBy.takeDamage((int) attackDamage);
                }
                return;
            }
        }

        if (arrived || !isAlive || path == null) return;

        int nextIndex = currentPointIndex + 1;
        if (nextIndex >= path.size) {
            arrived = true;
            return;
        }

        Vector2 target = path.get(nextIndex);
        Vector2 direction = new Vector2(target.x - position.x, target.y - position.y).nor();

        position.x += direction.x * speed * delta;
        position.y += direction.y * speed * delta;

        if (position.dst(target) < 5) {
            currentPointIndex++;
        }
    }

    public void draw(SpriteBatch batch) {
        if (!isAlive) return;

        float staticW = 100;
        float staticH = 100;
        float animW = 100;
        float animH = 100;

        float staticX = position.x - staticW / 2f + 50f;
        float staticY = position.y - staticH / 2f + 50f;

        if (isBlocked && attackAnim != null) {
            TextureRegion currentFrame = attackAnim.getKeyFrame(stateTime, true);
            batch.draw(currentFrame, staticX, staticY, animW, animH);
        } else {
            batch.draw(staticImg, staticX, staticY, staticW, staticH);
        }
    }

    @Override
    public void takeDamage(int damage) {
        health -= damage;
        if (health <= 0) isAlive = false;
    }

    @Override
    public boolean isAlive() {
        return isAlive;
    }

    @Override
    public Vector2 getPosition() {
        return position;
    }

    public int getReward() {
        return reward;
    }
}

package com.mygame.td.entity;

import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.Array;
import com.mygame.td.config.GameConfig;
import com.mygame.td.screen.SettingsScreen;

public class Tower {

    private Vector2 position;
    // --- 修改：从配置表读取射程 ---
    private float range = GameConfig.TOWER_RANGE;

    private Animation<TextureRegion> attackAnimation;
    private float stateTime = 0f;
    private boolean isAttacking = false;
    private int lastFrameIndex = -1;

    private Texture bulletImg;
    private Sound shootSound;
    private Sound hitSound;

    private static final int FRAME_WIDTH = 150;
    private static final int FRAME_HEIGHT = 343;

    public Tower(Texture spriteSheet, Texture bulletImg, Sound shootSound, Sound hitSound, float x, float y) {
        this.position = new Vector2(x, y);
        this.bulletImg = bulletImg;
        this.shootSound = shootSound;
        this.hitSound = hitSound;

        TextureRegion[][] tmp = TextureRegion.split(spriteSheet, FRAME_WIDTH, FRAME_HEIGHT);
        attackAnimation = new Animation<>(0.15f, tmp[0]);
        attackAnimation.setPlayMode(Animation.PlayMode.LOOP);
    }

    public Bullet update(float delta, Array<Enemy> enemies) {
        Enemy target = null;
        float minDst = range;

        for (Enemy e : enemies) {
            // --- 修改：调用 isAlive() 方法 ---
            if (!e.isAlive()) continue;
            float dst = position.dst(e.position);
            if (dst <= range && dst < minDst) {
                minDst = dst;
                target = e;
            }
        }

        Bullet bullet = null;

        if (target != null) {
            isAttacking = true;
            stateTime += delta;
            int currentFrameIndex = attackAnimation.getKeyFrameIndex(stateTime);

            if (currentFrameIndex == 0 && lastFrameIndex != 0) {
                bullet = new Bullet(bulletImg, position.x, position.y, target, hitSound);
                if (SettingsScreen.isSoundOn && shootSound != null) {
                    shootSound.play(SettingsScreen.soundVolume * 0.4f);
                }
            }
            lastFrameIndex = currentFrameIndex;
        } else {
            isAttacking = false;
            stateTime = 0f;
            lastFrameIndex = -1;
        }

        return bullet;
    }

    public void draw(SpriteBatch batch) {
        TextureRegion frame;
        if (isAttacking) {
            frame = attackAnimation.getKeyFrame(stateTime, true);
        } else {
            frame = attackAnimation.getKeyFrame(0f);
        }

        float tileWidth = 1600f / 24f;
        float tileHeight = 900f / 14f;
        float drawW = tileWidth;
        float drawH = (float) frame.getRegionHeight() / frame.getRegionWidth() * drawW;
        float drawX = position.x - drawW / 2f;
        float drawY = (position.y - tileHeight / 2f) + 5f;

        batch.draw(frame, drawX, drawY, drawW, drawH);
    }

    public Vector2 getPosition() { return position; }
    public float getRange() { return range; }
}

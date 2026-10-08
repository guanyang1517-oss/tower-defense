// 坚果墙 Blocker
package com.mygame.td.entity;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.Array;
import com.mygame.td.config.GameConfig;

// --- 修改：实现 IDamageable 接口 ---
public class Blocker implements IDamageable {

    private Vector2 position;
    private Animation<TextureRegion> attackAnimation;
    private float stateTime = 0f;
    private boolean isAttacking = false;

    private boolean alive = true; // 变量名改为 alive 避免冲突

    // --- 修改：从 GameConfig 读取数值 ---
    private float health = GameConfig.BLOCKER_MAX_HEALTH;
    private float returnDamage = GameConfig.BLOCKER_RETURN_DAMAGE;

    private float attackInterval = 1.0f;
    private float attackTimer = 0f;

    public Blocker(Texture sheet, float x, float y) {
        this.position = new Vector2(x, y);

        TextureRegion[][] tmp = TextureRegion.split(sheet, 150, 320);
        Array<TextureRegion> frames = new Array<>();

        if (tmp.length > 0) {
            for (int i = 0; i < tmp[0].length; i++) frames.add(tmp[0][i]);
        }

        attackAnimation = new Animation<>(0.15f, frames, Animation.PlayMode.NORMAL);
    }

    public void update(float delta, Array<Enemy> enemies) {
        if (!alive) return;

        attackTimer += delta;

        if (attackTimer >= attackInterval) {
            boolean didAttack = false;

            for (Enemy enemy : enemies) {
                if (enemy.isAlive() && enemy.blockedBy == this) {
                    enemy.takeDamage((int) returnDamage);
                    didAttack = true;
                }
            }

            if (didAttack) {
                attackTimer = 0;
                isAttacking = true;
                stateTime = 0f;
            }
        }

        if (isAttacking) {
            stateTime += delta;
            if (attackAnimation.isAnimationFinished(stateTime)) {
                isAttacking = false;
                stateTime = 0f;
            }
        }
    }

    public void draw(SpriteBatch batch) {
        if (!alive) return;

        TextureRegion frame;
        if (isAttacking) frame = attackAnimation.getKeyFrame(stateTime, false);
        else frame = attackAnimation.getKeyFrame(0f);

        float tileWidth = 1600f / 24f;
        float tileHeight = 900f / 14f;
        float drawW = tileWidth;
        float drawH = (float) frame.getRegionHeight() / frame.getRegionWidth() * drawW;

        float drawX = position.x - drawW / 2f;
        float drawY = (position.y - tileHeight / 2f) + 5f;

        batch.draw(frame, drawX, drawY, drawW, drawH);
    }

    // --- 接口实现 ---
    @Override
    public void takeDamage(int damage) {
        health -= damage;
        if (health <= 0) alive = false;
    }

    @Override
    public boolean isAlive() { return alive; }

    @Override
    public Vector2 getPosition() { return position; }
}

//子弹模块
package com.mygame.td.entity;

import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Vector2;
import com.mygame.td.screen.SettingsScreen;

public class Bullet {

    private static final float FIRE_OFFSET_Y = 30f;
    private static final float WORLD_WIDTH = 1600f;
    private static final float WORLD_HEIGHT = 900f;
    private static final float OUT_MARGIN = 20f;

    public Vector2 position;
    private Texture texture;
    private Enemy target;

    private Vector2 direction;

    private float speed = 500f;
    private int damage = 1;

    public boolean active = true;
    public boolean isHit = false;

    private Sound hitSound;

    public Bullet(Texture texture, float x, float y, Enemy target, Sound hitSound) {
        this.texture = texture;
        this.position = new Vector2(x, y + FIRE_OFFSET_Y);
        this.target = target;
        this.active = true;
        this.isHit = false;
        this.hitSound = hitSound;
        this.direction = new Vector2(1, 0);

        if (target != null) {
            float dx = target.position.x - position.x;
            float dy = target.position.y - position.y;
            direction.set(dx, dy);
            if (direction.len2() > 0) {
                direction.nor();
            }
        }
    }

    public void update(float delta) {
        if (target != null && target.isAlive()) {
            float dx = target.position.x - position.x;
            float dy = target.position.y - position.y;
            float distance = position.dst(target.position);

            if (distance < 15f) {
                target.takeDamage(damage);

                if (SettingsScreen.isSoundOn && hitSound != null) {
                    hitSound.play(SettingsScreen.soundVolume);
                }

                active = false;
                isHit = true;
                return;
            }

            direction.set(dx, dy);
            if (direction.len2() > 0) {
                direction.nor();
            }
        }

        position.x += direction.x * speed * delta;
        position.y += direction.y * speed * delta;

        if (position.x < -OUT_MARGIN || position.x > WORLD_WIDTH + OUT_MARGIN
            || position.y < -OUT_MARGIN || position.y > WORLD_HEIGHT + OUT_MARGIN) {
            active = false;
        }
    }

    public void draw(SpriteBatch batch) {
        batch.draw(texture, position.x - 4f, position.y - 4f);
    }
}

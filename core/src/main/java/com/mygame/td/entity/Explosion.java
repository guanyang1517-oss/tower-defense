package com.mygame.td.entity;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;

public class Explosion {
    public float x, y;
    private Animation<TextureRegion> animation;
    private float stateTime;
    public boolean isFinished = false;

    // 假设爆炸图是一行 4 列的序列帧
    public Explosion(Texture sheet, float x, float y) {
        this.x = x;
        this.y = y;
        this.stateTime = 0f;

        int frameCols = 4; // 假设有4帧
        int frameWidth = sheet.getWidth() / frameCols;
        int frameHeight = sheet.getHeight();

        TextureRegion[][] tmp = TextureRegion.split(sheet, frameWidth, frameHeight);

        // 0.05f 是每一帧播放的时间，越小爆炸越快
        animation = new Animation<>(0.05f, tmp[0]);
        animation.setPlayMode(Animation.PlayMode.NORMAL); // 只播放一次
    }

    public void update(float delta) {
        stateTime += delta;
        // 判断动画是否播放完毕
        if (animation.isAnimationFinished(stateTime)) {
            isFinished = true;
        }
    }

    public void draw(SpriteBatch batch) {
        if (!isFinished) {
            TextureRegion frame = animation.getKeyFrame(stateTime);
            // 居中绘制
            float width = frame.getRegionWidth();
            float height = frame.getRegionHeight();
            batch.draw(frame, x - width / 2f, y - height / 2f);
        }
    }
}

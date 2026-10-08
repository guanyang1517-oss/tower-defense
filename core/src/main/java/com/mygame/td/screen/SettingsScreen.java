package com.mygame.td.screen;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.utils.ScreenUtils;
import com.mygame.td.MainGame;
import com.mygame.td.save.SaveManager;
import com.mygame.td.save.SettingsData;

public class SettingsScreen implements Screen {

    private static final float WORLD_WIDTH = 1600f;
    private static final float WORLD_HEIGHT = 900f;
    private static final int VOLUME_STEP_PERCENT = 10;
    private static final float KNOB_WIDTH = 24f;
    private static final float TITLE_FONT_SCALE = 0.6f;

    private static final Rectangle MAIN_PANEL_RECT = new Rectangle(1250f, 285f, 340f, 590f);
    private static final Rectangle BOTTOM_PANEL_RECT = new Rectangle(1290f, 14f, 300f, 192f);
    private static final Rectangle MUSIC_BUTTON_RECT = new Rectangle(1314f, 550f, 244f, 60f);
    private static final Rectangle SOUND_BUTTON_RECT = new Rectangle(1314f, 380f, 244f, 60f);
    private static final Rectangle BACK_BUTTON_RECT = new Rectangle(1314f, 72f, 244f, 60f);
    private static final Rectangle MUSIC_SLIDER_RECT = new Rectangle(1314f, 510f, 244f, 10f);
    private static final Rectangle SOUND_SLIDER_RECT = new Rectangle(1314f, 340f, 244f, 10f);

    private final MainGame game;
    private final OrthographicCamera camera;
    private final Texture background;
    private final Texture mainPanelTexture;
    private final Texture bottomPanelTexture;
    private final Texture musicButtonTexture;
    private final Texture soundButtonTexture;
    private final Texture backButtonTexture;
    private final ShapeRenderer shapeRenderer;
    private final Vector3 touchPos = new Vector3();

    private boolean draggingMusic;
    private boolean draggingSound;
    private boolean backRequested;

    public static boolean isSoundOn = true;
    public static float musicVolume = 0.5f;
    public static float soundVolume = 0.8f;

    public SettingsScreen(final MainGame game) {
        this.game = game;
        this.camera = new OrthographicCamera();
        this.camera.setToOrtho(false, WORLD_WIDTH, WORLD_HEIGHT);
        this.background = new Texture("image/bj-1.jpg");
        this.mainPanelTexture = new Texture("image/菜单.png");
        this.bottomPanelTexture = new Texture("image/菜单-5.png");
        this.musicButtonTexture = new Texture("image/设置-1.png");
        this.soundButtonTexture = new Texture("image/设置-2.png");
        this.backButtonTexture = new Texture("image/设置-3.png");
        this.shapeRenderer = new ShapeRenderer();
    }

    @Override
    public void show() {
    }

    @Override
    public void render(float delta) {
        updateInputState();

        if (backRequested) {
            backRequested = false;
            game.setScreen(new MainMenuScreen(game));
            dispose();
            return;
        }

        ScreenUtils.clear(0f, 0f, 0f, 1f);
        camera.update();

        game.batch.setProjectionMatrix(camera.combined);
        game.batch.begin();
        game.batch.setColor(Color.WHITE);
        game.batch.draw(background, 0f, 0f, WORLD_WIDTH, WORLD_HEIGHT);
        game.batch.draw(mainPanelTexture, MAIN_PANEL_RECT.x, MAIN_PANEL_RECT.y, MAIN_PANEL_RECT.width, MAIN_PANEL_RECT.height);
        game.batch.draw(bottomPanelTexture, BOTTOM_PANEL_RECT.x, BOTTOM_PANEL_RECT.y, BOTTOM_PANEL_RECT.width, BOTTOM_PANEL_RECT.height);

        drawButton(musicButtonTexture, MUSIC_BUTTON_RECT, MUSIC_BUTTON_RECT.contains(touchPos.x, touchPos.y));
        drawButton(soundButtonTexture, SOUND_BUTTON_RECT, SOUND_BUTTON_RECT.contains(touchPos.x, touchPos.y));
        drawButton(backButtonTexture, BACK_BUTTON_RECT, BACK_BUTTON_RECT.contains(touchPos.x, touchPos.y));

        game.fontLarge.setColor(Color.WHITE);
        game.fontLarge.getData().setScale(TITLE_FONT_SCALE);
        game.fontLarge.draw(game.batch, "游戏设置", 1360f, 655f);
        game.fontLarge.getData().setScale(1f);
        game.batch.end();

        drawSliders();
    }

    private void drawButton(Texture texture, Rectangle rect, boolean hovered) {
        float drawX = hovered ? rect.x - 2f : rect.x;
        float drawY = hovered ? rect.y + 2f : rect.y;
        float drawWidth = hovered ? rect.width + 4f : rect.width;
        float drawHeight = hovered ? rect.height + 4f : rect.height;
        game.batch.draw(texture, drawX, drawY, drawWidth, drawHeight);
    }

    private void drawSliders() {
        Gdx.gl.glEnable(GL20.GL_BLEND);
        shapeRenderer.setProjectionMatrix(camera.combined);
        shapeRenderer.begin(ShapeRenderer.ShapeType.Filled);

        drawSlider(MUSIC_SLIDER_RECT, musicVolume, draggingMusic, new Color(0.78f, 0.82f, 0.92f, 0.95f));
        drawSlider(SOUND_SLIDER_RECT, soundVolume, draggingSound, new Color(0.74f, 0.84f, 0.98f, 0.95f));

        shapeRenderer.end();
        Gdx.gl.glDisable(GL20.GL_BLEND);
    }

    private void drawSlider(Rectangle rect, float volume, boolean dragging, Color knobColor) {
        shapeRenderer.setColor(0.08f, 0.08f, 0.10f, 0.95f);
        shapeRenderer.rect(rect.x - 6f, rect.y - 7f, rect.width + 12f, rect.height + 14f);

        shapeRenderer.setColor(0.18f, 0.18f, 0.22f, 0.98f);
        shapeRenderer.rect(rect.x, rect.y, rect.width, rect.height);

        shapeRenderer.setColor(0.25f, 0.56f, 0.94f, 0.98f);
        shapeRenderer.rect(rect.x, rect.y, rect.width * volume, rect.height);

        Rectangle knobRect = getKnobRect(rect, volume);
        shapeRenderer.setColor(dragging ? 0.98f : knobColor.r, dragging ? 0.98f : knobColor.g, dragging ? 1f : knobColor.b, 1f);
        shapeRenderer.rect(knobRect.x, knobRect.y, knobRect.width, knobRect.height);
        shapeRenderer.setColor(0.20f, 0.23f, 0.32f, 0.98f);
        shapeRenderer.rect(knobRect.x + 4f, knobRect.y + 4f, knobRect.width - 8f, knobRect.height - 8f);
    }

    private void updateInputState() {
        updateMouse();
        isSoundOn = true;

        boolean touched = Gdx.input.isTouched();
        if (Gdx.input.justTouched()) {
            if (BACK_BUTTON_RECT.contains(touchPos.x, touchPos.y)) {
                backRequested = true;
                return;
            }

            if (isOnSliderHandle(MUSIC_SLIDER_RECT, musicVolume) || MUSIC_SLIDER_RECT.contains(touchPos.x, touchPos.y)) {
                draggingMusic = true;
                setMusicVolume(updateVolumeBySlider(MUSIC_SLIDER_RECT, touchPos.x));
            } else if (isOnSliderHandle(SOUND_SLIDER_RECT, soundVolume) || SOUND_SLIDER_RECT.contains(touchPos.x, touchPos.y)) {
                draggingSound = true;
                setSoundVolume(updateVolumeBySlider(SOUND_SLIDER_RECT, touchPos.x));
            }
        }

        if (touched) {
            if (draggingMusic) {
                setMusicVolume(updateVolumeBySlider(MUSIC_SLIDER_RECT, touchPos.x));
            }
            if (draggingSound) {
                setSoundVolume(updateVolumeBySlider(SOUND_SLIDER_RECT, touchPos.x));
            }
        } else {
            draggingMusic = false;
            draggingSound = false;
        }
    }

    private void setMusicVolume(float newVolume) {
        if (Math.abs(musicVolume - newVolume) > 0.0001f) {
            musicVolume = newVolume;
            persistSettings();
        }
    }

    private void setSoundVolume(float newVolume) {
        if (Math.abs(soundVolume - newVolume) > 0.0001f) {
            soundVolume = newVolume;
            persistSettings();
        }
    }

    private void persistSettings() {
        SettingsData data = new SettingsData();
        data.soundEnabled = isSoundOn;
        data.musicVolume = musicVolume;
        data.soundVolume = soundVolume;
        SaveManager.saveSettings(data);
    }

    private void updateMouse() {
        touchPos.set(Gdx.input.getX(), Gdx.input.getY(), 0f);
        camera.unproject(touchPos);
    }

    private boolean isOnSliderHandle(Rectangle rect, float volume) {
        return getKnobRect(rect, volume).contains(touchPos.x, touchPos.y);
    }

    private Rectangle getKnobRect(Rectangle rect, float volume) {
        float centerX = rect.x + rect.width * volume;
        return new Rectangle(centerX - KNOB_WIDTH / 2f, rect.y - 11f, KNOB_WIDTH, 36f);
    }

    private float updateVolumeBySlider(Rectangle rect, float mouseX) {
        float percent = ((mouseX - rect.x) / rect.width) * 100f;
        percent = Math.round(percent / VOLUME_STEP_PERCENT) * VOLUME_STEP_PERCENT;
        if (percent < 0f) {
            percent = 0f;
        }
        if (percent > 100f) {
            percent = 100f;
        }
        return percent / 100f;
    }

    @Override
    public void resize(int width, int height) {
    }

    @Override
    public void pause() {
    }

    @Override
    public void resume() {
    }

    @Override
    public void hide() {
    }

    @Override
    public void dispose() {
        background.dispose();
        mainPanelTexture.dispose();
        bottomPanelTexture.dispose();
        musicButtonTexture.dispose();
        soundButtonTexture.dispose();
        backButtonTexture.dispose();
        shapeRenderer.dispose();
    }
}

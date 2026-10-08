package com.mygame.td.screen;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.utils.ScreenUtils;
import com.mygame.td.MainGame;
import com.mygame.td.save.SaveData;
import com.mygame.td.save.SaveManager;

public class MainMenuScreen implements Screen {

    private static final float WORLD_WIDTH = 1600f;
    private static final float WORLD_HEIGHT = 900f;

    private static final Rectangle LOGO_RECT = new Rectangle(14f, 735f, 300f, 152f);
    private static final Rectangle MAIN_PANEL_RECT = new Rectangle(1250f, 285f, 340f, 590f);
    private static final Rectangle EXIT_PANEL_RECT = new Rectangle(1290f, 14f, 300f, 192f);

    private static final Rectangle PLAY_RECT = new Rectangle(1314f, 560f, 244f, 60f);
    private static final Rectangle CONTINUE_RECT = new Rectangle(1314f, 450f, 244f, 60f);
    private static final Rectangle SETTINGS_RECT = new Rectangle(1314f, 340f, 244f, 60f);
    private static final Rectangle EXIT_RECT = new Rectangle(1314f, 72f, 244f, 60f);

    private final MainGame game;
    private final OrthographicCamera camera;
    private final Texture background;
    private final Texture logoTexture;
    private final Texture menuPanelTexture;
    private final Texture exitPanelTexture;
    private final Texture startButtonTexture;
    private final Texture continueButtonTexture;
    private final Texture settingsButtonTexture;
    private final Texture exitButtonTexture;
    private final Texture pixel;
    private final Vector3 touchPos = new Vector3();

    private boolean hasSaveFile;
    private float stateTime;

    public MainMenuScreen(final MainGame game) {
        this.game = game;
        this.camera = new OrthographicCamera();
        this.camera.setToOrtho(false, WORLD_WIDTH, WORLD_HEIGHT);

        this.background = new Texture("image/bj-1.jpg");
        this.logoTexture = loadTransparentLogo("image/log.png");
        this.menuPanelTexture = new Texture("image/菜单.png");
        this.exitPanelTexture = new Texture("image/菜单-5.png");
        this.startButtonTexture = new Texture("image/菜单-1.png");
        this.continueButtonTexture = new Texture("image/菜单-4.png");
        this.settingsButtonTexture = new Texture("image/菜单-2.png");
        this.exitButtonTexture = new Texture("image/菜单-3.png");
        this.pixel = createPixelTexture();

        refreshSaveState();
    }

    private Texture loadTransparentLogo(String path) {
        Pixmap pixmap = new Pixmap(Gdx.files.internal(path));
        for (int y = 0; y < pixmap.getHeight(); y++) {
            for (int x = 0; x < pixmap.getWidth(); x++) {
                int pixelValue = pixmap.getPixel(x, y);
                int r = (pixelValue >>> 24) & 0xff;
                int g = (pixelValue >>> 16) & 0xff;
                int b = (pixelValue >>> 8) & 0xff;
                int a = pixelValue & 0xff;
                if (a > 0 && r > 242 && g > 242 && b > 242) {
                    pixmap.drawPixel(x, y, 0x00000000);
                }
            }
        }
        Texture texture = new Texture(pixmap);
        pixmap.dispose();
        return texture;
    }

    private Texture createPixelTexture() {
        Pixmap pixmap = new Pixmap(1, 1, Pixmap.Format.RGBA8888);
        pixmap.setColor(Color.WHITE);
        pixmap.fill();
        Texture texture = new Texture(pixmap);
        pixmap.dispose();
        return texture;
    }

    private void refreshSaveState() {
        hasSaveFile = SaveManager.hasSave();
    }

    @Override
    public void show() {
        refreshSaveState();
    }

    @Override
    public void render(float delta) {
        stateTime += delta;
        refreshSaveState();

        ScreenUtils.clear(0f, 0f, 0f, 1f);
        camera.update();
        updateMouse();

        game.batch.setProjectionMatrix(camera.combined);
        game.batch.begin();
        game.batch.setColor(Color.WHITE);
        game.batch.draw(background, 0f, 0f, WORLD_WIDTH, WORLD_HEIGHT);

        game.batch.draw(logoTexture, LOGO_RECT.x, LOGO_RECT.y, LOGO_RECT.width, LOGO_RECT.height);
        game.batch.draw(menuPanelTexture, MAIN_PANEL_RECT.x, MAIN_PANEL_RECT.y, MAIN_PANEL_RECT.width, MAIN_PANEL_RECT.height);
        game.batch.draw(exitPanelTexture, EXIT_PANEL_RECT.x, EXIT_PANEL_RECT.y, EXIT_PANEL_RECT.width, EXIT_PANEL_RECT.height);

        drawButton(startButtonTexture, PLAY_RECT, PLAY_RECT.contains(touchPos.x, touchPos.y), 1f);
        drawContinueButton(CONTINUE_RECT.contains(touchPos.x, touchPos.y));
        drawButton(settingsButtonTexture, SETTINGS_RECT, SETTINGS_RECT.contains(touchPos.x, touchPos.y), 1f);
        drawButton(exitButtonTexture, EXIT_RECT, EXIT_RECT.contains(touchPos.x, touchPos.y), 1f);
        game.batch.end();

        handleClick();
    }

    private void updateMouse() {
        touchPos.set(Gdx.input.getX(), Gdx.input.getY(), 0f);
        camera.unproject(touchPos);
    }

    private void drawContinueButton(boolean hovered) {
        if (hasSaveFile) {
            float alpha = 0.90f + 0.10f * (float) Math.sin(stateTime * 2.8f);
            drawButton(continueButtonTexture, CONTINUE_RECT, hovered, alpha);
            return;
        }

        game.batch.setColor(0.40f, 0.40f, 0.43f, 0.78f);
        game.batch.draw(continueButtonTexture, CONTINUE_RECT.x, CONTINUE_RECT.y, CONTINUE_RECT.width, CONTINUE_RECT.height);
        game.batch.setColor(0f, 0f, 0f, 0.30f);
        game.batch.draw(pixel, CONTINUE_RECT.x + 14f, CONTINUE_RECT.y + 10f, CONTINUE_RECT.width - 28f, CONTINUE_RECT.height - 20f);
        game.batch.setColor(Color.WHITE);
    }

    private void drawButton(Texture texture, Rectangle rect, boolean hovered, float alpha) {
        float drawX = hovered ? rect.x - 3f : rect.x;
        float drawY = hovered ? rect.y + 2f : rect.y;
        float drawWidth = hovered ? rect.width + 6f : rect.width;
        float drawHeight = hovered ? rect.height + 4f : rect.height;

        game.batch.setColor(1f, 1f, 1f, alpha);
        game.batch.draw(texture, drawX, drawY, drawWidth, drawHeight);

        if (hovered) {
            game.batch.setColor(1f, 1f, 1f, 0.08f);
            game.batch.draw(pixel, rect.x + 16f, rect.y + 10f, rect.width - 32f, rect.height - 20f);
        }
        game.batch.setColor(Color.WHITE);
    }

    private void handleClick() {
        if (!Gdx.input.justTouched()) {
            return;
        }

        updateMouse();

        if (PLAY_RECT.contains(touchPos.x, touchPos.y)) {
            game.setScreen(new LevelSelectScreen(game));
            dispose();
            return;
        }

        if (hasSaveFile && CONTINUE_RECT.contains(touchPos.x, touchPos.y)) {
            SaveData data = SaveManager.loadGame();
            if (data != null) {
                String mapToLoad = data.mapPath != null ? data.mapPath : "map/level1.tmx";
                LevelScreen screen = new LevelScreen(game, mapToLoad, data.levelCode);
                screen.restoreFromSave(data);
                game.setScreen(screen);
                dispose();
            }
            return;
        }

        if (SETTINGS_RECT.contains(touchPos.x, touchPos.y)) {
            game.setScreen(new SettingsScreen(game));
            dispose();
            return;
        }

        if (EXIT_RECT.contains(touchPos.x, touchPos.y)) {
            Gdx.app.exit();
        }
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
        logoTexture.dispose();
        menuPanelTexture.dispose();
        exitPanelTexture.dispose();
        startButtonTexture.dispose();
        continueButtonTexture.dispose();
        settingsButtonTexture.dispose();
        exitButtonTexture.dispose();
        pixel.dispose();
    }
}

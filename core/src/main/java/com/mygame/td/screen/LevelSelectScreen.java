package com.mygame.td.screen;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.InputAdapter;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.GlyphLayout;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.ScreenUtils;
import com.mygame.td.MainGame;
import com.mygame.td.service.RemoteConfigService;

public class LevelSelectScreen implements Screen {

    private static final float WORLD_WIDTH = 1600f;
    private static final float WORLD_HEIGHT = 900f;

    private static final Rectangle MAIN_PANEL_RECT = new Rectangle(0f, 0f, 650f, 900f);
    private static final Rectangle BOTTOM_PANEL_RECT = new Rectangle(1290f, 14f, 300f, 192f);
    private static final Rectangle BACK_RECT = new Rectangle(1314f, 75f, 244f, 60f);
    private static final float LEVEL_BUTTON_X = 110f;
    private static final float LEVEL_BUTTON_Y = 410f;
    private static final float LEVEL_BUTTON_WIDTH = 500f;
    private static final float LEVEL_BUTTON_HEIGHT = 122f;
    private static final float LEVEL_BUTTON_GAP = 48f;
    private static final float LEVEL_TEXT_AREA_X_OFFSET = 150f;
    private static final float LEVEL_TEXT_AREA_WIDTH = 280f;
    private static final float LEVEL_TEXT_Y_OFFSET = 2f;
    private static final float LEVEL_TEXT_TARGET_HEIGHT = 30f;
    private static final float LEVEL_TEXT_CHAR_GAP = 2f;
    private static final float EMPTY_TEXT_X = 180f;
    private static final float EMPTY_TEXT_Y = 430f;
    private static final int MAX_VISIBLE_LEVELS = 3;

    private final MainGame game;
    private final OrthographicCamera camera;
    private final Texture background;
    private final Texture mainPanelTexture;
    private final Texture bottomPanelTexture;
    private final Texture levelButtonTexture;
    private final Texture backButtonTexture;
    private final Array<RemoteConfigService.LevelConfigData> levels;
    private final Array<Rectangle> levelRects;
    private final Vector3 touchPos = new Vector3();
    private final GlyphLayout titleLayout = new GlyphLayout();
    private final GlyphLayout charLayout = new GlyphLayout();
    private final InputAdapter inputAdapter = new InputAdapter() {
        @Override
        public boolean scrolled(float amountX, float amountY) {
            if (amountY > 0f) {
                scrollDown();
                return true;
            }
            if (amountY < 0f) {
                scrollUp();
                return true;
            }
            return false;
        }
    };

    private int visibleStartIndex;

    public LevelSelectScreen(final MainGame game) {
        this.game = game;
        this.camera = new OrthographicCamera();
        this.camera.setToOrtho(false, WORLD_WIDTH, WORLD_HEIGHT);
        this.background = new Texture("image/bj-2.jpg");
        this.mainPanelTexture = new Texture("image/菜单.png");
        this.bottomPanelTexture = new Texture("image/菜单-5.png");
        this.levelButtonTexture = new Texture("image/开始-1.png");
        this.backButtonTexture = new Texture("image/设置-3.png");
        this.levels = resolveLevels();
        this.levelRects = buildLevelRects(levels.size);
    }

    private Array<RemoteConfigService.LevelConfigData> resolveLevels() {
        Array<RemoteConfigService.LevelConfigData> resolved = new Array<RemoteConfigService.LevelConfigData>();
        if (game.remoteConfigService.getLevels().size > 0) {
            resolved.addAll(game.remoteConfigService.getLevels());
            return resolved;
        }

        RemoteConfigService.LevelConfigData level1 = new RemoteConfigService.LevelConfigData();
        level1.levelCode = "fallback-level-1";
        level1.levelName = "第一关";
        level1.mapPath = "map/level1.tmx";
        level1.maxWave = 10;

        RemoteConfigService.LevelConfigData level2 = new RemoteConfigService.LevelConfigData();
        level2.levelCode = "fallback-level-2";
        level2.levelName = "第二关";
        level2.mapPath = "map/level2.tmx";
        level2.maxWave = 10;

        resolved.add(level1);
        resolved.add(level2);
        return resolved;
    }

    private Array<Rectangle> buildLevelRects(int count) {
        Array<Rectangle> rects = new Array<Rectangle>();
        int visibleCount = Math.min(count, MAX_VISIBLE_LEVELS);
        for (int i = 0; i < visibleCount; i++) {
            rects.add(new Rectangle(
                    LEVEL_BUTTON_X,
                    LEVEL_BUTTON_Y - i * (LEVEL_BUTTON_HEIGHT + LEVEL_BUTTON_GAP),
                    LEVEL_BUTTON_WIDTH,
                    LEVEL_BUTTON_HEIGHT));
        }
        return rects;
    }

    @Override
    public void show() {
        Gdx.input.setInputProcessor(inputAdapter);
    }

    @Override
    public void render(float delta) {
        ScreenUtils.clear(0f, 0f, 0f, 1f);
        camera.update();
        updateMouse();

        game.batch.setProjectionMatrix(camera.combined);
        game.batch.begin();
        game.batch.setColor(Color.WHITE);
        game.batch.draw(background, 0f, 0f, WORLD_WIDTH, WORLD_HEIGHT);
        game.batch.draw(mainPanelTexture, MAIN_PANEL_RECT.x, MAIN_PANEL_RECT.y, MAIN_PANEL_RECT.width, MAIN_PANEL_RECT.height);
        game.batch.draw(bottomPanelTexture, BOTTOM_PANEL_RECT.x, BOTTOM_PANEL_RECT.y, BOTTOM_PANEL_RECT.width, BOTTOM_PANEL_RECT.height);

        for (int i = 0; i < levelRects.size; i++) {
            int levelIndex = visibleStartIndex + i;
            if (levelIndex >= levels.size) {
                break;
            }
            Rectangle rect = levelRects.get(i);
            drawButton(levelButtonTexture, rect, rect.contains(touchPos.x, touchPos.y));
            drawLevelText(levels.get(levelIndex), rect);
        }

        if (levelRects.size == 0) {
            game.fontLarge.getData().setScale(0.78f);
            game.fontLarge.setColor(Color.WHITE);
            game.fontLarge.draw(game.batch, "暂无关卡配置", EMPTY_TEXT_X, EMPTY_TEXT_Y);
            game.fontLarge.getData().setScale(1f);
        }

        drawButton(backButtonTexture, BACK_RECT, BACK_RECT.contains(touchPos.x, touchPos.y));
        game.batch.end();

        handleClick();
    }

    private void updateMouse() {
        touchPos.set(Gdx.input.getX(), Gdx.input.getY(), 0f);
        camera.unproject(touchPos);
    }

    private void drawButton(Texture texture, Rectangle rect, boolean hovered) {
        float drawX = hovered ? rect.x - 3f : rect.x;
        float drawY = hovered ? rect.y + 2f : rect.y;
        float drawWidth = hovered ? rect.width + 6f : rect.width;
        float drawHeight = hovered ? rect.height + 4f : rect.height;
        game.batch.setColor(1f, 1f, 1f, 1f);
        game.batch.draw(texture, drawX, drawY, drawWidth, drawHeight);
    }

    private void drawLevelText(RemoteConfigService.LevelConfigData level, Rectangle rect) {
        String levelName = level.levelName != null && level.levelName.trim().length() > 0
                ? level.levelName
                : level.levelCode;

        float textAreaX = rect.x + LEVEL_TEXT_AREA_X_OFFSET;
        float textCenterX = textAreaX + LEVEL_TEXT_AREA_WIDTH / 2f;
        game.fontLarge.setColor(Color.WHITE);

        float totalWidth = 0f;
        float[] charScales = new float[levelName.length()];
        float[] charWidths = new float[levelName.length()];

        game.fontLarge.getData().setScale(1f);
        for (int i = 0; i < levelName.length(); i++) {
            String ch = String.valueOf(levelName.charAt(i));
            charLayout.setText(game.fontLarge, ch);
            float charScale = LEVEL_TEXT_TARGET_HEIGHT / Math.max(charLayout.height, 1f);
            float charWidth = charLayout.width * charScale;
            charScales[i] = charScale;
            charWidths[i] = charWidth;
            totalWidth += charWidth;
            if (i < levelName.length() - 1) {
                totalWidth += LEVEL_TEXT_CHAR_GAP;
            }
        }

        float widthScale = 1f;
        if (totalWidth > LEVEL_TEXT_AREA_WIDTH) {
            widthScale = LEVEL_TEXT_AREA_WIDTH / totalWidth;
            totalWidth = LEVEL_TEXT_AREA_WIDTH;
        }

        float drawX = textCenterX - totalWidth / 2f;
        float drawY = rect.y + rect.height / 2f + LEVEL_TEXT_TARGET_HEIGHT * widthScale / 2f + LEVEL_TEXT_Y_OFFSET;

        for (int i = 0; i < levelName.length(); i++) {
            String ch = String.valueOf(levelName.charAt(i));
            float drawScale = charScales[i] * widthScale;
            game.fontLarge.getData().setScale(drawScale);
            titleLayout.setText(game.fontLarge, ch);
            game.fontLarge.draw(game.batch, titleLayout, drawX, drawY);
            drawX += charWidths[i] * widthScale;
            if (i < levelName.length() - 1) {
                drawX += LEVEL_TEXT_CHAR_GAP * widthScale;
            }
        }

        game.fontLarge.getData().setScale(1f);
    }

    private void handleClick() {
        if (!Gdx.input.justTouched()) {
            return;
        }

        updateMouse();

        for (int i = 0; i < levelRects.size; i++) {
            int levelIndex = visibleStartIndex + i;
            if (levelIndex >= levels.size) {
                break;
            }
            if (levelRects.get(i).contains(touchPos.x, touchPos.y)) {
                game.setScreen(new LevelScreen(game, levels.get(levelIndex)));
                dispose();
                return;
            }
        }

        if (BACK_RECT.contains(touchPos.x, touchPos.y)) {
            game.setScreen(new MainMenuScreen(game));
            dispose();
        }
    }

    private void scrollUp() {
        if (visibleStartIndex > 0) {
            visibleStartIndex--;
        }
    }

    private void scrollDown() {
        int maxStartIndex = Math.max(0, levels.size - MAX_VISIBLE_LEVELS);
        if (visibleStartIndex < maxStartIndex) {
            visibleStartIndex++;
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
        if (Gdx.input.getInputProcessor() == inputAdapter) {
            Gdx.input.setInputProcessor(null);
        }
    }

    @Override
    public void dispose() {
        background.dispose();
        mainPanelTexture.dispose();
        bottomPanelTexture.dispose();
        levelButtonTexture.dispose();
        backButtonTexture.dispose();
    }
}

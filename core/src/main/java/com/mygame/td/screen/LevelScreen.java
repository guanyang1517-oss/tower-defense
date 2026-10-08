package com.mygame.td.screen;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.audio.Music;
import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.maps.MapLayer;
import com.badlogic.gdx.maps.MapObject;
import com.badlogic.gdx.maps.objects.PolylineMapObject;
import com.badlogic.gdx.maps.tiled.TiledMap;
import com.badlogic.gdx.maps.tiled.TmxMapLoader;
import com.badlogic.gdx.maps.tiled.renderers.OrthogonalTiledMapRenderer;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.ObjectMap;
import com.badlogic.gdx.utils.ScreenUtils;
import com.mygame.td.MainGame;
import com.mygame.td.config.GameConfig;
import com.mygame.td.entity.Blocker;
import com.mygame.td.entity.Bullet;
import com.mygame.td.entity.Enemy;
import com.mygame.td.entity.Explosion;
import com.mygame.td.entity.Tower;
import com.mygame.td.save.SaveData;
import com.mygame.td.save.SaveManager;
import com.mygame.td.save.SettingsData;
import com.mygame.td.service.RemoteConfigService;

import java.util.Comparator;
import java.util.Iterator;

public class LevelScreen implements Screen {

    private static final float WORLD_WIDTH = 1600f;
    private static final float WORLD_HEIGHT = 900f;
    private static final Rectangle RESULT_PANEL_RECT = new Rectangle(564f, 300f, 480f, 278f);
    private static final Rectangle RESULT_BUTTON_RECT = new Rectangle(644f, 356f, 316f, 76f);
    private static final int VOLUME_STEP_PERCENT = 10;
    private static final float KNOB_WIDTH = 24f;
    private static final Rectangle IN_GAME_SETTINGS_RECT = new Rectangle(1312f, 842f, 246f, 60f);
    private static final Rectangle PAUSE_PANEL_RECT = new Rectangle(560f, 170f, 500f, 750f);
    private static final Rectangle PAUSE_MUSIC_RECT = new Rectangle(710f, 590f, 260f, 66f);
    private static final Rectangle PAUSE_SOUND_RECT = new Rectangle(710f, 480f, 260f, 66f);
    private static final Rectangle PAUSE_SAVE_RECT = new Rectangle(710f, 380f, 260f, 58f);
    private static final Rectangle PAUSE_CONTINUE_RECT = new Rectangle(710f, 290f, 260f, 66f);
    private static final Rectangle PAUSE_MENU_RECT = new Rectangle(710f, 200f, 260f, 66f);
    private static final Rectangle PAUSE_MUSIC_SLIDER_RECT = new Rectangle(740f, 560f, 228f, 10f);
    private static final Rectangle PAUSE_SOUND_SLIDER_RECT = new Rectangle(740f, 450f, 228f, 10f);

    final MainGame game;

    public enum UnitType { NONE, TOWER, BLOCKER }

    OrthographicCamera camera;
    ShapeRenderer shapeRenderer;

    TiledMap map;
    OrthogonalTiledMapRenderer mapRenderer;
    String currentMapPath;

    Texture enemyImg;
    Texture enemyAttackSheet;
    Texture towerSheet;
    TextureRegion towerIcon;
    Texture bulletImg;
    Texture blockerSheet;
    TextureRegion blockerIcon;
    Texture explosionSheet;
    Texture resultPanelTexture;
    Texture resultButtonTexture;
    Texture inGameSettingsTexture;
    Texture pausePanelTexture;
    Texture pauseMusicTexture;
    Texture pauseSoundTexture;
    Texture pauseSaveTexture;
    Texture pauseContinueTexture;
    Texture pauseMenuTexture;

    Music bgMusic;
    Sound shootSound;
    Sound hitSound;

    Array<Array<Vector2>> paths;
    Array<Enemy> enemies;
    Array<Tower> towers;
    Array<Bullet> bullets;
    Array<Blocker> blockers;
    Array<Explosion> explosions;
    ObjectMap<String, Texture> enemyTextureCache;

    int money = GameConfig.INITIAL_MONEY;
    int playerHealth = GameConfig.INITIAL_HEALTH;
    final int COST_TOWER = GameConfig.TOWER_COST;
    final int COST_BLOCKER = GameConfig.BLOCKER_COST;
    final float PREVIEW_TOWER_RANGE = GameConfig.TOWER_RANGE;

    boolean gameOver = false;
    boolean levelWon = false;

    int waveNumber = 1;
    int enemiesToSpawn = 5;
    float spawnTimer = 0f;
    boolean isWaveBreak = false;
    float breakTimer = 5f;
    int legacyWinWave = 10;

    RemoteConfigService.LevelConfigData currentLevelConfig;
    RemoteConfigService.LevelRuntimeData runtimeLevelData;
    int runtimeWaveIndex = -1;
    int runtimeSpawnIndex = -1;
    int remainingSpawnCount = 0;
    float currentSpawnInterval = 1.5f;
    int currentPathIndex = 0;
    String currentEnemyCode;

    final int COLS = 24;
    final int ROWS = 14;
    float tileWidth;
    float tileHeight;

    boolean[][] isRoad;
    boolean[][] hasBuilding;

    UnitType currentSelection = UnitType.NONE;

    Rectangle uiBoardRect;
    Rectangle cardTowerRect;
    Rectangle cardBlockerRect;

    boolean showSaveMessage = false;
    float saveMessageTimer = 0;
    boolean showPauseMenu = false;
    boolean draggingPauseMusic = false;
    boolean draggingPauseSound = false;
    private final Vector3 touchPos = new Vector3();

    private final Comparator<Object> ySortComparator = new Comparator<Object>() {
        @Override
        public int compare(Object o1, Object o2) {
            float y1 = (o1 instanceof Tower) ? ((Tower) o1).getPosition().y : (o1 instanceof Blocker) ? ((Blocker) o1).getPosition().y : 0;
            float y2 = (o2 instanceof Tower) ? ((Tower) o2).getPosition().y : (o2 instanceof Blocker) ? ((Blocker) o2).getPosition().y : 0;
            return Float.compare(y2, y1);
        }
    };

    public LevelScreen(final MainGame game, RemoteConfigService.LevelConfigData levelConfig) {
        this(game, levelConfig != null ? levelConfig.mapPath : "map/level1.tmx", levelConfig);
    }

    public LevelScreen(final MainGame game, String mapPath) {
        this(game, mapPath, (RemoteConfigService.LevelConfigData) null);
    }

    public LevelScreen(final MainGame game, String mapPath, String levelCode) {
        this(game, mapPath, levelCode != null ? game.remoteConfigService.findLevelByCode(levelCode) : null);
    }

    private LevelScreen(final MainGame game, String mapPath, RemoteConfigService.LevelConfigData levelConfig) {
        this.game = game;
        this.currentMapPath = mapPath;
        this.currentLevelConfig = levelConfig;

        camera = new OrthographicCamera();
        camera.setToOrtho(false, 1600, 900);
        shapeRenderer = new ShapeRenderer();

        tileWidth = 1600f / COLS;
        tileHeight = 900f / ROWS;

        enemyImg = new Texture("image/enemy.png");
        try { enemyAttackSheet = new Texture("image/enemy1.png"); } catch (Exception e) { enemyAttackSheet = enemyImg; }
        try { towerSheet = new Texture("image/tower2.png"); towerIcon = new TextureRegion(towerSheet, 0, 0, 150, 343); } catch (Exception e) { towerSheet = new Texture("image/tower.png"); towerIcon = new TextureRegion(towerSheet); }
        try { bulletImg = new Texture("image/bullet.png"); } catch (Exception e) { bulletImg = enemyImg; }
        try { blockerSheet = new Texture("image/blocker2.png"); blockerIcon = new TextureRegion(blockerSheet, 0, 0, 150, 320); } catch (Exception e) { blockerSheet = new Texture("image/blocker.png"); blockerIcon = new TextureRegion(blockerSheet); }
        try { explosionSheet = new Texture("image/explosion.png"); } catch (Exception e) { explosionSheet = null; }
        resultPanelTexture = new Texture("image/菜单-5.png");
        resultButtonTexture = new Texture("image/设置-3.png");
        inGameSettingsTexture = new Texture("image/菜单-2.png");
        pausePanelTexture = new Texture("image/菜单.png");
        pauseMusicTexture = new Texture("image/设置-1.png");
        pauseSoundTexture = new Texture("image/设置-2.png");
        pauseSaveTexture = loadTransparentTexture("image/游戏中-1.png");
        pauseContinueTexture = new Texture("image/菜单-4.png");
        pauseMenuTexture = new Texture("image/设置-3.png");

        try { bgMusic = Gdx.audio.newMusic(Gdx.files.internal("audio/bgm.mp3")); } catch (Exception e) { bgMusic = null; }
        try { shootSound = Gdx.audio.newSound(Gdx.files.internal("audio/shoot.wav")); } catch (Exception e) { shootSound = null; }
        try { hitSound = Gdx.audio.newSound(Gdx.files.internal("audio/hit.mp3")); } catch (Exception e) { hitSound = null; }

        if (bgMusic != null && SettingsScreen.isSoundOn) {
            bgMusic.setLooping(true);
            bgMusic.setVolume(SettingsScreen.musicVolume);
            bgMusic.play();
        }

        try { map = new TmxMapLoader().load(mapPath); } catch (Exception e) { Gdx.app.exit(); }

        if (map != null) {
            mapRenderer = new OrthogonalTiledMapRenderer(map);
            parsePath();
            initGrid();
        }

        if (currentLevelConfig == null) {
            currentLevelConfig = game.remoteConfigService.findLevelByMapPath(mapPath);
        }
        if (currentLevelConfig != null && currentLevelConfig.maxWave > 0) {
            legacyWinWave = currentLevelConfig.maxWave;
        }
        if (currentLevelConfig != null && currentLevelConfig.levelCode != null) {
            runtimeLevelData = game.remoteConfigService.getRuntimeLevel(currentLevelConfig.levelCode);
        } else {
            runtimeLevelData = game.remoteConfigService.getRuntimeLevelByMapPath(mapPath);
        }

        enemies = new Array<>();
        towers = new Array<>();
        bullets = new Array<>();
        blockers = new Array<>();
        explosions = new Array<>();
        enemyTextureCache = new ObjectMap<>();

        float boardW = 600f;
        float boardH = 150f;
        float boardX = (1600f - boardW) / 2f;
        float boardY = 10f;
        uiBoardRect = new Rectangle(boardX, boardY, boardW, boardH);

        float cardW = 100f;
        float cardH = 130f;
        float padding = 15f;
        float cardStartY = boardY + 10f;

        cardTowerRect = new Rectangle(boardX + padding, cardStartY, cardW, cardH);
        cardBlockerRect = new Rectangle(boardX + padding * 2 + cardW, cardStartY, cardW, cardH);

        if (hasRuntimeWaves()) {
            prepareRuntimeWave(0);
        }
    }

    private Texture loadTransparentTexture(String path) {
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

    private boolean hasRuntimeWaves() {
        return runtimeLevelData != null && runtimeLevelData.waves != null && runtimeLevelData.waves.size > 0;
    }

    private void parsePath() {
        paths = new Array<>();
        for (MapLayer layer : map.getLayers()) {
            if (layer.getName().startsWith("Road")) {
                for (MapObject obj : layer.getObjects()) {
                    if (obj instanceof PolylineMapObject) {
                        Array<Vector2> currentPath = new Array<>();
                        float[] v = ((PolylineMapObject) obj).getPolyline().getTransformedVertices();
                        for (int i = 0; i < v.length; i += 2) {
                            currentPath.add(new Vector2(v[i], v[i + 1]));
                        }
                        paths.add(currentPath);
                        break;
                    }
                }
            }
        }
    }

    private void initGrid() {
        isRoad = new boolean[COLS][ROWS];
        hasBuilding = new boolean[COLS][ROWS];
        for (int x = 0; x < COLS; x++) {
            for (int y = 0; y < ROWS; y++) {
                isRoad[x][y] = false;
                hasBuilding[x][y] = false;
            }
        }

        if (paths != null) {
            for (Array<Vector2> path : paths) {
                if (path.size > 1) {
                    for (int i = 0; i < path.size - 1; i++) {
                        Vector2 p1 = path.get(i), p2 = path.get(i + 1);
                        float dist = p1.dst(p2);
                        Vector2 tmp = new Vector2();
                        for (float k = 0; k <= dist; k += 5) {
                            tmp.set(p1).lerp(p2, k / dist);
                            int gx = (int) (tmp.x / tileWidth);
                            int gy = (int) (tmp.y / tileHeight);
                            if (gx >= 0 && gx < COLS && gy >= 0 && gy < ROWS) {
                                isRoad[gx][gy] = true;
                            }
                        }
                    }
                }
            }
        }
    }

    public void saveGame() {
        SaveData data = new SaveData();
        data.money = this.money;
        data.playerHealth = this.playerHealth;
        data.waveNumber = this.waveNumber;
        data.levelCode = currentLevelConfig != null ? currentLevelConfig.levelCode : null;
        data.mapPath = this.currentMapPath;
        for (Tower t : towers) data.buildings.add(new SaveData.BuildingData("TOWER", t.getPosition().x, t.getPosition().y));
        for (Blocker b : blockers) data.buildings.add(new SaveData.BuildingData("BLOCKER", b.getPosition().x, b.getPosition().y));
        SaveManager.saveGame(data);
        showSaveMessage = true;
        saveMessageTimer = 2.0f;
    }

    public void restoreFromSave(SaveData data) {
        this.money = data.money;
        this.playerHealth = data.playerHealth;
        towers.clear();
        blockers.clear();
        enemies.clear();
        bullets.clear();
        explosions.clear();

        for (int x = 0; x < COLS; x++) {
            for (int y = 0; y < ROWS; y++) {
                hasBuilding[x][y] = false;
            }
        }

        if (hasRuntimeWaves()) {
            prepareRuntimeWave(findRuntimeWaveIndex(data.waveNumber));
        } else {
            this.waveNumber = data.waveNumber;
            this.enemiesToSpawn = 5 + (waveNumber - 1) * 3;
            this.spawnTimer = 0f;
            this.isWaveBreak = false;
            this.breakTimer = 5f;
        }

        for (SaveData.BuildingData bd : data.buildings) {
            int gx = (int) (bd.x / tileWidth);
            int gy = (int) (bd.y / tileHeight);
            if (gx >= 0 && gx < COLS && gy >= 0 && gy < ROWS) {
                hasBuilding[gx][gy] = true;
                if ("TOWER".equals(bd.type)) {
                    towers.add(new Tower(towerSheet, bulletImg, shootSound, hitSound, bd.x, bd.y));
                } else if ("BLOCKER".equals(bd.type)) {
                    blockers.add(new Blocker(blockerSheet, bd.x, bd.y));
                }
            }
        }
    }

    @Override
    public void render(float delta) {
        ScreenUtils.clear(0, 0, 0, 1);
        if (!gameOver && !levelWon && !showPauseMenu) {
            updateLogic(delta);
        }
        updateSaveMessage(delta);

        camera.update();
        updateMouse();
        if (mapRenderer != null) {
            mapRenderer.setView(camera);
            mapRenderer.render();
        }
        if (!showPauseMenu) {
            drawRanges();
        }

        game.batch.setProjectionMatrix(camera.combined);
        game.batch.begin();
        Array<Object> drawables = new Array<>();
        drawables.addAll(towers);
        drawables.addAll(blockers);
        drawables.sort(ySortComparator);

        for (Object o : drawables) {
            if (o instanceof Tower) ((Tower) o).draw(game.batch);
            if (o instanceof Blocker) ((Blocker) o).draw(game.batch);
        }
        for (Enemy e : enemies) e.draw(game.batch);
        for (Bullet b : bullets) b.draw(game.batch);
        for (Explosion ex : explosions) ex.draw(game.batch);

        if (gameOver || levelWon) {
            drawResultOverlay();
        } else if (!showPauseMenu) {
            drawGhostImage();
        }
        game.batch.end();

        if (!gameOver && !levelWon) {
            drawPvZUI();
            if (showPauseMenu) {
                drawPauseOverlay();
            }
        }

        handleInput();
    }

    private void drawResultOverlay() {
        Vector3 touchPos = new Vector3(Gdx.input.getX(), Gdx.input.getY(), 0f);
        camera.unproject(touchPos);
        boolean hovered = RESULT_BUTTON_RECT.contains(touchPos.x, touchPos.y);

        game.batch.setColor(1f, 1f, 1f, 1f);
        game.batch.draw(resultPanelTexture, RESULT_PANEL_RECT.x, RESULT_PANEL_RECT.y, RESULT_PANEL_RECT.width, RESULT_PANEL_RECT.height);

        float buttonX = hovered ? RESULT_BUTTON_RECT.x - 3f : RESULT_BUTTON_RECT.x;
        float buttonY = hovered ? RESULT_BUTTON_RECT.y + 2f : RESULT_BUTTON_RECT.y;
        float buttonW = hovered ? RESULT_BUTTON_RECT.width + 6f : RESULT_BUTTON_RECT.width;
        float buttonH = hovered ? RESULT_BUTTON_RECT.height + 4f : RESULT_BUTTON_RECT.height;
        game.batch.draw(resultButtonTexture, buttonX, buttonY, buttonW, buttonH);

        game.fontLarge.getData().setScale(1.05f);
        game.fontLarge.setColor(Color.WHITE);
        game.fontLarge.draw(game.batch, gameOver ? "游戏失败" : "胜利通关", 678f, 520f);
        game.fontLarge.getData().setScale(1f);
    }

    private void drawPvZUI() {
        shapeRenderer.setProjectionMatrix(camera.combined);

        shapeRenderer.begin(ShapeRenderer.ShapeType.Filled);
        shapeRenderer.setColor(new Color(0.6f, 0.3f, 0.1f, 1f));
        shapeRenderer.rect(uiBoardRect.x, uiBoardRect.y, uiBoardRect.width, uiBoardRect.height);
        shapeRenderer.end();

        Gdx.gl.glLineWidth(3);
        shapeRenderer.begin(ShapeRenderer.ShapeType.Line);
        shapeRenderer.setColor(new Color(0.3f, 0.15f, 0.05f, 1f));
        shapeRenderer.rect(uiBoardRect.x, uiBoardRect.y, uiBoardRect.width, uiBoardRect.height);
        shapeRenderer.end();
        Gdx.gl.glLineWidth(1);

        drawSinglePvZCard(cardTowerRect, towerIcon, COST_TOWER, currentSelection == UnitType.TOWER, money >= COST_TOWER);
        drawSinglePvZCard(cardBlockerRect, blockerIcon, COST_BLOCKER, currentSelection == UnitType.BLOCKER, money >= COST_BLOCKER);

        game.batch.begin();
        float uiTopY = 880;
        game.fontSmall.setColor(Color.YELLOW);
        game.fontSmall.draw(game.batch, "金币：" + money, 40, uiTopY);
        game.fontSmall.setColor(Color.RED);
        game.fontSmall.draw(game.batch, "生命：" + playerHealth, 240, uiTopY);
        game.fontSmall.setColor(Color.CYAN);
        game.fontSmall.draw(game.batch, "波次：" + waveNumber, 440, uiTopY);

        drawOverlayButton(inGameSettingsTexture, IN_GAME_SETTINGS_RECT, IN_GAME_SETTINGS_RECT.contains(touchPos.x, touchPos.y), 1f);
        game.batch.end();
    }

    private void drawPauseOverlay() {
        Gdx.gl.glEnable(GL20.GL_BLEND);
        shapeRenderer.setProjectionMatrix(camera.combined);
        shapeRenderer.begin(ShapeRenderer.ShapeType.Filled);
        shapeRenderer.setColor(0f, 0f, 0f, 0.45f);
        shapeRenderer.rect(0f, 0f, WORLD_WIDTH, WORLD_HEIGHT);
        shapeRenderer.end();
        Gdx.gl.glDisable(GL20.GL_BLEND);

        game.batch.begin();
        game.batch.setColor(Color.WHITE);
        game.batch.draw(pausePanelTexture, PAUSE_PANEL_RECT.x, PAUSE_PANEL_RECT.y, PAUSE_PANEL_RECT.width, PAUSE_PANEL_RECT.height);
        drawOverlayButton(pauseMusicTexture, PAUSE_MUSIC_RECT, PAUSE_MUSIC_RECT.contains(touchPos.x, touchPos.y), 1f);
        drawOverlayButton(pauseSoundTexture, PAUSE_SOUND_RECT, PAUSE_SOUND_RECT.contains(touchPos.x, touchPos.y), 1f);
        drawOverlayButton(pauseSaveTexture, PAUSE_SAVE_RECT, PAUSE_SAVE_RECT.contains(touchPos.x, touchPos.y), 1f);
        drawOverlayButton(pauseContinueTexture, PAUSE_CONTINUE_RECT, PAUSE_CONTINUE_RECT.contains(touchPos.x, touchPos.y), 1f);
        drawOverlayButton(pauseMenuTexture, PAUSE_MENU_RECT, PAUSE_MENU_RECT.contains(touchPos.x, touchPos.y), 1f);
        if (showSaveMessage) {
            game.fontSmall.setColor(Color.CYAN);
            game.fontSmall.draw(game.batch, "保存成功", 720f, 336f);
        }
        game.batch.end();

        drawPauseSliders();
    }

    private void drawOverlayButton(Texture texture, Rectangle rect, boolean hovered, float alpha) {
        float drawX = hovered ? rect.x - 3f : rect.x;
        float drawY = hovered ? rect.y + 2f : rect.y;
        float drawWidth = hovered ? rect.width + 6f : rect.width;
        float drawHeight = hovered ? rect.height + 4f : rect.height;
        game.batch.setColor(1f, 1f, 1f, alpha);
        game.batch.draw(texture, drawX, drawY, drawWidth, drawHeight);
        game.batch.setColor(Color.WHITE);
    }

    private void drawPauseSliders() {
        Gdx.gl.glEnable(GL20.GL_BLEND);
        shapeRenderer.setProjectionMatrix(camera.combined);
        shapeRenderer.begin(ShapeRenderer.ShapeType.Filled);
        drawSlider(PAUSE_MUSIC_SLIDER_RECT, SettingsScreen.musicVolume, draggingPauseMusic, new Color(0.78f, 0.82f, 0.92f, 0.95f));
        drawSlider(PAUSE_SOUND_SLIDER_RECT, SettingsScreen.soundVolume, draggingPauseSound, new Color(0.74f, 0.84f, 0.98f, 0.95f));
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

    private Rectangle getKnobRect(Rectangle rect, float volume) {
        float centerX = rect.x + rect.width * volume;
        return new Rectangle(centerX - KNOB_WIDTH / 2f, rect.y - 11f, KNOB_WIDTH, 36f);
    }

    private void drawSinglePvZCard(Rectangle rect, TextureRegion icon, int cost, boolean isSelected, boolean canAfford) {
        float botH = 35f;
        float topH = rect.height - botH;

        shapeRenderer.begin(ShapeRenderer.ShapeType.Filled);
        shapeRenderer.setColor(new Color(0.76f, 0.88f, 0.64f, 1f));
        shapeRenderer.rect(rect.x, rect.y + botH, rect.width, topH);
        shapeRenderer.setColor(Color.WHITE);
        shapeRenderer.rect(rect.x, rect.y, rect.width, botH);
        shapeRenderer.setColor(Color.YELLOW);
        shapeRenderer.circle(rect.x + rect.width - 20, rect.y + botH / 2f, 10);
        shapeRenderer.end();

        shapeRenderer.begin(ShapeRenderer.ShapeType.Line);
        shapeRenderer.setColor(Color.DARK_GRAY);
        shapeRenderer.rect(rect.x, rect.y, rect.width, rect.height);
        shapeRenderer.line(rect.x, rect.y + botH, rect.x + rect.width, rect.y + botH);
        shapeRenderer.setColor(Color.BLACK);
        shapeRenderer.circle(rect.x + rect.width - 20, rect.y + botH / 2f, 10);

        if (isSelected) {
            Gdx.gl.glLineWidth(4);
            shapeRenderer.setColor(Color.GOLD);
            shapeRenderer.rect(rect.x - 2, rect.y - 2, rect.width + 4, rect.height + 4);
            Gdx.gl.glLineWidth(1);
        }
        shapeRenderer.end();

        game.batch.begin();
        float iconPad = 5f;
        float maxW = rect.width - iconPad * 2;
        float maxH = topH - iconPad * 2;
        float aspect = (float) icon.getRegionWidth() / icon.getRegionHeight();
        float drawW = maxH * aspect;
        float drawH = maxH;
        if (drawW > maxW) {
            drawW = maxW;
            drawH = drawW / aspect;
        }

        float drawX = rect.x + (rect.width - drawW) / 2f;
        float drawY = rect.y + botH + (topH - drawH) / 2f;
        game.batch.draw(icon, drawX, drawY, drawW, drawH);

        game.fontSmall.setColor(Color.BLACK);
        game.fontSmall.getData().setScale(0.8f);
        game.fontSmall.draw(game.batch, String.valueOf(cost), rect.x + 10, rect.y + 28);
        game.fontSmall.getData().setScale(1f);
        game.batch.end();

        if (!canAfford) {
            Gdx.gl.glEnable(GL20.GL_BLEND);
            shapeRenderer.begin(ShapeRenderer.ShapeType.Filled);
            shapeRenderer.setColor(0, 0, 0, 0.6f);
            shapeRenderer.rect(rect.x, rect.y, rect.width, rect.height);
            shapeRenderer.end();
            Gdx.gl.glDisable(GL20.GL_BLEND);
        }
    }

    private void drawRanges() {
        Gdx.gl.glEnable(GL20.GL_BLEND);
        Gdx.gl.glBlendFunc(GL20.GL_SRC_ALPHA, GL20.GL_ONE_MINUS_SRC_ALPHA);
        shapeRenderer.setProjectionMatrix(camera.combined);
        shapeRenderer.begin(ShapeRenderer.ShapeType.Filled);

        Vector3 mousePos = new Vector3(Gdx.input.getX(), Gdx.input.getY(), 0);
        camera.unproject(mousePos);

        if (currentSelection == UnitType.TOWER) {
            int gx = (int) (mousePos.x / tileWidth);
            int gy = (int) (mousePos.y / tileHeight);
            if (gx >= 0 && gx < COLS && gy >= 0 && gy < ROWS) {
                float cx = gx * tileWidth + tileWidth / 2f;
                float cy = gy * tileHeight + tileHeight / 2f;
                shapeRenderer.setColor(1, 1, 1, 0.2f);
                shapeRenderer.circle(cx, cy, PREVIEW_TOWER_RANGE);
            }
        }

        if (currentSelection == UnitType.NONE) {
            for (Tower t : towers) {
                if (t.getPosition().dst(mousePos.x, mousePos.y) < 40) {
                    shapeRenderer.setColor(0, 1, 0, 0.2f);
                    shapeRenderer.circle(t.getPosition().x, t.getPosition().y, t.getRange());
                }
            }
        }
        shapeRenderer.end();
        Gdx.gl.glDisable(GL20.GL_BLEND);
    }

    private void drawGhostImage() {
        if (currentSelection == UnitType.NONE) return;

        Vector3 touchPos = new Vector3(Gdx.input.getX(), Gdx.input.getY(), 0);
        camera.unproject(touchPos);
        int gx = (int) (touchPos.x / tileWidth);
        int gy = (int) (touchPos.y / tileHeight);
        boolean canBuild = true;

        if (gx < 0 || gx >= COLS || gy < 0 || gy >= ROWS) {
            canBuild = false;
        } else if (hasBuilding[gx][gy]) {
            canBuild = false;
        } else {
            boolean isRoadTile = isRoad[gx][gy];
            if (currentSelection == UnitType.TOWER && isRoadTile) canBuild = false;
            if (currentSelection == UnitType.BLOCKER && !isRoadTile) canBuild = false;
        }

        float drawX = gx * tileWidth + tileWidth / 2f;
        float bottomY = gy * tileHeight;
        float drawBaseY = bottomY + 5f;

        if (canBuild) game.batch.setColor(0.5f, 1f, 0.5f, 0.7f);
        else game.batch.setColor(1f, 0.2f, 0.2f, 0.7f);

        if (currentSelection == UnitType.TOWER) {
            float drawW = tileWidth;
            float drawH = (343f / 150f) * drawW;
            game.batch.draw(towerIcon, drawX - drawW / 2f, drawBaseY, drawW, drawH);
        } else {
            float drawW = tileWidth;
            float drawH = (320f / 150f) * drawW;
            game.batch.draw(blockerIcon, drawX - drawW / 2f, drawBaseY, drawW, drawH);
        }
        game.batch.setColor(1, 1, 1, 1);
    }

    private void updateLogic(float delta) {
        updateWaveFlow(delta);

        for (Tower t : towers) {
            Bullet b = t.update(delta, enemies);
            if (b != null) bullets.add(b);
        }

        for (Iterator<Blocker> iter = blockers.iterator(); iter.hasNext();) {
            Blocker b = iter.next();
            b.update(delta, enemies);
            if (!b.isAlive()) {
                int gx = (int) (b.getPosition().x / tileWidth);
                int gy = (int) (b.getPosition().y / tileHeight);
                if (gx >= 0 && gx < COLS && gy >= 0 && gy < ROWS) hasBuilding[gx][gy] = false;
                iter.remove();
            }
        }

        for (Iterator<Bullet> iter = bullets.iterator(); iter.hasNext();) {
            Bullet b = iter.next();
            b.update(delta);
            if (!b.active) {
                if (b.isHit && explosionSheet != null) {
                    explosions.add(new Explosion(explosionSheet, b.position.x, b.position.y));
                }
                iter.remove();
            }
        }

        for (Iterator<Explosion> iter = explosions.iterator(); iter.hasNext();) {
            Explosion ex = iter.next();
            ex.update(delta);
            if (ex.isFinished) iter.remove();
        }

        for (Iterator<Enemy> iter = enemies.iterator(); iter.hasNext();) {
            Enemy e = iter.next();
            e.update(delta);
            if (!e.isBlocked) {
                for (Blocker b : blockers) {
                    if (b.isAlive() && e.position.dst(b.getPosition()) < 32) {
                        e.isBlocked = true;
                        e.blockedBy = b;
                        break;
                    }
                }
            }
            if (e.arrived) {
                playerHealth--;
                if (playerHealth <= 0) gameOver = true;
                iter.remove();
            } else if (!e.isAlive()) {
                money += e.getReward();
                iter.remove();
            }
        }
    }

    private void updateWaveFlow(float delta) {
        if (hasRuntimeWaves()) {
            updateRuntimeWaveFlow(delta);
        } else {
            updateLegacyWaveFlow(delta);
        }
    }

    private void updateLegacyWaveFlow(float delta) {
        if (isWaveBreak) {
            breakTimer -= delta;
            if (breakTimer <= 0) {
                waveNumber++;
                isWaveBreak = false;
                enemiesToSpawn = 5 + (waveNumber - 1) * 3;
            }
            return;
        }

        if (enemiesToSpawn > 0) {
            spawnTimer += delta;
            if (spawnTimer > 1.5f) {
                if (paths.size > 0) {
                    int pathIndex = (waveNumber - 1) % paths.size;
                    enemies.add(new Enemy(enemyImg, enemyAttackSheet, paths.get(pathIndex)));
                    enemiesToSpawn--;
                    spawnTimer = 0f;
                }
            }
            return;
        }

        if (enemies.isEmpty()) {
            if (waveNumber >= legacyWinWave) {
                levelWon = true;
                return;
            }
            isWaveBreak = true;
            breakTimer = 5f;
        }
    }

    private void updateRuntimeWaveFlow(float delta) {
        if (runtimeWaveIndex < 0 || runtimeWaveIndex >= runtimeLevelData.waves.size) {
            return;
        }

        if (isWaveBreak) {
            breakTimer -= delta;
            if (breakTimer <= 0) {
                prepareRuntimeWave(runtimeWaveIndex + 1);
            }
            return;
        }

        if (remainingSpawnCount > 0) {
            spawnTimer += delta;
            if (spawnTimer >= currentSpawnInterval && spawnRuntimeEnemy()) {
                remainingSpawnCount--;
                spawnTimer = 0f;
                if (remainingSpawnCount <= 0) {
                    loadNextRuntimeSpawn();
                }
            }
            return;
        }

        if (!loadNextRuntimeSpawn() && enemies.isEmpty()) {
            if (runtimeWaveIndex >= runtimeLevelData.waves.size - 1) {
                levelWon = true;
            } else {
                isWaveBreak = true;
                breakTimer = runtimeLevelData.waves.get(runtimeWaveIndex).breakTime;
            }
        }
    }

    private void prepareRuntimeWave(int index) {
        if (!hasRuntimeWaves()) {
            return;
        }
        if (index < 0) {
            index = 0;
        }
        if (index >= runtimeLevelData.waves.size) {
            levelWon = enemies.isEmpty();
            return;
        }

        runtimeWaveIndex = index;
        RemoteConfigService.WaveRuntimeData wave = runtimeLevelData.waves.get(runtimeWaveIndex);
        waveNumber = wave.waveNumber;
        runtimeSpawnIndex = -1;
        remainingSpawnCount = 0;
        currentSpawnInterval = 1.5f;
        currentPathIndex = 0;
        currentEnemyCode = null;
        spawnTimer = 0f;
        isWaveBreak = false;
        loadNextRuntimeSpawn();
    }

    private int findRuntimeWaveIndex(int targetWaveNumber) {
        if (!hasRuntimeWaves()) {
            return -1;
        }
        for (int i = 0; i < runtimeLevelData.waves.size; i++) {
            if (runtimeLevelData.waves.get(i).waveNumber == targetWaveNumber) {
                return i;
            }
        }
        return 0;
    }

    private boolean loadNextRuntimeSpawn() {
        if (!hasRuntimeWaves() || runtimeWaveIndex < 0 || runtimeWaveIndex >= runtimeLevelData.waves.size) {
            return false;
        }

        RemoteConfigService.WaveRuntimeData wave = runtimeLevelData.waves.get(runtimeWaveIndex);
        if (runtimeSpawnIndex >= wave.spawns.size) {
            remainingSpawnCount = 0;
            currentEnemyCode = null;
            return false;
        }

        runtimeSpawnIndex++;
        if (runtimeSpawnIndex >= wave.spawns.size) {
            remainingSpawnCount = 0;
            currentEnemyCode = null;
            return false;
        }

        RemoteConfigService.SpawnRuntimeData spawn = wave.spawns.get(runtimeSpawnIndex);
        remainingSpawnCount = spawn.spawnCount;
        currentSpawnInterval = spawn.spawnInterval;
        currentPathIndex = spawn.pathIndex;
        currentEnemyCode = spawn.enemyCode;
        spawnTimer = 0f;
        return true;
    }

    private boolean spawnRuntimeEnemy() {
        if (paths == null || paths.size == 0) {
            return false;
        }

        int pathIndex = currentPathIndex;
        if (pathIndex < 0 || pathIndex >= paths.size) {
            pathIndex = Math.abs(pathIndex) % paths.size;
        }

        RemoteConfigService.EnemyTypeData enemyType = game.remoteConfigService.findEnemyType(currentEnemyCode);
        Texture staticTexture = getEnemyTexture(enemyType == null ? null : enemyType.texturePath, false);
        Texture attackTexture = getEnemyTexture(enemyType == null ? null : enemyType.attackTexturePath, true);
        enemies.add(new Enemy(staticTexture, attackTexture, paths.get(pathIndex), enemyType));
        return true;
    }

    private Texture getEnemyTexture(String path, boolean attackTexture) {
        if (path == null || path.trim().isEmpty()) {
            return attackTexture ? enemyAttackSheet : enemyImg;
        }
        if ("image/enemy.png".equals(path)) {
            return enemyImg;
        }
        if ("image/enemy1.png".equals(path)) {
            return enemyAttackSheet;
        }

        Texture cached = enemyTextureCache.get(path);
        if (cached != null) {
            return cached;
        }

        try {
            Texture texture = new Texture(path);
            enemyTextureCache.put(path, texture);
            return texture;
        } catch (Exception exception) {
            return attackTexture ? enemyAttackSheet : enemyImg;
        }
    }

    private void updateMouse() {
        touchPos.set(Gdx.input.getX(), Gdx.input.getY(), 0f);
        camera.unproject(touchPos);
    }

    private void updateSaveMessage(float delta) {
        if (!showSaveMessage) {
            return;
        }
        saveMessageTimer -= delta;
        if (saveMessageTimer <= 0f) {
            showSaveMessage = false;
            saveMessageTimer = 0f;
        }
    }

    private void openPauseMenu() {
        showPauseMenu = true;
        currentSelection = UnitType.NONE;
        draggingPauseMusic = false;
        draggingPauseSound = false;
    }

    private void closePauseMenu() {
        showPauseMenu = false;
        draggingPauseMusic = false;
        draggingPauseSound = false;
    }

    private void handlePauseInput() {
        if (Gdx.input.isButtonJustPressed(Input.Buttons.LEFT)) {
            if (isOnSliderHandle(PAUSE_MUSIC_SLIDER_RECT, SettingsScreen.musicVolume) || PAUSE_MUSIC_SLIDER_RECT.contains(touchPos.x, touchPos.y)) {
                draggingPauseMusic = true;
                draggingPauseSound = false;
                setMusicVolume(updateVolumeBySlider(PAUSE_MUSIC_SLIDER_RECT, touchPos.x));
                return;
            }
            if (isOnSliderHandle(PAUSE_SOUND_SLIDER_RECT, SettingsScreen.soundVolume) || PAUSE_SOUND_SLIDER_RECT.contains(touchPos.x, touchPos.y)) {
                draggingPauseSound = true;
                draggingPauseMusic = false;
                setSoundVolume(updateVolumeBySlider(PAUSE_SOUND_SLIDER_RECT, touchPos.x));
                return;
            }
            if (PAUSE_SAVE_RECT.contains(touchPos.x, touchPos.y)) {
                saveGame();
                return;
            }
            if (PAUSE_CONTINUE_RECT.contains(touchPos.x, touchPos.y) || IN_GAME_SETTINGS_RECT.contains(touchPos.x, touchPos.y)) {
                closePauseMenu();
                return;
            }
            if (PAUSE_MENU_RECT.contains(touchPos.x, touchPos.y)) {
                game.setScreen(new MainMenuScreen(game));
                dispose();
            }
        }

        if (Gdx.input.isTouched()) {
            if (draggingPauseMusic) {
                setMusicVolume(updateVolumeBySlider(PAUSE_MUSIC_SLIDER_RECT, touchPos.x));
            }
            if (draggingPauseSound) {
                setSoundVolume(updateVolumeBySlider(PAUSE_SOUND_SLIDER_RECT, touchPos.x));
            }
        } else {
            draggingPauseMusic = false;
            draggingPauseSound = false;
        }
    }

    private boolean isOnSliderHandle(Rectangle rect, float volume) {
        return getKnobRect(rect, volume).contains(touchPos.x, touchPos.y);
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

    private void setMusicVolume(float newVolume) {
        if (Math.abs(SettingsScreen.musicVolume - newVolume) <= 0.0001f) {
            return;
        }
        SettingsScreen.musicVolume = newVolume;
        if (bgMusic != null) {
            bgMusic.setVolume(newVolume);
        }
        persistSettings();
    }

    private void setSoundVolume(float newVolume) {
        if (Math.abs(SettingsScreen.soundVolume - newVolume) <= 0.0001f) {
            return;
        }
        SettingsScreen.soundVolume = newVolume;
        persistSettings();
    }

    private void persistSettings() {
        SettingsScreen.isSoundOn = true;
        SettingsData data = new SettingsData();
        data.soundEnabled = SettingsScreen.isSoundOn;
        data.musicVolume = SettingsScreen.musicVolume;
        data.soundVolume = SettingsScreen.soundVolume;
        SaveManager.saveSettings(data);
    }

    private void handleInput() {
        updateMouse();

        if (!gameOver && !levelWon && Gdx.input.isKeyJustPressed(Input.Keys.ESCAPE)) {
            if (showPauseMenu) {
                closePauseMenu();
            } else {
                openPauseMenu();
            }
            return;
        }

        if (showPauseMenu) {
            handlePauseInput();
            return;
        }

        if (Gdx.input.isButtonJustPressed(Input.Buttons.RIGHT)) {
            currentSelection = UnitType.NONE;
            return;
        }

        if (Gdx.input.isButtonJustPressed(Input.Buttons.LEFT)) {
            if (gameOver || levelWon) {
                if (RESULT_BUTTON_RECT.contains(touchPos.x, touchPos.y)) {
                    game.setScreen(new MainMenuScreen(game));
                    dispose();
                }
                return;
            }

            if (IN_GAME_SETTINGS_RECT.contains(touchPos.x, touchPos.y)) {
                openPauseMenu();
                return;
            }

            if (cardTowerRect.contains(touchPos.x, touchPos.y)) {
                currentSelection = UnitType.TOWER;
                return;
            }
            if (cardBlockerRect.contains(touchPos.x, touchPos.y)) {
                currentSelection = UnitType.BLOCKER;
                return;
            }

            if (currentSelection != UnitType.NONE) {
                int gx = (int) (touchPos.x / tileWidth);
                int gy = (int) (touchPos.y / tileHeight);
                if (gx < 0 || gx >= COLS || gy < 0 || gy >= ROWS) return;
                if (hasBuilding[gx][gy]) return;
                if (uiBoardRect.contains(touchPos.x, touchPos.y)) return;

                boolean isRoadTile = isRoad[gx][gy];
                if (currentSelection == UnitType.TOWER && isRoadTile) return;
                if (currentSelection == UnitType.BLOCKER && !isRoadTile) return;

                float buildX = gx * tileWidth + tileWidth / 2f;
                float buildY = gy * tileHeight + tileHeight / 2f;

                if (currentSelection == UnitType.TOWER) {
                    if (money >= COST_TOWER) {
                        towers.add(new Tower(towerSheet, bulletImg, shootSound, hitSound, buildX, buildY));
                        money -= COST_TOWER;
                        hasBuilding[gx][gy] = true;
                        currentSelection = UnitType.NONE;
                    }
                } else if (currentSelection == UnitType.BLOCKER) {
                    if (money >= COST_BLOCKER) {
                        blockers.add(new Blocker(blockerSheet, buildX, buildY));
                        money -= COST_BLOCKER;
                        hasBuilding[gx][gy] = true;
                        currentSelection = UnitType.NONE;
                    }
                }
            }
        }
    }

    @Override public void show() {}
    @Override public void resize(int width, int height) {}
    @Override public void pause() {}
    @Override public void resume() {}
    @Override public void hide() {}

    @Override
    public void dispose() {
        shapeRenderer.dispose();
        enemyImg.dispose();
        if (enemyAttackSheet != null && enemyAttackSheet != enemyImg) enemyAttackSheet.dispose();
        for (Texture texture : enemyTextureCache.values()) {
            texture.dispose();
        }
        towerSheet.dispose();
        bulletImg.dispose();
        if (blockerSheet != null) blockerSheet.dispose();
        if (explosionSheet != null) explosionSheet.dispose();
        resultPanelTexture.dispose();
        resultButtonTexture.dispose();
        inGameSettingsTexture.dispose();
        pausePanelTexture.dispose();
        pauseMusicTexture.dispose();
        pauseSoundTexture.dispose();
        pauseSaveTexture.dispose();
        pauseContinueTexture.dispose();
        pauseMenuTexture.dispose();
        if (map != null) map.dispose();
        if (mapRenderer != null) mapRenderer.dispose();
        if (bgMusic != null) bgMusic.dispose();
        if (shootSound != null) shootSound.dispose();
        if (hitSound != null) hitSound.dispose();
    }
}

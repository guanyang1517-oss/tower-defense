package com.mygame.td.service;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.JsonReader;
import com.badlogic.gdx.utils.JsonValue;
import com.badlogic.gdx.utils.ObjectMap;
import com.mygame.td.config.GameConfig;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

public class RemoteConfigService {

    private static final String BASE_URL = "http://127.0.0.1:8080/api/runtime";
    private static final int CONNECT_TIMEOUT = 1500;
    private static final int READ_TIMEOUT = 3000;
    private static final String CACHE_DIR = "runtime-cache";
    private static final String GAME_CONFIG_CACHE = "game-config.json";
    private static final String ENEMY_TYPES_CACHE = "enemy-types.json";
    private static final String LEVELS_CACHE = "levels.json";

    private final Array<LevelConfigData> levels = new Array<>();
    private final ObjectMap<String, LevelConfigData> levelsByCode = new ObjectMap<>();
    private final ObjectMap<String, LevelConfigData> levelsByMapPath = new ObjectMap<>();
    private final ObjectMap<String, EnemyTypeData> enemyTypesByCode = new ObjectMap<>();
    private final ObjectMap<Long, EnemyTypeData> enemyTypesById = new ObjectMap<>();
    private final ObjectMap<String, LevelRuntimeData> runtimeLevelsByCode = new ObjectMap<>();

    public void loadAll() {
        GameConfig.resetDefaults();
        loadGameConfig();
        loadEnemyTypes();
        loadLevels();
    }

    public Array<LevelConfigData> getLevels() {
        return levels;
    }

    public EnemyTypeData findEnemyType(String enemyCode) {
        return enemyTypesByCode.get(enemyCode);
    }

    public EnemyTypeData findEnemyType(long enemyTypeId) {
        return enemyTypesById.get(enemyTypeId);
    }

    public LevelConfigData findLevelByMapPath(String mapPath) {
        return levelsByMapPath.get(mapPath);
    }

    public LevelConfigData findLevelByCode(String levelCode) {
        return levelsByCode.get(levelCode);
    }

    public LevelRuntimeData getRuntimeLevelByMapPath(String mapPath) {
        LevelConfigData level = findLevelByMapPath(mapPath);
        if (level == null || level.levelCode == null) {
            return null;
        }
        return getRuntimeLevel(level.levelCode);
    }

    public LevelRuntimeData getRuntimeLevel(String levelCode) {
        LevelRuntimeData cached = runtimeLevelsByCode.get(levelCode);
        if (cached != null) {
            return cached;
        }
        try {
            JsonValue data = requestData("/levels/" + levelCode + "/waves", getRuntimeLevelCacheFile(levelCode));
            LevelRuntimeData runtimeData = parseRuntimeLevel(data);
            if (runtimeData != null) {
                runtimeLevelsByCode.put(levelCode, runtimeData);
            }
            return runtimeData;
        } catch (Exception exception) {
            Gdx.app.log("RemoteConfigService", "Load runtime level failed: " + exception.getMessage());
            return null;
        }
    }

    private void loadGameConfig() {
        try {
            JsonValue data = requestData("/game-config", GAME_CONFIG_CACHE);
            GameConfig.applyRemote(parseGameBalance(data));
        } catch (Exception exception) {
            Gdx.app.log("RemoteConfigService", "Load game config failed: " + exception.getMessage());
        }
    }

    private void loadEnemyTypes() {
        enemyTypesByCode.clear();
        enemyTypesById.clear();
        try {
            JsonValue data = requestData("/enemy-types", ENEMY_TYPES_CACHE);
            if (data == null) {
                return;
            }
            for (JsonValue item = data.child; item != null; item = item.next) {
                EnemyTypeData enemyType = parseEnemyType(item);
                if (enemyType.enemyCode != null) {
                    enemyTypesByCode.put(enemyType.enemyCode, enemyType);
                }
                enemyTypesById.put(enemyType.id, enemyType);
            }
        } catch (Exception exception) {
            Gdx.app.log("RemoteConfigService", "Load enemy types failed: " + exception.getMessage());
        }
    }

    private void loadLevels() {
        levels.clear();
        levelsByCode.clear();
        levelsByMapPath.clear();
        runtimeLevelsByCode.clear();
        try {
            JsonValue data = requestData("/levels", LEVELS_CACHE);
            if (data == null) {
                return;
            }
            for (JsonValue item = data.child; item != null; item = item.next) {
                LevelConfigData level = parseLevel(item);
                levels.add(level);
                if (level.levelCode != null) {
                    levelsByCode.put(level.levelCode, level);
                }
                levelsByMapPath.put(level.mapPath, level);
            }
        } catch (Exception exception) {
            Gdx.app.log("RemoteConfigService", "Load levels failed: " + exception.getMessage());
        }
    }

    private JsonValue requestData(String path, String cacheFileName) throws Exception {
        try {
            String responseText = httpGet(BASE_URL + path);
            saveCache(cacheFileName, responseText);
            return parseResponseData(responseText);
        } catch (Exception exception) {
            JsonValue cacheData = loadCache(cacheFileName);
            if (cacheData != null) {
                Gdx.app.log("RemoteConfigService", "Use cached config: " + cacheFileName);
                return cacheData;
            }
            throw exception;
        }
    }

    private JsonValue parseResponseData(String responseText) {
        JsonValue root = new JsonReader().parse(responseText);
        if (root == null || !root.getBoolean("success", false)) {
            throw new IllegalStateException(root == null ? "empty response" : root.getString("message", "request failed"));
        }
        return root.get("data");
    }

    private String httpGet(String url) throws Exception {
        HttpURLConnection connection = null;
        try {
            connection = (HttpURLConnection) new URL(url).openConnection();
            connection.setRequestMethod("GET");
            connection.setConnectTimeout(CONNECT_TIMEOUT);
            connection.setReadTimeout(READ_TIMEOUT);
            connection.setRequestProperty("Accept", "application/json");
            connection.setRequestProperty("Charset", "UTF-8");

            int statusCode = connection.getResponseCode();
            InputStream stream = statusCode >= 200 && statusCode < 300
                    ? connection.getInputStream()
                    : connection.getErrorStream();

            StringBuilder builder = new StringBuilder();
            BufferedReader reader = new BufferedReader(new InputStreamReader(stream, "UTF-8"));
            String line;
            while ((line = reader.readLine()) != null) {
                builder.append(line);
            }
            reader.close();

            if (statusCode < 200 || statusCode >= 300) {
                throw new IllegalStateException("HTTP " + statusCode + ": " + builder);
            }
            return builder.toString();
        } finally {
            if (connection != null) {
                connection.disconnect();
            }
        }
    }

    private void saveCache(String cacheFileName, String content) {
        try {
            FileHandle fileHandle = getCacheFile(cacheFileName);
            fileHandle.parent().mkdirs();
            fileHandle.writeString(content, false, "UTF-8");
        } catch (Exception exception) {
            Gdx.app.log("RemoteConfigService", "Save cache failed: " + cacheFileName + ", " + exception.getMessage());
        }
    }

    private JsonValue loadCache(String cacheFileName) {
        try {
            FileHandle fileHandle = getCacheFile(cacheFileName);
            if (!fileHandle.exists()) {
                return null;
            }
            return parseResponseData(fileHandle.readString("UTF-8"));
        } catch (Exception exception) {
            Gdx.app.log("RemoteConfigService", "Load cache failed: " + cacheFileName + ", " + exception.getMessage());
            return null;
        }
    }

    private FileHandle getCacheFile(String cacheFileName) {
        return Gdx.files.local(CACHE_DIR + "/" + cacheFileName);
    }

    private String getRuntimeLevelCacheFile(String levelCode) {
        return "level-" + sanitizeFileName(levelCode) + "-waves.json";
    }

    private String sanitizeFileName(String value) {
        if (value == null || value.trim().isEmpty()) {
            return "unknown";
        }
        return value.replaceAll("[^a-zA-Z0-9._-]", "_");
    }

    private GameBalanceData parseGameBalance(JsonValue data) {
        if (data == null) {
            return null;
        }
        GameBalanceData balance = new GameBalanceData();
        balance.initialMoney = data.getInt("initialMoney", GameConfig.DEFAULT_INITIAL_MONEY);
        balance.initialHealth = data.getInt("initialHealth", GameConfig.DEFAULT_INITIAL_HEALTH);
        balance.killReward = data.getInt("killReward", GameConfig.DEFAULT_KILL_REWARD);
        balance.towerCost = data.getInt("towerCost", GameConfig.DEFAULT_TOWER_COST);
        balance.towerRange = data.getFloat("towerRange", GameConfig.DEFAULT_TOWER_RANGE);
        balance.blockerCost = data.getInt("blockerCost", GameConfig.DEFAULT_BLOCKER_COST);
        balance.blockerMaxHealth = data.getFloat("blockerMaxHealth", GameConfig.DEFAULT_BLOCKER_MAX_HEALTH);
        balance.blockerReturnDamage = data.getFloat("blockerReturnDamage", GameConfig.DEFAULT_BLOCKER_RETURN_DAMAGE);
        balance.enemyBaseSpeed = data.getFloat("enemyBaseSpeed", GameConfig.DEFAULT_ENEMY_BASE_SPEED);
        balance.enemyBaseHealth = data.getInt("enemyBaseHealth", GameConfig.DEFAULT_ENEMY_BASE_HEALTH);
        balance.enemyAttackDamage = data.getFloat("enemyAttackDamage", GameConfig.DEFAULT_ENEMY_ATTACK_DAMAGE);
        return balance;
    }

    private EnemyTypeData parseEnemyType(JsonValue data) {
        EnemyTypeData enemyType = new EnemyTypeData();
        enemyType.id = data.getLong("id", 0L);
        enemyType.enemyCode = data.getString("enemyCode", null);
        enemyType.enemyName = data.getString("enemyName", enemyType.enemyCode);
        enemyType.texturePath = data.getString("texturePath", "image/enemy.png");
        enemyType.attackTexturePath = data.getString("attackTexturePath", "image/enemy1.png");
        enemyType.health = data.getInt("health", GameConfig.DEFAULT_ENEMY_BASE_HEALTH);
        enemyType.speed = data.getFloat("speed", GameConfig.DEFAULT_ENEMY_BASE_SPEED);
        enemyType.attackDamage = data.getFloat("attackDamage", GameConfig.DEFAULT_ENEMY_ATTACK_DAMAGE);
        enemyType.reward = data.getInt("reward", GameConfig.DEFAULT_KILL_REWARD);
        enemyType.enabled = data.getBoolean("enabled", true);
        return enemyType;
    }

    private LevelConfigData parseLevel(JsonValue data) {
        LevelConfigData level = new LevelConfigData();
        level.id = data.getLong("id", 0L);
        level.levelCode = data.getString("levelCode", null);
        level.levelName = data.getString("levelName", level.levelCode);
        level.mapPath = data.getString("mapPath", "map/level1.tmx");
        level.maxWave = data.getInt("maxWave", 10);
        level.enabled = data.getBoolean("enabled", true);
        level.remark = data.getString("remark", "");
        return level;
    }

    private LevelRuntimeData parseRuntimeLevel(JsonValue data) {
        if (data == null || data.get("waves") == null || data.getString("levelCode", null) == null) {
            return null;
        }

        LevelRuntimeData runtimeData = new LevelRuntimeData();
        runtimeData.levelId = data.getLong("levelId", 0L);
        runtimeData.levelCode = data.getString("levelCode", null);
        runtimeData.levelName = data.getString("levelName", runtimeData.levelCode);
        runtimeData.mapPath = data.getString("mapPath", null);
        runtimeData.maxWave = data.getInt("maxWave", 0);

        JsonValue waves = data.get("waves");
        for (JsonValue waveJson = waves.child; waveJson != null; waveJson = waveJson.next) {
            WaveRuntimeData wave = new WaveRuntimeData();
            wave.id = waveJson.getLong("id", 0L);
            wave.waveNumber = waveJson.getInt("waveNumber", 1);
            wave.breakTime = waveJson.getFloat("breakTime", 5f);

            JsonValue spawns = waveJson.get("spawns");
            if (spawns != null) {
                for (JsonValue spawnJson = spawns.child; spawnJson != null; spawnJson = spawnJson.next) {
                    SpawnRuntimeData spawn = new SpawnRuntimeData();
                    spawn.id = spawnJson.getLong("id", 0L);
                    spawn.enemyTypeId = spawnJson.getLong("enemyTypeId", 0L);
                    spawn.enemyCode = spawnJson.getString("enemyCode", null);
                    spawn.spawnCount = spawnJson.getInt("spawnCount", 0);
                    spawn.spawnInterval = spawnJson.getFloat("spawnInterval", 1.5f);
                    spawn.pathIndex = spawnJson.getInt("pathIndex", 0);
                    spawn.spawnOrder = spawnJson.getInt("spawnOrder", 0);
                    wave.spawns.add(spawn);
                }
            }

            runtimeData.waves.add(wave);
        }
        return runtimeData;
    }

    public static class GameBalanceData {
        public int initialMoney;
        public int initialHealth;
        public int killReward;
        public int towerCost;
        public float towerRange;
        public int blockerCost;
        public float blockerMaxHealth;
        public float blockerReturnDamage;
        public float enemyBaseSpeed;
        public int enemyBaseHealth;
        public float enemyAttackDamage;
    }

    public static class EnemyTypeData {
        public long id;
        public String enemyCode;
        public String enemyName;
        public String texturePath;
        public String attackTexturePath;
        public int health;
        public float speed;
        public float attackDamage;
        public int reward;
        public boolean enabled;
    }

    public static class LevelConfigData {
        public long id;
        public String levelCode;
        public String levelName;
        public String mapPath;
        public int maxWave;
        public boolean enabled;
        public String remark;
    }

    public static class LevelRuntimeData {
        public long levelId;
        public String levelCode;
        public String levelName;
        public String mapPath;
        public int maxWave;
        public Array<WaveRuntimeData> waves = new Array<>();
    }

    public static class WaveRuntimeData {
        public long id;
        public int waveNumber;
        public float breakTime;
        public Array<SpawnRuntimeData> spawns = new Array<>();
    }

    public static class SpawnRuntimeData {
        public long id;
        public long enemyTypeId;
        public String enemyCode;
        public int spawnCount;
        public float spawnInterval;
        public int pathIndex;
        public int spawnOrder;
    }
}

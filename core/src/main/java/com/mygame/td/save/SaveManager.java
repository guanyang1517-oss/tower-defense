package com.mygame.td.save;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.utils.Json;

public class SaveManager {

    private static final String SAVE_FILE = "save.json";
    private static final String SETTINGS_FILE = "settings.json";

    public static void saveGame(SaveData data) {
        Json json = new Json();
        String saveText = json.toJson(data);
        FileHandle file = Gdx.files.local(SAVE_FILE);
        file.writeString(saveText, false);
        Gdx.app.log("SaveManager", "游戏已保存到: " + file.path());
    }

    public static SaveData loadGame() {
        FileHandle file = Gdx.files.local(SAVE_FILE);
        if (!file.exists()) {
            return null;
        }

        try {
            Json json = new Json();
            return json.fromJson(SaveData.class, file.readString());
        } catch (Exception e) {
            Gdx.app.error("SaveManager", "存档损坏或无法读取", e);
            return null;
        }
    }

    public static boolean hasSave() {
        return Gdx.files.local(SAVE_FILE).exists();
    }

    public static void saveSettings(SettingsData data) {
        Json json = new Json();
        FileHandle file = Gdx.files.local(SETTINGS_FILE);
        file.writeString(json.toJson(data), false);
    }

    public static SettingsData loadSettings() {
        FileHandle file = Gdx.files.local(SETTINGS_FILE);
        if (!file.exists()) {
            return null;
        }

        try {
            Json json = new Json();
            return json.fromJson(SettingsData.class, file.readString());
        } catch (Exception e) {
            Gdx.app.error("SaveManager", "设置文件损坏或无法读取", e);
            return null;
        }
    }
}

package com.mygame.td;

import com.badlogic.gdx.Game;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.freetype.FreeTypeFontGenerator;
import com.badlogic.gdx.graphics.g2d.freetype.FreeTypeFontGenerator.FreeTypeFontParameter;
import com.mygame.td.screen.MainMenuScreen;
import com.mygame.td.screen.SettingsScreen;
import com.mygame.td.save.SaveManager;
import com.mygame.td.save.SettingsData;
import com.mygame.td.service.RemoteConfigService;

public class MainGame extends Game {

    public SpriteBatch batch;
    public BitmapFont fontLarge;
    public BitmapFont fontSmall;
    public final RemoteConfigService remoteConfigService = new RemoteConfigService();
    private FreeTypeFontGenerator fontGenerator;

    @Override
    public void create() {
        batch = new SpriteBatch();

        fontGenerator = new FreeTypeFontGenerator(Gdx.files.internal("font/myfont.ttc"));

        String chineseChars = "黑夜君临开始游戏继续游戏游戏设置退出游戏主菜单永夜降临守住最后防线战役菜单点击右侧按钮选择操作"
                + "已检测到本地存档可直接进入当前没有存档请先读取本地进度继续战斗未调整音量与音效开关离开返回桌面"
                + "欢迎回来选择关卡第一关第二关地图波数音乐音效开启关闭总菜单设置存档损坏无法读取保存成功金币生命"
                + "波次游戏结束点击屏幕返回可直接建造防御塔路障怪物路径此处已有建筑继续挑战"
                + "故土沦亡夜雨蚀骨血海深仇饮血淬刃化身渡夜负罪前行誓诛黑夜胜利通关游戏失败";
        String allChars = FreeTypeFontGenerator.DEFAULT_CHARS + chineseChars + " -_%0123456789，。：、";

        FreeTypeFontParameter parameter = new FreeTypeFontParameter();
        parameter.size = 60;
        parameter.borderWidth = 3;
        parameter.borderColor = Color.BLACK;
        parameter.shadowOffsetX = 5;
        parameter.shadowOffsetY = 5;
        parameter.incremental = true;
        parameter.characters = allChars;
        fontLarge = fontGenerator.generateFont(parameter);

        parameter.size = 36;
        parameter.borderWidth = 1;
        parameter.shadowOffsetX = 2;
        parameter.shadowOffsetY = 2;
        parameter.characters = allChars;
        fontSmall = fontGenerator.generateFont(parameter);

        SettingsData settingsData = SaveManager.loadSettings();
        if (settingsData != null) {
            SettingsScreen.isSoundOn = settingsData.soundEnabled;
            SettingsScreen.musicVolume = clamp(settingsData.musicVolume);
            SettingsScreen.soundVolume = clamp(settingsData.soundVolume);
        }

        remoteConfigService.loadAll();
        this.setScreen(new MainMenuScreen(this));
    }

    private float clamp(float value) {
        if (value < 0f) {
            return 0f;
        }
        if (value > 1f) {
            return 1f;
        }
        return value;
    }

    @Override
    public void dispose() {
        batch.dispose();
        fontLarge.dispose();
        fontSmall.dispose();
        if (fontGenerator != null) {
            fontGenerator.dispose();
        }
    }
}

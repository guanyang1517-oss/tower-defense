//数据模型
package com.mygame.td.save;

import java.util.ArrayList;

// 这个类专门用来把数据变成文字
public class SaveData {
    public int money;
    public int playerHealth;
    public int waveNumber;
    public String levelCode;
    public String mapPath; // 记住是在哪张地图保存的

    // 用来存塔的位置信息
    public ArrayList<BuildingData> buildings = new ArrayList<>();

    // 必须有一个空构造函数供 JSON 使用
    public SaveData() {}

    // 内部类：记录单个建筑的信息
    public static class BuildingData {
        public String type; // "TOWER" 或 "BLOCKER"
        public float x;
        public float y;

        public BuildingData() {}
        public BuildingData(String type, float x, float y) {
            this.type = type;
            this.x = x;
            this.y = y;
        }
    }
}

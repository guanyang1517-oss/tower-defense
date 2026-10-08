// 可受击对象接口
package com.mygame.td.entity;

import com.badlogic.gdx.math.Vector2;

public interface IDamageable {
    void takeDamage(int damage); // 承受伤害
    boolean isAlive();           // 是否存活
    Vector2 getPosition();       // 获取坐标
}

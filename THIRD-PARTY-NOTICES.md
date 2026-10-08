# 第三方资源声明

本文件说明本仓库中**不属于 MIT 协议覆盖范围**的内容。源代码本身以根目录的
[LICENSE](LICENSE)（MIT）授权，以下第三方组件的版权归各自权利人所有。

## 1. 程序库与脚手架代码

| 组件 | 位置 | 协议 |
| --- | --- | --- |
| libGDX 及 lwjgl3 桌面后端 | Gradle 依赖（未随仓库分发源码） | Apache License 2.0 |
| `StartupHelper.java` | `lwjgl3/src/main/java/com/mygame/td/lwjgl3/StartupHelper.java` | Apache License 2.0，来自 libGDX 的 gdx-liftoff 脚手架，作者 damios。该文件头部的声明注明其协议**仅适用于该文件本身** |
| Gradle Wrapper | `gradlew`、`gradlew.bat`、`gradle/wrapper/` | Apache License 2.0 |

## 2. 字体

`assets/font/` 下的以下文件为 **Microsoft 所有**，属于专有字体软件，不在 MIT 授权范围内：

- `msyh.ttc`（微软雅黑）
- `msyhbd.ttc`（微软雅黑 Bold）
- `msyhl.ttc`（微软雅黑 Light）
- `myfont.ttc`（与 `msyh.ttc` 内容相同的副本，项目内用于生成中文位图字体）

如果你要分发或商用本项目，**必须替换为自己拥有合法授权的字体**，否则会构成侵权。
仅使用源代码而不运行游戏时，可以删除整个 `assets/font/` 目录。

## 3. 美术与音频

`assets/audio/`、`assets/image/`、`assets/map/` 下的图片、音乐、音效与地图瓦片，
以及 `docs/screenshots/` 中由这些素材生成的界面截图，均为**课程设计演示用途**，
其中部分素材可能来自第三方，版权归原作者所有，同样不在 MIT 授权范围内。

二次分发或商用前请自行确认并替换为拥有授权的素材。

## 4. 如果你要基于本项目继续开发

如果你只想复用**代码逻辑**，可以安全地：

1. 保留 `core/`、`lwjgl3/src/`、`manage/` 下的源代码；
2. 删除或替换 `assets/` 与 `docs/screenshots/` 中所有美术、音频、字体资源；
3. 自行提供符合授权的替代素材。

这样你的衍生作品只需要遵守 MIT 协议。

# EasyBot Mods

EasyBot的Minecraft模组实现端,基于[Stonecutter](https://stonecutter.kikugie.dev/wiki/start/)实现多个加载器、多个游戏版本适配。

## 使用说明

- 使用 Gradle 任务中的 `"Set active project to ..."` 来更新 `src/` 目录下类文件可用的 Minecraft 版本。
- 使用 `buildAndCollect` Gradle 任务将模组发布文件存储在 `build/libs/` 目录。

## 播报冷却

在 `config/easybot/config.json` 的 `sync` 中配置冷却秒数，例如：

```json
"sync": {
  "chatImageSupport": true,
  "joinCooldownSeconds": 60,
  "quitCooldownSeconds": 60,
  "deathCooldownSeconds": 60
}
```

- 三项默认均为 `0`，`0` 或负数表示关闭；旧配置无需修改。
- 进服、退服按玩家 UUID 分别计时；死亡按玩家 UUID 和伤害类型分别计时。
- 首次正常播报，冷却中的重复事件不延长时间；切换死因不会清除原死因的冷却。
- 只跳过发往主程序的对应播报，不取消游戏事件或改变登录验证。`skipOptions` 仍优先生效。
- 修改某项时长并重载后，该类事件下次发生时清空原冷却；冷却不写入磁盘，重启后重新计时。

## 实用链接

- [Stonecutter 新手指南](https://stonecutter.kikugie.dev/wiki/start/)：*提示：您必须理解其运作原理！*
- [提问的智慧指南](https://github.com/ryanhanwu/How-To-Ask-Questions-The-Smart-Way/blob/main/README-zh_CN.md)：另附[视频版](https://www.youtube.com/results?search_query=How+To+Ask+Questions+The+Smart+Way)。


## 特别说明

不要使用IDE的全局代码优化功能,由于框架特性 部分“未使用”的代码，只会在实际打包时被引用，你应该选择性忽视他们 (如果你觉得烦可以加上if注释来消除这个警告,目前还没有做优化)

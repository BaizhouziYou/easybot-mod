# EasyBot Mods

EasyBot的Minecraft模组实现端,基于[Stonecutter](https://stonecutter.kikugie.dev/wiki/start/)实现多个加载器、多个游戏版本适配。

## 使用说明

- 使用 Gradle 任务中的 `"Set active project to ..."` 来更新 `src/` 目录下类文件可用的 Minecraft 版本。
- 使用 `buildAndCollect` Gradle 任务将模组发布文件存储在 `build/libs/` 目录。

## 播报冷却

管理员可用 `/easybot config` 查看冷却配置，或执行以下命令保存并即时生效。默认权限等级为 3，与 reload 相同；支持下文的命令开关、权限等级和 LuckPerms 节点配置。

```text
/easybot config sync.joinCooldownSeconds 60
/easybot config sync.quitCooldownSeconds 60
/easybot config sync.deathCooldownSeconds 30
```

省略秒数时查看该项；支持配置项和常用秒数补全。秒数须为 32 位整数，`0` 或负数关闭冷却。命令只开放上述三个冷却项，保存失败时保留原配置。

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

## 消息转发过滤

在 `config/easybot/config.json` 中配置，省略新字段时保持原有行为：

```json
"chatFilter": {
  "gameToGroup": {
    "maxLength": 100,
    "blockedKeywords": ["广告词"],
    "blockedPlayerNames": ["PlayerName"],
    "blockedPlayerUuids": []
  },
  "groupToGame": {
    "maxLength": 200,
    "blockedKeywords": ["广告词"]
  }
}
```

- 默认长度为 `0`（不限）、列表为空；超限或命中规则后整条消息不转发，不截断，也不取消游戏内聊天。
- 关键词是普通文本包含匹配，忽略大小写；颜色代码不参与匹配或计数。长度按 Unicode 码点计算（组合 emoji 可能占多个码点）。
- 游戏到群使用发送包里的 `player_name_raw` 和 UUID，不使用显示昵称；名称忽略大小写，UUID 使用带连字符的完整格式。Floodgate 玩家沿用现有身份转换规则。
- 群到游戏同时检查主程序传来的纯文本与富文本段的文本表示，可能包含昵称、模板前缀、图片说明等，不读取图片内容。连续文本段不能绕过关键词检查。现有协议未提供发言人身份，因此此方向不支持玩家名单。
- `/easybot say` 也受过滤限制，拒绝时提示“未转发”；控制台只检查消息内容和长度。`skipOptions.skipChat` 仍仅控制自动聊天同步。
- 配置保存后自动重载，也可使用 `/easybot reload`；无效配置保留上一份有效配置并在后台报告错误。

## 命令开关与权限

`command.enabled` 可为下表中的配置键设置 `false`，禁用对应命令。`command.permissionLevels` 可设置未指定权限节点时使用的原版权限等级（`0`–`4`）。例如：

```json
"command": {
  "allowBind": true,
  "waitTime": 3,
  "enabled": { "say": false },
  "permissionLevels": { "status": 3, "reload": 3 }
}
```

| 命令 | 配置键 | LuckPerms 权限节点 | 默认等级 |
| --- | --- | --- | --- |
| `/easybot`、`/easybot help` | `help` | `easybot.command.help` | 0 |
| `/easybot say <消息>` | `say` | `easybot.command.say` | 0 |
| `/easybot status`（服务状态） | `status` | `easybot.command.status` | 3 |
| `/easybot reload` | `reload` | `easybot.command.reload` | 3 |
| `/easybot config <配置项> [秒数]` | `config` | `easybot.command.config` | 3 |
| `/easybot bind`、`/easybot bind confirm` | `bind` | `easybot.command.bind` | 0 |
| `/easybot confirm <code>` | `confirm` | `easybot.command.confirm`，同时要求 `bind` 权限 | 0 |
| `/easybot bind status`（绑定状态） | `bindStatus` | `easybot.command.bindstatus` | 0 |

关闭 `allowBind` 或禁用 `bind` 会同时阻止发起和确认绑定，绑定状态查询可独立使用。帮助和补全遵守开关与权限；若禁用了 `reload`，仍可修改配置文件，由自动重载恢复。

LuckPerms 为可选依赖，不随 Mod 打包，需在子服安装对应加载器版本。节点显式允许/拒绝优先于原版权限等级，显式拒绝对 OP 同样有效；节点未设置或未安装 LuckPerms 时使用上表等级。禁用的命令不受权限授予影响。

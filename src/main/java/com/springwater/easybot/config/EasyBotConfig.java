package com.springwater.easybot.config;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.HashMap;
import java.util.UUID;

@Getter
@Setter
@ToString
public class EasyBotConfig {
    private String ws = "ws://127.0.0.1:26990/bridge";
    private boolean debug = false;
    private int reconnectInterval = 5000;
    private String token = "YOUR_TOKEN_HERE";
    private boolean ignoreError = false;
    private boolean updateNotify = true;
    private boolean enableWhiteList = false;
    
    private Message message = new Message();
    private Command command = new Command();
    private SkipOptions skipOptions = new SkipOptions();
    private Geyser geyser = new Geyser();
    private Event event = new Event();

    private Fabric fabric = new Fabric();
    
    private Sync sync = new Sync();
    private ChatFilter chatFilter = new ChatFilter();
    
    @Getter
    @Setter
    @ToString
    public static class Fabric{
        private boolean useMixinReport1201 = false;
    }
    
    @Getter
    @Setter
    @ToString
    public static class Message {
        private String bindStart = "[!] 绑定开始,请加群12345678输入: \"绑定 #code\" 进行绑定, 请在#time完成绑定!";
        private String bindSuccess = "§f[§a!§f] 绑定§f §a#account §f(§a#name§f) 成功!";
        private String bindFail = "§f[§c!§f] §c绑定失败 #why";
    }

    @Getter
    @Setter
    @ToString
    public static class Command {
        private boolean allowBind = true;
        private int waitTime = 3;
        private Map<String, Boolean> enabled = new HashMap<>();
        private Map<String, Integer> permissionLevels = new HashMap<>();
    }

    @Getter
    @Setter
    public static class ChatFilter {
        private FilterRules gameToGroup = new FilterRules();
        private FilterRules groupToGame = new FilterRules();
    }

    @Getter
    @Setter
    public static class FilterRules {
        private int maxLength = 0;
        private List<String> blockedKeywords = new ArrayList<>();
        private List<String> blockedPlayerNames = new ArrayList<>();
        private List<String> blockedPlayerUuids = new ArrayList<>();

        private void validate() {
            if (maxLength < 0) throw new IllegalArgumentException("maxLength 不能为负数");
            if (blockedKeywords == null || blockedPlayerNames == null || blockedPlayerUuids == null)
                throw new IllegalArgumentException("过滤列表不能为 null");
            for (var list : List.of(blockedKeywords, blockedPlayerNames, blockedPlayerUuids)) {
                for (String value : list) {
                    if (value == null || value.isBlank()) throw new IllegalArgumentException("过滤列表不能包含空值");
                }
            }
            for (String uuid : blockedPlayerUuids) {
                if (!UUID.fromString(uuid).toString().equalsIgnoreCase(uuid))
                    throw new IllegalArgumentException("无效的玩家 UUID: " + uuid);
            }
        }
    }

    public void validate() {
        if (ws == null || token == null || message == null || skipOptions == null || geyser == null
                || event == null || event.bindSuccess == null || fabric == null || sync == null)
            throw new IllegalArgumentException("基础配置不能为 null");
        if (command == null || chatFilter == null || chatFilter.gameToGroup == null || chatFilter.groupToGame == null)
            throw new IllegalArgumentException("command/chatFilter 不能为 null");
        chatFilter.gameToGroup.validate();
        chatFilter.groupToGame.validate();
        if (!chatFilter.groupToGame.blockedPlayerNames.isEmpty() || !chatFilter.groupToGame.blockedPlayerUuids.isEmpty())
            throw new IllegalArgumentException("groupToGame 不支持玩家名单，主程序未提供发言人身份");
        var commands = List.of("help", "say", "status", "reload", "config", "bind", "confirm", "bindStatus");
        if (command.enabled == null || command.permissionLevels == null)
            throw new IllegalArgumentException("命令配置不能为 null");
        command.enabled.forEach((name, value) -> {
            if (!commands.contains(name) || value == null) throw new IllegalArgumentException("无效的命令开关: " + name);
        });
        command.permissionLevels.forEach((name, value) -> {
            if (!commands.contains(name) || value == null || value < 0 || value > 4)
                throw new IllegalArgumentException("无效的命令权限等级: " + name);
        });
    }

    @Getter
    @Setter
    @ToString
    public static class SkipOptions {
        private boolean skipJoin = false;
        private boolean skipQuit = false;
        private boolean skipChat = false;
        private boolean skipDeath = false;
    }

    @Getter
    @Setter
    @ToString
    public static class Geyser {
        private boolean ignorePrefix = false;
        private boolean useRealUuid = false;
    }
    
    @Getter
    @Setter
    @ToString
    public static class Event{
        private boolean enableSuccessEvent = false;
        private List<String> bindSuccess = new ArrayList<>(List.of("say 玩家$player绑定成功,Id=$account,账号名字=$name")); // 这是个默认值
    }
    
    @Getter
    @Setter
    @ToString
    public static class Sync {
        private boolean chatImageSupport = true;
        private int joinCooldownSeconds = 0;
        private int quitCooldownSeconds = 0;
        private int deathCooldownSeconds = 0;
    }
}

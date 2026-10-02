package com.springwater.easybot.utils;

import com.springwater.easybot.bridge.message.Segment;
import com.springwater.easybot.bridge.packet.PlayerInfoWithRaw;
import com.springwater.easybot.config.ConfigLoader;
import com.springwater.easybot.config.EasyBotConfig.FilterRules;

import java.util.List;
import java.util.Locale;

public class ChatFilterUtils {
    public static String outgoingRejection(PlayerInfoWithRaw player, String message) {
        var rules = ConfigLoader.get().getChatFilter().getGameToGroup();
        if (player.getUuid() != null && !player.getUuid().isEmpty()) {
            if (rules.getBlockedPlayerNames().stream().anyMatch(name -> name.equalsIgnoreCase(player.getNameRaw()))
                    || rules.getBlockedPlayerUuids().stream().anyMatch(uuid -> uuid.equalsIgnoreCase(player.getUuid())))
                return "此玩家已被禁止转发消息";
        }
        return rejection(rules, message);
    }

    public static boolean blocksIncoming(String text, List<Segment> segments) {
        var rules = ConfigLoader.get().getChatFilter().getGroupToGame();
        if (rejection(rules, text) != null) return true;
        if (segments == null) return false;
        var plain = new StringBuilder();
        for (Segment segment : segments) {
            if (segment != null && segment.getText() != null) plain.append(segment.getText());
        }
        return rejection(rules, plain.toString()) != null;
    }

    private static String rejection(FilterRules rules, String text) {
        if (text == null) return null;
        // 检查转发文本，不修改原消息；颜色代码不能将关键词拆开绕过过滤。
        String plain = text.replaceAll("(?i)§[0-9A-FK-ORX]", "");
        if (rules.getMaxLength() > 0 && plain.codePointCount(0, plain.length()) > rules.getMaxLength())
            return "消息超过转发长度限制";
        String lower = plain.toLowerCase(Locale.ROOT);
        for (String keyword : rules.getBlockedKeywords()) {
            if (lower.contains(keyword.toLowerCase(Locale.ROOT))) return "消息包含禁止转发的关键词";
        }
        return null;
    }
}

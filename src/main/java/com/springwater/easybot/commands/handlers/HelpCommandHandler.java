package com.springwater.easybot.commands.handlers;

import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.context.CommandContext;
import com.springwater.easybot.commands.ICommandHandler;
import com.springwater.easybot.utils.PermissionUtils;
import com.springwater.easybot.platforms.ModData;
import net.minecraft.ChatFormatting;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;

public class HelpCommandHandler implements ICommandHandler {

    @Override
    public void register(LiteralArgumentBuilder<CommandSourceStack> stack) {
        stack
                .then(LiteralArgumentBuilder.<CommandSourceStack>literal("help").requires(source -> PermissionUtils.canUse(source, "help")).executes(this::sendGetHelp))
                .executes(this::sendGetHelp);
    }

    private int sendGetHelp(CommandContext<CommandSourceStack> context) {
        CommandSourceStack source = context.getSource();
        if (!PermissionUtils.canUse(source, "help")) {
            source.sendFailure(Component.literal("此命令已禁用或没有权限"));
            return 0;
        }

        MutableComponent root = Component.empty();
        root.append(Component.literal("--------------------------------------------------")
                .withStyle(ChatFormatting.GRAY));
        root.append(Component.literal("\n EasyBot Fabric V")
                .withStyle(ChatFormatting.GRAY)
                .append(Component.literal(ModData.VERSION)
                        .withStyle(ChatFormatting.GREEN)));
        if (PermissionUtils.canUse(source, "help")) root.append(buildCommandLine("/easybot help", "获取帮助"));
        if (PermissionUtils.canUse(source, "say")) root.append(buildCommandLine("/easybot say <消息>", "主动消息同步"));
        if (PermissionUtils.canUse(source, "status")) root.append(buildCommandLine("/easybot status", "获取状态"));
        if (PermissionUtils.canUse(source, "reload")) root.append(buildCommandLine("/easybot reload", "重载配置"));
        if (PermissionUtils.canUse(source, "config")) root.append(buildCommandLine("/easybot config <配置项> [秒数]", "查看或修改播报冷却"));
        root.append(Component.literal("\n--------------------------------------------------")
                .withStyle(ChatFormatting.GRAY));
        if (PermissionUtils.canUse(source, "bind")) root.append(buildCommandLine("/easybot bind", "绑定账号"));
        if (PermissionUtils.canUse(source, "confirm")) root.append(buildCommandLine("/easybot confirm <code>", "确认绑定"));
        if (PermissionUtils.canUse(source, "bindStatus")) root.append(buildCommandLine("/easybot bind status", "查看绑定状态"));
        source.sendSuccess(() -> root, false);
        return 1;
    }

    private MutableComponent buildCommandLine(String command, String description) {
        return Component.literal("\n")
                .append(Component.literal(command)
                        .withStyle(ChatFormatting.DARK_GREEN))
                .append(Component.literal(" - ")
                        .withStyle(ChatFormatting.GRAY))
                .append(Component.literal(description)
                        .withStyle(ChatFormatting.DARK_GRAY));
    }
}
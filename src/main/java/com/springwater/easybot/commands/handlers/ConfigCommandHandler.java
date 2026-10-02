package com.springwater.easybot.commands.handlers;

import com.mojang.brigadier.arguments.IntegerArgumentType;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.springwater.easybot.commands.ICommandHandler;
import com.springwater.easybot.config.ConfigLoader;
import com.springwater.easybot.platforms.ModData;
import com.springwater.easybot.utils.PermissionUtils;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.network.chat.Component;

public class ConfigCommandHandler implements ICommandHandler {
    @Override
    public void register(LiteralArgumentBuilder<CommandSourceStack> stack) {
        var config = Commands.literal("config")
                .requires(source -> PermissionUtils.canUse(source, "config"))
                .executes(context -> {
                    for (String key : ConfigLoader.COOLDOWN_KEYS) show(context.getSource(), key);
                    context.getSource().sendSystemMessage(Component.literal("用法: /easybot config <配置项> [秒数]，0 或负数关闭冷却"));
                    return 1;
                });
        for (String key : ConfigLoader.COOLDOWN_KEYS) {
            config.then(Commands.literal("sync." + key)
                    .executes(context -> show(context.getSource(), key))
                    .then(Commands.argument("seconds", IntegerArgumentType.integer())
                            .suggests((context, builder) -> {
                                for (String value : new String[]{"0", "30", "60"}) {
                                    if (value.startsWith(builder.getRemaining())) builder.suggest(value);
                                }
                                return builder.buildFuture();
                            })
                            .executes(context -> {
                                int seconds = IntegerArgumentType.getInteger(context, "seconds");
                                try {
                                    ConfigLoader.setCooldown(key, seconds);
                                    context.getSource().sendSuccess(() -> Component.literal("已保存并生效: sync." + key + " = " + seconds + " 秒"), false);
                                    return 1;
                                } catch (Exception e) {
                                    ModData.LOGGER.error("保存播报冷却配置失败", e);
                                    context.getSource().sendFailure(Component.literal("保存失败，冷却配置未更改，请检查服务器日志"));
                                    return 0;
                                }
                            })));
        }
        stack.then(config);
    }

    private int show(CommandSourceStack source, String key) {
        source.sendSystemMessage(Component.literal("sync." + key + " = " + ConfigLoader.getCooldown(key) + " 秒"));
        return 1;
    }
}

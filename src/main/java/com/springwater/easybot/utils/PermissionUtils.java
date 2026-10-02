package com.springwater.easybot.utils;

import net.minecraft.commands.CommandSourceStack;
import com.springwater.easybot.config.ConfigLoader;
import com.springwater.easybot.platforms.EasyBotModImpl;
import net.luckperms.api.LuckPermsProvider;
import net.luckperms.api.util.Tristate;
//? >= 1.21.11 {
import net.minecraft.server.permissions.Permissions;
//?}
public class PermissionUtils {
    public static boolean canUse(CommandSourceStack source, String command) {
        var config = ConfigLoader.get().getCommand();
        if (!config.getEnabled().getOrDefault(command, true)) return false;
        if ((command.equals("bind") || command.equals("confirm")) && !config.isAllowBind()) return false;
        if (command.equals("confirm") && !canUse(source, "bind")) return false;
        if (source.isPlayer() && EasyBotModImpl.INSTANCE.isModLoaded("luckperms")) {
            // 独立类仅在 LuckPerms 已加载时使用，缺少可选依赖不会影响启动。
            Boolean allowed = LuckPermsPermissions.check(source, "easybot.command." + command.toLowerCase(java.util.Locale.ROOT));
            if (allowed != null) return allowed;
        }
        int defaultLevel = command.equals("reload") || command.equals("status") ? 3 : 0;
        return hasPermission(source, config.getPermissionLevels().getOrDefault(command, defaultLevel));
    }

    private static class LuckPermsPermissions {
        private static boolean listening;

        private static synchronized void listen(net.luckperms.api.LuckPerms api) {
            if (listening) return;
            api.getEventBus().subscribe(net.luckperms.api.event.user.UserDataRecalculateEvent.class, event -> {
                var server = EasyBotModImpl.INSTANCE.getServer();
                if (server != null) server.execute(() -> {
                    var player = server.getPlayerList().getPlayer(event.getUser().getUniqueId());
                    if (player != null) server.getCommands().sendCommands(player);
                });
            });
            listening = true;
        }

        static Boolean check(CommandSourceStack source, String node) {
            try {
                var api = LuckPermsProvider.get();
                listen(api);
                var user = api.getUserManager().getUser(source.getPlayer().getUUID());
                if (user == null) return false;
                var result = user.getCachedData().getPermissionData().checkPermission(node);
                return result == Tristate.UNDEFINED ? null : result.asBoolean();
            } catch (IllegalStateException e) {
                return false;
            }
        }
    }

    public static boolean hasPermission(CommandSourceStack commandSourceStack, int permissionLevel) {
        if (permissionLevel == 0) return true;
        //? >= 1.21.11 {
        return commandSourceStack.permissions().hasPermission(
                switch (permissionLevel) {
                    case 1 -> Permissions.COMMANDS_MODERATOR;
                    case 2 -> Permissions.COMMANDS_GAMEMASTER;
                    case 3 -> Permissions.COMMANDS_ADMIN;
                    case 4 -> Permissions.COMMANDS_OWNER;
                    default -> throw new IllegalStateException("Unexpected value: " + permissionLevel);
                }
        );
        //?} else {
        /*return commandSourceStack.hasPermission(permissionLevel);
         *///?}
    }
}

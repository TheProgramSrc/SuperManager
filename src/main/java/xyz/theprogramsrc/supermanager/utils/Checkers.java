package xyz.theprogramsrc.supermanager.utils;

import org.bukkit.entity.Player;

public class Checkers {

    public static boolean hasPermission(Player player, String permission, String... wildcardPermissions){
        return player.hasPermission(permission) || player.isOp();
    }
}

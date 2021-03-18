package xyz.theprogramsrc.supermanager.commands;

import org.bukkit.entity.Player;
import xyz.theprogramsrc.supercoreapi.spigot.commands.CommandResult;
import xyz.theprogramsrc.supercoreapi.spigot.commands.SpigotCommand;
import xyz.theprogramsrc.supercoreapi.spigot.utils.SpigotConsole;
import xyz.theprogramsrc.supermanager.guis.MainGUI;

public class MainCommand extends SpigotCommand {

    @Override
    public String getCommand() {
        return "smanager";
    }

    @Override
    public String[] getAliases() {
        return new String[]{
                "supermanager",
        };
    }

    @Override
    public CommandResult onPlayerExecute(Player player, String[] args) {
        new MainGUI(player);
        return CommandResult.COMPLETED;
    }

    @Override
    public CommandResult onConsoleExecute(SpigotConsole spigotConsole, String[] strings) {
        return CommandResult.NOT_SUPPORTED;
    }

    @Override
    public String getPermission() {
        return "supermanager.admin";
    }
}

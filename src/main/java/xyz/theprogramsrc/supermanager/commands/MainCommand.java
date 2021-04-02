package xyz.theprogramsrc.supermanager.commands;

import org.bukkit.entity.Player;
import xyz.theprogramsrc.supercoreapi.global.utils.Utils;
import xyz.theprogramsrc.supercoreapi.spigot.commands.CommandResult;
import xyz.theprogramsrc.supercoreapi.spigot.commands.SpigotCommand;
import xyz.theprogramsrc.supercoreapi.spigot.utils.SpigotConsole;
import xyz.theprogramsrc.supermanager.L;
import xyz.theprogramsrc.supermanager.SuperManager;
import xyz.theprogramsrc.supermanager.guis.MainGUI;
import xyz.theprogramsrc.supermanager.objects.Module;

import java.util.List;
import java.util.stream.Collectors;

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
        if(args.length == 0){
            new MainGUI(player);
        }else{
            Module module = SuperManager.i.getModule(args[0]);
            if(module == null){
                this.getSuperUtils().sendMessage(player, this.getSettings().getPrefix()+ L.FAILED_TO_RETRIEVE_MODULE);
            }else{
                String moduleUsePermission = "supermanager.module." + module.getIdentifier() + ".use";
                if(!player.hasPermission(moduleUsePermission)){
                    this.getSuperUtils().sendMessage(player, this.getSettings().getPrefix() + L.NO_ACCESS_TO_MODULE
                            .options()
                            .placeholder("{ModuleName}", module.getDisplay())
                            .placeholder("{ModulePermission}", moduleUsePermission));
                }else{
                    if(args.length == 1){
                        if(!module.isEnabled()){
                            this.getSuperUtils().sendMessage(player, this.getSettings().getPrefix() + L.MODULE_DISABLED.options().placeholder("{ModuleName}", module.getDisplay()));
                        }else{
                            module.onAction(player);
                        }
                    }else{
                        if(args[1].equalsIgnoreCase("enable")){
                            if(!module.isEnabled()){
                                module.setEnabled(true);
                                this.getSuperUtils().sendMessage(player, this.getSettings().getPrefix() + L.MODULE_STATUS_CHANGED.options().placeholder("{ModuleName}", module.getDisplay()).placeholder("{Status}", Utils.parseEnabledBoolean(true)));
                            }else{
                                this.getSuperUtils().sendMessage(player, this.getSettings().getPrefix() + L.MODULE_ALREADY_ENABLED.options().placeholder("{ModuleName}", module.getDisplay()));
                            }
                        }else if(args[1].equalsIgnoreCase("disable")){
                            if(module.isEnabled()){
                                module.setEnabled(false);
                                this.getSuperUtils().sendMessage(player, this.getSettings().getPrefix() + L.MODULE_STATUS_CHANGED.options().placeholder("{ModuleName}", module.getDisplay()).placeholder("{Status}", Utils.parseEnabledBoolean(false)));
                            }else{
                                this.getSuperUtils().sendMessage(player, this.getSettings().getPrefix() + L.MODULE_ALREADY_DISABLED.options().placeholder("{ModuleName}", module.getDisplay()));
                            }
                        }else{
                            return CommandResult.INVALID_ARGS;
                        }
                    }
                }
            }
        }
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

    @Override
    public List<String> getCommandComplete(Player player, String alias, String[] args) {
        if(args.length == 0){
            return SuperManager.i.getEnabledModules().stream().map(Module::getIdentifier).collect(Collectors.toList());
        }else {
            if(args.length == 1){
                return SuperManager.i.getEnabledModules().stream().map(Module::getIdentifier).filter(id -> id.toLowerCase().contains(args[0].toLowerCase())).collect(Collectors.toList());
            }else if(args.length == 2){
                return Utils.toList("enable", "disable");
            }
        }
        return super.getCommandComplete(player, alias, args);
    }
}

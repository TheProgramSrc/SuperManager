package xyz.theprogramsrc.supermanager.modules.chatchannels.commands;

import org.bukkit.entity.Player;
import xyz.theprogramsrc.supercoreapi.global.utils.Utils;
import xyz.theprogramsrc.supercoreapi.spigot.commands.CommandResult;
import xyz.theprogramsrc.supercoreapi.spigot.commands.SpigotCommand;
import xyz.theprogramsrc.supercoreapi.spigot.utils.SpigotConsole;
import xyz.theprogramsrc.supermanager.L;
import xyz.theprogramsrc.supermanager.modules.chatchannels.ChatChannelsModule;
import xyz.theprogramsrc.supermanager.modules.chatchannels.ChatChannelsStorage;
import xyz.theprogramsrc.supermanager.modules.chatchannels.listeners.ChatChannelsManager;
import xyz.theprogramsrc.supermanager.modules.chatchannels.objects.ChatChannel;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

public class ChatChannelCommand extends SpigotCommand {

    @Override
    public String getPermission() {
        return "chatchannels.use";
    }

    @Override
    public String getCommand() {
        return ChatChannelsStorage.i.command();
    }

    @Override
    public CommandResult onPlayerExecute(Player player, String[] args) {
        if(!ChatChannelsModule.i.isEnabled() && !ChatChannelsModule.i.isRunning()) return CommandResult.COMPLETED;
        if(args.length == 0){
            return CommandResult.INVALID_ARGS;
        }else{
            if(args[0].equalsIgnoreCase("join")){
                if(args.length == 1){
                    return CommandResult.INVALID_ARGS;
                }else{
                    ChatChannel chatChannel = ChatChannelsStorage.i.get(args[1]);
                    if(chatChannel == null){
                        this.getSuperUtils().sendMessage(player, L.CHAT_CHANNELS_DOESNT_EXISTS.options().placeholder("{ChannelName}", args[1]).get());
                    }else{
                        if(!player.hasPermission(chatChannel.getJoinPermission())){
                            return CommandResult.NO_PERMISSION;
                        }else{
                            if(ChatChannelsManager.i.inChannel(chatChannel.getName()).length == chatChannel.getMaxPlayers()){
                                this.getSuperUtils().sendMessage(player, L.CHAT_CHANNELS_FULL.toString());
                            }else{
                                this.getSuperUtils().sendMessage(player, L.CHAT_CHANNELS_JOINED.options().placeholder("{ChannelName}", chatChannel.getName()).get());
                                ChatChannelsManager.i.joinChannel(player, chatChannel.getName());
                            }
                        }
                    }
                }
            }else if(args[0].equalsIgnoreCase("list")){
                if(!player.hasPermission("chatchannels.list")){
                    return CommandResult.NO_PERMISSION;
                }else{
                    StringBuilder msg = new StringBuilder();
                    msg.append("&a").append(L.CHANNELS.toString()).append(":\n");
                    for (ChatChannel cc : ChatChannelsStorage.i.all()) {
                        msg.append(L.CHAT_CHANNELS_LIST_ITEM.options().placeholder("{ChannelName}", cc.getName()).get()).append("\n");
                    }
                    this.getSuperUtils().sendMessage(player, msg.toString());
                }
            }else if(args[0].equalsIgnoreCase("permissions")){
                if(!player.hasPermission("chatchannels.permissions")){
                    return CommandResult.NO_PERMISSION;
                }else{
                    String msg = "&a" + L.PERMISSIONS.toString() + ":\n" +
                            "&7- &9" + L.JOIN + ": &e" + "chatchannels.<Channel Name>.join\n" +
                            "&7- &9" + L.WRITE + ": &e" + "chatchannels.<Channel Name>.write\n";
                    this.getSuperUtils().sendMessage(player, msg);
                }
            }else if(args[0].equalsIgnoreCase("help")){
                this.getSuperUtils().sendMessage(player,
                        L.CHAT_CHANNELS_HELP_JOIN.options().placeholder("{Command}", this.getCommand()) + "\n" +
                                L.CHAT_CHANNELS_HELP_LIST.options().placeholder("{Command}", this.getCommand()) + "\n" +
                                L.CHAT_CHANNELS_HELP_PERMISSIONS.options().placeholder("{Command}", this.getCommand())
                );
            }else if(args[0].equalsIgnoreCase("online")){
                if(!player.hasPermission("chatchannels.online")){
                    return CommandResult.NO_PERMISSION;
                }else{
                    this.getSuperUtils().sendMessage(player, L.CHAT_CHANNELS_ONLINE.options().placeholder("{Online}", ChatChannelsManager.i.onlineWith(player)+"").placeholder("{Max}", ChatChannelsStorage.i.get(ChatChannelsManager.i.getChannel(player)).getMaxPlayers()+"").get());
                }
            }else{
                return CommandResult.INVALID_ARGS;
            }
        }
        return CommandResult.COMPLETED;
    }

    @Override
    public CommandResult onConsoleExecute(SpigotConsole spigotConsole, String[] strings) {
        return CommandResult.NOT_SUPPORTED;
    }

    @Override
    public List<String> getCommandComplete(Player player, String alias, String[] args) {
        if(!ChatChannelsModule.i.isEnabled() && !ChatChannelsModule.i.isRunning()) return super.getCommandComplete(player, alias, args);
        if(args.length == 0){
            return Utils.toList("join", "list", "permissions", "help", "online");
        }else if(args.length == 1){
            return Utils.toList("join", "list", "permissions", "help", "online").stream().filter(s-> s.toLowerCase().contains(args[0].toLowerCase())).collect(Collectors.toList());
        }else if(args.length == 2){
            return Arrays.stream(ChatChannelsStorage.i.all()).filter(c-> player.hasPermission(c.getJoinPermission())).map(ChatChannel::getName).filter(name -> name.toLowerCase().contains(args[1].toLowerCase())).collect(Collectors.toList());
        }else{
            return super.getCommandComplete(player, alias, args);
        }
    }
}

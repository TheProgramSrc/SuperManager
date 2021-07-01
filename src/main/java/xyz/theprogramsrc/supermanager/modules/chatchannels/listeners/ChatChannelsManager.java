package xyz.theprogramsrc.supermanager.modules.chatchannels.listeners;

import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.player.AsyncPlayerChatEvent;
import org.bukkit.event.player.PlayerJoinEvent;

import me.clip.placeholderapi.PlaceholderAPI;
import xyz.theprogramsrc.supercoreapi.global.translations.Base;
import xyz.theprogramsrc.supercoreapi.global.utils.StringUtils;
import xyz.theprogramsrc.supercoreapi.global.utils.Utils;
import xyz.theprogramsrc.supercoreapi.spigot.SpigotModule;
import xyz.theprogramsrc.supermanager.modules.chatchannels.objects.ChatChannel;
import xyz.theprogramsrc.supermanager.modules.chatchannels.storage.ChatChannelsDataManager;

import java.util.Arrays;
import java.util.LinkedHashMap;
import java.util.Optional;
import java.util.UUID;

public class ChatChannelsManager extends SpigotModule {

    public static ChatChannelsManager i;
    private ChatChannelsDataManager chatChannelsDataManager;
    private boolean papi;

    public ChatChannelsManager(ChatChannelsDataManager chatChannelsDataManager){
        i = this;
        this.chatChannelsDataManager = chatChannelsDataManager;
        this.papi = this.getSuperUtils().isPlugin("PlaceholderAPI");
    }

    @EventHandler
    public void onJoin(PlayerJoinEvent e){
        Optional<ChatChannel> optional = this.chatChannelsDataManager.getChannel(this.chatChannelsDataManager.globalChannel());
        if(optional.isPresent()){
            this.chatChannelsDataManager.joinChannel(e.getPlayer(), optional.get());
        }
    }

    @EventHandler(ignoreCancelled = true, priority = EventPriority.HIGHEST)
    public void onChat(AsyncPlayerChatEvent e){
        if(e.getMessage().startsWith("/")) return;
        e.setCancelled(true);
        Player player = e.getPlayer();
        String originalMessage = e.getMessage();
        this.getSpigotTasks().runTask(() -> {
            ChatChannel channel = this.chatChannelsDataManager.currentChannel(player);
            String format = this.chatChannelsDataManager.format();
            if(player.hasPermission(channel.getWritePermission())){
                boolean color = e.getPlayer().hasPermission(channel.getWriteWithColorPermission());
                String msg = this.getSuperUtils().color(new StringUtils(format).placeholder("{Player}", player.getName()).placeholder("{Channel}", channel.getName()).placeholder("{Message}", color ? originalMessage : this.getSuperUtils().removeColor(originalMessage)).get());
                if(this.papi){
                    msg = PlaceholderAPI.setPlaceholders(player, msg);
                }

                final String toSend = msg;
                Bukkit.getConsoleSender().sendMessage(toSend);
                Arrays.stream(this.chatChannelsDataManager.getPlayersInChannel(channel)).filter(OfflinePlayer::isOnline).map(OfflinePlayer::getPlayer).forEach(p -> {
                    p.sendMessage(toSend);
                });
            }else{
                this.getSuperUtils().sendMessage(player, Base.NO_PERMISSION.toString());
            }
        });
    }
}

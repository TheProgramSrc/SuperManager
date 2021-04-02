package xyz.theprogramsrc.supermanager.modules.chatchannels.listeners;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.player.AsyncPlayerChatEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import xyz.theprogramsrc.supercoreapi.global.translations.Base;
import xyz.theprogramsrc.supercoreapi.spigot.SpigotModule;
import xyz.theprogramsrc.supermanager.modules.chatchannels.ChatChannelsStorage;

import java.util.LinkedHashMap;
import java.util.UUID;

public class ChatChannelsManager extends SpigotModule {

    public static ChatChannelsManager i;
    private ChatChannelsStorage storage;
    private LinkedHashMap<UUID, String> channels;

    @Override
    public void onLoad() {
        i = this;
        this.storage = ChatChannelsStorage.i;
        this.channels = new LinkedHashMap<>();
    }

    @EventHandler
    public void onJoin(PlayerJoinEvent e){
        this.channels.put(e.getPlayer().getUniqueId(), this.storage.defaultChannel());
    }

    @EventHandler(ignoreCancelled = true, priority = EventPriority.HIGHEST)
    public void onChat(AsyncPlayerChatEvent e){
        if(e.getMessage().startsWith("/")) return;
        e.setCancelled(true);
        this.getSpigotTasks().runTask(() -> {
            String channel = this.getChannel(e.getPlayer());
            if(channel == null) {
                channel = this.storage.defaultChannel();
                this.joinChannel(e.getPlayer(), this.storage.defaultChannel());
            }
            String format = this.storage.format();
            if(e.getPlayer().hasPermission("chatchannels." + channel + ".write")){
                boolean color = e.getPlayer().hasPermission("chatchannels.write.color");
                String baseMSG = this.getSuperUtils().color(format.replace("{Player}", e.getPlayer().getName()).replace("{Channel}", channel)).replace("{Message}", e.getMessage());
                String msg = color ? this.getSuperUtils().color(baseMSG) : baseMSG;
                Bukkit.getConsoleSender().sendMessage(msg);
                for (Player player : this.inChannel(channel)) {
                    player.sendMessage(msg);
                }
            }else{
                this.getSuperUtils().sendMessage(e.getPlayer(), Base.NO_PERMISSION.toString());
            }
        });
    }

    public String getChannel(Player player){
        return this.channels.get(player.getUniqueId());
    }

    public void joinChannel(Player player, String channel){
        this.channels.put(player.getUniqueId(), channel);
    }

    public Player[] inChannel(String name){
        return this.channels.entrySet().stream().filter(e-> e.getValue().equals(name)).map(e-> Bukkit.getPlayer(e.getKey())).toArray(Player[]::new);
    }

    public int onlineWith(Player player){
        return this.inChannel(this.getChannel(player)).length;
    }
}

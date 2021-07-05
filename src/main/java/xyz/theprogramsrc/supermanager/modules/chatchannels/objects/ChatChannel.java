package xyz.theprogramsrc.supermanager.modules.chatchannels.objects;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.UUID;

import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;

import xyz.theprogramsrc.supermanager.modules.chatchannels.storage.ChatChannelsDataManager;

public class ChatChannel {

    private final ChatChannelsDataManager dataManager;
    private final UUID uuid;
    private final String name, createdAt;
    private final int maxPlayers;

    public ChatChannel(String name, int maxPlayers){
        this(UUID.randomUUID(), name, maxPlayers);
    }

    public ChatChannel(UUID uuid, String name, int maxPlayers){
        this(uuid, name, maxPlayers, LocalDateTime.now().toString());
    }

    public ChatChannel(UUID uuid, String name, int maxPlayers, String createdAt){
        this.dataManager = ChatChannelsDataManager.i;
        this.uuid = uuid;
        this.name = name;
        this.maxPlayers = maxPlayers;
        this.createdAt = createdAt;
    }

    public boolean isGlobal(){
        boolean global = this.dataManager.globalChannel() == this.getUuid().toString();
        System.out.println("§6IS GLOBAL? " + (global ? 'y' : 'n') + "; " + this.dataManager.globalChannel() + " == " + this.getUuid().toString());
        return global;
    }

    public LocalDateTime getInstantCreated(){
        return LocalDateTime.parse(this.getCreatedAt());
    }

    public String getCreatedAt() {
      return createdAt;
    }

    public String getName() {
        return name;
    }

    public UUID getUuid() {
      return uuid;
    }

    public int getMaxPlayers() {
        return maxPlayers;
    }

    public String getJoinPermission(){
        return "chatchannels." + this.name + ".join";
    }

    public String getWritePermission(){
        return "chatchannels." + this.name + ".write";
    }

    public String getWriteWithColorPermission(){
        return "chatchannels." + this.name + ".write.color";
    }

    public int countOnline() {
        return this.dataManager.getOnlineInChannel(this);
    }

    public Player[] onlinePlayers(){
        return Arrays.stream(this.dataManager.getPlayersInChannel(this)).filter(OfflinePlayer::isOnline).map(OfflinePlayer::getPlayer).toArray(Player[]::new);
    }

    @Override
    public String toString() {
        return String.format("[ChatChannel:{id:%s,name:%s,maxPlayers:%s,createdAt:%s}]", this.uuid, this.name, this.maxPlayers, this.createdAt);
    }

}

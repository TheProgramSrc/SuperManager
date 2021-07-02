package xyz.theprogramsrc.supermanager.modules.chatchannels.objects;

import java.time.Instant;
import java.util.Arrays;
import java.util.UUID;

import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;

import xyz.theprogramsrc.supermanager.modules.chatchannels.storage.ChatChannelsDataManager;

public class ChatChannel {

    private final ChatChannelsDataManager dataManager;
    private final UUID uuid;
    private final String name;
    private final int maxPlayers;
    private final long createdAt;

    public ChatChannel(String name, int maxPlayers){
        this(UUID.randomUUID(), name, maxPlayers);
    }

    public ChatChannel(UUID uuid, String name, int maxPlayers){
        this(uuid, name, maxPlayers, Instant.now().toEpochMilli());
    }

    public ChatChannel(UUID uuid, String name, int maxPlayers, long createdAt){
        this.dataManager = ChatChannelsDataManager.i;
        this.uuid = uuid;
        this.name = name;
        this.maxPlayers = maxPlayers;
        this.createdAt = createdAt;
    }

    public boolean isGlobal(){
        return this.dataManager.globalChannel() == this.uuid + "";
    }

    public Instant getInstantCreated(){
        return Instant.ofEpochMilli(this.createdAt);
    }

    public long getCreatedAt() {
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

}

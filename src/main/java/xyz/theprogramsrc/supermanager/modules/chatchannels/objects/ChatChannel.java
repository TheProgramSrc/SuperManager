package xyz.theprogramsrc.supermanager.modules.chatchannels.objects;

import java.util.UUID;

public class ChatChannel {

    private final UUID uuid;
    private final String name;
    private final int maxPlayers;

    public ChatChannel(String name, int maxPlayers){
        this(UUID.randomUUID(), name, maxPlayers);
    }

    public ChatChannel(UUID uuid, String name, int maxPlayers){
        this.uuid = uuid;
        this.name = name;
        this.maxPlayers = maxPlayers;
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

}

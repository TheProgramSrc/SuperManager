package xyz.theprogramsrc.supermanager.modules.chatchannels.objects;

public class ChatChannel {

    private final String name;
    private final int maxPlayers;

    public ChatChannel(String name, int maxPlayers){
        this.name = name;
        this.maxPlayers = maxPlayers;
    }

    public String getName() {
        return name;
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

}

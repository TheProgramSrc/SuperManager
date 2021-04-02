package xyz.theprogramsrc.supermanager.modules.chatchannels.objects;

import xyz.theprogramsrc.supercoreapi.google.gson.JsonObject;
import xyz.theprogramsrc.supercoreapi.google.gson.JsonParser;

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

    public JsonObject toJSON() {
        JsonObject json = new JsonObject();
        json.addProperty("name", this.name);
        json.addProperty("max", this.maxPlayers);
        return json;
    }

    public static ChatChannel fromJSON(JsonObject json){
        return new ChatChannel(json.get("name").getAsString(), json.get("max").getAsInt());
    }
}

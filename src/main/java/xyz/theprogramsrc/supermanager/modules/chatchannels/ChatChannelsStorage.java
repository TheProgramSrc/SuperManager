package xyz.theprogramsrc.supermanager.modules.chatchannels;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import xyz.theprogramsrc.supercoreapi.global.files.JsonConfig;
import xyz.theprogramsrc.supercoreapi.global.utils.Utils;
import xyz.theprogramsrc.supercoreapi.google.gson.JsonArray;
import xyz.theprogramsrc.supercoreapi.google.gson.JsonElement;
import xyz.theprogramsrc.supercoreapi.google.gson.JsonObject;
import xyz.theprogramsrc.supermanager.SuperManager;
import xyz.theprogramsrc.supermanager.modules.chatchannels.listeners.ChatChannelsManager;
import xyz.theprogramsrc.supermanager.modules.chatchannels.objects.ChatChannel;

import java.io.File;
import java.util.LinkedHashMap;
import java.util.LinkedList;

public class ChatChannelsStorage {

    public static ChatChannelsStorage i;
    private final LinkedHashMap<String, ChatChannel> cache;
    private final JsonConfig jsonConfig;

    public ChatChannelsStorage(ChatChannelsModule chatChannelsModule){
        i = this;
        cache = new LinkedHashMap<>();
        this.jsonConfig = new JsonConfig(new File(chatChannelsModule.getModuleFolder(), "ChatChannels.json"));
        this.jsonConfig.add("format", "&7#{Channel} - {Player}&b»&f {Message}");
        this.jsonConfig.add("defaultMax", Bukkit.getServer().getMaxPlayers());
        this.jsonConfig.add("command", "cc");
        this.jsonConfig.add("globalChannel", "global");
        if(this.all().length == 0){
            this.save(new ChatChannel("global", this.getDefaultMax()));
        }
    }

    public String format(){
        return this.jsonConfig.getString("format");
    }

    public String defaultChannel(){
        return this.jsonConfig.getString("globalChannel");
    }

    public void setGlobalChannel(String channel){
        this.jsonConfig.set("globalChannel", channel);
    }

    public String command(){
        return this.jsonConfig.getString("command");
    }

    public int getDefaultMax(){
        return this.jsonConfig.getInt("defaultMax");
    }

    public void remove(String name){
        cache.remove(name);
        for (Player player : ChatChannelsManager.i.inChannel(name)) {
            ChatChannelsManager.i.joinChannel(player, this.defaultChannel());
        }
        JsonArray channels = this.jsonConfig.getOrCreateArray("channels");
        int i = 0;
        for (JsonElement e : channels) {
            if(e.getAsJsonObject().get("name").getAsString().equals(name)){
                channels.remove(i);
                break;
            }
            ++i;
        }
        this.jsonConfig.set("channels", channels);
    }

    public void save(ChatChannel chatChannel){
        SuperManager.i.debug("Saving channel " + chatChannel.getName());
        this.cache.remove(chatChannel.getName());
        JsonArray channels = this.jsonConfig.getOrCreateArray("channels");
        if(this.exists(chatChannel.getName())){
            int i = 0;
            for (JsonElement e : channels) {
                JsonObject json = e.getAsJsonObject();
                if(json.get("name").getAsString().equals(chatChannel.getName())){
                    channels.set(i, chatChannel.toJSON());
                }
                ++i;
            }
        }else{
            channels.add(chatChannel.toJSON());
        }
        this.jsonConfig.set("channels", channels);
    }

    public boolean exists(String name){
        JsonArray channels = this.jsonConfig.getOrCreateArray("channels");
        for (JsonElement e : channels) {
            JsonObject json = e.getAsJsonObject();
            if(json.get("name").getAsString().equals(name)){
                return true;
            }
        }

        return false;
    }

    public ChatChannel get(String name){
        if(cache.containsKey(name)) return cache.get(name);
        JsonArray channels = this.jsonConfig.getOrCreateArray("channels");
        for (JsonElement e : channels) {
            JsonObject json = e.getAsJsonObject();
            if(json.get("name").getAsString().equals(name)){
                ChatChannel chatChannel = ChatChannel.fromJSON(json);
                cache.put(chatChannel.getName(), chatChannel);
            }
        }
        return cache.get(name);
    }

    public ChatChannel[] all(){
        LinkedList<ChatChannel> list = new LinkedList<>();
        JsonArray channels = this.jsonConfig.getOrCreateArray("channels");
        for (JsonElement e : channels) {
            JsonObject json = e.getAsJsonObject();
            list.add(this.get(json.get("name").getAsString()));
        }

        return list.toArray(new ChatChannel[0]);
    }
}

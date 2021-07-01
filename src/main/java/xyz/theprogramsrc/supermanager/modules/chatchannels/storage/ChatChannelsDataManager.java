package xyz.theprogramsrc.supermanager.modules.chatchannels.storage;

import java.io.File;
import java.util.*;

import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;

import xyz.theprogramsrc.supercoreapi.global.files.yml.YMLConfig;
import xyz.theprogramsrc.supermanager.modules.chatchannels.objects.*;

public class ChatChannelsDataManager extends YMLConfig {

    public static ChatChannelsDataManager i;
    private final LinkedHashMap<UUID, ChatChannel> CHANNELS_CACHE;

    public ChatChannelsDataManager(File file) {
        super(file);
        i = this;
        this.CHANNELS_CACHE = new LinkedHashMap<>();
        this.addSetting(
            new ChatChannelsSetting("format", "&7#{Channel} - {Player}&b»&f {Message}"),
            new ChatChannelsSetting("command", "cc"), 
            new ChatChannelsSetting("globalChannel", "global")
        );
    }

    public void addSetting(ChatChannelsSetting... settings) {
        for (ChatChannelsSetting setting : settings) {
            this.add("Settings." + setting.getKey(), setting.getValue());
        }
    }

    public void saveSetting(ChatChannelsSetting... settings){
        for (ChatChannelsSetting setting : settings) {
            this.set("Settings." + setting.getKey(), setting.getValue());
        }
    }

    public ChatChannelsSetting getSetting(String key) {
        return new ChatChannelsSetting(key, this.getString("Settings." + key));
    }

    public ChatChannelsSetting[] getSettings() {
        LinkedList<ChatChannelsSetting> list = new LinkedList<>();
        for (String key : this.getSection("Settings").getKeys(false)) {
            list.add(this.getSetting(key));
        }

        return list.toArray(new ChatChannelsSetting[0]);
    }

    public boolean hasSetting(String key){
        return this.contains("Settings." + key);
    }

    public void addChannel(ChatChannel... channels) {
        for(ChatChannel channel : channels){
            if(!this.hasChannel(channel.getName())){
                this.saveChannel(channel);
            }
            this.CHANNELS_CACHE.remove(channel.getUuid());
        }
    }

    public void saveChannel(ChatChannel... channels){
        for(ChatChannel channel : channels){
            String path = "Channels." + channel.getUuid();
            this.set(path + ".Name", channel.getName());
            this.set(path + ".MaxPlayers", channel.getMaxPlayers());
            this.CHANNELS_CACHE.remove(channel.getUuid());
        }
    }

    public void removeChannel(UUID uuid){
        this.set("Channels." + uuid, null);
        this.CHANNELS_CACHE.remove(uuid);
    }

    public void removeChannel(String name) {
        Optional<ChatChannel> chatChannelOptional = this.getChannel(name);
        if(chatChannelOptional.isPresent()){
            this.removeChannel(chatChannelOptional.get().getUuid());
        }
    }

    public ChatChannel getChannel(UUID uuid){
        if(!this.CHANNELS_CACHE.containsKey(uuid)){
            String path = "Channels." + uuid;
            String name = this.getString(path + ".Name");
            int maxPlayers = this.getInt(path + ".MaxPlayers");
            this.CHANNELS_CACHE.put(uuid, new ChatChannel(uuid, name, maxPlayers));
        }
        return this.CHANNELS_CACHE.containsKey(uuid) ? this.CHANNELS_CACHE.get(uuid) : this.getChannel(uuid);
    }

    public Optional<ChatChannel> getChannel(String name){
        return Arrays.stream(this.getChannels()).filter(channel -> channel.getName() == name).findFirst();
    }

    public ChatChannel[] getChannels(){
        LinkedList<ChatChannel> list = new LinkedList<>();
        for(String key : this.getSection("Channels").getKeys(false)){
            UUID uuid = UUID.fromString(key);
            list.add(this.getChannel(uuid));
        }

        return list.toArray(new ChatChannel[0]);
    }

    public boolean hasChannel(UUID uuid){
        return this.contains("Channels." + uuid);
    }

    public boolean hasChannel(String name){
        return Arrays.stream(this.getChannels()).anyMatch(channel -> channel.getName() == name);
    }

    public String format(){
        return this.getSetting("format").getValue();
    }

    public void setFormat(String format){
        this.saveSetting(new ChatChannelsSetting("format", format));
    }

    public String globalChannel() {
        return this.getSetting("globalChannel").getValue();
    }

    public void setGlobalChannel(String channel){
        this.saveSetting(new ChatChannelsSetting("globalChannel", channel));
    }

    public String command(){
        return this.getSetting("command").getValue();
    }

    public void setCommand(String command){
        this.saveSetting(new ChatChannelsSetting("command", command.toLowerCase()));
    }

    public void joinChannel(OfflinePlayer player, ChatChannel chatChannel){
        String path = "Players." + player.getUniqueId();
        this.set(path + ".CurrentChannel", chatChannel.getUuid().toString());
    }

    public ChatChannel currentChannel(OfflinePlayer player){
        String path = "Players." + player.getUniqueId();
        if(!this.contains(path)){
            Optional<ChatChannel> optional = this.getChannel(this.globalChannel());
            if(optional.isPresent()){
                this.joinChannel(player, optional.get());
            }else{
                throw new RuntimeException("Couldn't find the Global Channel, make sure it exists!");
            }
        }

        return this.getChannel(UUID.fromString(this.getString(path + ".CurrentChannel")));
    }

    public OfflinePlayer[] getPlayersInChannel(ChatChannel channel){
        LinkedList<OfflinePlayer> players = new LinkedList<>();
        this.getSection("Players").getKeys(false).forEach(key -> {
            UUID uuid = UUID.fromString(key);
            if(channel.getUuid().toString() == this.getString("Players." + key + ".CurrentChannel")){
                players.add(Bukkit.getOfflinePlayer(uuid));
            }
        });

        return players.toArray(new OfflinePlayer[0]);
    }

    public OfflinePlayer[] getOnlineWithPlayer(OfflinePlayer player){
        return this.getPlayersInChannel(this.currentChannel(player));
    }
}

package xyz.theprogramsrc.supermanager.modules.chatchannels.storage;

import java.io.File;
import java.util.Arrays;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.Optional;
import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;

import xyz.theprogramsrc.supercoreapi.global.files.yml.YMLConfig;
import xyz.theprogramsrc.supermanager.modules.chatchannels.objects.ChatChannel;
import xyz.theprogramsrc.supermanager.modules.chatchannels.objects.ChatChannelsSetting;

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
            new ChatChannelsSetting("globalChannel", UUID.randomUUID().toString()),
            new ChatChannelsSetting("globalDateFormat", "dd/MM/yyyy HH:mm:ss")
        );

        if(!this.hasChannel(UUID.fromString(this.globalChannel()))){
            this.saveChannel(new ChatChannel(UUID.fromString(this.globalChannel()), "Global", Bukkit.getMaxPlayers()));
        }
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
            this.CHANNELS_CACHE.put(channel.getUuid(), channel);
        }
    }

    public void saveChannel(ChatChannel... channels){
        for(ChatChannel channel : channels){
            String path = "Channels." + channel.getUuid();
            this.set(path + ".Name", channel.getName());
            this.set(path + ".MaxPlayers", channel.getMaxPlayers());
            this.set(path + ".CreatedAt", channel.getCreatedAt());
            this.CHANNELS_CACHE.put(channel.getUuid(), channel);
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
            String createdAt = this.getString(path + ".CreatedAt");
            this.CHANNELS_CACHE.put(uuid, new ChatChannel(uuid, name, maxPlayers, createdAt));
        }
        return this.CHANNELS_CACHE.containsKey(uuid) ? this.CHANNELS_CACHE.get(uuid) : this.getChannel(uuid);
    }

    public Optional<ChatChannel> getChannel(String name){
        return Arrays.stream(this.getChannels()).filter(channel -> channel.getName().equalsIgnoreCase(name)).findFirst();
    }

    public ChatChannel[] getChannels(){
        LinkedList<ChatChannel> list = new LinkedList<>();
        if(this.getSection("Channels") != null){
            for(String key : this.getSection("Channels").getKeys(false)){
                list.add(this.getChannel(UUID.fromString(key)));
            }
        }
        return list.toArray(new ChatChannel[0]);
    }

    public boolean hasChannel(UUID uuid){
        return this.contains("Channels." + uuid);
    }

    public boolean hasChannel(String name){
        return Arrays.stream(this.getChannels()).anyMatch(channel -> channel.getName() == name);
    }

    public void joinChannel(OfflinePlayer player, ChatChannel chatChannel){
        String path = "Players." + player.getUniqueId();
        this.set(path + ".CurrentChannel", chatChannel.getUuid().toString());
    }

    public ChatChannel currentChannel(OfflinePlayer player){
        String path = "Players." + player.getUniqueId();
        if(!this.contains(path)){
            ChatChannel channel = this.getChannel(UUID.fromString(this.globalChannel()));
            this.joinChannel(player, channel);
        }

        return this.getChannel(UUID.fromString(this.getString(path + ".CurrentChannel")));
    }

    public OfflinePlayer[] getPlayersInChannel(ChatChannel channel){
        LinkedList<OfflinePlayer> players = new LinkedList<>();
        if(this.getSection("Players") != null){
            this.getSection("Players").getKeys(false).forEach(key -> {
                if(channel.getUuid().equals(UUID.fromString(this.getString("Players." + key + ".CurrentChannel")))){
                    players.add(Bukkit.getOfflinePlayer(UUID.fromString(key)));
                }
            });
        }

        return players.toArray(new OfflinePlayer[0]);
    }

    public OfflinePlayer[] getOnlineWithPlayer(OfflinePlayer player){
        return this.getPlayersInChannel(this.currentChannel(player));
    }

    public int getOnlineInChannel(ChatChannel channel){
        if(this.getSection("Players") == null) return 0;
        return ((int) this.getSection("Players").getKeys(false).stream().filter(key -> UUID.fromString(this.getString("Players." + key + ".CurrentChannel")).equals(channel.getUuid())).count());
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

    public String globalDateFormat(){
        return this.getSetting("globalDateFormat").getValue();
    }

    public void setGlobalDateFormat(String format){
        this.saveSetting(new ChatChannelsSetting("globalDateFormat", format));
    }
}

package xyz.theprogramsrc.supermanager.modules.chatchannels;

import org.bukkit.Bukkit;
import xyz.theprogramsrc.supercoreapi.global.storage.DataBaseStorage;
import xyz.theprogramsrc.supercoreapi.global.storage.universal.UniversalStorage;
import xyz.theprogramsrc.supermanager.SuperManager;
import xyz.theprogramsrc.supermanager.modules.chatchannels.objects.ChatChannel;
import xyz.theprogramsrc.supermanager.modules.chatchannels.objects.ChatChannelsSetting;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicReference;

public class ChatChannelsStorage extends DataBaseStorage {

    public static ChatChannelsStorage i;
    private final LinkedHashMap<String, ChatChannel> cache;
    private final LinkedHashMap<String, ChatChannelsSetting> settings_cache;
    private final String chatchannels_table, settings_table;

    public ChatChannelsStorage(ChatChannelsModule chatChannelsModule){
        super(chatChannelsModule.getPlugin(), UniversalStorage.database());
        i = this;
        cache = new LinkedHashMap<>();
        settings_cache = new LinkedHashMap<>();
        this.chatchannels_table = this.getTablePrefix() + "chatchannels_module_data";
        this.settings_table = this.getTablePrefix() + "chatchannels_module_settings";

        this.initTables();
        this.initSettings();

        SuperManager.i.getSpigotTasks().runTaskLater(20 * 5L, () -> {
            if(this.all().length == 0){
                this.addChannel(new ChatChannel(this.getGlobalChannel(), Bukkit.getServer().getMaxPlayers()));
            }
        });
    }

    private void initTables(){
        new Thread(() -> this.dataBase.connect(c-> {
            try{
                Statement statement = c.createStatement();
                statement.executeUpdate("CREATE TABLE IF NOT EXISTS `" + this.chatchannels_table + "` (channel_name VARCHAR(500), max_players INT)");
                statement.executeUpdate("CREATE TABLE IF NOT EXISTS `" + this.settings_table + "` (settings_id VARCHAR(500), settings_value VARCHAR(5000))");
            }catch (SQLException e){
                this.plugin.addError(e);
                this.plugin.log("&cFailed to initialize tables:");
                e.printStackTrace();
            }
        })).start();
    }

    private void initSettings(){
        this.addSetting(new ChatChannelsSetting("format", "&7#{Channel} - {Player}&b»&f {Message}"));
        this.addSetting(new ChatChannelsSetting("command", "cc"));
        this.addSetting(new ChatChannelsSetting("globalChannel", "global"));
    }

    public ChatChannelsSetting getSetting(String key){
        if(this.settings_cache.containsKey(key)) return this.settings_cache.get(key);
        final AtomicReference<ChatChannelsSetting> reference = new AtomicReference<>(null);
        this.dataBase.connect(c-> {
            try{
                Statement statement = c.createStatement();
                ResultSet rs = statement.executeQuery("SELECT * FROM `" + this.settings_table + "` WHERE `settings_id` = '" + key + "';");
                if(rs.next()){
                    reference.set(new ChatChannelsSetting(rs.getString("settings_id"), rs.getString("settings_value")));
                    this.settings_cache.put(rs.getString("settings_id"), reference.get());
                }
            }catch (SQLException e){
                this.plugin.addError(e);
                this.plugin.log("&cFailed to save ChatChannels Setting:");
                e.printStackTrace();
            }
        });
        return reference.get();
    }

    public void addSetting(ChatChannelsSetting setting){
        if(!this.existsSetting(setting.getKey())){
            this.saveSetting(setting);
        }
    }

    public void saveSetting(ChatChannelsSetting setting){
        this.saveSetting(setting, true);
    }

    public void saveSetting(ChatChannelsSetting setting, boolean in_thread){
        Runnable run = () -> this.dataBase.connect(c-> {
            try{
                Statement statement = c.createStatement();
                String query;
                if(this.existsSetting(setting.getKey())){
                    query = "UPDATE `" + this.settings_table + "` SET `settings_value` = '" + setting.getValue() + "' WHERE `settings_id` = '" + setting.getKey() +"';";
                }else{
                    query = "INSERT INTO `" + this.settings_table + "` (settings_id, settings_value) VALUES ('" + setting.getKey() + "', '" + setting.getValue() + "');";
                }

                statement.executeUpdate(query);
                this.settings_cache.remove(setting.getKey());
            }catch (SQLException e){
                this.plugin.addError(e);
                this.plugin.log("&cFailed to save ChatChannels Setting:");
                e.printStackTrace();
            }
        });
        if(in_thread){
            new Thread(run).start();
        }else{
            run.run();
        }
    }

    public boolean existsSetting(String key){
        final AtomicBoolean atomicBoolean = new AtomicBoolean(false);
        this.dataBase.connect(c-> {
            try{
                Statement statement = c.createStatement();
                ResultSet rs = statement.executeQuery("SELECT EXISTS(SELECT settings_id FROM `" + this.settings_table + "` WHERE `settings_id` = '" + key + "') AS `exists`;");
                atomicBoolean.set(rs.next() && rs.getBoolean("exists"));
            }catch (SQLException e){
                this.plugin.addError(e);
                this.plugin.log("&cFailed to save ChatChannels Setting:");
                e.printStackTrace();
            }
        });
        return atomicBoolean.get();
    }

    public String format(){
        return this.getSetting("format").getValue();
    }

    public String getGlobalChannel(){
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

    public void addChannel(ChatChannel chatChannel){
        if(!this.existsChannel(chatChannel.getName())){
            this.saveChannel(chatChannel);
        }
    }

    public void saveChannel(ChatChannel chatChannel){
        new Thread(()-> this.dataBase.connect(c-> {
            try{
                Statement statement = c.createStatement();
                String query;
                if(this.existsChannel(chatChannel.getName())){
                    query = String.format("UPDATE `%s` SET `max_players` = %s WHERE `channel_name` = '%s';", this.chatchannels_table, chatChannel.getMaxPlayers(), chatChannel.getName());
                }else{
                    query = String.format("INSERT INTO `%s` (channel_name, max_players) VALUES ('%s', %s);", this.chatchannels_table, chatChannel.getName(), chatChannel.getMaxPlayers());
                }

                statement.executeUpdate(query);
                cache.remove(chatChannel.getName());
            }catch (SQLException e){
                this.plugin.addError(e);
                this.plugin.log("&cFailed to save ChatChannel:");
                e.printStackTrace();
            }
        })).start();
    }

    public ChatChannel getChannel(String name){
        if(cache.containsKey(name)) cache.get(name);
        final AtomicReference<ChatChannel> reference = new AtomicReference<>(null);
        this.dataBase.connect(c-> {
            try {
                Statement statement = c.createStatement();
                ResultSet rs = statement.executeQuery("SELECT * FROM `" + this.chatchannels_table + "` WHERE `channel_name` = '" + name + "';");
                ChatChannel chatChannel = new ChatChannel(rs.getString("channel_name"), rs.getInt("max_players"));
                reference.set(chatChannel);
                cache.put(chatChannel.getName(), chatChannel);
            }catch (SQLException e){
                this.plugin.addError(e);
                this.plugin.log("&cFailed to retrieve channel:");
                e.printStackTrace();
            }
        });
        return reference.get();
    }

    public boolean existsChannel(String name){
        final AtomicBoolean atomicBoolean = new AtomicBoolean(false);
        this.dataBase.connect(c-> {
            try {
                Statement statement = c.createStatement();
                ResultSet rs = statement.executeQuery("SELECT EXISTS(SELECT `channel_name` FROM `" + this.chatchannels_table + "` WHERE `channel_name` = '" + name + "') AS `exists`;");
                atomicBoolean.set(rs.next() && rs.getBoolean("exists"));
            }catch (SQLException e){
                this.plugin.addError(e);
                this.plugin.log("&cFailed to retrieve channel:");
                e.printStackTrace();
            }
        });
        return atomicBoolean.get();
    }

    public void removeChannel(String name){
        new Thread(() -> this.dataBase.connect(c-> {
            try {
                Statement statement = c.createStatement();
                statement.executeUpdate("DELETE * FROM `" + this.chatchannels_table + "` WHERE `channel_name` = '" + name + "';");
            }catch (SQLException e){
                this.plugin.addError(e);
                this.plugin.log("&cFailed to retrieve channel:");
                e.printStackTrace();
            }
        })).start();
    }

    public ChatChannel[] all(){
        LinkedList<ChatChannel> list = new LinkedList<>();
        this.dataBase.connect(c-> {
            try{
                Statement statement = c.createStatement();
                ResultSet rs = statement.executeQuery("SELECT * FROM `" + this.chatchannels_table + "`;");
                while(rs.next()){
                    ChatChannel chatChannel = new ChatChannel(rs.getString("channel_name"), rs.getInt("max_players"));
                    list.add(chatChannel);
                    cache.put(chatChannel.getName(), chatChannel);
                }
            }catch (SQLException e){
                this.plugin.addError(e);
                this.plugin.log("&cFailed to retrieve ChatChannels:");
                e.printStackTrace();
            }
        });

        return list.toArray(new ChatChannel[0]);
    }
}

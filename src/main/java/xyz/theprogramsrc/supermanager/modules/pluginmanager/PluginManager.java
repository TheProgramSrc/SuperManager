package xyz.theprogramsrc.supermanager.modules.pluginmanager;

import java.io.File;
import java.io.IOException;
import java.nio.charset.Charset;
import java.util.LinkedHashMap;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.server.ServerLoadEvent;
import org.bukkit.plugin.Plugin;

import xyz.theprogramsrc.supercoreapi.global.utils.Utils;
import xyz.theprogramsrc.supercoreapi.global.utils.files.FileUtils;
import xyz.theprogramsrc.supercoreapi.libs.google.gson.JsonObject;
import xyz.theprogramsrc.supercoreapi.libs.google.gson.JsonParser;
import xyz.theprogramsrc.supercoreapi.libs.xseries.XMaterial;
import xyz.theprogramsrc.supercoreapi.spigot.guis.action.ClickAction;
import xyz.theprogramsrc.supercoreapi.spigot.items.SimpleItem;
import xyz.theprogramsrc.supermanager.L;
import xyz.theprogramsrc.supermanager.SuperManager;
import xyz.theprogramsrc.supermanager.guis.MainGUI;
import xyz.theprogramsrc.supermanager.modules.pluginmanager.guis.PluginBrowser;
import xyz.theprogramsrc.supermanager.modules.pluginmanager.objects.SPlugin;
import xyz.theprogramsrc.supermanager.objects.Module;

public class PluginManager extends Module {

    public static LinkedHashMap<String, SPlugin> plugins = new LinkedHashMap<>();
    public static String token = SuperManager.i.getSettingsStorage().getConfig().getString("songoda-token", "");

    @EventHandler
    public void onServerLoad(ServerLoadEvent event){
        Plugin[] pluginsArray = Bukkit.getPluginManager().getPlugins();
        this.getSpigotTasks().runAsyncTask(() -> {
            for(Plugin plugin : pluginsArray){
                File pluginFolder = plugin.getDataFolder();
                File superManagerFile = new File(pluginFolder, "SuperManager.json");
                if(Utils.isConnected()){
                    try{
                        if(!superManagerFile.exists()){
                            FileUtils.downloadUsingCommons(("https://raw.githubusercontent.com/TheProgramSrc/PluginsResources/master/SuperManager/PluginManager/{Plugin}.json".replace("{Plugin}", plugin.getDescription().getName())), superManagerFile);
                        }
                    }catch(IOException ignored){}
                }
    
                if(superManagerFile.exists()){
                    try {
                        String data = xyz.theprogramsrc.supercoreapi.libs.apache.commons.io.FileUtils.readFileToString(superManagerFile, Charset.defaultCharset());
                        JsonObject json = JsonParser.parseString(data).getAsJsonObject();
                        int id = json.get("id").getAsInt();
                        String name = json.get("name").getAsString();
                        String platform = json.get("platform").getAsString();
                        plugins.put(name, new SPlugin(id, name, platform));
                    } catch (IOException e) {
                        this.plugin.addError(e);
                        this.log("&cError while reading file SuperManager.json from " + plugin.getName() + " (" + plugin.getDescription().getVersion() + ")");
                        e.printStackTrace();
                    }
                }
            }
    
            if(plugins.size() > 0){
                this.log("&aLoaded &c" + plugins.size() + "&a plugins.");
            }
        });
    }

    public static boolean validateToken(){
        if(token == null) return false;
        if(token.equals("")) return false;
        if(token.equals(" ")) return false;

        return token.matches("^[a-fA-F0-9]{32}$");
    }

    @Override
    public String getDisplay() {
        return L.PLUGIN_MANAGER_DISPLAY.toString();
    }

    @Override
    public String getIdentifier() {
        return "plugin_manager";
    }

    @Override
    public SimpleItem getDisplayItem() {
        return new SimpleItem(XMaterial.CHEST)
                .setDisplayName("&a" + L.PLUGIN_MANAGER_NAME)
                .setLore(
                        "&7",
                        "&7" + L.PLUGIN_MANAGER_LORE
                );
    }

    @Override
    public void onAction(Player player) {
        new PluginBrowser(player){
            @Override
            public void onBack(ClickAction clickAction) {
                new MainGUI(clickAction.getPlayer());
            }
        };
    }
}
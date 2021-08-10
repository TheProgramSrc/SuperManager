package xyz.theprogramsrc.supermanager.modules.pluginmanager;

import java.io.File;
import java.io.IOException;
import java.net.CookieHandler;
import java.net.CookieManager;
import java.nio.charset.Charset;
import java.util.LinkedHashMap;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.server.ServerLoadEvent;
import org.bukkit.plugin.Plugin;

import xyz.theprogramsrc.supercoreapi.global.networking.ConnectionBuilder;
import xyz.theprogramsrc.supercoreapi.global.utils.Utils;
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
    public static String token = SuperManager.token;
    public static CookieManager cookieManager = new CookieManager();

    @Override
    public void onEnable() {
        CookieHandler.setDefault(cookieManager);
    }

    @EventHandler
    public void onServerLoad(ServerLoadEvent event){
        Plugin[] pluginsArray = Bukkit.getPluginManager().getPlugins();
        this.getSpigotTasks().runAsyncTask(() -> {
            for(Plugin bukkitPlugin : pluginsArray){
                File superManagerFile = new File(Utils.folder(bukkitPlugin.getDataFolder()), "SuperManager.json");
                if(Utils.isConnected()){ // Always download in case of updates
                    String url = "https://raw.githubusercontent.com/TheProgramSrc/PluginsResources/master/SuperManager/PluginManager/{Plugin}.json".replace("{Plugin}", bukkitPlugin.getName());
                    if(this.isValidUrl(url)){
                        try{
                            String data = Utils.readWithInputStream(url);
                            if(data != null){
                                xyz.theprogramsrc.supercoreapi.libs.apache.commons.io.FileUtils.writeStringToFile(superManagerFile, data, Charset.defaultCharset(), false);
                            }
                        }catch(IOException ignored){}
                    }
                }
    
                if(superManagerFile.exists()){
                    try {
                        String data = xyz.theprogramsrc.supercoreapi.libs.apache.commons.io.FileUtils.readFileToString(superManagerFile, Charset.defaultCharset());
                        JsonObject json = JsonParser.parseString(data).getAsJsonObject();
                        int id = json.get("id").getAsInt();
                        String name = json.get("name").getAsString();
                        String platform = json.get("platform").getAsString();
                        String fileName = json.has("fileName") ? json.get("fileName").getAsString() : name + ".jar";
                        String downloadUrl = json.has("downloadUrl") ? json.get("downloadUrl").getAsString() : null; 
                        plugins.put(name, new SPlugin(id, name, platform, fileName, downloadUrl));
                    } catch (IOException e) {
                        this.plugin.addError(e);
                        this.log("&cError while reading file SuperManager.json from " + bukkitPlugin.getName() + " (" + bukkitPlugin.getDescription().getVersion() + ")");
                        e.printStackTrace();
                    }
                }
            }

            for(SPlugin plugin : plugins.values()){
                plugin.parseProduct(); // Here we just pre load the products from their respective api.
            }
    
            if(plugins.size() > 0){
                this.log("&aLoaded &c" + plugins.size() + "&a plugins.");
            }
        });
    }

    private boolean isValidUrl(String url){
        try{
            return ConnectionBuilder.connect(url).getResponseCode().startsWith("2");
        }catch(IOException e){
            return false;
        }
    }
    public static boolean validateToken(){
        return SuperManager.validateToken();
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
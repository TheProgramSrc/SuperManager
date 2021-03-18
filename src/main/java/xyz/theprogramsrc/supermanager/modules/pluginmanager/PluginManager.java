package xyz.theprogramsrc.supermanager.modules.pluginmanager;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;
import xyz.theprogramsrc.supercoreapi.apache.commons.io.FileUtils;
import xyz.theprogramsrc.supercoreapi.spigot.guis.action.ClickAction;
import xyz.theprogramsrc.supercoreapi.spigot.items.SimpleItem;
import xyz.theprogramsrc.supercoreapi.spigot.utils.xseries.XMaterial;
import xyz.theprogramsrc.supermanager.L;
import xyz.theprogramsrc.supermanager.guis.MainGUI;
import xyz.theprogramsrc.supermanager.modules.pluginmanager.guis.PluginBrowser;
import xyz.theprogramsrc.supermanager.modules.pluginmanager.objects.SPlugin;
import xyz.theprogramsrc.supermanager.objects.Module;

import java.io.File;
import java.io.IOException;
import java.nio.charset.Charset;
import java.util.LinkedHashMap;

public class PluginManager extends Module {

    public static LinkedHashMap<String, SPlugin> plugins;
    public static String token;

    @Override
    public void onEnable() {
        if(plugins == null) plugins = new LinkedHashMap<>();
        token = this.getSettings().getConfig().contains("songoda-token") ? this.getSettings().getConfig().getString("songoda-token") : null;
        for(Plugin plugin : Bukkit.getPluginManager().getPlugins()){
            File pluginFolder = plugin.getDataFolder();
            File superManagerFile = new File(pluginFolder, "SuperManager.json");
            if(superManagerFile.exists()){
                try {
                    String data = FileUtils.readFileToString(superManagerFile, Charset.defaultCharset());
                    JsonObject json = new JsonParser().parse(data).getAsJsonObject();
                    int id = json.get("id").getAsInt();
                    String name = json.get("name").getAsString();
                    boolean songoda = json.get("songoda").getAsBoolean();
                    plugins.put(name, new SPlugin(id, name, songoda));
                } catch (IOException e) {
                    this.plugin.addError(e);
                    this.log("&cError while reading file SuperManager.json from " + plugin.getName() + " (" + plugin.getDescription().getVersion() + ")");
                    e.printStackTrace();
                }
            }
        }

        this.log("&aLoaded &c" + plugins.size() + "&a plugins.");
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
    public void onAction(ClickAction clickAction) {
        new PluginBrowser(clickAction.getPlayer()){
            @Override
            public void onBack(ClickAction clickAction) {
                new MainGUI(clickAction.getPlayer());
            }
        };
    }
}
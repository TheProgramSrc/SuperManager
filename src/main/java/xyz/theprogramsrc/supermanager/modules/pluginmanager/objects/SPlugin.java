package xyz.theprogramsrc.supermanager.modules.pluginmanager.objects;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.URL;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;
import org.jsoup.Connection.Response;
import org.jsoup.Jsoup;

import xyz.theprogramsrc.supercoreapi.global.files.yml.YMLConfig;
import xyz.theprogramsrc.supercoreapi.global.networking.ConnectionBuilder;
import xyz.theprogramsrc.supercoreapi.global.networking.CustomConnection;
import xyz.theprogramsrc.supercoreapi.global.utils.Utils;
import xyz.theprogramsrc.supercoreapi.libs.apache.commons.io.FileUtils; 
import xyz.theprogramsrc.supercoreapi.libs.google.gson.JsonObject;
import xyz.theprogramsrc.supermanager.L; 
import xyz.theprogramsrc.supermanager.SuperManager;
import xyz.theprogramsrc.supermanager.modules.pluginmanager.apiwrappers.SongodaAPI;
import xyz.theprogramsrc.supermanager.modules.pluginmanager.apiwrappers.SpigotAPI;
import xyz.theprogramsrc.supermanager.modules.pluginmanager.apiwrappers.objects.Product;
import xyz.theprogramsrc.supermanager.modules.pluginmanager.apiwrappers.objects.Version;

public class SPlugin {

    private final int id;
    private final String name, platform, fileName, downloadUrl;
    private boolean premium;
    private long lastPremiumCheck;

    public SPlugin(int id, String name, String platform, String fileName, String downloadUrl) {
        this.id = id;
        this.name = name;
        this.platform = platform;
        this.fileName = fileName;
        this.downloadUrl = downloadUrl;
    }

    public int getId() {
        return this.id;
    }

    public String getName() {
        return this.name;
    }

    public String getPlatform() {
        return this.platform;
    }

    public String getFileName(){
        return this.fileName;
    }

    public String getDownloadUrl() {
        return this.downloadUrl;
    }

    public boolean isSongoda(){
        return this.getPlatform().equals("Songoda");
    }

    public boolean isSpigot(){
        return this.getPlatform().equals("Spigot");
    }

    public boolean isMCMarket(){
        return this.getPlatform().equals("MCMarket");
    }

    public boolean isBukkit(){
        return this.getPlatform().equals("Bukkit");
    }

    public boolean isTheProgramSrc(){
        return this.getPlatform().equals("TheProgramSrc");
    }

    public Product parseProduct(){
        try {
            if(this.isSongoda()){
                return SongodaAPI.getProduct(this.getId()+"");
            }else if(this.isSpigot()){
                return SpigotAPI.getProduct(this.getId()+"");
            }
    
        }catch (IOException e){
            e.printStackTrace();
        }
        return null;
    }

    public String getCurrentVersion(){
        Plugin plugin = this.getPlugin();
        if(plugin == null) return L.UNKNOWN_VERSION.toString();
        return plugin.getDescription().getVersion();
    }

    public boolean isUpdateAvailable(){
        Product p = this.parseProduct();
        if(p == null) return false;
        Version current = p.versionFromName(this.getPlugin().getDescription().getVersion());
        Version latest = p.getLatestVersion();
        return current.shouldUpdateTo(latest);
    }

    public Plugin getPlugin(){
        return Bukkit.getPluginManager().getPlugin(this.getName());
    }

    public String getLatestVersion(){
        Product p = this.parseProduct();
        if(p == null) return L.UNKNOWN_VERSION.toString();
        return p.getLatestVersion().getName();
    }

    public void downloadUpdate(Player player){
        Product p = this.parseProduct();
        if(p == null) return;
        YMLConfig cfg = new YMLConfig(new File("bukkit.yml"));
        File pluginsFolder = new File(SuperManager.i.getServerFolder(), "plugins/"); // Don't need to mkdir since this plugin won't be working if there is no plugins folder
        File updateFolder = Utils.folder(new File(pluginsFolder, cfg.getString("settings.update-folder", "update") + "/"));
        final SuperManager pl = SuperManager.i;
        final Version latest = this.parseProduct().getLatestVersion();
        pl.getSpigotTasks().runAsyncTask(() -> {
            if(this.isSpigot()){
                String url = this.getDownloadUrl() == null ? null : this.getDownloadUrl().replace("{Name}", this.getName()).replace("{Version}", latest.getName()).replace("{VersionId}", latest.getId()).replace("{ProductId}", this.id+"");
                if(url == null) {
                    pl.getSuperUtils().sendMessage(player, pl.getSettingsStorage().getPrefix() + L.PLUGIN_MANAGER_ERROR_ON_DOWNLOAD.options().placeholder("{PluginName}", this.getName()));
                    pl.log("&cYou currently can't download files from Spigot unless you have a direct link.");
                }else{
                    try{
                        Response response = Jsoup.connect(url)
                            .userAgent("Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/74.0.3729.169 Safari/537.36")
                            .referrer("https://google.com/")
                            .ignoreHttpErrors(true)
                            .followRedirects(true)
                            .timeout(30000)
                            .ignoreContentType(true)
                            .execute();
                        if(response.statusCode() == 200){
                            pl.getSuperUtils().sendMessage(player, pl.getSettingsStorage().getPrefix() + L.PLUGIN_MARKETPLACE_INSTALLING_PLUGIN.options().placeholder("{PluginName}", this.getName()));
                            String fileName = (this.fileName.endsWith(".jar") ? this.fileName : (this.fileName + ".jar")).replace("{Name}", this.getName()).replace("{Version}", this.getLatestVersion());
                            File updateFile = new File(updateFolder, fileName);
                            FileOutputStream fileOutputStream = new FileOutputStream(updateFile);
                            fileOutputStream.write(response.bodyAsBytes());
                            fileOutputStream.close();
                            this.clearAndMove(pluginsFolder, updateFile);
                            pl.getSuperUtils().sendMessage(player, pl.getSettingsStorage().getPrefix() + L.PLUGIN_MANAGER_SUCCESS_DOWNLOAD.options().placeholder("{PluginName}", this.getName()));
                        }else{
                            pl.log("&cInvalid status code '" + response.statusCode() + "' when downloading from '" + url + "'");
                            pl.getSuperUtils().sendMessage(player, pl.getSettingsStorage().getPrefix() + L.PLUGIN_MANAGER_ERROR_ON_DOWNLOAD.options().placeholder("{PluginName}", this.getName()));
                        }
                    }catch(IOException e){
                        pl.getSuperUtils().sendMessage(player, pl.getSettingsStorage().getPrefix() + L.PLUGIN_MANAGER_ERROR_ON_DOWNLOAD.options().placeholder("{PluginName}", this.getName()));
                        pl.addError(e);
                        pl.log("&cFailed to download plugin update:");
                        e.printStackTrace();
                    }
                }
            } else if(this.isSongoda()){
                try{
                    String url = latest.getDownloadUrl();
                    String fileName = latest.getFileName();
                    File updateFile = new File(updateFolder, fileName);
                    FileUtils.copyURLToFile(new URL(url), updateFile);
                    pl.getSuperUtils().sendMessage(player, pl.getSettingsStorage().getPrefix() + L.PLUGIN_MARKETPLACE_INSTALLING_PLUGIN.options().placeholder("{PluginName}", this.getName()));
                    this.clearAndMove(pluginsFolder, updateFile);
                    pl.getSuperUtils().sendMessage(player, pl.getSettingsStorage().getPrefix() + L.PLUGIN_MANAGER_SUCCESS_DOWNLOAD.options().placeholder("{PluginName}", this.getName()));    
                }catch(IOException e){
                    pl.getSuperUtils().sendMessage(player, pl.getSettingsStorage().getPrefix() + L.PLUGIN_MANAGER_ERROR_ON_DOWNLOAD.options().placeholder("{PluginName}", this.getName()));
                    pl.addError(e);
                    pl.log("&cFailed to download plugin update:");
                    e.printStackTrace();
                }
            }else {
                pl.log("&cThe platform '" + this.getPlatform() + "' is currently unsupported.");
                pl.getSuperUtils().sendMessage(player, pl.getSettingsStorage().getPrefix() + L.PLUGIN_MANAGER_ERROR_ON_DOWNLOAD.options().placeholder("{PluginName}", this.getName()));
            }
        });
    }

    public boolean isPremium() {
        try{
            if(this.isSongoda()){
                if(this.lastPremiumCheck == 0L){
                    this.lastPremiumCheck = System.currentTimeMillis();
                }else{
                    if((System.currentTimeMillis() - this.lastPremiumCheck) <= Utils.toMillis(500)){
                        return this.premium;
                    }else{
                        this.lastPremiumCheck = System.currentTimeMillis();
                    }
                }
        
                CustomConnection connection = new ConnectionBuilder("https://songoda.com/api/v2/products/id/" + id).connect();
                JsonObject json = connection.getResponseJson();
                if(json != null){
                    json = json.get("data").getAsJsonObject();
                    this.premium = !json.get("payment_method").getAsString().equals("None");
                }
                
                return this.premium;
            }
        }catch (IOException e){
            SuperManager.i.log("&cError while connecting with the API:");
            SuperManager.i.addError(e);
            e.printStackTrace();
        }
    
        
        return false;
    }

    private void clearAndMove(File pluginsFolder, File updateFile) {
        final SuperManager pl = SuperManager.i;
        pl.getSpigotTasks().runAsyncTask(() -> {
            try{
                File previous = new File(pluginsFolder, this.getName() + "-" + this.getCurrentVersion() + ".jar");
                if(!previous.exists()) previous = new File(pluginsFolder, this.getName() + ".jar");
                if(!previous.exists()) previous = new File(pluginsFolder, this.getName().toLowerCase() + "-" + this.getCurrentVersion() + ".jar");
                if(!previous.exists()) previous = new File(pluginsFolder, this.getName().toLowerCase() + ".jar");
                if(previous.exists()){
                    FileUtils.forceDelete(previous);
                    FileUtils.moveFileToDirectory(updateFile, pluginsFolder, true);
                }   
            }catch(IOException e){
                pl.log("&cFailed to install plugin update:");
                pl.addError(e);
                e.printStackTrace();
            }
        });
    }

}
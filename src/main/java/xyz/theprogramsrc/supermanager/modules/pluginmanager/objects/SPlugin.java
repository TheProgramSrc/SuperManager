package xyz.theprogramsrc.supermanager.modules.pluginmanager.objects;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.HttpURLConnection;
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
import xyz.theprogramsrc.supercoreapi.libs.google.gson.JsonObject;
import xyz.theprogramsrc.supermanager.L;
import xyz.theprogramsrc.supermanager.SuperManager;
import xyz.theprogramsrc.supermanager.modules.pluginmanager.apiwrappers.SongodaAPI;
import xyz.theprogramsrc.supermanager.modules.pluginmanager.apiwrappers.SpigotAPI;
import xyz.theprogramsrc.supermanager.modules.pluginmanager.apiwrappers.objects.Product;
import xyz.theprogramsrc.supermanager.modules.pluginmanager.apiwrappers.objects.Version;

public class SPlugin {

    private final int id;
    private final String name;
    private final String platform;
    private boolean premium;
    private long lastPremiumCheck;

    public SPlugin(int id, String name, String platform) {
        this.id = id;
        this.name = name;
        this.platform = platform;
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
        Plugin plugin = Bukkit.getPluginManager().getPlugin(this.getName());
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
        String url = p.getLatestVersion().getDownloadUrl();
        if(this.isPremium()){
            url += "?token=" + SuperManager.i.getSettingsStorage().getConfig().getString("songoda-token");
        }
        YMLConfig cfg = new YMLConfig(new File("bukkit.yml"));
        File pluginsFolder = new File(SuperManager.i.getServerFolder(), "plugins/"); // Don't need to mkdir since this plugin won't be working if there is no plugins folder
        File updateFolder = Utils.folder(new File(pluginsFolder, cfg.getString("settings.update-folder") + "/"));
        final SuperManager pl = SuperManager.i;
        final String downloadUrl = url + "";
        pl.getSpigotTasks().runAsyncTask(() -> {
            try {
                Response response = Jsoup.connect(downloadUrl) // Always try to bypass cloudflare just in case
                    .userAgent("Mozilla/5.0 (X11; Linux x86_64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/51.0.2704.103 Safari/537.36")
                    .ignoreHttpErrors(true)
                    .followRedirects(true)
                    .timeout(30000)
                    .execute();
                pl.getSpigotTasks().runTaskLater(Utils.toTicks(7), () -> {
                    pl.getSpigotTasks().runAsyncTask(() -> {
                        try{
                            Response downloadResponse = Jsoup.connect(downloadUrl)
                                .userAgent("Mozilla/5.0 (X11; Linux x86_64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/51.0.2704.103 Safari/537.36")
                                .ignoreHttpErrors(true)
                                .followRedirects(true)
                                .timeout(30000)
                                .cookies(response.cookies())
                                .execute();
                                if(response.statusCode() == 200){
                                    FileOutputStream fileOutputStream = new FileOutputStream(new File(updateFolder, this.name + ".jar"));
                                    fileOutputStream.write(downloadResponse.bodyAsBytes());
                                    fileOutputStream.close();
                                    pl.getSuperUtils().sendMessage(player, pl.getSettingsStorage().getPrefix() + L.PLUGIN_MANAGER_SUCCESS_DOWNLOAD.options().placeholder("{PluginName}", this.getName()));
                                }else{
                                    pl.getSuperUtils().sendMessage(player, pl.getSettingsStorage().getPrefix() + L.PLUGIN_MANAGER_ERROR_ON_DOWNLOAD.options().placeholder("{PluginName}", this.getName()));
                                }
                        }catch (Exception e){
                            pl.getSuperUtils().sendMessage(player, pl.getSettingsStorage().getPrefix() + L.PLUGIN_MANAGER_ERROR_ON_DOWNLOAD.options().placeholder("{PluginName}", this.getName()));
                            pl.addError(e);
                            pl.log("&cFailed to download plugin update:");
                            e.printStackTrace();
                        }
                    });
                });
            }catch (Exception e){
                pl.getSuperUtils().sendMessage(player, pl.getSettingsStorage().getPrefix() + L.PLUGIN_MANAGER_ERROR_ON_DOWNLOAD.options().placeholder("{PluginName}", this.getName()));
                pl.addError(e);
                pl.log("&cFailed to download plugin update:");
                e.printStackTrace();
            }
        });
    }

    public boolean isPremium() {
        if(!this.isSongoda()) return false;
        if(this.lastPremiumCheck == 0L){
            this.lastPremiumCheck = System.currentTimeMillis();
        }else{
            if((System.currentTimeMillis() - this.lastPremiumCheck) <= Utils.toMillis(500)){
                return this.premium;
            }else{
                this.lastPremiumCheck = System.currentTimeMillis();
            }
        }

        try{
            CustomConnection connection = new ConnectionBuilder("https://songoda.com/api/v2/products/id/" + id).connect();
            JsonObject json = connection.getResponseJson();
            if(json != null){
                json = json.get("data").getAsJsonObject();
                this.premium = !json.get("payment_method").getAsString().equals("None");
            }
        }catch (IOException e){
            SuperManager.i.log("&cError while connecting with the SongodaAPI:");
            SuperManager.i.addError(e);
            e.printStackTrace();
        }
        return this.premium;
    }

    private static String followRedirects(String url) {
        URL urlTmp;
        try {
           urlTmp = new URL(url);
        } catch (Exception var10) {
           return url;
        }
  
        HttpURLConnection connection;
        try {
           connection = (HttpURLConnection)urlTmp.openConnection();
        } catch (Exception var9) {
           return url;
        }
  
        try {
           connection.getResponseCode();
        } catch (Exception var8) {
           return url;
        }
  
        String redUrl = connection.getURL().toString();
        connection.disconnect();
        if (!redUrl.equals(url)) {
           return redUrl;
        } else {
           try {
              URL obj = new URL(url);
              HttpURLConnection conn = (HttpURLConnection)obj.openConnection();
              conn.setReadTimeout(5000);
              conn.addRequestProperty("Accept-Language", "en-US,en;q=0.8");
              conn.addRequestProperty("User-Agent", "Mozilla");
              conn.addRequestProperty("Referer", "google.com");
              boolean redirect = false;
              int status = conn.getResponseCode();
              if (status != 200 && (status == 302 || status == 301 || status == 303)) {
                 redirect = true;
              }
  
              if (redirect) {
                 return conn.getHeaderField("Location");
              }
           } catch (Exception var11) {
              ;
           }
  
           return url;
        }
     }
}
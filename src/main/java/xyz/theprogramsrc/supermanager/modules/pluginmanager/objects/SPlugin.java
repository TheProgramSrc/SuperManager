package xyz.theprogramsrc.supermanager.modules.pluginmanager.objects;

import java.io.*;
import java.net.*;
import java.util.*;
import java.util.stream.Collectors;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;

import xyz.theprogramsrc.supercoreapi.global.files.yml.YMLConfig;
import xyz.theprogramsrc.supercoreapi.global.networking.ConnectionBuilder;
import xyz.theprogramsrc.supercoreapi.global.networking.CustomConnection;
import xyz.theprogramsrc.supercoreapi.global.utils.Utils;
import xyz.theprogramsrc.supercoreapi.libs.google.gson.JsonObject;
import xyz.theprogramsrc.supermanager.L;
import xyz.theprogramsrc.supermanager.SuperManager;
import xyz.theprogramsrc.supermanager.modules.pluginmanager.PluginManager;
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
        final String downloadUrl = followRedirects(url);
        pl.getSpigotTasks().runAsyncTask(() -> {
            try {
                CookieManager cm = PluginManager.cookieManager;
                ConnectionBuilder cb = new ConnectionBuilder(downloadUrl);
                if(cm.getCookieStore().getCookies().size() > 0){
                    cb.addProperty("Cookie", cm.getCookieStore().getCookies().stream().map(HttpCookie::toString).collect(Collectors.joining(";")));
                }
                cb.addProperty("User-Agent", "Mozilla/5.0 (Macintosh; Intel Mac OS X 10_15_7) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/91.0.4472.106 Safari/537.36");
                CustomConnection cc = cb.connect();
                if(cc.isResponseNotNull()){
                    String contents = cc.getResponseString().toLowerCase();
                    if(contents.contains("ddos protection by cloudflare") && contents.contains("this process is automatic.") && contents.contains("your browser will redirect to your requested content shortly")){
                        // Wait 7 secs and start again
                        pl.getSpigotTasks().runTaskLater(Utils.toTicks(7), () -> {
                            Map<String, List<String>> headerFields = cc.getConnection().getHeaderFields();
                            List<String> cookiesHeader = headerFields.get("Set-Cookie");
                            if(cookiesHeader == null) cookiesHeader = new ArrayList<String>();
                            for(String cookie : cookiesHeader){
                                cm.getCookieStore().add(null, HttpCookie.parse(cookie).get(0));
                            }

                            this.downloadUpdate(player);
                        });
                    }else{
                        // Now we just download the file with the cookies applied
                        BufferedInputStream bufferedInputStream = new BufferedInputStream(cc.getInputStream());
                        FileOutputStream fileInputStream = new FileOutputStream(new File(updateFolder, this.getName() + ".jar"));
                        byte[] buffer = new byte[1024];

                        int count;
                        while((count = bufferedInputStream.read(buffer, 0, 1024)) != -1) {
                            fileInputStream.write(buffer, 0, count);
                        }

                        fileInputStream.close();
                        bufferedInputStream.close();
                        pl.getSuperUtils().sendMessage(player, pl.getSettingsStorage().getPrefix() + L.PLUGIN_MANAGER_SUCCESS_DOWNLOAD.options().placeholder("{PluginName}", this.getName()));
                    }
                }else{
                    pl.getSuperUtils().sendMessage(player, pl.getSettingsStorage().getPrefix() + L.PLUGIN_MANAGER_ERROR_ON_DOWNLOAD.options().placeholder("{PluginName}", this.getName()));
                }
            }catch (IOException e){
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
package xyz.theprogramsrc.supermanager.modules.pluginmanager.objects;

import org.bukkit.Bukkit;
import org.bukkit.plugin.Plugin;

import xyz.theprogramsrc.supercoreapi.global.files.yml.YMLConfig;
import xyz.theprogramsrc.supercoreapi.global.networking.ConnectionBuilder;
import xyz.theprogramsrc.supercoreapi.global.networking.CustomConnection;
import xyz.theprogramsrc.supercoreapi.global.utils.Utils;
import xyz.theprogramsrc.supercoreapi.global.utils.files.FileUtils;
import xyz.theprogramsrc.supercoreapi.libs.google.gson.*;
import xyz.theprogramsrc.supermanager.L;
import xyz.theprogramsrc.supermanager.SuperManager;
import xyz.theprogramsrc.supermanager.modules.pluginmanager.PluginManager;

import java.io.File;
import java.io.IOException;

public class SPlugin {

    private final int id;
    private final String name;
    private final boolean songoda;
    private String latestVersion;
    private boolean updateAvailable, premium;
    private long lastCheck = 0L, lastPremiumCheck;

    public SPlugin(int id, String name, boolean songoda) {
        this.id = id;
        this.name = name;
        this.songoda = songoda;
        this.latestVersion = null;
    }

    public int getId() {
        return this.id;
    }

    public String getName() {
        return this.name;
    }

    public boolean isSongoda() {
        return this.songoda;
    }

    public String getCurrentVersion(){
        Plugin plugin = Bukkit.getPluginManager().getPlugin(this.getName());
        if(plugin == null) return L.UNKNOWN_VERSION.toString();
        return plugin.getDescription().getVersion();
    }

    public String getLatestVersion() {
        if(this.latestVersion == null){
            this.isUpdateAvailable();
        }

        return this.latestVersion;
    }

    public boolean isUpdateAvailable(){
        if(this.lastCheck == 0L){
            this.lastCheck = System.currentTimeMillis();
        }else{
            if((System.currentTimeMillis() - this.lastCheck) <= Utils.toMillis(10)){
                return this.updateAvailable;
            }else{
                this.lastCheck = System.currentTimeMillis();
            }
        }

        try{
            CustomConnection connection = new ConnectionBuilder(this.isSongoda() ? ("https://songoda.com/api/v2/products/id/" + this.id) : ("https://api.spiget.org/v2/resources/" + this.id + "/versions?size=10&sort=-releaseDate")).connect();
            if((connection.getResponseCode()+"").startsWith("2")){
                if(this.isSongoda()){
                    JsonObject json = connection.getResponseJson();
                    if(json == null){
                        SuperManager.i.log("&cThe SongodaAPI returned a null json response while checking for " + this.getName() + " updates");
                        this.updateAvailable = false;
                    }else{
                        json = json.get("data").getAsJsonObject();
                        if(json.isJsonNull()){
                            SuperManager.i.log("&cThe SongodaAPI returned a null json response while checking for " + this.getName() + " updates");
                            this.updateAvailable = false;
                        }else{
                            long currentVersionReleaseDate = 0L;
                            long latestVersionReleaseDate = 0L;
                            int index = 0;
                            for (JsonElement versionElement : json.get("versions").getAsJsonArray()) { // First in array will always be the latest version
                                JsonObject version = versionElement.getAsJsonObject();
                                if(index == 0) this.latestVersion = version.get("version").getAsString();
                                if(version.isJsonNull()) continue;
                                latestVersionReleaseDate = version.get("created_at").getAsNumber().longValue();
                                if(this.latestVersion.equals(this.getCurrentVersion())){
                                    currentVersionReleaseDate = version.get("created_at").getAsNumber().longValue();
                                }
                                index++;
                            }

                            if(currentVersionReleaseDate == 0L || latestVersionReleaseDate == 0L){
                                SuperManager.i.log("&cUnable to find the current version in SongodaAPI.");
                                this.updateAvailable = false;
                            }else{
                                this.updateAvailable = latestVersionReleaseDate - currentVersionReleaseDate != 0L;
                            }
                        }
                    }
                }else{
                    JsonElement jsonElement = JsonParser.parseString(connection.getResponseString());
                    if(jsonElement.isJsonNull()){
                        SuperManager.i.log("&cThe SpigetAPI returned a null json response while checking for " + this.getName() + " updates");
                        this.updateAvailable = false;
                    }else{
                        JsonArray versionsArray = jsonElement.getAsJsonArray();
                        long currentVersionReleaseDate = 0L;
                        long latestVersionReleaseDate = 0L;
                        int index = 0;
                        for (JsonElement element : versionsArray) { // first in array will always be the latest
                            if(element.isJsonNull()) continue;
                            JsonObject json = element.getAsJsonObject();
                            if(json.isJsonNull()) continue;
                            if(index == 0) this.latestVersion = json.get("name").getAsString();
                            latestVersionReleaseDate = json.get("releaseDate").getAsNumber().longValue();
                            if(this.latestVersion.equals(this.getCurrentVersion())){
                                currentVersionReleaseDate = json.get("releaseDate").getAsNumber().longValue();
                            }
                            index++;
                        }

                        if(currentVersionReleaseDate == 0L || latestVersionReleaseDate == 0L){
                            SuperManager.i.log("&cUnable to find the current version in SpigetAPI.");
                            this.updateAvailable = false;
                        }else{
                            this.updateAvailable = latestVersionReleaseDate - currentVersionReleaseDate != 0L;
                        }
                    }
                }
            }else{
                SuperManager.i.log("&cUnable to connect with " + (this.isSongoda() ? "SongodaAPI" : "SpigetAPI") + ".");
                SuperManager.i.log("&cError code: &a" + connection.getResponseCode());
                SuperManager.i.log("&cError message: &a" + connection.getResponseMessage());
                this.updateAvailable = false;
            }
        }catch (IOException e){
            SuperManager.i.addError(e);
            e.printStackTrace();
            this.updateAvailable = false;
        }
        return this.updateAvailable;
    }

    public Plugin getPlugin(){
        return Bukkit.getPluginManager().getPlugin(this.getName());
    }

    public boolean downloadUpdate(){
        String url = this.getDownloadURL(this.getLatestVersion());
        if(url == null) return false;
        // Test Connection
        try{
            CustomConnection connection = new ConnectionBuilder(url).connect();
            if(!(connection.getResponseCode()+"").startsWith("2")){
                SuperManager.i.log("&cDownload connection test failed:");
                SuperManager.i.log("&cReturned code: &7" + connection.getResponseCode());
                SuperManager.i.log("&cReturned message: &7" + connection.getResponseMessage());
                return false;
            }
        }catch (IOException e){
            SuperManager.i.log("&cDownload connection test failed:");
            SuperManager.i.addError(e);
            e.printStackTrace();
            return false;
        }

        // Success test and download the product!
        YMLConfig cfg = new YMLConfig(new File("bukkit.yml"));
        File pluginsFolder = new File(SuperManager.i.getServerFolder(), "plugins/"); // Don't need to mkdir since this plugin won't be working if there is no plugins folder
        File updateFolder = Utils.folder(new File(pluginsFolder, cfg.getString("settings.update-folder") + "/"));
        return FileUtils.downloadUsingStream(url, new File(updateFolder, this.getName() + ".jar"));
    }

    private String getDownloadURL(String version){
        if(this.isSongoda()){ // Make request to the API
            try{
                CustomConnection connection = new ConnectionBuilder("https://songoda.com/api/v2/products/id/" + this.getId()).connect();
                if((connection.getResponseCode()+"").startsWith("2")){
                    JsonObject json = connection.getResponseJson();
                    if(json == null) return null;
                    JsonObject data = json.get("data").getAsJsonObject();
                    String slug = data.get("slug").getAsString();
                    return this.isPremium() ? String.format("https://songoda.com/product/%s/download/%s?token=%s", slug, version, PluginManager.token) : String.format("https://songoda.com/product/%s/download/%s", slug, version);
                }else{
                    SuperManager.i.log("&cUnable to connect with SongodaAPI.");
                    SuperManager.i.log("&cError code: &a" + connection.getResponseCode());
                    SuperManager.i.log("&cError message: &a" + connection.getResponseMessage());
                    return null;
                }
            }catch (IOException e){
                SuperManager.i.log("&cUnable to connect with SongodaAPI.");
                SuperManager.i.addError(e);
                e.printStackTrace();
                return null;
            }
        }else{
            return "https://api.spiget.org/v2/resources/" + this.id + "/download";
        }
    }

    public boolean isPremium() {
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
            CustomConnection connection = new ConnectionBuilder("https://songoda.com/api/v2/products/id" + id).connect();
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
}
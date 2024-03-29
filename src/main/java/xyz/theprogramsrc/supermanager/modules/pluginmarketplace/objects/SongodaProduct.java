package xyz.theprogramsrc.supermanager.modules.pluginmarketplace.objects;

import java.io.File;
import java.io.IOException;
import java.net.URI;
import java.net.URL;
import java.util.LinkedHashMap;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.plugin.PluginDescriptionFile;

import xyz.theprogramsrc.supercoreapi.global.networking.CustomConnection;
import xyz.theprogramsrc.supercoreapi.global.utils.Utils;
import xyz.theprogramsrc.supercoreapi.global.utils.files.FileUtils;
import xyz.theprogramsrc.supercoreapi.libs.google.gson.JsonArray;
import xyz.theprogramsrc.supercoreapi.libs.google.gson.JsonParser;
import xyz.theprogramsrc.supermanager.L;
import xyz.theprogramsrc.supermanager.SuperManager;
import xyz.theprogramsrc.supermanager.utils.PluginUtils;

public class SongodaProduct {

    private final int id, downloads, views;
    private final String name, description, owner, url, price, currency, paymentMethod, filename, downloadUrl, tagline, supportedVersions;
    private final LinkedHashMap<String, String> currencySymbols = new LinkedHashMap<>();

    public SongodaProduct(int id, String name, String description, String owner, String url, String price, String currency, String paymentMethod, String filename, String downloadUrl, int views, int downloads, String tagline, String supportedVersions){
        this.id = id;
        this.name = name;
        this.description = description;
        this.owner = (owner != null ? owner : "").replace("[\"", "").replace("\"]", "");
        this.url = url;
        this.paymentMethod = paymentMethod;
        this.downloads = downloads;
        this.views = views;
        this.filename = filename;
        this.downloadUrl = downloadUrl;
        this.tagline = tagline;
        this.supportedVersions = supportedVersions;
        if(paymentMethod.equalsIgnoreCase("patreon")){
            this.price = "15.0";
            this.currency = "USD";
        }else{
            this.price = price;
            this.currency = currency;
        }
        currencySymbols.put("EUR", "€");
        currencySymbols.put("USD", "$");
        currencySymbols.put("AUD", "AU$");
        currencySymbols.put("GBP", "£");
    }

    public String getName() {
        return name != null ? name : "";
    }

    public String getTagline(){
        return tagline != null ? tagline : "";
    }

    public String getDescription() {
        return description != null ? description : "";
    }

    public String getOwner() {
        return owner;
    }

    public String getUrl() {
        return url;
    }

    public String getCurrency() {
        return currency;
    }

    public String getPrice() {
        return price;
    }

    public double getPriceAsDouble(){
        return Double.parseDouble(price);
    }

    public String getPaymentMethod() {
        return paymentMethod;
    }

    public boolean isFree(){
        return !this.isSongodaPlus() && (this.getPriceAsDouble() == 0.0 || this.getPaymentMethod().equalsIgnoreCase("none"));
    }

    public boolean isSongodaPlus(){
        return this.getPaymentMethod().equalsIgnoreCase("songoda+") || this.getPaymentMethod().equalsIgnoreCase("songoda+ program") || (this.getOwner().equalsIgnoreCase("songoda") && this.getPriceAsDouble() == 0.1);
    }

    public int getId() {
        return id;
    }

    public String getDownloads() {
        return this.formatNumber(this.downloads);
    }

    public String getViews() {
        return this.formatNumber(this.views);
    }

    public String getFilename() {
        return filename;
    }

    public String getDownloadUrl() {
        return downloadUrl;
    }

    public String getSupportedVersions() {
        return supportedVersions;
    }

    public void download(Player player){
        SuperManager.i.getSpigotTasks().runAsyncTask(() -> {
            if(!this.isFree()){
                SuperManager.i.getSuperUtils().sendMessage(player, SuperManager.i.getSettingsStorage().getPrefix() + L.PLUGIN_MARKETPLACE_CANNOT_DOWNLOAD_PAID_PLUGIN);
            }else{
                try{
                    // This is ready for Premium Products, BUT it's not working yet xD is always throwing error 403 (feel free to try in your browser with the product download url and attaching at the end '?token=<api-token>')
                    URI baseUri = new URL(this.getDownloadUrl()).toURI(); 
                    URL javaURL = new URI(baseUri.getScheme(), baseUri.getAuthority(), baseUri.getPath(), ("token=" + SuperManager.i.getSettingsStorage().getConfig().getString("songoda-token")), null).toURL();
                    CustomConnection connection = new CustomConnection(javaURL, javaURL.openConnection());
                    if(connection.getResponseString() != null && !(connection.getResponseCode()+"").startsWith("2")){
                        if(connection.getResponseString().toLowerCase().contains("msg")){
                            JsonArray data = JsonParser.parseString(connection.getResponseString()).getAsJsonArray();
                            if(data.size() != 2){
                                SuperManager.i.getSuperUtils().sendMessage(player, SuperManager.i.getSettingsStorage().getPrefix() + L.PLUGIN_MARKETPLACE_MESSAGE_RESPONSE.options().placeholder("{Message}", connection.getResponseString()));
                            }else{
                                SuperManager.i.getSuperUtils().sendMessage(player, SuperManager.i.getSettingsStorage().getPrefix() + L.PLUGIN_MARKETPLACE_MESSAGE_RESPONSE.options().placeholder("{Message}", data.get(1).getAsString()));
                            }
                        }else{
                            SuperManager.i.getSuperUtils().sendMessage(player, SuperManager.i.getSettingsStorage().getPrefix() + L.PLUGIN_MARKETPLACE_MESSAGE_RESPONSE.options().placeholder("{Message}", connection.getResponseString()));
                        }
                    }

                    File downloadFolder = Utils.folder(new File(SuperManager.i.getPluginFolder(), "downloads/"));
                    File pluginsFolder = Utils.folder(new File(SuperManager.i.getServerFolder(), "plugins/"));
                    if(new File(pluginsFolder, this.getFilename()).exists()){
                        SuperManager.i.getSuperUtils().sendMessage(player, SuperManager.i.getSettingsStorage().getPrefix() + L.PLUGIN_MARKETPLACE_PLUGIN_ALREADY_INSTALLED.options().placeholder("{PluginName}", this.getName()));
                    }else{
                        File output = new File(downloadFolder, this.getFilename());
                        if(FileUtils.downloadUsingStream(this.downloadUrl, output)){
                            if(filename.endsWith(".jar")){
                                PluginDescriptionFile descriptionFile = SuperManager.i.getPluginLoader().getPluginDescription(output);
                                    if(Bukkit.getPluginManager().getPlugin(descriptionFile.getName()) != null){
                                        SuperManager.i.getSuperUtils().sendMessage(player, SuperManager.i.getSettingsStorage().getPrefix() + L.PLUGIN_MARKETPLACE_PLUGIN_ALREADY_INSTALLED.options().placeholder("{PluginName}", this.getName()));
                                    }else{
                                        SuperManager.i.getSuperUtils().sendMessage(player, SuperManager.i.getSettingsStorage().getPrefix() + L.PLUGIN_MARKETPLACE_INSTALLING_PLUGIN.options().placeholder("{PluginName}", this.getName()));
                                        if(output.renameTo(new File(pluginsFolder, output.getName()))){
                                            if(PluginUtils.enable(descriptionFile.getName())){
                                                SuperManager.i.getSuperUtils().sendMessage(player, SuperManager.i.getSettingsStorage().getPrefix() + L.PLUGIN_MARKETPLACE_PLUGIN_ENABLED.options().placeholder("{PluginName}", this.getName()));
                                                SuperManager.i.getSuperUtils().sendMessage(player, SuperManager.i.getSettingsStorage().getPrefix() + L.PLUGIN_MARKETPLACE_PLUGIN_ENABLE_WARNING.options().placeholder("{PluginName}", this.getName()));
                                            }else{
                                                SuperManager.i.getSuperUtils().sendMessage(player, SuperManager.i.getSettingsStorage().getPrefix() + L.PLUGIN_MARKETPLACE_FAILED_TO_ENABLE_PLUGIN.options().placeholder("{PluginName}", this.getName()));
                                            }
                                        }else{
                                            throw new IOException("Failed to move '" + output.getPath() + "'");
                                        }
                                    }
                            }else{
                                SuperManager.i.getSuperUtils().sendMessage(player, SuperManager.i.getSettingsStorage().getPrefix() + L.PLUGIN_MARKETPLACE_INVALID_FILE.options().placeholder("{Path}", output.getPath()).placeholder("{PluginName}", this.getName()));
                            }
                        }else{
                            SuperManager.i.getSuperUtils().sendMessage(player, SuperManager.i.getSettingsStorage().getPrefix() + L.PLUGIN_MARKETPLACE_FAILED_PLUGIN_DOWNLOAD.options().placeholder("{PluginName}", this.getName()).get());
                        }
                    }
                }catch (Exception e){
                    SuperManager.i.addError(e);
                    SuperManager.i.log("&cFailed to download plugin '" + this.getName() + "'");
                    SuperManager.i.getSuperUtils().sendMessage(player, SuperManager.i.getSettingsStorage().getPrefix() + L.PLUGIN_MARKETPLACE_FAILED_PLUGIN_DOWNLOAD.options().placeholder("{PluginName}", this.getName()));
                    e.printStackTrace();
                }
            }
        });
    }

    private String formatNumber(long number) {
        if (number < 1000) {
            return "" + number;
        }

        int exp = (int) (Math.log(number) / Math.log(1000));

        return String.format("%.1f%c", number / Math.pow(1000, exp), "kMGTPE".charAt(exp - 1));
    }

    public String getPriceString() {
        if(this.isFree()) return "&e" + L.FREE;
        if(this.isSongodaPlus()) return "&6&l" + L.SONGODA_PLUS;
        return "&e" + this.currencySymbols.get(this.getCurrency()) + this.getPrice();
    }
}
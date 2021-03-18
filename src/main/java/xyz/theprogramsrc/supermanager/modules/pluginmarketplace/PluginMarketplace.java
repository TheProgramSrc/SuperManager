package xyz.theprogramsrc.supermanager.modules.pluginmarketplace;

import xyz.theprogramsrc.supercoreapi.global.networking.ConnectionBuilder;
import xyz.theprogramsrc.supercoreapi.global.networking.CustomConnection;
import xyz.theprogramsrc.supercoreapi.google.gson.JsonArray;
import xyz.theprogramsrc.supercoreapi.google.gson.JsonElement;
import xyz.theprogramsrc.supercoreapi.google.gson.JsonObject;
import xyz.theprogramsrc.supercoreapi.spigot.guis.action.ClickAction;
import xyz.theprogramsrc.supercoreapi.spigot.items.SimpleItem;
import xyz.theprogramsrc.supercoreapi.spigot.utils.xseries.XMaterial;
import xyz.theprogramsrc.supermanager.L;
import xyz.theprogramsrc.supermanager.guis.MainGUI;
import xyz.theprogramsrc.supermanager.modules.pluginmarketplace.guis.SongodaProductBrowser;
import xyz.theprogramsrc.supermanager.modules.pluginmarketplace.objects.SongodaProduct;
import xyz.theprogramsrc.supermanager.objects.Module;

import java.io.IOException;
import java.util.LinkedList;

public class PluginMarketplace extends Module {

    public static LinkedList<SongodaProduct> products;
    private static long lastCheck;
    private static PluginMarketplace i;

    @Override
    public void onEnable() {
        i = this;
        products = new LinkedList<>();
        lastCheck = 0L;
        Thread thread = new Thread(() -> {
            try{
                loadProducts();
            }catch (IOException e){
                this.log("&cFailed to load products:");
                e.printStackTrace();
                this.plugin.addError(e);
            }
        });
        thread.setPriority(7); // 0 (LOWEST) - 5 (NORMAL) - 10 (HIGH)
        thread.start();
    }

    @Override
    public String getDisplay() {
        return "Plugin Marketplace";
    }

    @Override
    public String getIdentifier() {
        return "plugin_marketplace";
    }

    @Override
    public boolean requireInternetConnection() {
        return true;
    }

    @Override
    public SimpleItem getDisplayItem() {
        return new SimpleItem(XMaterial.CHEST)
                .setDisplayName("&aPlugin marketplace")
                .setLore(
                        "&7",
                        "&7Click to explore the &cSongoda Marketplace",
                        "&7and install some plugins with 1 click."
                );
    }

    @Override
    public void onAction(ClickAction clickAction) {
        if(products.size() < 200){ // We know that the marketplace has at least 200 products
            this.getSuperUtils().sendMessage(clickAction.getPlayer(), this.getSettings().getPrefix() + L.PLUGIN_MARKETPLACE_STILL_LOADING);
        }

        new SongodaProductBrowser(clickAction.getPlayer()){
            @Override
            public void onBack(ClickAction clickAction) {
                new MainGUI(clickAction.getPlayer());
            }
        };
    }

    public static void loadProducts() throws IOException{
        loadProducts(false);
    }

    public static void loadProducts(boolean overrideCache) throws IOException{
        long now = System.currentTimeMillis();
        if((now - lastCheck) > (1000 * 60 * 3) || lastCheck == 0L || overrideCache || products.size() == 0){ // Only refresh every 3 minutes to avoid issues with the songoda server.
            lastCheck = now;
            int pages = pages();
            products.clear();
            for(int i = 0; i < pages; ++i){
                try{
                    int page = i+1;
                    PluginMarketplace.i.debug("Loaded " + (page) + " page(s), " + (pages - page) + " page(s) left.");
                    CustomConnection connection = new ConnectionBuilder("https://songoda.com/api/v2/products?page=" + page).connect();
                    JsonObject json = connection.getResponseJson();
                    if(json == null) continue;
                    for(JsonElement el : json.get("data").getAsJsonArray()){
                        JsonObject productJson = el.getAsJsonObject();
                        if(productJson.get("approved").getAsBoolean() && productJson.get("class").getAsString().equalsIgnoreCase("plugin") && productJson.get("status").getAsString().equalsIgnoreCase("approved")){
                            String slug = productJson.get("slug").getAsString();
                            JsonArray versions = productJson.get("versions").getAsJsonArray();
                            if(versions.size() < 1) continue;
                            JsonObject version = versions.get(0).getAsJsonObject();
                            String owner = "Unknown";
                            try{
                                if(productJson.get("team_id").isJsonNull()){
                                    CustomConnection profileConnection = new ConnectionBuilder("https://songoda.com/api/v2/profiles/id/" + productJson.get("user_id").getAsInt()).connect();
                                    JsonObject profileJson = profileConnection.getResponseJson();
                                    if(profileJson == null) continue;
                                    owner = profileJson.get("data").getAsJsonObject().get("name").getAsString();
                                }else{
                                    int team = productJson.get("team_id").getAsInt();
                                    if(team >= 1){
                                        CustomConnection profileConnection = new ConnectionBuilder("https://songoda.com/api/v2/teams/id/" + team).connect();
                                        JsonObject profileJson = profileConnection.getResponseJson();
                                        if(profileJson == null) continue;
                                        owner = profileJson.get("data").getAsJsonObject().get("name").getAsString();
                                    }else{
                                        CustomConnection profileConnection = new ConnectionBuilder("https://songoda.com/api/v2/profiles/id/" + productJson.get("user_id").getAsInt()).connect();
                                        JsonObject profileJson = profileConnection.getResponseJson();
                                        if(profileJson == null) continue;
                                        owner = profileJson.get("data").getAsJsonObject().get("name").getAsString();
                                    }
                                }
                            }catch (IOException ex){ continue; }
                            int id = productJson.get("id").getAsInt();
                            String product = productJson.get("name").getAsString();
                            String url = "https://songoda.com/marketplace/product/" + productJson.get("id").getAsString();
                            String description = productJson.get("description").getAsString();
                            String filename = version.get("filename").getAsString();
                            String downloadUrl = String.format("https://songoda.com/product/%s/download/%s", slug, version.get("version").getAsString());
                            String paymentMethod = productJson.get("payment_method").getAsString();
                            double price = paymentMethod.equalsIgnoreCase("none") ? 0 : productJson.get("price").getAsDouble();
                            String currency = productJson.get("currency").getAsString();
                            int downloads = productJson.get("downloads").getAsInt();
                            int views = productJson.get("views").getAsInt();
                            products.add(new SongodaProduct(id, product, description, owner, url, price+"", currency, paymentMethod, filename, downloadUrl, views, downloads));
                        }
                    }
                }catch (IOException ignored){
                }
            }
        }
    }

    private static int pages() throws IOException {
        CustomConnection connection = new ConnectionBuilder("https://songoda.com/api/v2/products").connect();
        JsonObject json = connection.getResponseJson();
        if(json == null){
            throw new NullPointerException("Failed to retrieve the data from the Songoda API: Null response.");
        }else{
            JsonObject meta = json.get("meta").getAsJsonObject();
            return meta.get("last_page").getAsInt();
        }
    }
}
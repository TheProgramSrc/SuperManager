package xyz.theprogramsrc.supermanager.modules.pluginmarketplace;

import org.bukkit.entity.Player;
import xyz.theprogramsrc.supercoreapi.global.networking.ConnectionBuilder;
import xyz.theprogramsrc.supercoreapi.global.networking.CustomConnection;
import xyz.theprogramsrc.supercoreapi.global.utils.Utils;
import xyz.theprogramsrc.supercoreapi.libs.google.gson.*;
import xyz.theprogramsrc.supercoreapi.libs.xseries.XMaterial;
import xyz.theprogramsrc.supercoreapi.spigot.guis.action.ClickAction;
import xyz.theprogramsrc.supercoreapi.spigot.items.SimpleItem;
import xyz.theprogramsrc.supermanager.L;
import xyz.theprogramsrc.supermanager.guis.MainGUI;
import xyz.theprogramsrc.supermanager.modules.pluginmarketplace.guis.SongodaProductBrowser;
import xyz.theprogramsrc.supermanager.modules.pluginmarketplace.objects.SongodaProduct;
import xyz.theprogramsrc.supermanager.objects.Module;

import java.io.IOException;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.stream.Collectors;

public class PluginMarketplace extends Module {

    private static LinkedHashMap<Integer, String> owners, owners2;
    public static LinkedHashMap<Integer, SongodaProduct> products;
    private static LinkedList<Integer> bannedProducts;
    private static final int per_page = 50;
    private static long lastCheck;
    private static PluginMarketplace i;

    @Override
    public void onEnable() {
        i = this;
        owners = new LinkedHashMap<>(); // Users
        owners2 = new LinkedHashMap<>(); // Teams
        products = new LinkedHashMap<>();
        // A list of products that should not be shown. Search them if you want to, but their data in the API is empty, like only the creator and the name of the product
        // Some of those product won't even show because they're not approved
        bannedProducts = new LinkedList<>(Utils.toList(545, 268, 425, 43, 594, 411, 438, 262, 403, 354, 388, 450, 529, 416, 377, 511, 324, 319));
        lastCheck = 0L;
        new Thread(() -> {
            try{
                loadProducts();
            }catch (IOException e){
                this.log("&cFailed to load products:");
                e.printStackTrace();
                this.plugin.addError(e);
            }
        }).start();
    }

    @Override
    public void onDisable() {
        products.clear();
        owners.clear();
        owners2.clear();
        bannedProducts.clear();
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
    public void onAction(Player player) {
        if(products.size() < 250){ // We know that the marketplace has at least 200 products
            this.getSuperUtils().sendMessage(player, this.getSettings().getPrefix() + L.PLUGIN_MARKETPLACE_STILL_LOADING);
        }

        new SongodaProductBrowser(player){
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
                if(!PluginMarketplace.i.isEnabled() || !PluginMarketplace.i.isRunning()) break;
                try{
                    int page = i+1;
                    PluginMarketplace.i.debug("Loading page " + (page) + ", " + (pages - page) + " page(s) left.");
                    CustomConnection connection = new ConnectionBuilder("https://songoda.com/api/v2/products?per_page="+per_page+"&page=" + page).connect();
                    JsonObject json = connection.getResponseJson();
                    if(json == null) continue;
                    int x = 0;
                    int productsArraySize = json.get("data").getAsJsonArray().size();
                    for(JsonElement el : json.get("data").getAsJsonArray()){
                        if(!PluginMarketplace.i.isEnabled() || !PluginMarketplace.i.isRunning()) break;
                        x++;
                        PluginMarketplace.i.debug("Loading product " + x + " in page " + page + ", " + (productsArraySize-x) + " product(s) left.");
                        try {
                            JsonObject productJson = el.getAsJsonObject();
                            if(productJson == null) continue;
                            if(productJson.get("approved").getAsBoolean() && (productJson.get("class") != null && !productJson.get("class").isJsonNull() && productJson.get("class").getAsString().equalsIgnoreCase("plugin")) && (productJson.get("status") != null && !productJson.get("status").isJsonNull() && productJson.get("status").getAsString().equalsIgnoreCase("approved")) && (productJson.get("user_id") != null && !productJson.get("user_id").isJsonNull() && productJson.get("user_id").getAsInt() != 12522) && (productJson.get("id") != null && !productJson.get("id").isJsonNull() && !bannedProducts.contains(productJson.get("id").getAsInt()))){
                                if(isNull(productJson, true)) continue;
                                int id = productJson.get("id").getAsInt();
                                if(products.containsKey(id)) continue;
                                String slug = productJson.get("slug").getAsString();
                                JsonArray versions = productJson.get("versions").getAsJsonArray();
                                if(versions.size() < 1) continue;
                                JsonObject version = versions.get(0).getAsJsonObject();
                                String owner;
                                int user_id = productJson.get("user_id").getAsInt();
                                try{
                                    if(productJson.get("team_id").isJsonNull()){
                                        if(!owners.containsKey(user_id)){
                                            CustomConnection profileConnection = new ConnectionBuilder("https://songoda.com/api/v2/profiles/id/" + user_id).connect();
                                            JsonObject profileJson = profileConnection.getResponseJson();
                                            if(profileJson == null) continue;
                                            JsonObject data = profileJson.get("data").getAsJsonObject();
                                            if(isNull(data, false)) continue;
                                            owners.put(user_id, data.get("name").getAsString());
                                        }
                                        owner = owners.get(user_id);
                                    }else{
                                        int team = productJson.get("team_id").getAsInt();
                                        if(team >= 1){
                                            if(!owners2.containsKey(team)){
                                                CustomConnection profileConnection = new ConnectionBuilder("https://songoda.com/api/v2/teams/id/" + team).connect();
                                                JsonObject profileJson = profileConnection.getResponseJson();
                                                if(profileJson == null) continue;
                                                JsonObject data = profileJson.get("data").getAsJsonObject();
                                                if(isNull(data, false)) continue;
                                                owners2.put(team, data.get("name").getAsString());
                                            }
                                            owner = owners2.get(team);
                                        }else{
                                            if(!owners.containsKey(user_id)){
                                                CustomConnection profileConnection = new ConnectionBuilder("https://songoda.com/api/v2/profiles/id/" + user_id).connect();
                                                JsonObject profileJson = profileConnection.getResponseJson();
                                                if(profileJson == null) continue;
                                                JsonObject data = profileJson.get("data").getAsJsonObject();
                                                if(isNull(data, false)) continue;
                                                owners.put(user_id, data.get("name").getAsString());
                                            }
                                            owner = owners.get(user_id);
                                        }
                                    }
                                }catch (IOException ignored){ continue; }
                                if(owner.isEmpty() || owner.equals(" ")){
                                    owner = "Unknown";
                                }

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
                                String tagline = productJson.get("tagline") == null || productJson.get("tagline").isJsonNull() ? null : productJson.get("tagline").getAsString();
                                LinkedList<String> supportedVersions = new LinkedList<>();
                                version.get("minecraft_version").getAsJsonArray().forEach(e-> supportedVersions.add(e.getAsString()));
                                products.put(id, new SongodaProduct(id, product, description, owner, url, price+"", currency, paymentMethod, filename, downloadUrl, views, downloads, tagline, String.join(", ", supportedVersions)));
                            }

                        }catch (Exception ignored){}
                    }

                    if(page - pages == 0) {
                        PluginMarketplace.i.debug("Loaded " + products.size() + " products");
                    }else{
                        try{
                            Thread.sleep(5000);
                        }catch (InterruptedException ignored) {}
                    }
                }catch (IOException ignored){ }
            }
        }
    }

    private static boolean isNull(JsonObject json, boolean product) {
        if(json == null) return true;
        if(json.isJsonNull()) return true;
        String[] keys = product ? new String[]{"id", "name", "slug", "user_id", "description", "versions"} : new String[]{"name"};
        for (String key : keys) {
            if(json.get(key) == null){
                PluginMarketplace.i.debug("Failed test on key " + key + " (null_obj)");
                return true;
            }

            if(json.get(key).isJsonNull()){
                PluginMarketplace.i.debug("Failed test on key " + key + " (json_null)");
                return true;
            }
        }

        return false;
    }

    private static int pages() throws IOException {
        CustomConnection connection = new ConnectionBuilder("https://songoda.com/api/v2/products?per_page=" + per_page).connect();
        JsonObject json = connection.getResponseJson();
        if(json == null){
            throw new NullPointerException("Failed to retrieve the data from the Songoda API: Null response.");
        }else{
            JsonObject meta = json.get("meta").getAsJsonObject();
            return meta.get("last_page").getAsInt();
        }
    }
}
package xyz.theprogramsrc.supermanager.modules.pluginmarketplace;

import java.io.IOException;
import java.util.Arrays;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.function.Consumer;

import org.bukkit.entity.Player;

import xyz.theprogramsrc.supercoreapi.global.networking.ConnectionBuilder;
import xyz.theprogramsrc.supercoreapi.global.networking.CustomConnection;
import xyz.theprogramsrc.supercoreapi.global.utils.Utils;
import xyz.theprogramsrc.supercoreapi.libs.google.gson.JsonArray;
import xyz.theprogramsrc.supercoreapi.libs.google.gson.JsonObject;
import xyz.theprogramsrc.supercoreapi.libs.xseries.XMaterial;
import xyz.theprogramsrc.supercoreapi.spigot.gui.objets.GuiAction;
import xyz.theprogramsrc.supercoreapi.spigot.items.SimpleItem;
import xyz.theprogramsrc.supercoreapi.spigot.utils.tasks.SpigotTasks;
import xyz.theprogramsrc.supermanager.L;
import xyz.theprogramsrc.supermanager.guis.MainGUI;
import xyz.theprogramsrc.supermanager.modules.pluginmarketplace.guis.SongodaProductBrowser;
import xyz.theprogramsrc.supermanager.modules.pluginmarketplace.objects.SongodaProduct;
import xyz.theprogramsrc.supermanager.objects.Module;

public class PluginMarketplace extends Module {

    private static LinkedHashMap<Integer, String> owners = new LinkedHashMap<>();
    private static LinkedHashMap<Integer, String> owners2 = new LinkedHashMap<>();
    public static LinkedHashMap<Integer, SongodaProduct> products = new LinkedHashMap<>();
    private static LinkedList<Integer> bannedProducts = new LinkedList<>(Utils.toList(545, 268, 425, 43, 594, 411, 438, 262, 403, 354, 388, 450, 529, 416, 377, 511, 324, 319));;
    private static final int perPage = 50;
    private static long lastCheck = 0l;
    private static PluginMarketplace i;
    private static boolean loading = false;

    @Override
    public void onEnable() {
        i = this;
        loadProducts(false);
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
                .setDisplayName("&c" + L.PLUGIN_MARKETPLACE_NAME)
                .setLore(
                        "&7",
                        "&7" + L.PLUGIN_MARKETPLACE_LORE
                );
    }

    @Override
    public void onAction(Player player) {
        if(loading){
            this.getSuperUtils().sendMessage(player, this.getSettings().getPrefix() + L.PLUGIN_MARKETPLACE_STILL_LOADING);
        }

        new SongodaProductBrowser(player){
            @Override
            public void onBack(GuiAction clickAction) {
                new MainGUI(clickAction.player);
            }
        };
    }

    public static void loadProducts(boolean overrideCache) {
        SpigotTasks tasks = PluginMarketplace.i.getSpigotTasks();
        tasks.runAsyncTask(() -> {
            try{
                loading = true;
                // Only load if overrideCache is true or if the cache is empty or if lastCheck was more than 5 minutes ago
                if((overrideCache || products.isEmpty() || System.currentTimeMillis() - lastCheck > 300000) && i.isEnabled() && i.isRunning()){
                    lastCheck = System.currentTimeMillis();
                    int pages = PluginMarketplace.pages();
                    products.clear();
                    for(int i = 1; i <= pages; i++){
                        final int page = i;
                        PluginMarketplace.i.debug("Loading page " + i + ", " + (pages - i) + " page(s) left.");
                        tasks.runTaskLater((Utils.toTicks(5) * i), () -> tasks.runAsyncTask(() -> loadPage(page)));
                        if(i - pages == 0) {
                            PluginMarketplace.i.debug("Loaded " + products.size() + " products");
                        }
                    }
                }
                loading = false;
            }catch(Exception e){
                if(i.isDebugEnabled()) e.printStackTrace();
            }
        });
    }

    private static void loadPage(int page){
        Consumer<String> debug = msg -> PluginMarketplace.i.debug(msg);
        try{
            CustomConnection connection = new ConnectionBuilder("https://songoda.com/api/v2/products?page=" + page + "&per_page=" + perPage).connect();
            JsonObject json = connection.getResponseJson();
            if(json == null){ 
                debug.accept("Page " + page + " returned null response!");
                return;
            }

            JsonArray products = json.getAsJsonArray("data");
            for(int i = 0; i < products.size(); i++){
                debug.accept("Loading product " + (i + 1) + " in page " + page + ", " + (products.size() - i) + " product(s) left.");
                JsonObject productJson = products.get(i).getAsJsonObject();
                if(productJson != null) {
                    String[] productJsonKeysTest = testKeysInJson(productJson, "id", "name", "slug", "user_id", "description", "versions");
                    if(productJsonKeysTest.length > 0){
                        debug.accept("Product " + productJson.get("id").getAsString() + " has invalid keys: " + String.join(", ", productJsonKeysTest));
                    }else{
                        int userId = productJson.get("user_id").getAsInt();
                        int productId = productJson.get("id").getAsInt();
                        if(isProductValid(productJson, userId, productId)){
                            String slug = productJson.get("slug").getAsString();
                            JsonObject version = productJson.getAsJsonArray("versions").get(0).getAsJsonObject();
                            String owner = extractOwner(productJson, userId);
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
                            PluginMarketplace.products.put(productId, new SongodaProduct(productId, product, description, owner, url, price+"", currency, paymentMethod, filename, downloadUrl, views, downloads, tagline, String.join(", ", supportedVersions)));
                        }
                    }
                }
            }
        }catch (Exception exception){
            if(i.plugin.getPluginDataStorage().isDebugEnabled()) exception.printStackTrace();
        }
    }

    private static String extractOwner(JsonObject productJson, int userId) {
        try{
            if(productJson.get("team_id").isJsonNull()){
                return getUserNameFromId(userId);
            }else{
                int teamId = productJson.get("team_id").getAsInt();
                if(teamId != 0){
                    return getTeamNameFromId(teamId);
                }else{
                    return getUserNameFromId(userId);
                }
            }
        }catch(Exception e){
            if(i.isDebugEnabled()) e.printStackTrace();
        }

        return "Unknown";
    }

    private static String getTeamNameFromId(int teamId){
        try{
            if(!owners2.containsKey(teamId)){
                CustomConnection profileConnection = new ConnectionBuilder("https://songoda.com/api/v2/teams/id/" + teamId).connect();
                JsonObject teamJson = profileConnection.getResponseJson();
                if(teamJson != null){
                    JsonObject data = teamJson.get("data").getAsJsonObject();
                    String[] testKeys = testKeysInJson(data, "name");
                    if(testKeys.length > 0){
                        i.debug("Team " + teamId + " has invalid keys: " + String.join(", ", testKeys));
                    }else{
                        String name = data.get("name").getAsString();
                        owners2.put(teamId, name);
                        return name;
                    }
                }
            }else{
                return owners2.get(teamId);
            }
        }catch(Exception e){
            if(i.isDebugEnabled()) e.printStackTrace();
        }

        return "Unknown";
    }

    private static String getUserNameFromId(int userId){
        try{
            if(!owners.containsKey(userId)){
                CustomConnection profileConnection = new ConnectionBuilder("https://songoda.com/api/v2/profiles/id/" + userId).connect();
                JsonObject profileJson = profileConnection.getResponseJson();
                if(profileJson == null) return "Unknown";
                JsonObject data = profileJson.get("data").getAsJsonObject();
                String[] testKeys = testKeysInJson(data, "name");
                if(testKeys.length > 0){
                    i.debug("Profile " + userId + " has invalid keys: " + String.join(", ", testKeys));
                    return "Unknown";
                }else{
                    String name = data.get("name").getAsString();
                    owners.put(userId, name);
                    return name;
                }
            }

            return owners.get(userId);
        }catch(Exception e){
            if(i.isDebugEnabled()) e.printStackTrace();
        }
        return "Unknown";
    }

    private static boolean isProductValid(JsonObject productJson, int userId, int productId) {
        return userId != 12522 && productJson.get("enabled").getAsBoolean() && productJson.get("approved").getAsBoolean() && productJson.get("status").getAsString().equalsIgnoreCase("approved") && !PluginMarketplace.products.containsKey(productId) && !bannedProducts.contains(productId) && productJson.getAsJsonArray("versions").size() > 0;
    }

    private static String[] testKeysInJson(JsonObject json, String... keys){
        if(json == null) return new String[]{"Null json object!"};
        if(json.isJsonNull()) return new String[]{"Json is null!"};
        return Arrays.stream(keys).filter(key -> !json.has(key) || json.get(key) == null || json.get(key).isJsonNull()).map(key -> {
            if(!json.has(key)){
                return key + " is not present!";
            }

            if(json.get(key) == null){
                return key + " is null!";
            }

            if(json.get(key).isJsonNull()){
                return key + " is Json Null!";
            }

            return null;
        }).filter(str -> str != null && Utils.nonNull(str)).toArray(String[]::new);
    }

    private static int pages(){
        try{
            CustomConnection connection = new ConnectionBuilder("https://songoda.com/api/v2/products?per_page=" + perPage).connect();
            JsonObject json = connection.getResponseJson();
            if(json == null){
                throw new NullPointerException("Failed to retrieve the data from the Songoda API: Null response.");
            }else{
                JsonObject meta = json.get("meta").getAsJsonObject();
                return meta.get("last_page").getAsInt();
            }
        }catch (IOException ex){
            PluginMarketplace.i.debug("Failed to retrieve the data from the Songoda API: " + ex.getMessage());
            return 0;
        }
    }
}
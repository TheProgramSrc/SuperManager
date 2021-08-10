package xyz.theprogramsrc.supermanager.modules.pluginmanager.apiwrappers;

import java.io.IOException;
import java.util.LinkedHashMap;
import java.util.LinkedList;

import xyz.theprogramsrc.supercoreapi.global.networking.ConnectionBuilder;
import xyz.theprogramsrc.supercoreapi.global.networking.CustomConnection;
import xyz.theprogramsrc.supercoreapi.libs.google.gson.JsonArray;
import xyz.theprogramsrc.supercoreapi.libs.google.gson.JsonElement;
import xyz.theprogramsrc.supercoreapi.libs.google.gson.JsonObject;
import xyz.theprogramsrc.supermanager.modules.pluginmanager.apiwrappers.objects.Product;
import xyz.theprogramsrc.supermanager.modules.pluginmanager.apiwrappers.objects.Version;

public class SongodaAPI {

    private static final String base = "https://songoda.com/api/v2/";

    private static final LinkedHashMap<String, Product> CACHE = new LinkedHashMap<String, Product>();

    public static Product getProduct(String id) throws IOException{
        if(CACHE.containsKey(id)) return CACHE.get(id);
        String url = SongodaAPI.base + "products/id/" + id;
        CustomConnection connection = ConnectionBuilder.connect(url);
        if(connection.isResponseNotNull() && connection.isValidResponse()){
            JsonObject json = connection.getResponseJson().get("data").getAsJsonObject();
            String name = json.get("name").getAsString();
            Product product = new Product(id, name, getProductVersions(id));
            CACHE.put(id, product);
            return product;
        }
        return null;
    }

    public static Version[] getProductVersions(String productId) throws IOException{
        String url = SongodaAPI.base + "products/id/" + productId;
        CustomConnection connection = ConnectionBuilder.connect(url);
        if(connection.isResponseNotNull() && connection.isValidResponse()){
            LinkedList<Version> versions = new LinkedList<>();
            JsonArray array = connection.getResponseJson().get("data").getAsJsonObject().get("versions").getAsJsonArray();
            for(JsonElement el : array) {
                JsonObject json = el.getAsJsonObject();
                String id = json.get("id").getAsInt() + "";
                String name = json.get("version").getAsString();
                long createdAt = json.get("created_at").getAsInt() * 1000L;
                int downloads = json.get("downloads").getAsInt(); 
                String downloadUrl = json.get("url").getAsString().replace("/version/", "/download/");
                String fileName = json.get("filename").getAsString();
                versions.add(new Version(productId, id, name, createdAt, downloads, downloadUrl, fileName));
            }

            return versions.toArray(new Version[0]);
        }

        return new Version[0];
    }
    
}

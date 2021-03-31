package xyz.theprogramsrc.supermanager.modules.usermanager.objects;

import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;
import xyz.theprogramsrc.supercoreapi.global.utils.Utils;
import xyz.theprogramsrc.supercoreapi.google.gson.JsonArray;
import xyz.theprogramsrc.supercoreapi.google.gson.JsonObject;
import xyz.theprogramsrc.supercoreapi.google.gson.JsonParser;
import xyz.theprogramsrc.supercoreapi.spigot.utils.skintexture.SkinTexture;

import java.util.LinkedHashMap;
import java.util.UUID;

public class User {

    private final UUID uuid;
    private final String name;
    private final SkinTexture skinTexture;
    private final LinkedHashMap<String, Object> data;

    public User(UUID uuid, String name, SkinTexture skinTexture){
        this.uuid = uuid;
        this.name = name;
        this.skinTexture = skinTexture;
        this.data = new LinkedHashMap<>();
    }

    public boolean getDataAsBoolean(String key){
        return ((boolean) this.getData(key));
    }

    public Number getDataAsNumber(String key){
        return ((Number) this.getData(key));
    }

    public String getDataAsString(String key){
        return this.getData(key).toString();
    }

    public User setData(String key, Object value){
        this.data.put(key, value);
        return this;
    }

    public boolean hasData(String key){
        return this.data.containsKey(key);
    }

    public Object getData(String key){
        return this.data.get(key);
    }

    public UUID getUUID() {
        return this.uuid;
    }

    public String getName(){
        return this.name;
    }

    public Player getPlayer(){
        return this.getOfflinePlayer().getPlayer();
    }

    public OfflinePlayer getOfflinePlayer(){
        return Bukkit.getOfflinePlayer(this.getUUID());
    }

    public boolean isOnline(){
        return this.getOfflinePlayer().isOnline();
    }

    public boolean isAdmin(){
        return this.getPlayer().hasPermission("supermanager.admin");
    }

    public SkinTexture getSkin(){
        return this.skinTexture;
    }

    @Override
    public String toString() {
        JsonObject json = new JsonObject();
        json.addProperty("name", this.name);
        json.addProperty("uuid", this.uuid.toString());
        if(this.skinTexture != null) json.addProperty("skin", this.skinTexture.getUrl());
        JsonArray data = new JsonArray();
        this.data.forEach((key, value) ->{
            JsonObject json1 = new JsonObject();
            json1.addProperty("key", key);
            if(value instanceof Boolean){
                json1.addProperty("type", "boolean");
                json1.addProperty("value", ((boolean)value));
            }else if(value instanceof Number){
                json1.addProperty("type", "number");
                json1.addProperty("value", ((Number)value));
            }else{
                json1.addProperty("type", "string");
                json1.addProperty("value", value.toString());
            }

            data.add(json1);
        });
        json.add("data", data);
        return json.toString();
    }

    public static User fromJSON(String data){
        JsonObject json = new JsonParser().parse(data).getAsJsonObject();

        final User user = new User(
                UUID.fromString(json.get("uuid").getAsString()),
                json.get("name").getAsString(),
                json.has("skin") ? new SkinTexture(json.get("skin").getAsString()) : null
        );

        json.get("data").getAsJsonArray().forEach(e-> {
            JsonObject j1 = e.getAsJsonObject();
            String key = j1.get("key").getAsString();
            String type = j1.get("type").getAsString();
            Object value;
            if(type.equalsIgnoreCase("boolean")){
                value = j1.get("value").getAsBoolean();
            }else if(type.equalsIgnoreCase("number")){
                value = j1.get("value").getAsNumber();
            }else{
                value = j1.get("value").getAsString();
            }
            user.setData(key, value);
        });

        return user;
    }

    public static User create(Player player){
        return new User(
                player.getUniqueId(),
                player.getName(),
                Utils.isConnected() ? SkinTexture.fromPlayer(player) : null
        );
    }
}

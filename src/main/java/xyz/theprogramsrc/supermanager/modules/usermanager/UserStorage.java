package xyz.theprogramsrc.supermanager.modules.usermanager;

import java.io.File;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.UUID;

import xyz.theprogramsrc.supercoreapi.global.files.yml.YMLConfig;
import xyz.theprogramsrc.supercoreapi.global.utils.Utils;
import xyz.theprogramsrc.supercoreapi.spigot.utils.skintexture.SkinTexture;
import xyz.theprogramsrc.supermanager.modules.usermanager.objects.User;

public class UserStorage extends YMLConfig {

    private final LinkedHashMap<UUID, User> CACHE;

    public UserStorage(File file) {
        super(file);
        this.CACHE = new LinkedHashMap<>();
    }

    public void save(User user){
        String path = "Users." + user.getUUID();
        this.set(path + ".Name", user.getName());
        this.set(path + ".Skin", user.getSkin() != null ? user.getSkin().toString() : null);
        this.set(path + ".Data", Utils.encodeBase64(user.dataToString()));
    }

    public boolean exists(UUID uuid){
        return this.contains("Users." + uuid);
    }

    public User get(UUID uuid){
        if(!this.CACHE.containsKey(uuid)) {
            String path = "Users." + uuid;
            String name = this.getString(path + ".Name");
            SkinTexture skin = this.contains(path + ".Skin") ? new SkinTexture(this.getString(path + ".Skin")) : null;
            User user = new User(uuid, name, skin);
            if(this.contains(path + ".Data")){
                String data = Utils.decodeBase64(this.getString(path + ".Data"));
                user.loadDataFromString(data);
            }
            this.CACHE.put(uuid, user);
        }

        return this.CACHE.containsKey(uuid) ? this.CACHE.get(uuid) : this.get(uuid);
    }

    public User[] get(){
        LinkedList<User> users = new LinkedList<>();
        if(this.getSection("Users") != null){
            this.getSection("Users").getKeys(false).forEach(key -> users.add(this.get(UUID.fromString(key))));
        }
        return users.toArray(new User[0]);
    }
}

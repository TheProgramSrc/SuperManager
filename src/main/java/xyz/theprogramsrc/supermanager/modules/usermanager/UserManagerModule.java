package xyz.theprogramsrc.supermanager.modules.usermanager;

import xyz.theprogramsrc.supercoreapi.global.files.JsonConfig;
import xyz.theprogramsrc.supercoreapi.global.storage.universal.UniversalStorage;
import xyz.theprogramsrc.supercoreapi.spigot.guis.action.ClickAction;
import xyz.theprogramsrc.supercoreapi.spigot.items.SimpleItem;
import xyz.theprogramsrc.supercoreapi.spigot.utils.xseries.XMaterial;
import xyz.theprogramsrc.supermanager.modules.usermanager.listeners.PlayerListener;
import xyz.theprogramsrc.supermanager.modules.usermanager.objects.User;
import xyz.theprogramsrc.supermanager.objects.Module;

import java.util.Arrays;

public class UserManagerModule extends Module {

    private UserStorage userStorage;

    @Override
    public void onEnable() {
        super.onEnable();
        this.userStorage = new UserStorage(this.plugin, UniversalStorage.database());
        new PlayerListener(this.userStorage);
    }

    @Override
    public void onDisable() {
        super.onDisable();
    }

    @Override
    public String getDisplay() {
        return "User Manager";
    }

    @Override
    public String getIdentifier() {
        return "user_manager";
    }

    @Override
    public SimpleItem getDisplayItem() {
        return new SimpleItem(XMaterial.PLAYER_HEAD);
    }

    @Override
    public void onAction(ClickAction clickAction) {

    }

    public UserStorage getUserStorage() {
        return userStorage;
    }

    public User[] getUsers(){
        return this.userStorage.get();
    }

    public User[] getOnlineUsers(){
        return Arrays.stream(this.getUsers()).filter(User::isOnline).toArray(User[]::new);
    }
}

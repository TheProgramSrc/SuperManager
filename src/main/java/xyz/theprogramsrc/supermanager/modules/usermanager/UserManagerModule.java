package xyz.theprogramsrc.supermanager.modules.usermanager;

import org.bukkit.entity.Player;
import xyz.theprogramsrc.supercoreapi.libs.xseries.XMaterial;
import xyz.theprogramsrc.supercoreapi.spigot.guis.action.ClickAction;
import xyz.theprogramsrc.supercoreapi.spigot.items.SimpleItem;
import xyz.theprogramsrc.supermanager.L;
import xyz.theprogramsrc.supermanager.guis.MainGUI;
import xyz.theprogramsrc.supermanager.modules.usermanager.guis.UserBrowser;
import xyz.theprogramsrc.supermanager.modules.usermanager.listeners.PlayerListener;
import xyz.theprogramsrc.supermanager.modules.usermanager.objects.User;
import xyz.theprogramsrc.supermanager.objects.Module;

import java.io.File;
import java.util.Arrays;

public class UserManagerModule extends Module {

    private UserStorage userStorage;

    @Override
    public void onEnable() {
        super.onEnable();
        this.userStorage = new UserStorage(new File(this.getModuleFolder(), "Users.yml"));
        new PlayerListener(this.userStorage);
    }

    @Override
    public void onDisable() {
        super.onDisable();
    }

    @Override
    public String getDisplay() {
        return L.USER_MANAGER_DISPLAY.toString();
    }

    @Override
    public String getIdentifier() {
        return "user_manager";
    }

    @Override
    public SimpleItem getDisplayItem() {
        return new SimpleItem(XMaterial.PLAYER_HEAD)
                .setDisplayName("&a" + L.USER_MANAGER_NAME)
                .setLore(
                        "&7",
                        "&7" + L.USER_MANAGER_LORE
                );
    }

    @Override
    public void onAction(Player player) {
        new UserBrowser(player, this){
            @Override
            public void onBack(ClickAction clickAction) {
                new MainGUI(clickAction.getPlayer());
            }
        };
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

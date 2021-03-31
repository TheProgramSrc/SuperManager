package xyz.theprogramsrc.supermanager.modules.usermanager.guis;

import org.bukkit.entity.Player;
import xyz.theprogramsrc.supercoreapi.global.utils.Utils;
import xyz.theprogramsrc.supercoreapi.spigot.guis.BrowserGUI;
import xyz.theprogramsrc.supercoreapi.spigot.guis.GUIButton;
import xyz.theprogramsrc.supercoreapi.spigot.items.SimpleItem;
import xyz.theprogramsrc.supercoreapi.spigot.utils.xseries.XMaterial;
import xyz.theprogramsrc.supermanager.L;
import xyz.theprogramsrc.supermanager.modules.usermanager.UserManagerModule;
import xyz.theprogramsrc.supermanager.modules.usermanager.objects.User;

import java.util.LinkedList;

public class UserBrowser extends BrowserGUI<User> {

    private final UserManagerModule userManagerModule;
    private boolean onlineOnly = false;

    public UserBrowser(Player player, UserManagerModule userManagerModule) {
        super(player);
        this.userManagerModule = userManagerModule;
        this.open();
    }

    @Override
    protected GUIButton[] getButtons() {
        LinkedList<GUIButton> buttons = new LinkedList<>(Utils.toList(super.getButtons()));
        SimpleItem item = new SimpleItem(this.onlineOnly ? XMaterial.ENDER_EYE : XMaterial.ENDER_PEARL)
                .setDisplayName("&aToggle Online Only")
                .setLore(
                        "&7",
                        "&7Click to toggle Online Only Filter"
                );
        buttons.add(new GUIButton(47, item, a-> {
            this.onlineOnly = !this.onlineOnly;
            this.open();
        }));
        return buttons.toArray(new GUIButton[0]);
    }

    @Override
    public User[] getObjects() {
        return this.onlineOnly ? this.userManagerModule.getOnlineUsers() : this.userManagerModule.getUsers();
    }

    @Override
    public GUIButton getButton(User user) {
        SimpleItem item = new SimpleItem(XMaterial.PLAYER_HEAD)
                .setDisplayName("&a" + L.USER_MANAGER_BROWSER_ITEM_NAME)
                .setLore(
                        "&7",
                        "&7" + L.USER_MANAGER_BROWSER_ITEM_LORE
                ).setSkin(user.getSkin())
                .addPlaceholder("{UserName}", user.getName());
        return new GUIButton(item, a->{

        });
    }

    @Override
    protected String getTitle() {
        return L.USER_MANAGER_BROWSER_TITLE.toString();
    }
}

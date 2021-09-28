package xyz.theprogramsrc.supermanager.modules.usermanager.guis;

import org.bukkit.entity.Player;

import xyz.theprogramsrc.supercoreapi.libs.xseries.XMaterial;
import xyz.theprogramsrc.supercoreapi.spigot.gui.BrowserGui;
import xyz.theprogramsrc.supercoreapi.spigot.gui.objets.GuiAction;
import xyz.theprogramsrc.supercoreapi.spigot.gui.objets.GuiEntry;
import xyz.theprogramsrc.supercoreapi.spigot.gui.objets.GuiModel;
import xyz.theprogramsrc.supercoreapi.spigot.gui.objets.GuiTitle;
import xyz.theprogramsrc.supercoreapi.spigot.items.SimpleItem;
import xyz.theprogramsrc.supermanager.L;
import xyz.theprogramsrc.supermanager.modules.usermanager.UserManagerModule;
import xyz.theprogramsrc.supermanager.modules.usermanager.objects.User;

public class UserBrowser extends BrowserGui<User> {

    private final UserManagerModule userManagerModule;
    private boolean onlineOnly = false;

    public UserBrowser(Player player, UserManagerModule userManagerModule) {
        super(player, false);
        this.userManagerModule = userManagerModule;
        this.backEnabled = true;
        this.open();
    }

    @Override
    public void onBuild(GuiModel m) {
        super.onBuild(m);
        SimpleItem item = new SimpleItem(this.onlineOnly ? XMaterial.ENDER_EYE : XMaterial.ENDER_PEARL)
                .setDisplayName("&a" + (this.onlineOnly ? L.USER_MANAGER_BROWSER_ALL_ONLINE_NAME : L.USER_MANAGER_BROWSER_ONLINE_ONLY_NAME))
                .setLore(
                        "&7",
                        "&7" + (this.onlineOnly ? L.USER_MANAGER_BROWSER_ALL_ONLINE_LORE : L.USER_MANAGER_BROWSER_ONLINE_ONLY_LORE)
                );
        m.setButton(47, new GuiEntry(item, a->{
            this.onlineOnly = !this.onlineOnly;
            this.open();
        }));
    }

    @Override
    public User[] getObjects() {
        return this.onlineOnly ? this.userManagerModule.getOnlineUsers() : this.userManagerModule.getUsers();
    }

    @Override
    public String[] getSearchTags(User u) {
        return new String[]{
            u.getName(),
            u.getUUID().toString(),
        };
    }

    @Override
    public GuiEntry getEntry(User user) {
        SimpleItem item = new SimpleItem(XMaterial.PLAYER_HEAD)
                .setDisplayName("&a" + L.USER_MANAGER_BROWSER_ITEM_NAME)
                .setLore(
                        "&7",
                        "&7" + L.USER_MANAGER_BROWSER_ITEM_LORE
                ).setSkin(user.getSkin())
                .addPlaceholder("{UserName}", user.getName());
        return new GuiEntry(item, a-> new UserView(a.player, user, this.userManagerModule.getUserStorage()){
            @Override
            public void onBack(GuiAction clickAction) {
                UserBrowser.this.open();
            }
        });
    }

    @Override
    public GuiTitle getTitle() {
        return GuiTitle.of(L.USER_MANAGER_BROWSER_TITLE.toString());
    }
}

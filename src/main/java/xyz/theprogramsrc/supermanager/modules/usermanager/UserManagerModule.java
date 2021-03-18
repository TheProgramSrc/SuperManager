package xyz.theprogramsrc.supermanager.modules.usermanager;

import xyz.theprogramsrc.supercoreapi.spigot.guis.action.ClickAction;
import xyz.theprogramsrc.supercoreapi.spigot.items.SimpleItem;
import xyz.theprogramsrc.supercoreapi.spigot.utils.xseries.XMaterial;
import xyz.theprogramsrc.supermanager.objects.Module;

public class UserManagerModule extends Module {

    @Override
    public void onEnable() {
        super.onEnable();
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
}

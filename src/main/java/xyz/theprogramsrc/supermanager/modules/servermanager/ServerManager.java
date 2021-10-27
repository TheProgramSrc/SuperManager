package xyz.theprogramsrc.supermanager.modules.servermanager;

import org.bukkit.entity.Player;

import xyz.theprogramsrc.supercoreapi.global.files.yml.YMLConfig;
import xyz.theprogramsrc.supercoreapi.libs.xseries.XMaterial;
import xyz.theprogramsrc.supercoreapi.spigot.items.SimpleItem;
import xyz.theprogramsrc.supermanager.L;
import xyz.theprogramsrc.supermanager.guis.MainGUI;
import xyz.theprogramsrc.supermanager.modules.servermanager.guis.ServerManagerGUI;
import xyz.theprogramsrc.supermanager.modules.servermanager.listeners.ServerManagerListener;
import xyz.theprogramsrc.supermanager.objects.Module;

public class ServerManager extends Module {

    public static ServerManager i;
    public YMLConfig cfg;

    @Override
    public void onEnable() {
        i = this;
        this.cfg = new YMLConfig(this.getModuleFolder(), "ServerManager.yml");
        new ServerManagerListener();
    }

    @Override
    public String getDisplay() {
        return L.SERVER_MANAGER_DISPLAY.toString();
    }

    @Override
    public String getIdentifier() {
        return "server_manager";
    }

    @Override
    public SimpleItem getDisplayItem() {
        return new SimpleItem(XMaterial.COMMAND_BLOCK)
            .setDisplayName("&a" + L.SERVER_MANAGER_NAME)
            .setLore(
                "&7",
                "&7" + L.SERVER_MANAGER_LORE
            );
    }

    @Override
    public void onAction(Player player) {
        new ServerManagerGUI(player, a -> new MainGUI(a.player));
    }
   
}
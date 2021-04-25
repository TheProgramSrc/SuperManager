package xyz.theprogramsrc.supermanager.modules.filemanager;

import org.bukkit.entity.Player;
import xyz.theprogramsrc.supercoreapi.spigot.guis.action.ClickAction;
import xyz.theprogramsrc.supercoreapi.spigot.items.SimpleItem;
import xyz.theprogramsrc.supercoreapi.spigot.utils.xseries.XMaterial;
import xyz.theprogramsrc.supermanager.L;
import xyz.theprogramsrc.supermanager.guis.MainGUI;
import xyz.theprogramsrc.supermanager.modules.filemanager.guis.FileBrowserGUI;
import xyz.theprogramsrc.supermanager.objects.Module;

import java.io.File;

public class FileManager extends Module {

    @Override
    public String getDisplay() {
        return L.FILE_MANAGER_DISPLAY.toString();
    }

    @Override
    public String getIdentifier() {
        return "file_manager";
    }

    @Override
    public SimpleItem getDisplayItem() {
        return new SimpleItem(XMaterial.WRITABLE_BOOK)
                .setDisplayName("&a" + L.FILE_MANAGER_NAME)
                .setLore(
                        "&7",
                        "&7" + L.FILE_MANAGER_LORE
                );
    }

    @Override
    public void onAction(Player player) {
        new FileBrowserGUI(player.getPlayer(), new File(".")){
            @Override
            public void onBack(ClickAction clickAction) {
                new MainGUI(player.getPlayer());
            }
        };
    }
}

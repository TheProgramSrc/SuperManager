package xyz.theprogramsrc.supermanager.modules.worldmanager;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import xyz.theprogramsrc.supercoreapi.spigot.guis.action.ClickAction;
import xyz.theprogramsrc.supercoreapi.spigot.items.SimpleItem;
import xyz.theprogramsrc.supercoreapi.spigot.utils.xseries.XMaterial;
import xyz.theprogramsrc.supermanager.L;
import xyz.theprogramsrc.supermanager.guis.MainGUI;
import xyz.theprogramsrc.supermanager.modules.worldmanager.guis.WorldBrowserGUI;
import xyz.theprogramsrc.supermanager.modules.worldmanager.objects.SWorld;
import xyz.theprogramsrc.supermanager.objects.Module;

import java.util.LinkedHashMap;

public class WorldManager extends Module {

    private LinkedHashMap<String, SWorld> worlds;

    @Override
    public void onEnable() {
        this.worlds = new LinkedHashMap<>();
        Bukkit.getWorlds().stream().map(w-> new SWorld(w.getName(), this)).forEach(w-> {
            this.worlds.put(w.getName(), w);
        });
    }

    @Override
    public String getDisplay() {
        return L.WORLD_MANAGER_DISPLAY.toString();
    }

    @Override
    public String getIdentifier() {
        return "world_manager";
    }

    @Override
    public SimpleItem getDisplayItem() {
        return new SimpleItem(XMaterial.OAK_SAPLING)
                .setDisplayName("&a" + L.WORLD_MANAGER_NAME)
                .setLore(
                        "&7",
                        "&7" + L.WORLD_MANAGER_LORE
                );
    }

    @Override
    public void onAction(Player player) {
        new WorldBrowserGUI(player, this){
            @Override
            public void onBack(ClickAction clickAction){
                new MainGUI(clickAction.getPlayer());
            }
        };
    }

    public LinkedHashMap<String, SWorld> getWorlds() {
        return worlds;
    }
}

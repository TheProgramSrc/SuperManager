package xyz.theprogramsrc.supermanager.modules.worldmanager.guis;

import org.bukkit.World;
import org.bukkit.entity.Player;

import xyz.theprogramsrc.supercoreapi.libs.xseries.XMaterial;
import xyz.theprogramsrc.supercoreapi.spigot.guis.BrowserGUI;
import xyz.theprogramsrc.supercoreapi.spigot.guis.GUIButton;
import xyz.theprogramsrc.supercoreapi.spigot.items.SimpleItem;
import xyz.theprogramsrc.supermanager.L;
import xyz.theprogramsrc.supermanager.modules.worldmanager.WorldManager;
import xyz.theprogramsrc.supermanager.modules.worldmanager.objects.SWorld;

public class WorldBrowserGUI extends BrowserGUI<SWorld> {

    private final WorldManager worldManager;

    public WorldBrowserGUI(Player player, WorldManager worldManager) {
        super(player);
        this.worldManager = worldManager;
        this.backEnabled = true;
        this.open();
    }

    @Override
    public SWorld[] getObjects() {
        return this.worldManager.getWorlds().values().toArray(new SWorld[0]);
    }

    @Override
    public GUIButton getButton(SWorld sWorld) {
        World.Environment env = sWorld.getEnvironment();
        XMaterial material;
        if(env == World.Environment.NORMAL){
            material = XMaterial.GRASS_BLOCK;
        }else if(env == World.Environment.NETHER){
            material = XMaterial.NETHERRACK;
        }else if(env == World.Environment.THE_END){
            material = XMaterial.END_STONE;
        }else{
            material = XMaterial.BEDROCK;
        }

        SimpleItem item = new SimpleItem(material)
                .setDisplayName("&a" + L.WORLD_MANAGER_BROWSER_GUI_ITEM_NAME)
                .setLore(
                        "&7",
                        "&7" + L.WORLD_MANAGER_BROWSER_GUI_ITEM_LORE_MANAGE,
                        "&7" + L.WORLD_MANAGER_BROWSER_GUI_ITEM_LORE_LAST_BACKUP
                )
                .addPlaceholder("{WorldName}", sWorld.getName())
                .addPlaceholder("{LastBackup}", sWorld.getLastBackupTime());
        return new GUIButton(item, a->{
            new WorldViewGUI(a.getPlayer(), sWorld, a1-> this.open());
        });
    }

    @Override
    protected String getTitle() {
        return L.WORLD_MANAGER_BROWSER_GUI_TITLE.options().placeholder("{WorldAmount}", this.getObjects().length+"").get();
    }
}

package xyz.theprogramsrc.supermanager.modules.worldmanager.guis;

import org.bukkit.World;
import org.bukkit.entity.Player;

import xyz.theprogramsrc.supercoreapi.libs.xseries.XMaterial;
import xyz.theprogramsrc.supercoreapi.spigot.gui.BrowserGui;
import xyz.theprogramsrc.supercoreapi.spigot.gui.objets.GuiEntry;
import xyz.theprogramsrc.supercoreapi.spigot.gui.objets.GuiTitle;
import xyz.theprogramsrc.supercoreapi.spigot.items.SimpleItem;
import xyz.theprogramsrc.supermanager.L;
import xyz.theprogramsrc.supermanager.modules.worldmanager.WorldManager;
import xyz.theprogramsrc.supermanager.modules.worldmanager.objects.SWorld;

public class WorldBrowserGUI extends BrowserGui<SWorld> {

    private final WorldManager worldManager;

    public WorldBrowserGUI(Player player, WorldManager worldManager) {
        super(player, false);
        this.worldManager = worldManager;
        this.backEnabled = true;
        this.open();
    }

    @Override
    public SWorld[] getObjects() {
        return this.worldManager.getWorlds().values().toArray(new SWorld[0]);
    }

    @Override
    public String[] getSearchTags(SWorld w) {
        return new String[]{w.getName()};
    }

    @Override
    public GuiEntry getEntry(SWorld sWorld) {
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
        return new GuiEntry(item, a->{
            new WorldViewGUI(a.player, sWorld, a1-> this.open());
        });
    }

    @Override
    public GuiTitle getTitle() {
        return GuiTitle.of(L.WORLD_MANAGER_BROWSER_GUI_TITLE.options().placeholder("{WorldAmount}", this.getObjects().length+"").get());
    }
}

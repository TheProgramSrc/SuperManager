package xyz.theprogramsrc.supermanager.modules.worldmanager.guis;

import org.bukkit.entity.Player;
import xyz.theprogramsrc.supercoreapi.Recall;
import xyz.theprogramsrc.supercoreapi.libs.xseries.XMaterial;
import xyz.theprogramsrc.supercoreapi.spigot.guis.GUI;
import xyz.theprogramsrc.supercoreapi.spigot.guis.GUIButton;
import xyz.theprogramsrc.supercoreapi.spigot.guis.action.ClickAction;
import xyz.theprogramsrc.supercoreapi.spigot.guis.objects.GUIRows;
import xyz.theprogramsrc.supercoreapi.spigot.items.SimpleItem;
import xyz.theprogramsrc.supermanager.L;
import xyz.theprogramsrc.supermanager.modules.worldmanager.objects.SWorld;

public class WorldViewGUI extends GUI {

    private final SWorld sWorld;
    private final Recall<ClickAction> onBack;

    public WorldViewGUI(Player player, SWorld sWorld, Recall<ClickAction> onBack) {
        super(player);
        this.sWorld = sWorld;
        this.onBack = onBack;
        this.open();
    }

    @Override
    protected GUIRows getRows() {
        return GUIRows.ONE;
    }

    @Override
    protected String getTitle() {
        return L.WORLD_MANAGER_WORLD_VIEW_GUI_TITLE.options().placeholder("{WorldName}", this.sWorld.getName()).toString();
    }

    @Override
    protected GUIButton[] getButtons() {
        return new GUIButton[]{
                new GUIButton(this.getRows().getSize()-1, this.getPreloadedItems().getBackItem(), this.onBack::run),
                this.getBackupButton()
        };
    }

    private GUIButton getBackupButton(){
        SimpleItem item = new SimpleItem(XMaterial.ANVIL)
                .setDisplayName("&a" + L.WORLD_MANAGER_WORLD_VIEW_GUI_CREATE_BACKUP_NAME)
                .setLore(
                        "&7",
                        "&7" + L.WORLD_MANAGER_WORLD_VIEW_GUI_CREATE_BACKUP_LORE_CREATE,
                        "&7",
                        "&7" + L.WORLD_MANAGER_WORLD_VIEW_GUI_CREATE_BACKUP_LORE_LAST_TIME,
                        "&7" + L.WORLD_MANAGER_WORLD_VIEW_GUI_CREATE_BACKUP_LORE_LAST_PATH
                )
                .addPlaceholder("{LastBackupAt}", this.sWorld.getLastBackupTime())
                .addPlaceholder("{LastBackupPath}", this.sWorld.getLastBackupPath());

        return new GUIButton(0, item, a-> {
            this.close();
            this.getSuperUtils().sendMessage(a.getPlayer(), "&a" + L.WORLD_MANAGER_BACKUP_CREATING);
            this.getSpigotTasks().runAsyncTask(() -> {
                if(this.sWorld.backup()){
                    this.getSuperUtils().sendMessage(a.getPlayer(), "&a" + L.WORLD_MANAGER_BACKUP_SUCCESS.options().placeholder("{Path}", this.sWorld.getLastBackupPath()));
                }else{
                    this.getSuperUtils().sendMessage(a.getPlayer(), "&c" + L.WORLD_MANAGER_BACKUP_FAILED);
                }
            });
        });
    }
}

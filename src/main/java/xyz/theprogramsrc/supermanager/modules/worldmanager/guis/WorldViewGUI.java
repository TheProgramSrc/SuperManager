package xyz.theprogramsrc.supermanager.modules.worldmanager.guis;

import java.io.File;
import java.util.function.Consumer;

import org.bukkit.entity.Player;

import xyz.theprogramsrc.supercoreapi.libs.xseries.XMaterial;
import xyz.theprogramsrc.supercoreapi.spigot.gui.Gui;
import xyz.theprogramsrc.supercoreapi.spigot.gui.objets.GuiAction;
import xyz.theprogramsrc.supercoreapi.spigot.gui.objets.GuiEntry;
import xyz.theprogramsrc.supercoreapi.spigot.gui.objets.GuiModel;
import xyz.theprogramsrc.supercoreapi.spigot.gui.objets.GuiRows;
import xyz.theprogramsrc.supercoreapi.spigot.gui.objets.GuiTitle;
import xyz.theprogramsrc.supercoreapi.spigot.items.SimpleItem;
import xyz.theprogramsrc.supermanager.L;
import xyz.theprogramsrc.supermanager.modules.worldmanager.objects.SWorld;

public class WorldViewGUI extends Gui {

    private final SWorld sWorld;
    private final Consumer<GuiAction> onBack;

    public WorldViewGUI(Player player, SWorld sWorld, Consumer<GuiAction> onBack) {
        super(player, false);
        this.sWorld = sWorld;
        this.onBack = onBack;
        this.open();
    }

    @Override
    public GuiRows getRows() {
        return GuiRows.ONE;
    }

    @Override
    public GuiTitle getTitle() {
        return GuiTitle.of(L.WORLD_MANAGER_WORLD_VIEW_GUI_TITLE.options().placeholder("{WorldName}", this.sWorld.getName()).toString());
    }

    @Override
    public void onBuild(GuiModel m) {
        m.setButton(this.getRows().size-1, new GuiEntry(this.getPreloadedItems().getBackItem(), this.onBack::accept));
        m.setButton(0, this.getBackupButton());
    }

    private GuiEntry getBackupButton(){
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
                .addPlaceholder("{LastBackupPath}", new File(this.sWorld.getLastBackupPath()).getPath());

        return new GuiEntry(item, a-> {
            this.close();
            this.getSuperUtils().sendMessage(a.player, "&a" + L.WORLD_MANAGER_BACKUP_CREATING);
            this.getSpigotTasks().runAsyncTask(() -> {
                if(this.sWorld.backup()){
                    this.getSuperUtils().sendMessage(a.player, "&a" + L.WORLD_MANAGER_BACKUP_SUCCESS.options().placeholder("{Path}", this.sWorld.getLastBackupPath()));
                }else{
                    this.getSuperUtils().sendMessage(a.player, "&c" + L.WORLD_MANAGER_BACKUP_FAILED);
                }
            });
        });
    }
}

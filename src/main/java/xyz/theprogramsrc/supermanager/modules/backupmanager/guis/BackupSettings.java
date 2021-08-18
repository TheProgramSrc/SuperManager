package xyz.theprogramsrc.supermanager.modules.backupmanager.guis;

import java.io.File;
import java.util.LinkedList;

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

public class BackupSettings extends Gui {

    private Runnable onBack;

    public BackupSettings(Player player, Runnable onBack) {
        super(player, false);
        this.onBack = onBack;
        this.open();
    }

    @Override
    public GuiRows getRows() {
        return GuiRows.FOUR;
    }

    @Override
    public GuiTitle getTitle() {
        return GuiTitle.of(L.BACKUP_MANAGER_SETTINGS_TITLE.toString());
    }

    @Override
    public void onBuild(GuiModel model) {
        model.setButton(11, this.getScheduleButton());
        model.setButton(15, this.getSelectBackupsFolderButton());
        model.setButton(this.getRows().size-1, new GuiEntry(this.getPreloadedItems().getBackItem(), a-> this.onBack.run()));
    }

    private GuiEntry getSelectBackupsFolderButton(){ // 15
        SimpleItem item = new SimpleItem(XMaterial.CHEST)
            .setDisplayName("&6" + L.BACKUP_MANAGER_SETTINGS_SELECT_BACKUPS_FOLDER_NAME)
            .setLore(
                "&7",
                "&7" + L.BACKUP_MANAGER_SETTINGS_SELECT_BACKUPS_FOLDER_LORE
            );
        return new GuiEntry(item, a-> {
            new BackupsFolderSelector(a.player, new File(".")){
                
                @Override
                public void onBack(GuiAction action) {
                    BackupSettings.this.open();
                }
            };
        });
    }

    private GuiEntry getScheduleButton(){ // 11
        SimpleItem item = new SimpleItem(XMaterial.CLOCK)
            .setDisplayName("&e" + L.BACKUP_MANAGER_SETTINGS_SCHEDULE_NAME)
            .setLore(
                "&7",
                "&7" + L.BACKUP_MANAGER_SETTINGS_SCHEDULE_LORE
            );
        return new GuiEntry(item, a-> {
            LinkedList<String> paths = new LinkedList<>();
            new BackupFileBrowser(a.player, new File("."), paths){
                
                @Override
                public void onBack(GuiAction clickAction) {
                    BackupSettings.this.open();
                }
            };
        });
    }
    
}

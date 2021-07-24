package xyz.theprogramsrc.supermanager.modules.backupmanager.guis;

import java.io.File;
import java.util.LinkedList;

import org.bukkit.entity.Player;

import xyz.theprogramsrc.supercoreapi.libs.xseries.XMaterial;
import xyz.theprogramsrc.supercoreapi.spigot.guis.GUI;
import xyz.theprogramsrc.supercoreapi.spigot.guis.GUIButton;
import xyz.theprogramsrc.supercoreapi.spigot.guis.action.ClickAction;
import xyz.theprogramsrc.supercoreapi.spigot.guis.objects.GUIRows;
import xyz.theprogramsrc.supercoreapi.spigot.items.SimpleItem;
import xyz.theprogramsrc.supermanager.L;

public class BackupSettings extends GUI {

    private Runnable onBack;

    public BackupSettings(Player player, Runnable onBack) {
        super(player);
        this.onBack = onBack;
        this.open();
    }

    @Override
    protected GUIRows getRows() {
        return GUIRows.FOUR;
    }

    @Override
    protected String getTitle() {
        return L.BACKUP_MANAGER_SETTINGS_TITLE.toString();
    }

    @Override
    protected GUIButton[] getButtons() {
        return new GUIButton[]{
            this.getScheduleButton(),
            this.getSelectBackupsFolderButton(),
            new GUIButton(this.getRows().getSize()-1, this.getPreloadedItems().getBackItem(), a-> this.onBack.run())
        };
    }

    private GUIButton getSelectBackupsFolderButton(){
        SimpleItem item = new SimpleItem(XMaterial.CHEST)
            .setDisplayName("&6" + L.BACKUP_MANAGER_SETTINGS_SELECT_BACKUPS_FOLDER_NAME)
            .setLore(
                "&7",
                "&7" + L.BACKUP_MANAGER_SETTINGS_SELECT_BACKUPS_FOLDER_LORE
            );
        return new GUIButton(15, item, a-> {
            new BackupsFolderSelector(a.getPlayer(), new File(".")){
                
                @Override
                public void onBack(ClickAction action) {
                    BackupSettings.this.open();
                }
            };
        });
    }

    private GUIButton getScheduleButton(){
        SimpleItem item = new SimpleItem(XMaterial.CLOCK)
            .setDisplayName("&e" + L.BACKUP_MANAGER_SETTINGS_SCHEDULE_NAME)
            .setLore(
                "&7",
                "&7" + L.BACKUP_MANAGER_SETTINGS_SCHEDULE_LORE
            );
        return new GUIButton(11, item, a-> {
            LinkedList<String> paths = new LinkedList<>();
            new BackupFileBrowser(a.getPlayer(), new File("."), paths){
                
                @Override
                public void onBack(ClickAction clickAction) {
                    BackupSettings.this.open();
                }
            };
        });
    }
    
}

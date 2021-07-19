package xyz.theprogramsrc.supermanager.modules.backupmanager.guis;

import java.time.format.DateTimeFormatter;
import java.util.Arrays;
import java.util.List;

import org.bukkit.entity.Player;

import xyz.theprogramsrc.supercoreapi.global.translations.Base;
import xyz.theprogramsrc.supercoreapi.libs.xseries.XMaterial;
import xyz.theprogramsrc.supercoreapi.spigot.guis.BrowserGUI;
import xyz.theprogramsrc.supercoreapi.spigot.guis.GUIButton;
import xyz.theprogramsrc.supercoreapi.spigot.guis.action.ClickType;
import xyz.theprogramsrc.supercoreapi.spigot.items.SimpleItem;
import xyz.theprogramsrc.supermanager.L;
import xyz.theprogramsrc.supermanager.modules.backupmanager.BackupManager;
import xyz.theprogramsrc.supermanager.modules.backupmanager.objects.Backup;

public class BackupBrowser extends BrowserGUI<Backup> {

    private final BackupManager backupManager;
    private final DateTimeFormatter formatter;

    public BackupBrowser(Player player) {
        super(player);
        this.backupManager = BackupManager.i;
        this.formatter = DateTimeFormatter.ofPattern(this.backupManager.backupStorage.getDateFormatter());
        this.open();
    }

    @Override
    protected GUIButton[] getButtons() {
        List<GUIButton> list = Arrays.asList(super.getButtons());
        SimpleItem settingsItem = new SimpleItem(XMaterial.COMMAND_BLOCK)
            .setDisplayName("&c" + L.BACKUP_MANAGER_BROWSER_SETTINGS_NAME)
            .setLore(
                "&7",
                "&7" + L.BACKUP_MANAGER_BROWSER_SETTINGS_LORE
            );
        list.add(new GUIButton(47, settingsItem, a-> new BackupSettings(a.getPlayer(), a2-> {
            this.open();
        })));
        return list.toArray(new GUIButton[0]);
    }

    @Override
    public GUIButton getButton(Backup backup) {
        SimpleItem item = new SimpleItem(XMaterial.ENDER_CHEST) 
            .setDisplayName(L.BACKUP_MANAGER_BROWSER_ITEM_NAME.toString())
            .setLore(
                "&7",
                "&9" + Base.RIGHT_CLICK + "&7 " + L.BACKUP_MANAGER_BROWSER_ITEM_DELETE,
                "&7",
                "&7" + L.BACKUP_MANAGER_BROWSER_ITEM_LAST_BACKUP_AT,
                "&7" + L.BACKUP_MANAGER_BROWSER_ITEM_NEXT_BACKUP_AT,
                "&7" + L.BACKUP_MANAGER_BROWSER_ITEM_PATH
            )
            .addPlaceholder("{BackupName}", backup.getName())
            .addPlaceholder("{LastBackupAt}", this.formatter.format(backup.getLastBackup().toInstant()))
            .addPlaceholder("{NextBackupAt}", this.formatter.format(backup.getNextBackup().toInstant()))
            .addPlaceholder("{BackupPath}", backup.getBackupPath());;
        return new GUIButton(item, a-> {
            if(a.getAction() == ClickType.RIGHT_CLICK){
                this.backupManager.backupStorage.delete(backup);
                this.open();
            }
        });
    }

    @Override
    public Backup[] getObjects() {
        return this.backupManager.backupStorage.getAll();
    }

    @Override
    protected String getTitle() {
        return L.BACKUP_MANAGER_BROWSER_TITLE.toString();
    }
    
}

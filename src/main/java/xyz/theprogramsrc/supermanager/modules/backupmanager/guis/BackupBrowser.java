package xyz.theprogramsrc.supermanager.modules.backupmanager.guis;

import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.Arrays;
import java.util.Locale;

import org.bukkit.entity.Player;

import xyz.theprogramsrc.supercoreapi.global.translations.Base;
import xyz.theprogramsrc.supercoreapi.libs.xseries.XMaterial;
import xyz.theprogramsrc.supercoreapi.spigot.dialog.Dialog;
import xyz.theprogramsrc.supercoreapi.spigot.gui.BrowserGui;
import xyz.theprogramsrc.supercoreapi.spigot.gui.objets.GuiAction.ClickType;
import xyz.theprogramsrc.supercoreapi.spigot.gui.objets.GuiEntry;
import xyz.theprogramsrc.supercoreapi.spigot.gui.objets.GuiModel;
import xyz.theprogramsrc.supercoreapi.spigot.gui.objets.GuiTitle;
import xyz.theprogramsrc.supercoreapi.spigot.items.SimpleItem;
import xyz.theprogramsrc.supermanager.L;
import xyz.theprogramsrc.supermanager.modules.backupmanager.BackupManager;
import xyz.theprogramsrc.supermanager.modules.backupmanager.objects.Backup;

public class BackupBrowser extends BrowserGui<Backup> {

    private final BackupManager backupManager;
    private final DateTimeFormatter formatter;

    public BackupBrowser(Player player) {
        super(player, false);
        this.backEnabled = true;
        this.backupManager = BackupManager.i;
        this.formatter = DateTimeFormatter.ofPattern(this.backupManager.backupStorage.getDateFormatter()).withLocale(Locale.getDefault()).withZone(ZoneId.systemDefault());
        this.open();
    }

    @Override
    public void onBuild(GuiModel model) {
        super.onBuild(model);
        SimpleItem settingsItem = new SimpleItem(XMaterial.COMMAND_BLOCK)
            .setDisplayName("&c" + L.BACKUP_MANAGER_BROWSER_SETTINGS_NAME)
            .setLore(
                "&7",
                "&7" + L.BACKUP_MANAGER_BROWSER_SETTINGS_LORE
            );
        model.setButton(47, new GuiEntry(settingsItem, a -> new BackupSettings(a.player, this::open)));
    }

    @Override
    public String[] getSearchTags(Backup b) {
        return new String[]{
            b.getName(),
            b.getUuid().toString(),
        };
    }

    @Override
    public GuiEntry getEntry(final Backup backup) {
        SimpleItem item = new SimpleItem(XMaterial.ENDER_CHEST) 
            .setDisplayName(L.BACKUP_MANAGER_BROWSER_ITEM_NAME.toString())
            .setLore(
                "&7",
                "&9" + Base.LEFT_CLICK + "&7 " + L.BACKUP_MANAGER_BROWSER_ITEM_RENAME,
                "&9Q&7 " + L.BACKUP_MANAGER_BROWSER_ITEM_BACKUP_NOW,
                "&9" + Base.RIGHT_CLICK + "&7 " + L.BACKUP_MANAGER_BROWSER_ITEM_DELETE,
                "&7",
                "&7" + L.BACKUP_MANAGER_BROWSER_ITEM_LAST_BACKUP_AT,
                "&7" + L.BACKUP_MANAGER_BROWSER_ITEM_NEXT_BACKUP_AT,
                "&7" + L.BACKUP_MANAGER_BROWSER_ITEM_NEXT_BACKUP_IN
            )
            .addPlaceholder("{BackupName}", backup.getName())
            .addPlaceholder("{LastBackupAt}", this.formatter.format(backup.getLastBackup()))
            .addPlaceholder("{NextBackupAt}", this.formatter.format(backup.getNextBackup()))
            .addPlaceholder("{NextBackupIn}", (backup.getSecondsLeftBeforeBackup() < 1 ? L.BACKUP_MANAGER_BACKUP_IN_PROCESS : L.BACKUP_MANAGER_BROWSER_ITEM_SECONDS_LEFT) + "")
            .addPlaceholder("{SecondsLeftForNextBackup}", backup.getSecondsLeftBeforeBackup() + "");
        return new GuiEntry(item, a -> {
            if(a.clickType == ClickType.RIGHT_CLICK){
                this.close();
                this.backupManager.backupStorage.delete(backup);
                this.getSuperUtils().sendMessage(a.player, "&a" + L.BACKUP_MANAGER_BACKUP_DELETED);
            }else if(a.clickType == ClickType.Q){
                this.close();
                this.getSuperUtils().sendMessage(a.player, "&a" + L.BACKUP_MANAGER_BACKUP_IN_PROCESS);
                backup.backup(a.player);
            }else if(a.clickType == ClickType.LEFT_CLICK){
                new Dialog(a.player){

                    @Override
                    public String getTitle(){
                        return L.BACKUP_MANAGER_RENAME_BACKUP_TITLE.toString();
                    }

                    @Override
                    public String getSubtitle() {
                        return L.BACKUP_MANAGER_RENAME_BACKUP_SUBTITLE.toString();
                    }

                    @Override
                    public String getActionbar() {
                        return L.BACKUP_MANAGER_RENAME_BACKUP_ACTIONBAR.toString();
                    }

                    @Override
                    public boolean onResult(String input){
                        backup.setName(input);
                        BackupManager.i.backupStorage.save(backup);
                        return true;
                    }
                }.addPlaceholder("{BackupName}", backup.getName()).setRecall(p-> this.open());
            }
        });
    }

    @Override
    public Backup[] getObjects() {
        return Arrays.stream(this.backupManager.backupStorage.getAll()).toArray(Backup[]::new);
    }

    @Override
    public GuiTitle getTitle() {
        return GuiTitle.of(L.BACKUP_MANAGER_BROWSER_TITLE.toString());
    }
    
}

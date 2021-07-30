package xyz.theprogramsrc.supermanager.modules.backupmanager.guis;

import java.time.Duration;
import java.time.Instant;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.Arrays;
import java.util.Locale;

import org.bukkit.entity.Player;

import xyz.theprogramsrc.supercoreapi.global.translations.Base;
import xyz.theprogramsrc.supercoreapi.libs.xseries.XMaterial;
import xyz.theprogramsrc.supercoreapi.spigot.dialog.Dialog;
import xyz.theprogramsrc.supercoreapi.spigot.guis.BrowserGUI;
import xyz.theprogramsrc.supercoreapi.spigot.guis.GUIButton;
import xyz.theprogramsrc.supercoreapi.spigot.guis.action.ClickType;
import xyz.theprogramsrc.supercoreapi.spigot.guis.events.GUIEvent;
import xyz.theprogramsrc.supercoreapi.spigot.guis.events.GUIOpenEvent;
import xyz.theprogramsrc.supercoreapi.spigot.items.SimpleItem;
import xyz.theprogramsrc.supermanager.L;
import xyz.theprogramsrc.supermanager.modules.backupmanager.BackupManager;
import xyz.theprogramsrc.supermanager.modules.backupmanager.objects.Backup;

public class BackupBrowser extends BrowserGUI<Backup> {

    private final BackupManager backupManager;
    private final DateTimeFormatter formatter;

    public BackupBrowser(Player player) {
        super(player);
        this.backEnabled = true;
        this.backupManager = BackupManager.i;
        this.formatter = DateTimeFormatter.ofPattern(this.backupManager.backupStorage.getDateFormatter()).withLocale(Locale.getDefault()).withZone(ZoneId.systemDefault());
        this.open();
    }

    @Override
    public void onEvent(GUIEvent e) {
        if(e instanceof GUIOpenEvent){
            SimpleItem settingsItem = new SimpleItem(XMaterial.COMMAND_BLOCK)
            .setDisplayName("&c" + L.BACKUP_MANAGER_BROWSER_SETTINGS_NAME)
            .setLore(
                "&7",
                "&7" + L.BACKUP_MANAGER_BROWSER_SETTINGS_LORE
            );
            this.addButton(new GUIButton(47, settingsItem, a-> new BackupSettings(a.getPlayer(), this::open)));
        }
    }

    @Override
    public GUIButton getButton(final Backup backup) {
        long seconds = Duration.between(Instant.now(), backup.getNextBackup()).getSeconds();
        SimpleItem item = new SimpleItem(XMaterial.ENDER_CHEST) 
            .setDisplayName(L.BACKUP_MANAGER_BROWSER_ITEM_NAME.toString())
            .setLore(
                "&7",
                "&9" + Base.LEFT_CLICK + "&7 " + L.BACKUP_MANAGER_BROWSER_ITEM_RENAME,
                "&9" + Base.MIDDLE_CLICK + "&7 " + L.BACKUP_MANAGER_BROWSER_ITEM_BACKUP_NOW,
                "&9" + Base.RIGHT_CLICK + "&7 " + L.BACKUP_MANAGER_BROWSER_ITEM_DELETE,
                "&7",
                "&7" + L.BACKUP_MANAGER_BROWSER_ITEM_LAST_BACKUP_AT,
                "&7" + L.BACKUP_MANAGER_BROWSER_ITEM_NEXT_BACKUP_AT,
                "&7" + L.BACKUP_MANAGER_BROWSER_ITEM_NEXT_BACKUP_IN
            )
            .addPlaceholder("{BackupName}", backup.getName())
            .addPlaceholder("{LastBackupAt}", this.formatter.format(backup.getLastBackup()))
            .addPlaceholder("{NextBackupAt}", this.formatter.format(backup.getNextBackup()))
            .addPlaceholder("{NextBackupIn}", (seconds < 1 ? L.BACKUP_MANAGER_BACKUP_IN_PROCESS : L.BACKUP_MANAGER_BROWSER_ITEM_SECONDS_LEFT) + "")
            .addPlaceholder("{SecondsLeftForNextBackup}", seconds + "");
        return new GUIButton(item, a -> {
            if(a.getAction() == ClickType.RIGHT_CLICK){
                this.close();
                this.backupManager.backupStorage.delete(backup);
                this.getSuperUtils().sendMessage(a.getPlayer(), "&a" + L.BACKUP_MANAGER_BACKUP_DELETED);
            }else if(a.getAction() == ClickType.MIDDLE_CLICK){
                this.close();
                this.getSuperUtils().sendMessage(a.getPlayer(), "&a" + L.BACKUP_MANAGER_BACKUP_IN_PROCESS);
                backup.backup(a.getPlayer());
            }else if(a.getAction() == ClickType.LEFT_CLICK){
                new Dialog(a.getPlayer()){

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
        return Arrays.stream(this.backupManager.backupStorage.getAll()).filter(b-> this.backupManager.backupStorage.has(b.getUuid())).toArray(Backup[]::new);
    }

    @Override
    protected String getTitle() {
        return L.BACKUP_MANAGER_BROWSER_TITLE.toString();
    }
    
}

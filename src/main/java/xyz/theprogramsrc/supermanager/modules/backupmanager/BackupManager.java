package xyz.theprogramsrc.supermanager.modules.backupmanager;

import java.io.File;
import java.time.Instant;
import java.util.Date;
import java.util.LinkedList;
import java.util.UUID;

import org.bukkit.entity.Player;

import xyz.theprogramsrc.supercoreapi.global.files.yml.YMLConfig;
import xyz.theprogramsrc.supercoreapi.libs.xseries.XMaterial;
import xyz.theprogramsrc.supercoreapi.spigot.guis.action.ClickAction;
import xyz.theprogramsrc.supercoreapi.spigot.items.SimpleItem;
import xyz.theprogramsrc.supermanager.L;
import xyz.theprogramsrc.supermanager.guis.MainGUI;
import xyz.theprogramsrc.supermanager.modules.backupmanager.guis.BackupBrowser;
import xyz.theprogramsrc.supermanager.modules.backupmanager.objects.Backup;
import xyz.theprogramsrc.supermanager.objects.Module;

public class BackupManager extends Module{

    public static LinkedList<UUID> queue = new LinkedList<UUID>();
    public static BackupManager i;
    public BackupStorage backupStorage;

    @Override
    public void onEnable(){
        i = this;
        YMLConfig cfg = new YMLConfig(new File(this.getModuleFolder(), "Backups.yml"));
        this.backupStorage = new BackupStorage(cfg);
        this.loadScheduledBackups();
    }

    public void loadScheduledBackups(){
        // Check every second if we have new backups to be added
        this.getSpigotTasks().runAsyncRepeatingTask(0L, 20L, () -> {
            Date now = Date.from(Instant.now());
            for(Backup backup : this.backupStorage.getAll()){
                if(!this.backupStorage.has(backup.getUuid())) continue;
                if(queue.contains(backup.getUuid())) continue;
                queue.add(backup.getUuid());
                if (now.after(Date.from(backup.getNextBackup()))) {
                    backup.backup(null);
                }
            }
        });
    }

    @Override
    public String getIdentifier() {
        return "backup_manager";
    }

    @Override
    public String getDisplay() {
        return L.BACKUP_MANAGER_DISPLAY.toString();
    }

    @Override
    public SimpleItem getDisplayItem() {
        return new SimpleItem(XMaterial.ENDER_CHEST)
            .setDisplayName("&5" + L.BACKUP_MANAGER_NAME)
            .setLore(
                "&7",
                "&7" + L.BACKUP_MANAGER_LORE
            );
    }

    @Override
    public void onAction(Player player) {
        new BackupBrowser(player){
            @Override
            public void onBack(ClickAction clickAction) {
                new MainGUI(clickAction.getPlayer());
            }
        };
    }
  
}

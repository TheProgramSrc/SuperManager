package xyz.theprogramsrc.supermanager.modules.backupmanager.guis;

import java.io.File;
import java.time.Instant;
import java.util.Arrays;
import java.util.Calendar;
import java.util.LinkedList;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicLong;

import org.bukkit.entity.Player;

import xyz.theprogramsrc.supercoreapi.global.translations.Base;
import xyz.theprogramsrc.supercoreapi.global.utils.StringUtils;
import xyz.theprogramsrc.supercoreapi.global.utils.Utils;
import xyz.theprogramsrc.supercoreapi.libs.xseries.XMaterial;
import xyz.theprogramsrc.supercoreapi.spigot.dialog.Dialog;
import xyz.theprogramsrc.supercoreapi.spigot.gui.BrowserGui;
import xyz.theprogramsrc.supercoreapi.spigot.gui.objets.GuiAction;
import xyz.theprogramsrc.supercoreapi.spigot.gui.objets.GuiAction.ClickType;
import xyz.theprogramsrc.supercoreapi.spigot.gui.objets.GuiEntry;
import xyz.theprogramsrc.supercoreapi.spigot.gui.objets.GuiModel;
import xyz.theprogramsrc.supercoreapi.spigot.gui.objets.GuiTitle;
import xyz.theprogramsrc.supercoreapi.spigot.items.SimpleItem;
import xyz.theprogramsrc.supermanager.L;
import xyz.theprogramsrc.supermanager.SuperManager;
import xyz.theprogramsrc.supermanager.modules.backupmanager.BackupManager;
import xyz.theprogramsrc.supermanager.modules.backupmanager.BackupStorage;
import xyz.theprogramsrc.supermanager.modules.backupmanager.objects.Backup;

public class BackupFileBrowser extends BrowserGui<File>{

    private final LinkedList<String> filesToBackup;
    private File currentFolder;

    public BackupFileBrowser(Player player, File folder, LinkedList<String> filesToBackup){
        super(player, false);
        this.filesToBackup = filesToBackup;
        this.currentFolder = folder;
        this.backEnabled = true;
        this.open();
    }

    @Override
    public void onBuild(GuiModel model) {
        super.onBuild(model);
        SimpleItem item = new SimpleItem(XMaterial.EMERALD)
            .setDisplayName("&a" + L.BACKUP_MANAGER_FILE_BROWSER_SAVE_NAME)
            .setLore(
                "&7",
                "&7" + L.BACKUP_MANAGER_FILE_BROWSER_SAVE_LORE
            ).addPlaceholder("{SelectedFilesAmount}", this.filesToBackup.size()+"");
        model.setButton(47, new GuiEntry(item, a-> {
            this.getSpigotTasks().runAsyncTask(() -> {
                L[] msg = new L[]{
                    L.BACKUP_MANAGER_AVAILABLE_TIME_UNITS,
                    L.BACKUP_MANAGER_AVAILABLE_TIME_UNITS_SECOND,
                    L.BACKUP_MANAGER_AVAILABLE_TIME_UNITS_MINUTE,
                    L.BACKUP_MANAGER_AVAILABLE_TIME_UNITS_HOUR
                };
                for(L l : msg){
                    this.getSuperUtils().sendMessage(a.player, l.toString());
                }
            });

            final AtomicLong atomicSeconds = new AtomicLong();
            new Dialog(a.player){
                
                @Override
                public String getTitle(){
                    return L.BACKUP_MANAGER_SCHEDULE_BACKUP_TITLE.toString();
                }

                @Override
                public String getSubtitle(){
                    return L.BACKUP_MANAGER_SCHEDULE_BACKUP_SUBTITLE.toString();
                }

                @Override
                public String getActionbar(){
                    return L.BACKUP_MANAGER_SCHEDULE_BACKUP_ACTIONBAR.toString();
                }

                @Override
                public boolean onResult(String input){
                    long seconds = SuperManager.getTimeSecondsFromString(input);
                    if(seconds == 0L){
                        this.getSuperUtils().sendMessage(a.player, L.INVALID_TIME_FORMAT.toString());
                        return false;
                    }
                    atomicSeconds.set(seconds);
                    return true;
                }
            }.setRecall(player-> { 
                // We let know to the player that we initialized the backup creation
                this.getSuperUtils().sendMessage(player, L.BACKUP_MANAGER_CREATING_BACKUP.toString());
                // We retrieve the time in seconds
                long seconds = atomicSeconds.get();
                // Get the current date using calendar
                Calendar calendar = Calendar.getInstance();
                // Parse into date
                Instant now = calendar.getTime().toInstant();
                // Generate UUID
                UUID uuid = UUID.randomUUID();
                // Get backup storage
                BackupStorage backupStorage = BackupManager.i.backupStorage;

                // Create backup file name
                String backupFileName = new StringUtils(backupStorage.getBackupFileName())
                    .placeholder("{Name}", uuid.toString())
                    .placeholder("{UUID}", uuid.toString())
                    .placeholder("{Day}", calendar.get(Calendar.DAY_OF_MONTH)+"")
                    .placeholder("{Month}", calendar.get(Calendar.MONTH)+"")
                    .placeholder("{Year}", calendar.get(Calendar.YEAR)+"")
                    .placeholder("{Hour}", calendar.get(Calendar.HOUR_OF_DAY)+"")
                    .placeholder("{Minute}", calendar.get(Calendar.MINUTE)+"")
                    .placeholder("{Second}", calendar.get(Calendar.SECOND)+"")
                    .get();
                // Create backup file
                File backupFile = new File(backupStorage.getBackupsFolder(), backupFileName);
                // Create backup
                Backup backup = new Backup(uuid, uuid.toString(), backupFile.getPath(), this.filesToBackup, seconds, now, now);
                // Add backup to storage
                backupStorage.save(backup);
                // Notify player
                this.getSuperUtils().sendMessage(a.player, L.BACKUP_MANAGER_BACKUP_CREATED.toString());
                // Generate new backup
                backup.backup(player);
            });
        }));
    }

    @Override
    public String[] getSearchTags(File f) {
        return new String[]{f.getName()};
    }

    @Override
    public GuiEntry getEntry(File file) {
        SimpleItem item = new SimpleItem(file.isDirectory() ? XMaterial.CHEST : XMaterial.PAPER)
            .setDisplayName("&a" + L.BACKUP_MANAGER_FILE_BROWSER_ITEM_NAME)
            .setLore(
                "&7",
                "&9" + Base.LEFT_CLICK + "&7 " + (!this.filesToBackup.contains(file.getPath()) ? L.BACKUP_MANAGER_FILE_BROWSER_ITEM_ADD_TO_LIST : L.BACKUP_MANAGER_FILE_BROWSER_ITEM_REMOVE_FROM_LIST)
            ).setGlowing(this.filesToBackup.contains(file.getPath()));
        
        if(file.isDirectory()){
            item.addLoreLine("&9" + Base.RIGHT_CLICK + "&7 " + L.BACKUP_MANAGER_FILE_BROWSER_ITEM_OPEN_FOLDER);
        }
        item.addPlaceholder("{FileName}", file.getName()).setGlowing(this.filesToBackup.contains(file.getPath()));
        return new GuiEntry(item, a-> {
            if(file.isDirectory() && a.clickType == ClickType.RIGHT_CLICK){
                new BackupFileBrowser(a.player, file, this.filesToBackup){
                    @Override
                    public void onBack(GuiAction clickAction) {
                        BackupFileBrowser.this.open();
                    }
                };
            }else{
                if(this.filesToBackup.contains(file.getPath())){
                    this.filesToBackup.remove(file.getPath());
                }else{
                    this.filesToBackup.add(file.getPath());
                }
                this.open();
            }
        });
            
    }

    @Override
    public File[] getObjects() {
        File[] files = this.currentFolder.listFiles();
        if(files == null) return new File[0];
        return Arrays.stream(files).filter(Utils::nonNull).toArray(File[]::new);
    }

    @Override
    public GuiTitle getTitle() {
        return GuiTitle.of(L.BACKUP_MANAGER_FILE_BROWSER_TITLE.toString());
    }
    
}

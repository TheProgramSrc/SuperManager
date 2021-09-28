package xyz.theprogramsrc.supermanager.modules.backupmanager.objects;

import java.io.File;
import java.time.Duration;
import java.time.Instant;
import java.time.ZoneId;
import java.time.temporal.ChronoUnit;
import java.util.Calendar;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

import org.bukkit.command.CommandSender;

import xyz.theprogramsrc.supercoreapi.global.utils.StringUtils;
import xyz.theprogramsrc.supercoreapi.global.utils.Utils;
import xyz.theprogramsrc.supercoreapi.libs.zip4j.ZipFile;
import xyz.theprogramsrc.supermanager.L;
import xyz.theprogramsrc.supermanager.SuperManager;
import xyz.theprogramsrc.supermanager.modules.backupmanager.BackupManager;
import xyz.theprogramsrc.supermanager.modules.backupmanager.BackupStorage;

public class Backup {

    private final UUID uuid;
    private String name, backupPath;
    private final LinkedList<String> paths;
    private long timeBetweenBackups;
    private Instant lastBackup, nextBackup;
    private final BackupStorage backupStorage;

    public Backup(UUID uuid, String name, String backupPath, LinkedList<String> paths, long timeBetweenBackups, Instant lastBackup, Instant nextBackup) {
        this.uuid = uuid;
        this.name = name;
        this.backupPath = backupPath;
        this.paths = paths;
        this.timeBetweenBackups = timeBetweenBackups;
        this.lastBackup = lastBackup;
        this.nextBackup = nextBackup;
        this.backupStorage = BackupManager.i.backupStorage;
    }

    public UUID getUuid() {
        return this.uuid;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getName(){
        return this.name;
    }

    public String getBackupPath() {
        return this.backupPath;
    }

    public Instant getLastBackup() {
        return this.lastBackup;
    }
    public Instant getNextBackup() {
        return this.nextBackup;
    }

    public long getTimeBetweenBackups() {
        return this.timeBetweenBackups;
    }

    public LinkedList<String> getPaths() {
        return this.paths;
    }

    public long getSecondsLeftBeforeBackup(){
        return Duration.between(Instant.now(), this.getNextBackup()).getSeconds();
    }

    public void backup(CommandSender sender) {
        SuperManager.i.getSpigotTasks().runAsyncTask(() -> {
            try{
                // Get the current date using calendar
                Calendar calendar = Calendar.getInstance();
                // Parse into date
                Date now = calendar.getTime();

                String backupFileName = new StringUtils(this.backupStorage.getBackupFileName())
                    .placeholder("{Name}", this.getName())
                    .placeholder("{UUID}", uuid.toString())
                    .placeholder("{Day}", calendar.get(Calendar.DAY_OF_MONTH)+"")
                    .placeholder("{Month}", calendar.get(Calendar.MONTH)+"")
                    .placeholder("{Year}", calendar.get(Calendar.YEAR)+"")
                    .placeholder("{Hour}", calendar.get(Calendar.HOUR_OF_DAY)+"")
                    .placeholder("{Minute}", calendar.get(Calendar.MINUTE)+"")
                    .placeholder("{Second}", calendar.get(Calendar.SECOND)+"")
                    .get();
                List<File> files = this.getPaths().stream().map(path -> new File(path)).filter(File::exists).collect(Collectors.toList());
                File backupsFolder = Utils.folder(this.backupStorage.getBackupsFolder());
                ZipFile backup = new ZipFile(new File(backupsFolder, (backupFileName.endsWith(".zip") ? backupFileName : (backupFileName + ".zip"))));
                backup.addFiles(files);
                backup.close();
                this.backupPath = backup.getFile().getPath();
                this.nextBackup = now.toInstant().plus(this.timeBetweenBackups, ChronoUnit.SECONDS).atZone(ZoneId.systemDefault()).toInstant();
                this.lastBackup = now.toInstant();
                BackupManager.i.backupStorage.save(this);
                if(sender != null) sender.sendMessage(Utils.ct(L.BACKUP_MANAGER_SUCCESSFULLY_BACKED_UP_DATA.options().placeholder("{FilesAmount}", files.size() + "").placeholder("{NextBackupAt}", Date.from(this.nextBackup).toString()).get()));
            } catch(Exception e) {
                if(sender != null) sender.sendMessage(Utils.ct(L.BACKUP_MANAGER_FAILED_TO_BACKUP_DATA.toString()));
                BackupManager.i.log("&cFailed to execute backup '" + this.getName() + "':", true);
                BackupManager.i.getPlugin().addError(e);
                e.printStackTrace();
            }
        });
    }

    public File getFile(){
        return new File(this.backupPath);
    }
    
}

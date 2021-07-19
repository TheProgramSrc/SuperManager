package xyz.theprogramsrc.supermanager.modules.backupmanager.objects;

import java.io.File;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import java.util.Date;
import java.util.LinkedList;
import java.util.UUID;

import xyz.theprogramsrc.supercoreapi.global.utils.Utils;
import xyz.theprogramsrc.supercoreapi.global.utils.files.ZipUtils;
import xyz.theprogramsrc.supermanager.SuperManager;
import xyz.theprogramsrc.supermanager.modules.backupmanager.BackupManager;

public class Backup {

    private final UUID uuid;
    private String name, backupPath;
    private final LinkedList<String> paths;
    private int timeBetweenBackups;
    private Date lastBackup, nextBackup;

    public Backup(UUID uuid, String name, String backupPath, LinkedList<String> paths, int timeBetweenBackups, Date lastBackup, Date nextBackup) {
        this.uuid = uuid;
        this.name = name;
        this.backupPath = backupPath;
        this.paths = paths;
        this.timeBetweenBackups = timeBetweenBackups;
        this.lastBackup = lastBackup;
        this.nextBackup = nextBackup;
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

    public Date getLastBackup() {
        return this.lastBackup;
    }
    public Date getNextBackup() {
        return this.nextBackup;
    }

    public int getTimeBetweenBackups() {
        return this.timeBetweenBackups;
    }

    public LinkedList<String> getPaths() {
        return this.paths;
    }

    public void backup() {
        SuperManager.i.getSpigotTasks().runAsyncTask(() -> {
            try{
                Instant instant = Instant.now();
                Date date = Date.from(instant);
                String now = DateTimeFormatter.ofPattern("yyyy-MM-dd_HH-mm-ss").format(instant);
                File[] files = this.getPaths().stream().map(path -> new File(path)).toArray(File[]::new);
                File backup = ZipUtils.zipFiles(Utils.folder(new File(BackupManager.i.getModuleFolder(), "backups/")), (this.getUuid() + now + ".zip"), files);
                if(backup != null){
                    this.backupPath = backup.getAbsolutePath();
                    this.nextBackup = Date.from(LocalDateTime.from(date.toInstant()).plus(this.timeBetweenBackups, ChronoUnit.SECONDS).atZone(ZoneId.systemDefault()).toInstant());
                    this.lastBackup = date;
                    BackupManager.i.backupStorage.save(this);
                }
            }catch(Exception e){
                BackupManager.i.log("&cFailed to execute backup '" + this.getUuid() + "':", true);
                BackupManager.i.getPlugin().addError(e);
                e.printStackTrace();
            }
        });
    }

    public File getFile(){
        return new File(this.backupPath);
    }
    
}

package xyz.theprogramsrc.supermanager.modules.backupmanager;

import java.io.File;
import java.time.Instant;
import java.time.format.DateTimeFormatter;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.UUID;

import xyz.theprogramsrc.supercoreapi.global.files.yml.YMLConfig;
import xyz.theprogramsrc.supercoreapi.global.utils.Utils;
import xyz.theprogramsrc.supercoreapi.spigot.SpigotModule;
import xyz.theprogramsrc.supermanager.modules.backupmanager.objects.Backup;

public class BackupStorage extends SpigotModule {

    private final LinkedHashMap<UUID, Backup> CACHE = new LinkedHashMap<>();
    private final YMLConfig cfg;
    public BackupStorage(YMLConfig cfg){
        this.cfg = cfg;
        this.cfg.add("DateFormatter", "dd/MM/yyyy HH:mm:ss");
        this.cfg.add("BackupsFolder", new File("BackupManager/Backups/").getPath());
        this.cfg.add("BackupFileName", "{Name}-{UUID}-{Day}.{Month}.{Year}_{Hour}.{Minute}.{Second}");

        this.cfg.addComment("DateFormatter", "This is the format of the date that will be shown in the in-game gui.");
        this.cfg.addComment("BackupsFolder", "This is the folder where the backups will be located.");
        this.cfg.addComment("BackupFileName", "Format of the backup file name. The .zip extension is added automatically.");

        this.getSpigotTasks().runAsyncRepeatingTask(0L, 50L, () -> {
            for(UUID uuid : CACHE.keySet()){
                if(!this.has(uuid)){
                    this.CACHE.remove(uuid);
                }
            }
        });
    }

    public String getBackupFileName(){
        String name = this.cfg.getString("BackupFileName");
        // Check if the name ends with .zip, if it doesn't then add it, if it does then do nothing
        if(!name.endsWith(".zip")){
            name += ".zip";
        }
        return name;
    }

    public File getBackupsFolder() {
        return Utils.folder(new File(this.cfg.getString("BackupsFolder")));
    }

    public void setBackupsFolder(String path){
        this.cfg.set("BackupsFolder", Utils.folder(new File(path)).getPath());
    }

    public String getDateFormatter(){
        return this.cfg.getString("DateFormatter");
    }

    public boolean has(UUID uuid) {
        return this.cfg.contains("Backups." + uuid);
    }

    public void delete(Backup backup){
        this.CACHE.remove(backup.getUuid());
        this.cfg.getConfig().remove("Backups." + backup.getUuid());
        this.cfg.save();
    }

    public void save(Backup backup){
        DateTimeFormatter saveFormat = DateTimeFormatter.ISO_INSTANT;
        String path = "Backups." + backup.getUuid();
        this.cfg.set(path + ".Name", backup.getName());
        this.cfg.set(path + ".BackupPath", backup.getBackupPath());
        this.cfg.set(path + ".Paths", backup.getPaths());
        this.cfg.set(path + ".TimeBetweenBackups", backup.getTimeBetweenBackups());
        this.cfg.set(path + ".LastBackup", saveFormat.format(backup.getLastBackup()));
        this.cfg.set(path + ".NextBackup", saveFormat.format(backup.getNextBackup()));
        this.CACHE.remove(backup.getUuid());
    }

    public Backup get(UUID uuid){
        if(!this.CACHE.containsKey(uuid)){
            DateTimeFormatter saveFormat = DateTimeFormatter.ISO_INSTANT;
            String path = "Backups." + uuid.toString();
            if(!this.cfg.contains(path + ".Paths")) return null;
            List<String> paths = this.cfg.getStringList(path + ".Paths");
            long timeBetweenBackups = this.cfg.getLong(path + ".TimeBetweenBackups");
            
            Instant lastBackup = Instant.from(saveFormat.parse(this.cfg.getString(path + ".LastBackup")));
            Instant nextBackup = Instant.from(saveFormat.parse(this.cfg.getString(path + ".NextBackup")));
            String name = this.cfg.getString(path + ".Name");
            String backupPath = this.cfg.getString(path + ".BackupPath");
            this.CACHE.put(uuid, new Backup(uuid, name, backupPath, new LinkedList<String>(paths), timeBetweenBackups, lastBackup, nextBackup));
        }

        return this.CACHE.containsKey(uuid) ? this.CACHE.get(uuid) : this.get(uuid);
    }

    public Backup[] getAll(){
        if(this.cfg.getSection("Backups") == null) return new Backup[0];
        return this.cfg.getSection("Backups").getKeys(false).stream().map(UUID::fromString).filter(this::has).map(this::get).toArray(Backup[]::new);
    }
    
}

package xyz.theprogramsrc.supermanager.modules.backupmanager;

import java.sql.Date;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.UUID;

import xyz.theprogramsrc.supercoreapi.global.files.yml.YMLConfig;
import xyz.theprogramsrc.supercoreapi.spigot.SpigotModule;
import xyz.theprogramsrc.supermanager.modules.backupmanager.objects.Backup;

public class BackupStorage extends SpigotModule {

    private final LinkedHashMap<UUID, Backup> CACHE = new LinkedHashMap<>();
    private final YMLConfig cfg;
    public BackupStorage(YMLConfig cfg){
        this.cfg = cfg;
        this.cfg.add("DateFormatter", "dd/MM/yyyy HH:mm:ss");
    }

    public String getDateFormatter(){
        return this.cfg.getString("DateFormatter");
    }

    public void delete(Backup backup){
        this.cfg.getConfig().remove("Backups." + backup.getUuid());
        this.cfg.save();
    }

    public void save(Backup backup){
        String path = "Backups." + backup.getUuid();
        this.cfg.set(path + ".Name", backup.getName());
        this.cfg.set(path + ".BackupPath", backup.getBackupPath());
        this.cfg.set(path + ".Paths", backup.getPaths());
        this.cfg.set(path + ".TimeBetweenBackups", backup.getTimeBetweenBackups());
        this.cfg.set(path + ".LastBackup", backup.getLastBackup().toString());
        this.cfg.set(path + ".NextBackup", backup.getNextBackup().toString());
        this.CACHE.remove(backup.getUuid());
    }

    public Backup get(UUID uuid){
        if(!this.CACHE.containsKey(uuid)){
            String path = "Backups." + uuid.toString();
            if(!this.cfg.contains(path + ".Paths")) return null;
            List<String> paths = this.cfg.getStringList(path + ".Paths");
            int timeBetweenBackups = this.cfg.getInt(path + ".TimeBetweenBackups");
            Date lastBackup = Date.valueOf(this.cfg.getString(path + ".LastBackup"));
            Date nextBackup = Date.valueOf(this.cfg.getString(path + ".NextBackup"));
            String name = this.cfg.getString(path + ".Name");
            String backupPath = this.cfg.getString(path + ".BackupPath");
            this.CACHE.put(uuid, new Backup(uuid, name, backupPath, new LinkedList<String>(paths), timeBetweenBackups, lastBackup, nextBackup));
        }

        return this.CACHE.containsKey(uuid) ? this.CACHE.get(uuid) : this.get(uuid);
    }

    public Backup[] getAll(){
        if(this.cfg.getSection("Backups") == null) return new Backup[0];
        return this.cfg.getSection("Backups").getKeys(false).stream().map(UUID::fromString).map(this::get).toArray(Backup[]::new);
    }
    
}

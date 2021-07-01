package xyz.theprogramsrc.supermanager.modules.worldmanager.objects;

import org.bukkit.Bukkit;
import org.bukkit.World;
import xyz.theprogramsrc.supercoreapi.global.files.JsonConfig;
import xyz.theprogramsrc.supercoreapi.global.files.utils.ZipUtils;
import xyz.theprogramsrc.supercoreapi.global.utils.Utils;
import xyz.theprogramsrc.supermanager.modules.worldmanager.WorldManager;

import java.io.File;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.temporal.TemporalAccessor;

public class SWorld {

    private final String name;
    private final WorldManager worldManager;
    private final File backupsFolder;
    private JsonConfig cfg;

    public SWorld(String name, WorldManager worldManager){
        this.name = name;
        this.worldManager = worldManager;
        this.backupsFolder = Utils.folder(new File(worldManager.getModuleFolder(), "backups/"));
        this.cfg = new JsonConfig(new File(this.getBukkitWorld().getWorldFolder(), "WorldManager.json"));
    }

    public WorldManager getWorldManager() {
        return worldManager;
    }

    public String getName() {
        return name;
    }

    public World getBukkitWorld(){
        return Bukkit.getWorld(this.getName());
    }

    public World.Environment getEnvironment(){
        return this.getBukkitWorld().getEnvironment();
    }

    public int onlinePlayers(){
        return this.getBukkitWorld().getPlayers().size();
    }

    public String getLastBackupPath(){
        return this.cfg.contains("last_backups") ? this.cfg.getString("last_backup") : "N/A";
    }

    public String getLastBackupTime(){
        if(this.getLastBackupPath().endsWith(".zip")){
            String fileName = this.getLastBackupPath().replace("_backup.zip", "");
            String time = fileName.substring(fileName.lastIndexOf('-'));
            TemporalAccessor date = DateTimeFormatter.ofPattern("dd.MM.yyyy-HH.mm.ss").parse(time);
            return DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm:ss").format(date);
        }else{
            return "N/A";
        }
    }

    public boolean backup(){
        File worldFolder = this.getBukkitWorld().getWorldFolder();
        if(worldFolder.exists()){
            File backupsFolder = this.backupsFolder;
            if(!backupsFolder.exists()) backupsFolder.mkdirs();
            String time = DateTimeFormatter.ofPattern("dd.MM.yyyy-HH.mm.ss").format(LocalDateTime.now());
            String fileName = String.format("%s-%s_backup.zip", worldFolder.getName(), time);
            String lastBackup = this.cfg.contains("last_backup") ? this.cfg.getString("last_backup") : null;
            try{
                this.cfg.set("last_backup", new File(backupsFolder, fileName).getPath());
                File file = ZipUtils.zipFiles(backupsFolder, fileName, worldFolder);
                return file.exists();
            }catch (Exception e){
                this.cfg.set("last_backup", lastBackup);
                this.worldManager.getPlugin().addError(e);
                this.worldManager.log("&cFailed to backup world '" + this.getName() + "'", true);
                e.printStackTrace();
            }
        }else{
            this.worldManager.log("&cCouldn't find the world folder '" + worldFolder.getName() + "'", true);
        }
        return false;
    }
}

package xyz.theprogramsrc.supermanager.modules.worldmanager.objects;

import java.io.File;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;

import org.bukkit.Bukkit;
import org.bukkit.World;

import xyz.theprogramsrc.supercoreapi.global.files.JsonConfig;
import xyz.theprogramsrc.supercoreapi.global.utils.Utils;
import xyz.theprogramsrc.supercoreapi.libs.zip4j.ZipFile;
import xyz.theprogramsrc.supermanager.modules.worldmanager.WorldManager;
public class SWorld {

    private final String name;
    private final WorldManager worldManager;
    private final File backupsFolder;
    private JsonConfig cfg;
    private DateTimeFormatter fileTimeFormat = DateTimeFormatter.ofPattern("yyyy-MM-dd_HH-mm-ss");

    public SWorld(String name, WorldManager worldManager){
        this.name = name;
        this.worldManager = worldManager;
        this.backupsFolder = Utils.folder(new File(worldManager.getModuleFolder(), "WorldBackups/"));
        this.cfg = new JsonConfig(new File(this.getBukkitWorld().getWorldFolder(), "WorldManager.json"));
        System.out.println("Loading world '" + name + "' with the data: §6" + this.getLastBackupPath());
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
        return this.cfg.contains("last_backup") ? this.cfg.getString("last_backup") : "N/A";
    }

    public String getLastBackupTime(){
        return this.cfg.contains("last_backup_time") ? DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm:ss").format(ZonedDateTime.parse(this.cfg.getString("last_backup_time"))) : "N/A";
    }

    public boolean backup(){
        File worldFolder = this.getBukkitWorld().getWorldFolder();
        if(worldFolder.exists()){
            File backupsFolder = Utils.folder(this.backupsFolder);
            ZonedDateTime now = ZonedDateTime.now();
            String time = this.fileTimeFormat.format(now);
            String fileName = String.format("%s-%s_backup.zip", worldFolder.getName(), time);
            try{
                ZipFile zip = new ZipFile(new File(backupsFolder, fileName));
                zip.addFolder(worldFolder);
                zip.close();
                this.cfg.set("last_backup", zip.getFile().getPath());
                this.cfg.set("last_backup_time", now.toString());
                this.cfg.reload();
                return true;
            }catch (Exception e){
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

package xyz.theprogramsrc.supermanager.modules.backupmanager;

import org.bukkit.entity.Player;

import xyz.theprogramsrc.supercoreapi.spigot.items.SimpleItem;
import xyz.theprogramsrc.supermanager.objects.Module;

public class BackupManager extends Module{


    @Override
    public String getIdentifier() {
        return "backup_manager";
    }

    @Override
    public String getDisplay() {
        return "Backup Manager";
    }

    @Override
    public SimpleItem getDisplayItem() {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public void onAction(Player player) {
        // TODO Auto-generated method stub
        
    }

  
}

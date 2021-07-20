package xyz.theprogramsrc.supermanager.modules.backupmanager.guis;

import org.bukkit.entity.Player;

import xyz.theprogramsrc.supercoreapi.spigot.guis.GUI;
import xyz.theprogramsrc.supercoreapi.spigot.guis.GUIButton;
import xyz.theprogramsrc.supercoreapi.spigot.guis.objects.GUIRows;
import xyz.theprogramsrc.supermanager.L;

public class BackupSettings extends GUI {

    private Runnable onBack;

    public BackupSettings(Player player, Runnable onBack) {
        super(player);
        this.onBack = onBack;
        this.open();
    }

    @Override
    protected GUIRows getRows() {
        return GUIRows.FOUR;
    }

    @Override
    protected String getTitle() {
        return L.BACKUP_MANAGER_SETTINGS_TITLE.toString();
    }

    @Override
    protected GUIButton[] getButtons() {
        return new GUIButton[]{
            new GUIButton(this.getRows().getSize()-1, this.getPreloadedItems().getBackItem(), a-> this.onBack.run())
        };
    }
    
}

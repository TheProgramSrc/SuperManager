package xyz.theprogramsrc.supermanager.modules.backupmanager.guis;

import java.io.File;
import java.util.Arrays;

import org.bukkit.entity.Player;

import xyz.theprogramsrc.supercoreapi.global.translations.Base;
import xyz.theprogramsrc.supercoreapi.libs.xseries.XMaterial;
import xyz.theprogramsrc.supercoreapi.spigot.gui.BrowserGui;
import xyz.theprogramsrc.supercoreapi.spigot.gui.objets.GuiAction;
import xyz.theprogramsrc.supercoreapi.spigot.gui.objets.GuiAction.ClickType;
import xyz.theprogramsrc.supercoreapi.spigot.gui.objets.GuiEntry;
import xyz.theprogramsrc.supercoreapi.spigot.gui.objets.GuiTitle;
import xyz.theprogramsrc.supercoreapi.spigot.items.SimpleItem;
import xyz.theprogramsrc.supermanager.L;
import xyz.theprogramsrc.supermanager.modules.backupmanager.BackupManager;

public class BackupsFolderSelector extends BrowserGui<File>{


    private File currentFolder;

    public BackupsFolderSelector(Player player, File currentFolder) {
        super(player, false);
        this.backEnabled = true;
        this.currentFolder = currentFolder;
        this.open();
    }

    @Override
    public File[] getObjects() {
        File[] files = this.currentFolder.listFiles();
        if(files == null) return new File[0];
        return Arrays.stream(files).filter(f-> f.isDirectory()).sorted().toArray(File[]::new);
    }

    @Override
    public String[] getSearchTags(File file) {
        return new String[]{file.getName()};
    }

    @Override
    public GuiEntry getEntry(File f) {
        SimpleItem item = new SimpleItem(XMaterial.CHEST)
            .setDisplayName("&6" + L.BACKUP_MANAGER_BACKUP_FOLDER_SELECTOR_ITEM_NAME)
            .setLore("&7");
        File[] files = f.listFiles();
        if(files == null) files = new File[0];
        boolean hasFolders = Arrays.stream(files).anyMatch(it -> it.isDirectory());
        if(hasFolders){
            item.addLoreLine("&9" + Base.LEFT_CLICK + "&7 " + L.BACKUP_MANAGER_BACKUP_FOLDER_SELECTOR_ITEM_OPEN_FOLDER);
        }

        item.addLoreLine("&9" + Base.RIGHT_CLICK + "&7 " + L.BACKUP_MANAGER_BACKUP_FOLDER_SELECTOR_ITEM_SELECT_FOLDER).addPlaceholder("{FileName}", f.getName());
        return new GuiEntry(item, a-> {
            if(a.clickType == ClickType.LEFT_CLICK && hasFolders){
                new BackupsFolderSelector(a.player, f){
                    @Override
                    public void onBack(GuiAction guiAction) {
                        BackupsFolderSelector.this.open();
                    }
                };
            }else if(a.clickType == ClickType.RIGHT_CLICK){
                BackupManager.i.backupStorage.setBackupsFolder(f.getPath());
                this.close();
                this.getSuperUtils().sendMessage(a.player, Base.DONE.toString());
            }
        });
    }

    @Override
    public GuiTitle getTitle() {
        return GuiTitle.of(L.BACKUP_MANAGER_BACKUP_FOLDER_SELECTOR_TITLE.toString());
    }
    
}

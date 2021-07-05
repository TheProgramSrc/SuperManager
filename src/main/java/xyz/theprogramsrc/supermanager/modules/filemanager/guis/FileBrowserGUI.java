package xyz.theprogramsrc.supermanager.modules.filemanager.guis;

import org.bukkit.entity.Player;
import xyz.theprogramsrc.supercoreapi.libs.apache.commons.io.FileUtils;
import xyz.theprogramsrc.supercoreapi.global.translations.Base;
import xyz.theprogramsrc.supercoreapi.global.utils.Utils;
import xyz.theprogramsrc.supercoreapi.spigot.guis.BrowserGUI;
import xyz.theprogramsrc.supercoreapi.spigot.guis.GUIButton;
import xyz.theprogramsrc.supercoreapi.spigot.guis.action.ClickAction;
import xyz.theprogramsrc.supercoreapi.spigot.guis.action.ClickType;
import xyz.theprogramsrc.supercoreapi.spigot.items.SimpleItem;
import xyz.theprogramsrc.supercoreapi.libs.xseries.XMaterial;
import xyz.theprogramsrc.supermanager.L;
import xyz.theprogramsrc.supermanager.modules.filemanager.guis.editors.YMLEditor;

import java.io.File;
import java.io.IOException;
import java.util.Arrays;

public class FileBrowserGUI extends BrowserGUI<File> {

    private final File file, rootFolder;

    public FileBrowserGUI(Player player, File currentFolder) {
        super(player);
        this.file = currentFolder;
        this.rootFolder = new File(".");
        this.backEnabled = true;
        this.open();
    }

    @Override
    public File[] getObjects() {
        File[] files = this.file.listFiles();
        if(files == null) files = new File[0];
        return Arrays.stream(files).filter(Utils::nonNull).sorted((f1, f2) -> (f2.isDirectory() ? 1 : 0) - (f1.isDirectory() ? 1 : 0)).toArray(File[]::new);
    }

    @Override
    public GUIButton getButton(File file) {
        SimpleItem item;
        boolean editable = canEdit(file);
        if(file.isDirectory()){ // Directory
            item = new SimpleItem(XMaterial.CHEST)
                    .setDisplayName("&6" + L.FILE_MANAGER_FILE_BROWSER_DIRECTORY_NAME)
                    .setLore(
                            "&7",
                            "&9" + Base.LEFT_CLICK + "&7 " + L.FILE_MANAGER_FILE_BROWSER_DIRECTORY_LEFT,
                            "&9" + Base.RIGHT_CLICK + "&7 " + L.FILE_MANAGER_FILE_BROWSER_DIRECTORY_RIGHT
                    );
        }else if(editable){ // Editable file
            item = new SimpleItem(XMaterial.PAPER)
                    .setDisplayName("&a" + L.FILE_MANAGER_FILE_BROWSER_EDITABLE_NAME)
                    .setLore(
                            "&7",
                            "&9" + Base.LEFT_CLICK + "&7 " + L.FILE_MANAGER_FILE_BROWSER_EDITABLE_LEFT,
                            "&9" + Base.RIGHT_CLICK + "&7 " + L.FILE_MANAGER_FILE_BROWSER_EDITABLE_RIGHT
                    );
        }else{ // Non Editable File
            item = new SimpleItem(XMaterial.GRAY_STAINED_GLASS_PANE)
                    .setDisplayName("&a" + L.FILE_MANAGER_FILE_BROWSER_NON_EDITABLE_NAME)
                    .setLore(
                            "&7",
                            "&9" + Base.RIGHT_CLICK + "&7 " + L.FILE_MANAGER_FILE_BROWSER_NON_EDITABLE_RIGHT
                    );
        }

        item.addPlaceholder("{FileName}", file.getName()).addPlaceholder("{FilePath}", file.getPath());
        return new GUIButton(item, a-> {
            ClickType clickType = a.getAction();
            if(file.isDirectory()){
                if(clickType == ClickType.LEFT_CLICK){
                    new FileBrowserGUI(a.getPlayer(), file){
                        @Override
                        public void onBack(ClickAction clickAction) {
                            FileBrowserGUI.this.open();
                        }
                    };
                }else{
                    File parent = file.getParentFile();
                    if(parent == null || parent == this.rootFolder || file == this.spigotPlugin.getPluginFile() || file == this.spigotPlugin.getPluginFolder()){
                        this.close();
                        this.getSuperUtils().sendMessage(a.getPlayer(), L.FILE_MANAGER_PROTECTED_FILE.options().placeholder("{FileName}", file.getName()).toString());
                    }else{
                        try{
                            FileUtils.deleteDirectory(file);
                            this.open();
                        }catch (IOException e){
                            this.close();
                            this.getSuperUtils().sendMessage(a.getPlayer(), L.FILE_MANAGER_FAILED_TO_DELETE_DIRECTORY.options().placeholder("{FileName}", file.getName()).toString());
                            this.plugin.addError(e);
                            this.log("&cFailed to delete directory:");
                            e.printStackTrace();
                        }
                    }
                }
            }else if(editable){
                if(file.getName().endsWith(".yml")){ // Open YML File Editor
                    new YMLEditor(a.getPlayer(), file){
                        @Override
                        public void onBack(ClickAction clickAction) {
                            FileBrowserGUI.this.open();
                        }
                    };
                }
            }else{
                if(clickType == ClickType.RIGHT_CLICK){
                    this.close();
                    FileUtils.deleteQuietly(file);
                }
            }
        });
    }

    @Override
    protected String getTitle() {
        String path = this.file.equals(this.rootFolder) ? "." : this.file.getPath();
        return "&aFile Browser &7> &9{Path}".replace("{Path}", path);
    }

    private boolean canEdit(final File file){
        String[] extensions = new String[]{
                ".yml",
        };
        return Arrays.stream(extensions).anyMatch(extension -> file.getName().endsWith(extension));
    }
}

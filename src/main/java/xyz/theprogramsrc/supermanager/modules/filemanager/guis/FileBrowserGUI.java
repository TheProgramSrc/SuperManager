package xyz.theprogramsrc.supermanager.modules.filemanager.guis;

import java.io.File;
import java.io.IOException;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.stream.Collectors;

import org.bukkit.entity.Player;

import xyz.theprogramsrc.supercoreapi.global.translations.Base;
import xyz.theprogramsrc.supercoreapi.libs.apache.commons.io.FileUtils;
import xyz.theprogramsrc.supercoreapi.libs.xseries.XMaterial;
import xyz.theprogramsrc.supercoreapi.spigot.gui.BrowserGui;
import xyz.theprogramsrc.supercoreapi.spigot.gui.objets.GuiAction;
import xyz.theprogramsrc.supercoreapi.spigot.gui.objets.GuiAction.ClickType;
import xyz.theprogramsrc.supercoreapi.spigot.gui.objets.GuiEntry;
import xyz.theprogramsrc.supercoreapi.spigot.gui.objets.GuiTitle;
import xyz.theprogramsrc.supercoreapi.spigot.items.SimpleItem;
import xyz.theprogramsrc.supermanager.L;
import xyz.theprogramsrc.supermanager.modules.filemanager.guis.editors.YMLEditor;

public class FileBrowserGUI extends BrowserGui<File> {

    private final File file, rootFolder;

    public FileBrowserGUI(Player player, File currentFolder) {
        super(player, false);
        this.file = currentFolder;
        this.rootFolder = new File(".");
        this.backEnabled = true;
        this.open();
    }

    @Override
    public String[] getSearchTags(File f) {
        return new String[]{f.getName()};
    }

    @Override
    public File[] getObjects() {
        File[] listFiles = this.file.listFiles();
        if(listFiles == null) listFiles = new File[0];
        LinkedList<File> files = new LinkedList<File>();
        LinkedList<File> folders = new LinkedList<File>();
        Arrays.stream(listFiles).forEach(f-> {
            if(f.isDirectory()){
                folders.add(f);
            }else{
                files.add(f);
            }
        });

        LinkedList<File> result = new LinkedList<>();
        result.addAll(folders.stream().sorted((f1, f2) -> f1.getName().hashCode() - f2.getName().hashCode()).collect(Collectors.toList()));
        result.addAll(files.stream().sorted((f1, f2) -> f1.getName().hashCode() - f2.getName().hashCode()).collect(Collectors.toList()));
        return result.toArray(new File[0]);
    }

    @Override
    public GuiEntry getEntry(File file) {
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
        return new GuiEntry(item, a-> {
            ClickType clickType = a.clickType;
            if(file.isDirectory()){
                if(clickType == ClickType.LEFT_CLICK){
                    new FileBrowserGUI(a.player, file){
                        @Override
                        public void onBack(GuiAction clickAction) {
                            FileBrowserGUI.this.open();
                        }
                    };
                }else{
                    File parent = file.getParentFile();
                    if(parent == null || parent == this.rootFolder || file == this.spigotPlugin.getPluginFile() || file == this.spigotPlugin.getPluginFolder()){
                        this.close();
                        this.getSuperUtils().sendMessage(a.player, L.FILE_MANAGER_PROTECTED_FILE.options().placeholder("{FileName}", file.getName()).toString());
                    }else{
                        try{
                            FileUtils.deleteDirectory(file);
                            this.open();
                        }catch (IOException e){
                            this.close();
                            this.getSuperUtils().sendMessage(a.player, L.FILE_MANAGER_FAILED_TO_DELETE_DIRECTORY.options().placeholder("{FileName}", file.getName()).toString());
                            this.plugin.addError(e);
                            this.log("&cFailed to delete directory:");
                            e.printStackTrace();
                        }
                    }
                }
            }else if(editable){
                if(file.getName().endsWith(".yml")){ // Open YML File Editor
                    new YMLEditor(a.player, file){
                        @Override
                        public void onBack(GuiAction clickAction) {
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
    public GuiTitle getTitle() {
        String path = this.file.equals(this.rootFolder) ? "." : this.file.getPath();
        return GuiTitle.of(L.FILE_MANAGER_FILE_BROWSER_TITLE.options().placeholder("{Path}", path).get());
    }

    private boolean canEdit(final File file){
        String[] extensions = new String[]{
                ".yml",
        };
        return Arrays.stream(extensions).anyMatch(extension -> file.getName().endsWith(extension));
    }
}

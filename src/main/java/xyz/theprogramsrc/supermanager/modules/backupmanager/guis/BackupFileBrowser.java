package xyz.theprogramsrc.supermanager.modules.backupmanager.guis;

import java.io.File;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicLong;

import org.bukkit.entity.Player;

import xyz.theprogramsrc.supercoreapi.global.translations.Base;
import xyz.theprogramsrc.supercoreapi.global.utils.Utils;
import xyz.theprogramsrc.supercoreapi.libs.xseries.XMaterial;
import xyz.theprogramsrc.supercoreapi.spigot.dialog.Dialog;
import xyz.theprogramsrc.supercoreapi.spigot.guis.BrowserGUI;
import xyz.theprogramsrc.supercoreapi.spigot.guis.GUIButton;
import xyz.theprogramsrc.supercoreapi.spigot.guis.action.ClickAction;
import xyz.theprogramsrc.supercoreapi.spigot.guis.action.ClickType;
import xyz.theprogramsrc.supercoreapi.spigot.items.SimpleItem;
import xyz.theprogramsrc.supermanager.SuperManager;
import xyz.theprogramsrc.supermanager.modules.backupmanager.objects.Backup;

public class BackupFileBrowser extends BrowserGUI<File>{

    private final LinkedList<String> filesToBackup;
    private File currentFolder;

    public BackupFileBrowser(Player player, File folder, LinkedList<String> filesToBackup){
        super(player);
        this.filesToBackup = filesToBackup;
        this.currentFolder = folder;
        this.backEnabled = true;
        this.open();
    }

    @Override
    protected GUIButton[] getButtons() {
        LinkedList<GUIButton> buttons = new LinkedList<>(Utils.toList(super.getButtons()));
        SimpleItem item = new SimpleItem(XMaterial.EMERALD)
            .setDisplayName("&aSave")
            .setLore(
                "&7",
                "&7Save the selected files (" + this.filesToBackup.size() + ")"
            );
        buttons.add(new GUIButton(47, item, a-> {
            this.getSpigotTasks().runAsyncTask(() -> {
                String[] msg = new String[]{
                    "&6Available Format >> &7Amount&8TimeUnit&6 << Examples:",
                    "&7- &c1s -> 1 Second",
                    "&7- &c1m -> 1 Minute",
                    "&7- &c1h -> 1 Hour",
                    "&7- &c1d -> 1 Day",
                };
                for(String s : msg){
                    this.getSuperUtils().sendMessage(a.getPlayer(), s);
                }
            });

            final AtomicLong atomicSeconds = new AtomicLong();
            new Dialog(a.getPlayer()){
                
                @Override
                public String getTitle(){
                    return "&eSchedule Backup";
                }

                @Override
                public String getSubtitle(){
                    return "&7Write the time between backups.";
                }

                @Override
                public String getActionbar(){
                    return "&cMake sure to use the given format";
                }

                @Override
                public boolean onResult(String input){
                    long seconds = SuperManager.getTimeSecondsFromString(input);
                    if(seconds == 0L){
                        this.getSuperUtils().sendMessage(a.getPlayer(), "&cInvalid time format.");
                        return false;
                    }
                    atomicSeconds.set(seconds);
                    return true;
                }
            }.setRecall(p-> { 
                // First we turn the seconds into millis
                long millis = atomicSeconds.get() * 1000L;
                // Now we create a backup object
                Backup backup = new Backup(UUID.randomUUID().toString(), currentFolder.getName(), millis);
            });
        }));

        return buttons.toArray(new GUIButton[0]);
    }

    @Override
    public GUIButton getButton(File file) {
        SimpleItem item = new SimpleItem(file.isDirectory() ? XMaterial.CHEST : XMaterial.PAPER)
            .setDisplayName("&a" + file.getName())
            .setLore(
                "&7",
                this.filesToBackup.contains(file.getAbsolutePath()) ? "&9Left Click&c Remove&7 from the backup list" : "&9Left Click&a Add&7 to the list"
            );
        
        if(file.isDirectory()){
            item.addLoreLine("&9" + Base.RIGHT_CLICK + "&7 Open Folder");
        }

        return new GUIButton(item, a-> {
            if(file.isDirectory() && a.getAction() == ClickType.RIGHT_CLICK){
                new BackupFileBrowser(a.getPlayer(), file, this.filesToBackup){
                    @Override
                    public void onBack(ClickAction clickAction) {
                        BackupFileBrowser.this.open();
                    }
                };
            }else{
                if(this.filesToBackup.contains(file.getAbsolutePath())){
                    this.filesToBackup.remove(file.getAbsolutePath());
                }else{
                    this.filesToBackup.add(file.getAbsolutePath());
                }
                this.open();
            }
        });
    }

    @Override
    public File[] getObjects() {
        File[] files = this.currentFolder.listFiles();
        if(files == null) return new File[0];
        return Arrays.stream(files).filter(Utils::nonNull).toArray(File[]::new);
    }

    @Override
    protected String getTitle() {
        return "&9Backups &7> &cSelect files";
    }
    
}

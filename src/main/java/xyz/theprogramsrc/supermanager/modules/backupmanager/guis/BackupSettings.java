package xyz.theprogramsrc.supermanager.modules.backupmanager.guis;

import org.bukkit.entity.Player;

import xyz.theprogramsrc.supercoreapi.libs.xseries.XMaterial;
import xyz.theprogramsrc.supercoreapi.spigot.guis.GUI;
import xyz.theprogramsrc.supercoreapi.spigot.guis.GUIButton;
import xyz.theprogramsrc.supercoreapi.spigot.guis.objects.GUIRows;
import xyz.theprogramsrc.supercoreapi.spigot.items.SimpleItem;
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

    private GUIButton getScheduleButton(){
        SimpleItem item = new SimpleItem(XMaterial.CLOCK)
            .setDisplayName("&e" + L.BACKUP_MANAGER_SETTINGS_SCHEDULE_NAME)
            .setLore(
                "&7",
                "&7" + L.BACKUP_MANAGER_SETTINGS_SCHEDULE_LORE
            );
        return new GUIButton(11, item, a-> {
            // First we select the files to backup
            
            /* 
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
                        this.getSuperUtils().sendMessage(a.getPlayer(), "&cInvalid time format.");)
                        return false;
                    }
                    atomicSeconds.set(seconds);
                    return true;
                }
            };
            */
        });
    }
    
}

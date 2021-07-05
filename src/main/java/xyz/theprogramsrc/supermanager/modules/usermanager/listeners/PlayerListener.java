package xyz.theprogramsrc.supermanager.modules.usermanager.listeners;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerMoveEvent;
import xyz.theprogramsrc.supercoreapi.spigot.SpigotModule;
import xyz.theprogramsrc.supercoreapi.spigot.utils.skintexture.SkinTexture;
import xyz.theprogramsrc.supermanager.modules.usermanager.UserStorage;
import xyz.theprogramsrc.supermanager.modules.usermanager.objects.User;

public class PlayerListener extends SpigotModule {

    private final UserStorage userStorage;

    public PlayerListener(UserStorage userStorage){
        this.userStorage = userStorage;
        this.getSpigotTasks().runRepeatingTask(0L, 20L, () -> {
            if(!this.plugin.getPluginDataStorage().isLowResourceUsageEnabled()){
                for(final Player player : Bukkit.getOnlinePlayers()){
                    this.getSpigotTasks().runAsyncTask(() -> {
                        if(!userStorage.exists(player.getUniqueId())){
                            userStorage.save(new User(player.getUniqueId(), player.getName(), null));
                        }
                    });
                }
    
                for(User user : this.userStorage.get()){
                    if(!user.hasSkin() && user.isOnline()){
                        SkinTexture skin = null;
                        try {
                            skin = this.spigotPlugin.getSkinManager().getSkin(user.getPlayer());
                        }catch(Exception ignored){}
                        if(skin != null){
                            user.setSkinTexture(skin);
                            userStorage.save(user);
                        }
                    }
                }
            }
        });
    }

    @EventHandler(priority = EventPriority.NORMAL)
    public void onJoin(PlayerJoinEvent e){
        Player player = e.getPlayer();
        User user;
        if(!this.userStorage.exists(player.getUniqueId())){
            user = new User(player.getUniqueId(), player.getName(), null);
        }else{
            user = this.userStorage.get(player.getUniqueId());
        }

        if(!user.hasSkin()){
            SkinTexture skin = null;
            try {
                skin = this.spigotPlugin.getSkinManager().getSkin(player);
            }catch (Exception ignored){}
            if(skin != null){
                user.setSkinTexture(skin);
            }
        }
        this.userStorage.save(user);
    }

    @EventHandler(priority = EventPriority.LOW)
    public void onMove(PlayerMoveEvent e){
        Player player = e.getPlayer();
        User user = this.userStorage.get(player.getUniqueId());
        if(user != null){
            if(user.hasData("frozen")){
                if(user.getDataAsBoolean("frozen")){
                    e.setCancelled(true);
                }
            }
        }
    }
}

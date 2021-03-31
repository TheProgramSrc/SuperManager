package xyz.theprogramsrc.supermanager.modules.usermanager.listeners;

import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.player.PlayerJoinEvent;
import xyz.theprogramsrc.supercoreapi.spigot.SpigotModule;
import xyz.theprogramsrc.supermanager.modules.usermanager.UserStorage;
import xyz.theprogramsrc.supermanager.modules.usermanager.objects.User;

public class PlayerListener extends SpigotModule {

    private final UserStorage userStorage;

    public PlayerListener(UserStorage userStorage){
        this.userStorage = userStorage;
    }

    @EventHandler
    public void onJoin(PlayerJoinEvent e){
        if(!this.userStorage.exists(e.getPlayer().getUniqueId())){
            this.userStorage.save(User.create(e.getPlayer()));
        }
    }
}

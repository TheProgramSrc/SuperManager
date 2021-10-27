package xyz.theprogramsrc.supermanager.modules.servermanager.listeners;

import java.awt.image.BufferedImage;
import java.io.File;

import javax.imageio.ImageIO;

import org.bukkit.event.EventHandler;
import org.bukkit.event.server.ServerListPingEvent;

import xyz.theprogramsrc.supercoreapi.spigot.SpigotModule;
import xyz.theprogramsrc.supermanager.modules.servermanager.ServerManager;

public class ServerManagerListener extends SpigotModule {

    @EventHandler
    public void onPing(ServerListPingEvent e){
        String line1 = ServerManager.i.cfg.getString("MOTD.Line1", "&aCustom MOTD!");
        String line2 = ServerManager.i.cfg.getString("MOTD.Line2", "&9Thanks to SuperManager :D");
        e.setMotd(this.getSuperUtils().color(line1 + "\n" + line2));
        e.setMaxPlayers(ServerManager.i.cfg.getInt("MaxPlayers", 100));
        File icon = new File(ServerManager.i.cfg.getString("ServerIcon"));
        if(ServerManager.i.cfg.contains("ServerIcon") && icon.exists()){
            try{
                BufferedImage image = ImageIO.read(icon);
                e.setServerIcon(this.spigotPlugin.getServer().loadServerIcon(image));
            }catch(Exception ex){
                ex.printStackTrace();
            }
        }
    }
}

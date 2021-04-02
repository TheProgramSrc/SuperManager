package xyz.theprogramsrc.supermanager.modules.chatchannels.guis;

import org.bukkit.entity.Player;
import xyz.theprogramsrc.supercoreapi.spigot.guis.GUI;
import xyz.theprogramsrc.supercoreapi.spigot.guis.objects.GUIRows;
import xyz.theprogramsrc.supermanager.modules.chatchannels.ChatChannelsStorage;

public class ChatChannelsSettings extends GUI {

    private ChatChannelsStorage storage;

    public ChatChannelsSettings(Player player) {
        super(player);
        this.storage = ChatChannelsStorage.i;
        this.open();
    }

    @Override
    protected GUIRows getRows() {
        return GUIRows.THREE;
    }

    @Override
    protected String getTitle() {
        return null;
    }
}

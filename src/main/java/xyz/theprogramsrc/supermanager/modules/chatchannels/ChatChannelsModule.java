package xyz.theprogramsrc.supermanager.modules.chatchannels;

import java.io.File;

import org.bukkit.entity.Player;
import org.bukkit.event.HandlerList;
import xyz.theprogramsrc.supercoreapi.libs.xseries.XMaterial;
import xyz.theprogramsrc.supercoreapi.spigot.items.SimpleItem;
import xyz.theprogramsrc.supermanager.L;
import xyz.theprogramsrc.supermanager.modules.chatchannels.commands.ChatChannelCommand;
import xyz.theprogramsrc.supermanager.modules.chatchannels.guis.ChatChannelBrowser;
import xyz.theprogramsrc.supermanager.modules.chatchannels.listeners.ChatChannelsManager;
import xyz.theprogramsrc.supermanager.modules.chatchannels.storage.ChatChannelsDataManager;
import xyz.theprogramsrc.supermanager.objects.Module;

public class ChatChannelsModule extends Module {

    public static ChatChannelsModule i;
    private ChatChannelsManager chatChannelsManager;
    private ChatChannelsDataManager chatChannelsDataManager;

    @Override
    public void onEnable() {
        i = this;
        this.chatChannelsDataManager = new ChatChannelsDataManager(new File(this.getModuleFolder(), "ChatChannels.yml"));
        this.chatChannelsManager = new ChatChannelsManager(this.chatChannelsDataManager);
        new ChatChannelCommand();
    }

    @Override
    public void onDisable() {
        HandlerList.unregisterAll(this.chatChannelsManager);
    }

    @Override
    public String getDisplay() {
        return L.CHAT_CHANNELS_DISPLAY.toString();
    }

    @Override
    public String getIdentifier() {
        return "chat_channels";
    }

    @Override
    public SimpleItem getDisplayItem() {
        return new SimpleItem(XMaterial.OAK_SIGN)
                .setDisplayName("&c" + L.CHAT_CHANNELS_NAME)
                .setLore(
                        "&7",
                        "&7" + L.CHAT_CHANNELS_LORE
                );
    }

    @Override
    public void onAction(Player player) {
        new ChatChannelBrowser(player, this.chatChannelsDataManager);
    }
}

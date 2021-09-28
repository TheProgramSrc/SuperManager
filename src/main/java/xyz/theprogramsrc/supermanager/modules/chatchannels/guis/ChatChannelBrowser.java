package xyz.theprogramsrc.supermanager.modules.chatchannels.guis;

import java.time.format.DateTimeFormatter;

import org.bukkit.entity.Player;

import xyz.theprogramsrc.supercoreapi.global.translations.Base;
import xyz.theprogramsrc.supercoreapi.libs.xseries.XMaterial;
import xyz.theprogramsrc.supercoreapi.spigot.gui.BrowserGui;
import xyz.theprogramsrc.supercoreapi.spigot.gui.objets.GuiAction;
import xyz.theprogramsrc.supercoreapi.spigot.gui.objets.GuiAction.ClickType;
import xyz.theprogramsrc.supercoreapi.spigot.gui.objets.GuiEntry;
import xyz.theprogramsrc.supercoreapi.spigot.gui.objets.GuiModel;
import xyz.theprogramsrc.supercoreapi.spigot.gui.objets.GuiTitle;
import xyz.theprogramsrc.supercoreapi.spigot.items.SimpleItem;
import xyz.theprogramsrc.supermanager.L;
import xyz.theprogramsrc.supermanager.guis.MainGUI;
import xyz.theprogramsrc.supermanager.modules.chatchannels.objects.ChatChannel;
import xyz.theprogramsrc.supermanager.modules.chatchannels.storage.ChatChannelsDataManager;

public class ChatChannelBrowser extends BrowserGui<ChatChannel> {

    private final ChatChannelsDataManager chatChannelsDataManager;

    public ChatChannelBrowser(Player player, ChatChannelsDataManager chatChannelsDataManager) {
        super(player, false);
        this.chatChannelsDataManager = chatChannelsDataManager;
        this.backEnabled = true;
        this.open();
    }

    @Override 
    public void onBack(GuiAction clickAction) {
        new MainGUI(clickAction.player);
    }

    @Override
    public ChatChannel[] getObjects() {
        return this.chatChannelsDataManager.getChannels();
    }

    @Override
    public String[] getSearchTags(ChatChannel c) { 
        return new String[]{c.getName(), c.getUuid().toString()};
    }

    @Override
    public GuiEntry getEntry(final ChatChannel chatChannel) {
        SimpleItem item = new SimpleItem(XMaterial.OAK_SIGN)
                .setDisplayName("&a" + L.CHAT_CHANNELS_BROWSER_ITEM_NAME)
                .setLore(
                    "&7",
                    "&7" + L.CHAT_CHANNELS_BROWSER_ITEM_CREATED_AT,
                    "&7" + L.CHAT_CHANNELS_BROWSER_ITEM_ID,
                    "&7"
                );
        
        if(!chatChannel.isGlobal()){
            item.addLoreLines(
                "&9" + Base.LEFT_CLICK + "&7 " + L.CHAT_CHANNELS_BROWSER_ITEM_LEFT_ACTION,
                "&9" + Base.RIGHT_CLICK + "&7 " + L.CHAT_CHANNELS_BROWSER_ITEM_RIGHT_ACTION
            );
        }
        
        item.addPlaceholder("{ChannelName}", chatChannel.getName())
            .addPlaceholder("{ChannelId}", chatChannel.getUuid().toString())
            .addPlaceholder("{CreatedAt}", DateTimeFormatter.ofPattern(this.chatChannelsDataManager.globalDateFormat()).format(chatChannel.getInstantCreated()));
        return new GuiEntry(item, a->{
            if(a.clickType == ClickType.LEFT_CLICK){
                this.chatChannelsDataManager.setGlobalChannel(chatChannel.getUuid().toString());
            }else if(a.clickType == ClickType.RIGHT_CLICK && !chatChannel.isGlobal()){
                this.chatChannelsDataManager.removeChannel(chatChannel.getUuid());
            }
            this.open();
        });
    }

    @Override
    public void onBuild(GuiModel model) {
        super.onBuild(model);
        SimpleItem item = new SimpleItem(XMaterial.COMMAND_BLOCK)
                .setDisplayName("&a" + L.CHAT_CHANNELS_BROWSER_SETTINGS_NAME)
                .setLore(
                        "&7",
                        "&7" + L.CHAT_CHANNELS_BROWSER_SETTINGS_LORE
                );
        model.setButton(47, new GuiEntry(item, a -> {
            new ChatChannelsSettings(a.player, this.chatChannelsDataManager, this::open);
        }));

    }

    @Override
    public GuiTitle getTitle() {
        return GuiTitle.of(L.CHAT_CHANNELS_BROWSER_TITLE.toString());
    }
}

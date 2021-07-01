package xyz.theprogramsrc.supermanager.modules.chatchannels.guis;

import org.bukkit.entity.Player;
import xyz.theprogramsrc.supercoreapi.global.translations.Base;
import xyz.theprogramsrc.supercoreapi.global.utils.Utils;
import xyz.theprogramsrc.supercoreapi.libs.xseries.XMaterial;
import xyz.theprogramsrc.supercoreapi.spigot.guis.BrowserGUI;
import xyz.theprogramsrc.supercoreapi.spigot.guis.GUIButton;
import xyz.theprogramsrc.supercoreapi.spigot.guis.action.ClickAction;
import xyz.theprogramsrc.supercoreapi.spigot.guis.action.ClickType;
import xyz.theprogramsrc.supercoreapi.spigot.items.SimpleItem;
import xyz.theprogramsrc.supermanager.L;
import xyz.theprogramsrc.supermanager.guis.MainGUI;
import xyz.theprogramsrc.supermanager.modules.chatchannels.objects.ChatChannel;
import xyz.theprogramsrc.supermanager.modules.chatchannels.storage.ChatChannelsDataManager;

import java.util.LinkedList;

public class ChatChannelBrowser extends BrowserGUI<ChatChannel> {

    private final ChatChannelsDataManager chatChannelsDataManager;

    public ChatChannelBrowser(Player player, ChatChannelsDataManager chatChannelsDataManager) {
        super(player);
        this.chatChannelsDataManager = chatChannelsDataManager;
        this.backEnabled = true;
        this.open();
    }

    @Override
    public void onBack(ClickAction clickAction) {
        new MainGUI(clickAction.getPlayer());
    }

    @Override
    public ChatChannel[] getObjects() {
        return this.chatChannelsDataManager.getChannels();
    }

    @Override
    public GUIButton getButton(final ChatChannel chatChannel) {
        SimpleItem item = new SimpleItem(XMaterial.OAK_SIGN)
                .setDisplayName("&a" + L.CHAT_CHANNELS_BROWSER_ITEM_NAME)
                .setLore("&7");
        if(!this.chatChannelsDataManager.globalChannel().equals(chatChannel.getName())){
            item.addLoreLine("&9" + Base.LEFT_CLICK + "&7 " + L.CHAT_CHANNELS_BROWSER_ITEM_LEFT_ACTION);
        }
        item.addLoreLine("&9" + Base.RIGHT_CLICK + "&7 " + L.CHAT_CHANNELS_BROWSER_ITEM_RIGHT_ACTION);
        item.addPlaceholder("{ChannelName}", chatChannel.getName());
        return new GUIButton(item, a->{
            if(a.getAction() == ClickType.LEFT_CLICK){
                this.chatChannelsDataManager.setGlobalChannel(chatChannel.getName());
            }else if(a.getAction() == ClickType.RIGHT_CLICK){
                this.chatChannelsDataManager.removeChannel(chatChannel.getUuid());
            }
            this.open();
        });
    }

    @Override
    protected GUIButton[] getButtons() {
        LinkedList<GUIButton> buttons = new LinkedList<>(Utils.toList(super.getButtons()));
        SimpleItem item = new SimpleItem(XMaterial.COMMAND_BLOCK)
                .setDisplayName("&a" + L.CHAT_CHANNELS_BROWSER_SETTINGS_NAME)
                .setLore(
                        "&7",
                        "&7" + L.CHAT_CHANNELS_BROWSER_SETTINGS_LORE
                );
        buttons.add(new GUIButton(47, item, a-> new ChatChannelsSettings(a.getPlayer(), this.chatChannelsDataManager, this::open)));
        return buttons.toArray(new GUIButton[0]);
    }

    @Override
    protected String getTitle() {
        return L.CHAT_CHANNELS_BROWSER_TITLE.toString();
    }
}

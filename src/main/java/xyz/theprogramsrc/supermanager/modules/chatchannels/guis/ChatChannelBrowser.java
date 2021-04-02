package xyz.theprogramsrc.supermanager.modules.chatchannels.guis;

import org.bukkit.entity.Player;
import xyz.theprogramsrc.supercoreapi.global.translations.Base;
import xyz.theprogramsrc.supercoreapi.global.utils.Utils;
import xyz.theprogramsrc.supercoreapi.spigot.dialog.Dialog;
import xyz.theprogramsrc.supercoreapi.spigot.guis.BrowserGUI;
import xyz.theprogramsrc.supercoreapi.spigot.guis.GUIButton;
import xyz.theprogramsrc.supercoreapi.spigot.guis.action.ClickAction;
import xyz.theprogramsrc.supercoreapi.spigot.guis.action.ClickType;
import xyz.theprogramsrc.supercoreapi.spigot.items.SimpleItem;
import xyz.theprogramsrc.supercoreapi.spigot.utils.xseries.XMaterial;
import xyz.theprogramsrc.supermanager.L;
import xyz.theprogramsrc.supermanager.guis.MainGUI;
import xyz.theprogramsrc.supermanager.modules.chatchannels.ChatChannelsStorage;
import xyz.theprogramsrc.supermanager.modules.chatchannels.objects.ChatChannel;

import java.util.LinkedList;

public class ChatChannelBrowser extends BrowserGUI<ChatChannel> {

    private final ChatChannelsStorage chatChannelsStorage;

    public ChatChannelBrowser(Player player, ChatChannelsStorage chatChannelsStorage) {
        super(player);
        this.chatChannelsStorage = chatChannelsStorage;
        this.backEnabled = true;
        this.open();
    }

    @Override
    public void onBack(ClickAction clickAction) {
        new MainGUI(clickAction.getPlayer());
    }

    @Override
    public ChatChannel[] getObjects() {
        return this.chatChannelsStorage.all();
    }

    @Override
    public GUIButton getButton(final ChatChannel chatChannel) {
        SimpleItem item = new SimpleItem(XMaterial.OAK_SIGN)
                .setDisplayName("&a" + L.CHAT_CHANNELS_BROWSER_ITEM_NAME)
                .setLore("&7");
        if(!this.chatChannelsStorage.defaultChannel().equals(chatChannel.getName())){
            item.addLoreLine("&9" + Base.LEFT_CLICK + "&7 " + L.CHAT_CHANNELS_BROWSER_ITEM_LEFT_ACTION);
        }
        item.addLoreLine("&9" + Base.RIGHT_CLICK + "&7 " + L.CHAT_CHANNELS_BROWSER_ITEM_RIGHT_ACTION);
        item.addPlaceholder("{ChannelName}", chatChannel.getName());
        return new GUIButton(item, a->{
            if(a.getAction() == ClickType.LEFT_CLICK){
                this.chatChannelsStorage.setGlobalChannel(chatChannel.getName());
            }else if(a.getAction() == ClickType.RIGHT_CLICK){
                this.chatChannelsStorage.remove(chatChannel.getName());
            }
            this.open();
        });
    }

    @Override
    protected GUIButton[] getButtons() {
        LinkedList<GUIButton> buttons = new LinkedList<>(Utils.toList(super.getButtons()));
        SimpleItem item = new SimpleItem(XMaterial.ANVIL)
                .setDisplayName("&a" + L.CHAT_CHANNELS_BROWSER_CREATE_NAME)
                .setLore(
                        "&7",
                        "&7" + L.CHAT_CHANNELS_BROWSER_CREATE_LORE
                );
        buttons.add(new GUIButton(47, item, a-> new Dialog(a.getPlayer()){
            @Override
            public String getTitle() {
                return L.CHAT_CHANNELS_CREATOR_DIALOG_TITLE.toString();
            }

            @Override
            public String getSubtitle() {
                return L.CHAT_CHANNELS_CREATOR_DIALOG_SUBTITLE.toString();
            }

            @Override
            public String getActionbar() {
                return L.CHAT_CHANNELS_CREATOR_DIALOG_ACTIONBAR.toString();
            }

            @Override
            public boolean onResult(String s) {
                if(s.contains(" ")){
                    this.getSuperUtils().sendMessage(this.getPlayer(), L.CHAT_CHANNELS_CONTAINS_SPACES.toString());
                }else if(ChatChannelBrowser.this.chatChannelsStorage.get(s) != null){
                    this.getSuperUtils().sendMessage(this.getPlayer(), L.CHAT_CHANNELS_ALREADY_EXISTS.toString());
                }else{
                    ChatChannelBrowser.this.chatChannelsStorage.save(new ChatChannel(s, ChatChannelBrowser.this.chatChannelsStorage.getDefaultMax()));
                    ChatChannelBrowser.this.open();
                    return true;
                }
                return false;
            }
        }));
        return buttons.toArray(new GUIButton[0]);
    }

    @Override
    protected String getTitle() {
        return L.CHAT_CHANNELS_BROWSER_TITLE.toString();
    }
}

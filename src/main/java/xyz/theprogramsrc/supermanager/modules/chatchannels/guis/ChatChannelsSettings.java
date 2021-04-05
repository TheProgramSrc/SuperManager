package xyz.theprogramsrc.supermanager.modules.chatchannels.guis;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import xyz.theprogramsrc.supercoreapi.spigot.dialog.Dialog;
import xyz.theprogramsrc.supercoreapi.spigot.guis.GUI;
import xyz.theprogramsrc.supercoreapi.spigot.guis.GUIButton;
import xyz.theprogramsrc.supercoreapi.spigot.guis.objects.GUIRows;
import xyz.theprogramsrc.supercoreapi.spigot.items.SimpleItem;
import xyz.theprogramsrc.supercoreapi.spigot.utils.xseries.XMaterial;
import xyz.theprogramsrc.supermanager.L;
import xyz.theprogramsrc.supermanager.modules.chatchannels.ChatChannelsStorage;
import xyz.theprogramsrc.supermanager.modules.chatchannels.objects.ChatChannel;

public class ChatChannelsSettings extends GUI {

    private final ChatChannelsStorage storage;
    private final Runnable back;

    public ChatChannelsSettings(Player player, Runnable back) {
        super(player);
        this.back = back;
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

    @Override
    protected GUIButton[] getButtons() {
        return new GUIButton[]{
                new GUIButton(this.getRows().getSize()-1, this.getPreloadedItems().getBackItem(), a-> this.back.run()),
                this.getCreateChatChannelButton(),

        };
    }

    private GUIButton getCreateChatChannelButton(){
        SimpleItem item = new SimpleItem(XMaterial.ANVIL)
                .setDisplayName("&a" + L.CHAT_CHANNELS_SETTINGS_CREATE_NAME)
                .setLore(
                        "&7",
                        "&7" + L.CHAT_CHANNELS_SETTINGS_CREATE_LORE
                );
        return new GUIButton(10, item, a-> new Dialog(a.getPlayer()){
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
                }else if(ChatChannelsSettings.this.storage.existsChannel(s)){
                    this.getSuperUtils().sendMessage(this.getPlayer(), L.CHAT_CHANNELS_ALREADY_EXISTS.toString());
                }else{
                    ChatChannelsSettings.this.storage.saveChannel(new ChatChannel(s, Bukkit.getMaxPlayers()));
                    ChatChannelsSettings.this.open();
                    return true;
                }
                return false;
            }
        });
    }
}

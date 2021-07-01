package xyz.theprogramsrc.supermanager.modules.chatchannels.guis;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import xyz.theprogramsrc.supercoreapi.libs.xseries.XMaterial;
import xyz.theprogramsrc.supercoreapi.spigot.dialog.Dialog;
import xyz.theprogramsrc.supercoreapi.spigot.guis.GUI;
import xyz.theprogramsrc.supercoreapi.spigot.guis.GUIButton;
import xyz.theprogramsrc.supercoreapi.spigot.guis.objects.GUIRows;
import xyz.theprogramsrc.supercoreapi.spigot.items.SimpleItem;
import xyz.theprogramsrc.supermanager.L;
import xyz.theprogramsrc.supermanager.modules.chatchannels.storage.ChatChannelsDataManager;
import xyz.theprogramsrc.supermanager.modules.chatchannels.objects.ChatChannel;

public class ChatChannelsSettings extends GUI {

    private final ChatChannelsDataManager chatChannelsDataManager;
    private final Runnable back;

    public ChatChannelsSettings(Player player, ChatChannelsDataManager chatChannelsDataManager, Runnable back) {
        super(player);
        this.back = back;
        this.chatChannelsDataManager = chatChannelsDataManager;
        this.open();
    }

    @Override
    protected GUIRows getRows() {
        return GUIRows.THREE;
    }

    @Override
    protected String getTitle() {
        return L.CHAT_CHANNELS_SETTINGS_TITLE.toString();
    }

    @Override
    protected GUIButton[] getButtons() {
        return new GUIButton[]{
                new GUIButton(this.getRows().getSize()-1, this.getPreloadedItems().getBackItem(), a-> this.back.run()),
                this.getCreateChatChannelButton(),
                this.getUpdateFormatButton(),

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
                }else if(ChatChannelsSettings.this.chatChannelsDataManager.hasChannel(s)){
                    this.getSuperUtils().sendMessage(this.getPlayer(), L.CHAT_CHANNELS_ALREADY_EXISTS.toString());
                }else{
                    ChatChannelsSettings.this.chatChannelsDataManager.saveChannel(new ChatChannel(s, Bukkit.getMaxPlayers()));
                    ChatChannelsSettings.this.open();
                    return true;
                }
                return false;
            }
        });
    }

    private GUIButton getUpdateFormatButton(){
        SimpleItem item = new SimpleItem(XMaterial.PAPER)
                .setDisplayName("&a" + L.CHAT_CHANNELS_SETTINGS_UPDATE_FORMAT_NAME)
                .setLore(
                        "&7",
                        "&7" + L.CHAT_CHANNELS_SETTINGS_UPDATE_FORMAT_LORE,
                        "&7" + L.CHAT_CHANNELS_SETTINGS_UPDATE_FORMAT_PREVIEW
                ).addPlaceholder("{ChatFormat}", this.chatChannelsDataManager.format());
        return new GUIButton(12, item, a-> {
            this.getSuperUtils().sendMessage(a.getPlayer(), "&9Available Placeholders:");
            this.getSuperUtils().sendMessage(a.getPlayer(), "&e{Message} &7- &cMessage");
            this.getSuperUtils().sendMessage(a.getPlayer(), "&e{Channel} &7- &cChannel Name");
            this.getSuperUtils().sendMessage(a.getPlayer(), "&e{Player} &7- &cPlayer Name");
            new Dialog(a.getPlayer()){
                @Override
                public String getTitle() {
                    return L.CHAT_CHANNELS_UPDATE_FORMAT_DIALOG_TITLE.toString();
                }

                @Override
                public String getSubtitle() {
                    return L.CHAT_CHANNELS_UPDATE_FORMAT_DIALOG_SUBTITLE.toString();
                }

                @Override
                public String getActionbar() {
                    return L.CHAT_CHANNELS_UPDATE_FORMAT_DIALOG_ACTIONBAR.toString();
                }

                @Override
                public boolean onResult(String s) {
                    ChatChannelsSettings.this.chatChannelsDataManager.setFormat(s);
                    ChatChannelsSettings.this.open();
                    return true;
                }
            }.addPlaceholder("{ChatFormat}", this.chatChannelsDataManager.format());
        });
    }
}

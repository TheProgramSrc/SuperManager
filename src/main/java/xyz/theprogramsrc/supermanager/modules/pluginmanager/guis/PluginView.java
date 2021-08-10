package xyz.theprogramsrc.supermanager.modules.pluginmanager.guis;

import java.util.LinkedList;

import org.bukkit.entity.Player;

import xyz.theprogramsrc.supercoreapi.Recall;
import xyz.theprogramsrc.supercoreapi.global.utils.Utils;
import xyz.theprogramsrc.supercoreapi.libs.xseries.XMaterial;
import xyz.theprogramsrc.supercoreapi.spigot.dialog.Dialog;
import xyz.theprogramsrc.supercoreapi.spigot.guis.GUI;
import xyz.theprogramsrc.supercoreapi.spigot.guis.GUIButton;
import xyz.theprogramsrc.supercoreapi.spigot.guis.action.ClickAction;
import xyz.theprogramsrc.supercoreapi.spigot.guis.objects.GUIRows;
import xyz.theprogramsrc.supercoreapi.spigot.items.SimpleItem;
import xyz.theprogramsrc.supermanager.L;
import xyz.theprogramsrc.supermanager.SuperManager;
import xyz.theprogramsrc.supermanager.modules.pluginmanager.PluginManager;
import xyz.theprogramsrc.supermanager.modules.pluginmanager.objects.SPlugin;

public class PluginView extends GUI {

    private final SPlugin sPlugin;
    private final Recall<ClickAction> back;

    public PluginView(SPlugin sPlugin, Player player, Recall<ClickAction> onBack){
        super(player);
        this.sPlugin = sPlugin;
        this.back = onBack;
        this.open();
    }

    @Override
    protected GUIRows getRows() {
        return GUIRows.FOUR;
    }

    @Override
    protected String getTitle() {
        return L.PLUGIN_VIEW_TITLE.options().placeholder("{PluginName}", this.sPlugin.getName()).toString();
    }

    @Override
    protected GUIButton[] getButtons() {
        LinkedList<GUIButton> buttons = new LinkedList<>();
        buttons.add(new GUIButton(this.getRows().getSize()-1, this.getPreloadedItems().getBackItem(), this.back::run));
        buttons.add(this.getCheckUpdatesButton());
        if(this.sPlugin.isUpdateAvailable()){
            buttons.add(this.getDownloadUpdateButton());
        }
        return buttons.toArray(new GUIButton[0]);
    }

    private GUIButton getCheckUpdatesButton(){
        SimpleItem item = new SimpleItem(XMaterial.EMERALD)
                .setDisplayName("&a" + L.PLUGIN_VIEW_CHECK_UPDATE_ITEM_NAME)
                .setLore(
                        "&7",
                        "&7" + L.PLUGIN_VIEW_CHECK_UPDATE_ITEM_LORE
                );

        return new GUIButton(11, item, a->{
            this.close();
            if(!Utils.isConnected()){
                this.getSuperUtils().sendMessage(a.getPlayer(), this.getSettings().getPrefix() + "&c" + L.NO_CONNECTION);
            }else{
                boolean updateAvailable = this.sPlugin.isUpdateAvailable();
                if(this.sPlugin.getLatestVersion() == null){
                    this.getSuperUtils().sendMessage(a.getPlayer(), this.getSettings().getPrefix() + "&c" + L.PLUGIN_MANAGER_FAILED_TO_CHECK_FOR_UPDATES.options().placeholder("{PluginName}", this.sPlugin.getName()));
                }else{
                    if(updateAvailable){
                        this.getSuperUtils().sendMessage(a.getPlayer(), this.getSettings().getPrefix() + L.PLUGIN_MANAGER_NEW_UPDATE_AVAILABLE.options().placeholder("{PluginName}", this.sPlugin.getName()).placeholder("{CurrentVersion}", this.sPlugin.getCurrentVersion()).placeholder("{NewVersion}", this.sPlugin.getLatestVersion()));
                    }else{
                        this.getSuperUtils().sendMessage(a.getPlayer(), this.getSettings().getPrefix() + L.PLUGIN_MANAGER_ALREADY_UP_TO_DATE.options().placeholder("{PluginName}", this.sPlugin.getName()));
                    }
                }
            }
        });
    }

    private GUIButton getDownloadUpdateButton(){
        SimpleItem item = new SimpleItem(XMaterial.ANVIL)
                .setDisplayName("&a" + L.PLUGIN_VIEW_DOWNLOAD_UPDATE_ITEM_NAME)
                .setLore(
                        "&7",
                        "&7" + L.PLUGIN_VIEW_DOWNLOAD_UPDATE_ITEM_LORE
                ).addPlaceholder("{PluginName}", this.sPlugin.getName());
        return new GUIButton(15, item, a-> {
            this.close();
            boolean premium = this.sPlugin.isPremium();
            if(premium){
                if(!SuperManager.validateToken()){
                    // Ask for token
                    this.getSuperUtils().sendMessage(this.getPlayer(), this.getSettings().getPrefix() + L.TOKEN_WILL_NOT_BE_SHARED);
                    new Dialog(a.getPlayer()){
                        @Override
                        public String getTitle() {
                            return L.DIALOG_TOKEN_INPUT_TITLE.toString();
                        }

                        @Override
                        public String getSubtitle() {
                            return L.DIALOG_TOKEN_INPUT_SUBTITLE.toString();
                        }

                        @Override
                        public String getActionbar() {
                            return L.DIALOG_TOKEN_INPUT_ACTIONBAR.toString();
                        }

                        @Override
                        public boolean onResult(String s) {
                            this.getSettings().getConfig().set("songoda-token", s);
                            this.getSettings().getConfig().load();
                            SuperManager.token = s;
                            this.getSuperUtils().sendMessage(this.getPlayer(), this.getSettings().getPrefix() + L.TOKEN_SAVED);
                            return true;
                        }
                    };
                }else{
                    this.startDownload(a);
                }
            }else{
                this.startDownload(a);
            }
        });
    }

    private void startDownload(ClickAction a){
        this.getSuperUtils().sendMessage(a.getPlayer(), this.getSettings().getPrefix() + L.PLUGIN_MANAGER_DOWNLOADING_UPDATE.options().placeholder("{PluginName}", this.sPlugin.getName()));
        this.sPlugin.downloadUpdate(a.getPlayer());
    }
}
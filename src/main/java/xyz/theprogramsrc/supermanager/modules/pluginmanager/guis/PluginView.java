package xyz.theprogramsrc.supermanager.modules.pluginmanager.guis;

import java.util.function.Consumer;

import org.bukkit.entity.Player;

import xyz.theprogramsrc.supercoreapi.global.utils.Utils;
import xyz.theprogramsrc.supercoreapi.libs.xseries.XMaterial;
import xyz.theprogramsrc.supercoreapi.spigot.dialog.Dialog;
import xyz.theprogramsrc.supercoreapi.spigot.gui.Gui;
import xyz.theprogramsrc.supercoreapi.spigot.gui.objets.GuiAction;
import xyz.theprogramsrc.supercoreapi.spigot.gui.objets.GuiEntry;
import xyz.theprogramsrc.supercoreapi.spigot.gui.objets.GuiModel;
import xyz.theprogramsrc.supercoreapi.spigot.gui.objets.GuiRows;
import xyz.theprogramsrc.supercoreapi.spigot.gui.objets.GuiTitle;
import xyz.theprogramsrc.supercoreapi.spigot.items.SimpleItem;
import xyz.theprogramsrc.supermanager.L;
import xyz.theprogramsrc.supermanager.SuperManager;
import xyz.theprogramsrc.supermanager.modules.pluginmanager.objects.SPlugin;

public class PluginView extends Gui { 

    private final SPlugin sPlugin;
    private final Consumer<GuiAction> back;

    public PluginView(SPlugin sPlugin, Player player, Consumer<GuiAction> onBack){
        super(player, false);
        this.sPlugin = sPlugin;
        this.back = onBack;
        this.open();
    }

    @Override
    public GuiRows getRows() {
        return GuiRows.FOUR;
    }

    @Override
    public GuiTitle getTitle() {
        return GuiTitle.of(L.PLUGIN_VIEW_TITLE.options().placeholder("{PluginName}", this.sPlugin.getName()).toString());
    }
    
    @Override
    public void onBuild(GuiModel model) {
        model.setButton(this.getRows().size -1 , new GuiEntry(this.getPreloadedItems().getBackItem(), this.back::accept));
        model.setButton(11, this.getCheckUpdatesButton());
        if(this.sPlugin.isUpdateAvailable()){
            model.setButton(15, this.getDownloadUpdateButton());
        }
    }

    private GuiEntry getCheckUpdatesButton(){
        SimpleItem item = new SimpleItem(XMaterial.EMERALD)
                .setDisplayName("&a" + L.PLUGIN_VIEW_CHECK_UPDATE_ITEM_NAME)
                .setLore(
                        "&7",
                        "&7" + L.PLUGIN_VIEW_CHECK_UPDATE_ITEM_LORE
                );

        return new GuiEntry(item, a->{
            this.close();
            if(!Utils.isConnected()){
                this.getSuperUtils().sendMessage(a.player, this.getSettings().getPrefix() + "&c" + L.NO_CONNECTION);
            }else{
                boolean updateAvailable = this.sPlugin.isUpdateAvailable();
                if(this.sPlugin.getLatestVersion() == null){
                    this.getSuperUtils().sendMessage(a.player, this.getSettings().getPrefix() + "&c" + L.PLUGIN_MANAGER_FAILED_TO_CHECK_FOR_UPDATES.options().placeholder("{PluginName}", this.sPlugin.getName()));
                }else{
                    if(updateAvailable){
                        this.getSuperUtils().sendMessage(a.player, this.getSettings().getPrefix() + L.PLUGIN_MANAGER_NEW_UPDATE_AVAILABLE.options().placeholder("{PluginName}", this.sPlugin.getName()).placeholder("{CurrentVersion}", this.sPlugin.getCurrentVersion()).placeholder("{NewVersion}", this.sPlugin.getLatestVersion()));
                    }else{
                        this.getSuperUtils().sendMessage(a.player, this.getSettings().getPrefix() + L.PLUGIN_MANAGER_ALREADY_UP_TO_DATE.options().placeholder("{PluginName}", this.sPlugin.getName()));
                    }
                }
            }
        });
    }

    private GuiEntry getDownloadUpdateButton(){
        SimpleItem item = new SimpleItem(XMaterial.ANVIL)
                .setDisplayName("&a" + L.PLUGIN_VIEW_DOWNLOAD_UPDATE_ITEM_NAME)
                .setLore(
                        "&7",
                        "&7" + L.PLUGIN_VIEW_DOWNLOAD_UPDATE_ITEM_LORE
                ).addPlaceholder("{PluginName}", this.sPlugin.getName());
        return new GuiEntry(item, a-> {
            this.close();
            boolean premium = this.sPlugin.isPremium();
            if(premium){
                if(!SuperManager.validateToken()){
                    // Ask for token
                    this.getSuperUtils().sendMessage(this.player, this.getSettings().getPrefix() + L.TOKEN_WILL_NOT_BE_SHARED);
                    new Dialog(a.player){
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

    private void startDownload(GuiAction a){
        this.getSuperUtils().sendMessage(a.player, this.getSettings().getPrefix() + L.PLUGIN_MANAGER_DOWNLOADING_UPDATE.options().placeholder("{PluginName}", this.sPlugin.getName()));
        this.sPlugin.downloadUpdate(a.player);
    }
}
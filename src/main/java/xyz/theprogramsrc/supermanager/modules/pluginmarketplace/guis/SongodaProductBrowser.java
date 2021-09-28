package xyz.theprogramsrc.supermanager.modules.pluginmarketplace.guis;

import org.bukkit.entity.Player;

import xyz.theprogramsrc.supercoreapi.global.translations.Base;
import xyz.theprogramsrc.supercoreapi.global.utils.Utils;
import xyz.theprogramsrc.supercoreapi.libs.xseries.XMaterial;
import xyz.theprogramsrc.supercoreapi.spigot.dialog.Dialog;
import xyz.theprogramsrc.supercoreapi.spigot.gui.BrowserGui;
import xyz.theprogramsrc.supercoreapi.spigot.gui.objets.GuiAction.ClickType;
import xyz.theprogramsrc.supercoreapi.spigot.gui.objets.GuiEntry;
import xyz.theprogramsrc.supercoreapi.spigot.gui.objets.GuiTitle;
import xyz.theprogramsrc.supercoreapi.spigot.items.SimpleItem;
import xyz.theprogramsrc.supermanager.L;
import xyz.theprogramsrc.supermanager.SuperManager;
import xyz.theprogramsrc.supermanager.modules.pluginmarketplace.PluginMarketplace;
import xyz.theprogramsrc.supermanager.modules.pluginmarketplace.objects.SongodaProduct;

public class SongodaProductBrowser extends BrowserGui<SongodaProduct> {

    private boolean canDownloadPremium = false;

    public SongodaProductBrowser(Player player) {
        super(player, false);
        this.backEnabled = true;
        this.open();
    }

    @Override
    public SongodaProduct[] getObjects() {
        return PluginMarketplace.products.values().toArray(new SongodaProduct[0]);
    }

    @Override
    public String[] getSearchTags(SongodaProduct p) {
        return new String[]{
            p.getName(),
            p.getTagline()
        };
    }

    @Override
    public GuiEntry getEntry(SongodaProduct songodaProduct) {
        SimpleItem item = new SimpleItem(XMaterial.CHEST)
                .setDisplayName("&a" + (songodaProduct.getTagline() != null ? L.PLUGIN_MARKETPLACE_CARD_NAME : L.PLUGIN_MARKETPLACE_CARD_NAME_NO_TAGLINE))
                .setLore(
                        "&7",
                        "&9" + Base.LEFT_CLICK + "&7 " + L.PLUGIN_MARKETPLACE_LEFT_ACTION
                );
        if(songodaProduct.getPaymentMethod().equalsIgnoreCase("None")){
            item.addLoreLine("&9" + Base.RIGHT_CLICK + "&7 " + L.PLUGIN_MARKETPLACE_RIGHT_ACTION);
        }

        item.addLoreLines(
                "&7",
                "&7" + L.PLUGIN_MARKETPLACE_CARD_AUTHOR,
                "&7" + L.PLUGIN_MARKETPLACE_CARD_SUPPORTED_VERSIONS,
                "&7" + L.PLUGIN_MARKETPLACE_CARD_PRICE
        )
        .addPlaceholder("{ProductAuthor}", songodaProduct.getOwner())
        .addPlaceholder("{ProductName}", songodaProduct.getName())
        .addPlaceholder("{ProductTagline}", songodaProduct.getTagline() != null ? songodaProduct.getTagline() : "")
        .addPlaceholder("{ProductPrice}", songodaProduct.getPriceString())
        .addPlaceholder("{SupportedVersions}", songodaProduct.getSupportedVersions());

        item.addLoreLines(Utils.breakText(songodaProduct.getDescription(), 40, "&7"));
        return new GuiEntry(item, a-> {
            if(a.clickType == ClickType.LEFT_CLICK){
                this.close();
                this.getSuperUtils().sendMessage(a.player, "&a" + songodaProduct.getName() + ":");
                this.getSuperUtils().sendMessage(a.player, "&c" + songodaProduct.getUrl());
            }else if(a.clickType == ClickType.RIGHT_CLICK){
                boolean shouldAskForToken = !songodaProduct.isFree() && !SuperManager.validateToken();
                if(shouldAskForToken && this.canDownloadPremium){
                    // Ask for token
                    this.getSuperUtils().sendMessage(a.player, this.getSettings().getPrefix() + L.TOKEN_WILL_NOT_BE_SHARED);
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
                    this.close();
                    SuperManager.i.getSuperUtils().sendMessage(a.player, SuperManager.i.getSettingsStorage().getPrefix() + L.PLUGIN_MARKETPLACE_DOWNLOADING_PRODUCT.options().placeholder("{ProductName}", songodaProduct.getName()).get());
                    songodaProduct.download(a.player);
                }
            }
        });
    }

    @Override
    public GuiTitle getTitle() {
        return GuiTitle.of(L.PLUGIN_MARKETPLACE_TITLE.toString());
    }
}
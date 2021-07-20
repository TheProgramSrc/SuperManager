package xyz.theprogramsrc.supermanager.modules.pluginmarketplace.guis;

import java.util.LinkedHashMap;

import org.bukkit.entity.Player;

import xyz.theprogramsrc.supercoreapi.global.translations.Base;
import xyz.theprogramsrc.supercoreapi.global.utils.Utils;
import xyz.theprogramsrc.supercoreapi.libs.xseries.XMaterial;
import xyz.theprogramsrc.supercoreapi.spigot.guis.BrowserGUI;
import xyz.theprogramsrc.supercoreapi.spigot.guis.GUIButton;
import xyz.theprogramsrc.supercoreapi.spigot.guis.action.ClickType;
import xyz.theprogramsrc.supercoreapi.spigot.items.SimpleItem;
import xyz.theprogramsrc.supermanager.L;
import xyz.theprogramsrc.supermanager.SuperManager;
import xyz.theprogramsrc.supermanager.modules.pluginmarketplace.PluginMarketplace;
import xyz.theprogramsrc.supermanager.modules.pluginmarketplace.objects.SongodaProduct;

public class SongodaProductBrowser extends BrowserGUI<SongodaProduct> {

    private final LinkedHashMap<String, String> currencySymbols = new LinkedHashMap<>();

    public SongodaProductBrowser(Player player) {
        super(player);
        this.backEnabled = true;
        currencySymbols.put("EUR", "€");
        currencySymbols.put("USD", "$");
        currencySymbols.put("AUD", "AU$");
        currencySymbols.put("GBP", "£");
        this.open();
    }

    @Override
    public SongodaProduct[] getObjects() {
        return PluginMarketplace.products.values().toArray(new SongodaProduct[0]);
    }

    @Override
    public GUIButton getButton(SongodaProduct songodaProduct) {
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
        .addPlaceholder("{ProductPrice}", songodaProduct.isFree() ? "&aFree" : (songodaProduct.isSongodaPlus() ? "&6&lSongoda+ Exclusive" : ("&e" + this.currencySymbols.get(songodaProduct.getCurrency()) + songodaProduct.getPrice())))
        .addPlaceholder("{SupportedVersions}", songodaProduct.getSupportedVersions());

        item.addLoreLines(Utils.breakText(songodaProduct.getDescription(), 40, "&7"));
        return new GUIButton(item).setAction(a-> {
            if(a.getAction() == ClickType.LEFT_CLICK){
                this.close();
                this.getSuperUtils().sendMessage(a.getPlayer(), "&a" + songodaProduct.getName() + ":");
                this.getSuperUtils().sendMessage(a.getPlayer(), "&c" + songodaProduct.getUrl());
            }else if(a.getAction() == ClickType.RIGHT_CLICK){
                this.close();
                SuperManager.i.getSuperUtils().sendMessage(a.getPlayer(), SuperManager.i.getSettingsStorage().getPrefix() + L.PLUGIN_MARKETPLACE_DOWNLOADING_PRODUCT.options().placeholder("{ProductName}", songodaProduct.getName()).get());
                songodaProduct.download(a.getPlayer());
            }
        });
    }

    @Override
    protected String getTitle() {
        return L.PLUGIN_MARKETPLACE_TITLE.toString();
    }
}
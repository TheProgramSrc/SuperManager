package xyz.theprogramsrc.supermanager.modules.pluginmanager.guis;

import org.bukkit.entity.Player;
import xyz.theprogramsrc.supercoreapi.spigot.guis.BrowserGUI;
import xyz.theprogramsrc.supercoreapi.spigot.guis.GUIButton;
import xyz.theprogramsrc.supercoreapi.spigot.items.SimpleItem;
import xyz.theprogramsrc.supercoreapi.spigot.utils.xseries.XMaterial;
import xyz.theprogramsrc.supermanager.L;
import xyz.theprogramsrc.supermanager.modules.pluginmanager.PluginManager;
import xyz.theprogramsrc.supermanager.modules.pluginmanager.objects.SPlugin;

public abstract class PluginBrowser extends BrowserGUI<SPlugin> {

    public PluginBrowser(Player player) {
        super(player);
        this.backEnabled = true;
        this.open();
    }

    @Override
    public SPlugin[] getObjects() {
        return PluginManager.plugins.values().toArray(new SPlugin[0]);
    }

    @Override
    public GUIButton getButton(SPlugin sPlugin) {
        SimpleItem item = new SimpleItem(XMaterial.CHEST)
                .setDisplayName(L.PLUGIN_MANAGER_BROWSER_ITEM_NAME.toString())
                .setLore(
                        "&7",
                        "&7" + L.PLUGIN_MANAGER_BROWSER_ITEM_LORE.toString()
                ).addPlaceholder("{PluginName}", sPlugin.getName()).addPlaceholder("{PluginVersion}", sPlugin.getCurrentVersion());
        return new GUIButton(item).setAction(a-> new PluginView(sPlugin, a.getPlayer(), a1-> PluginBrowser.this.open()));
    }

    @Override
    protected String getTitle() {
        return L.PLUGIN_MANAGER_BROWSER_TITLE.toString();
    }
}
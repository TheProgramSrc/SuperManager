package xyz.theprogramsrc.supermanager.modules.pluginmanager.guis;

import org.bukkit.entity.Player;

import xyz.theprogramsrc.supercoreapi.libs.xseries.XMaterial;
import xyz.theprogramsrc.supercoreapi.spigot.gui.BrowserGui;
import xyz.theprogramsrc.supercoreapi.spigot.gui.objets.GuiEntry;
import xyz.theprogramsrc.supercoreapi.spigot.gui.objets.GuiTitle;
import xyz.theprogramsrc.supercoreapi.spigot.items.SimpleItem;
import xyz.theprogramsrc.supermanager.L;
import xyz.theprogramsrc.supermanager.modules.pluginmanager.PluginManager;
import xyz.theprogramsrc.supermanager.modules.pluginmanager.objects.SPlugin;

public abstract class PluginBrowser extends BrowserGui<SPlugin> {

    public PluginBrowser(Player player) {
        super(player, false);
        this.backEnabled = true;
        this.open();
    }

    @Override
    public SPlugin[] getObjects() {
        return PluginManager.plugins.values().toArray(new SPlugin[0]);
    }

    @Override
    public String[] getSearchTags(SPlugin p) {
        return new String[]{p.getPlatform(), p.getName()};
    }

    @Override
    public GuiEntry getEntry(SPlugin sPlugin) {
        SimpleItem item = new SimpleItem(XMaterial.CHEST)
                .setDisplayName(L.PLUGIN_MANAGER_BROWSER_ITEM_NAME.toString())
                .setLore(
                        "&7",
                        "&7" + L.PLUGIN_MANAGER_BROWSER_ITEM_LORE.toString()
                ).addPlaceholder("{PluginName}", sPlugin.getName()).addPlaceholder("{PluginVersion}", sPlugin.getCurrentVersion());
        return new GuiEntry(item, a-> new PluginView(sPlugin, a.player, a1-> PluginBrowser.this.open()));
    }

    @Override
    public GuiTitle getTitle() {
        return GuiTitle.of(L.PLUGIN_MANAGER_BROWSER_TITLE.toString());
    }
}
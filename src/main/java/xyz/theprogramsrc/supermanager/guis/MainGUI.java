package xyz.theprogramsrc.supermanager.guis;

import java.util.Arrays;

import org.bukkit.entity.Player;

import xyz.theprogramsrc.supercoreapi.global.translations.Base;
import xyz.theprogramsrc.supercoreapi.global.utils.Utils;
import xyz.theprogramsrc.supercoreapi.spigot.gui.BrowserGui;
import xyz.theprogramsrc.supercoreapi.spigot.gui.objets.GuiAction;
import xyz.theprogramsrc.supercoreapi.spigot.gui.objets.GuiAction.ClickType;
import xyz.theprogramsrc.supercoreapi.spigot.gui.objets.GuiEntry;
import xyz.theprogramsrc.supercoreapi.spigot.gui.objets.GuiTitle;
import xyz.theprogramsrc.supercoreapi.spigot.items.SimpleItem;
import xyz.theprogramsrc.supermanager.L;
import xyz.theprogramsrc.supermanager.SuperManager;
import xyz.theprogramsrc.supermanager.objects.Module;
import xyz.theprogramsrc.supermanager.utils.Checkers;

public class MainGUI extends BrowserGui<Module> {

    public MainGUI(Player player) {
        super(player, false);
        this.backEnabled = true;
        this.open();
    }

    @Override
    public Module[] getObjects() {
        return Arrays.stream(SuperManager.i.getModules()).filter(Utils::nonNull).filter(m-> m.getPermission() == null || this.player.hasPermission(m.getPermission())).toArray(Module[]::new); // Request all the modules and check if there is some null module or if the user has permissions to see them.
    }

    @Override
    public String[] getSearchTags(Module m) {
        return new String[]{
            m.getDisplay(),
            m.getIdentifier(),
        };
    }

    @Override
    public GuiEntry getEntry(Module module) {
        SimpleItem moduleDisplayItem = module.getDisplayItem(); // Request item
        if(module.isEnabled()){
            moduleDisplayItem.addLoreLines(
                    "&7",
                    "&9" + Base.RIGHT_CLICK + "&c " + L.MODULE_MANAGER_DISABLE_ACTION
            );
        }else{
            moduleDisplayItem.addLoreLines(
                    "&7",
                    "&9" + Base.RIGHT_CLICK + "&a " + L.MODULE_MANAGER_ENABLE_ACTION
            );
        } // If enabled show disable action. If disabled show enable action
        return new GuiEntry(moduleDisplayItem, clickAction -> {
            if(clickAction.clickType == ClickType.RIGHT_CLICK){
                if(Checkers.hasPermission(clickAction.player, "supermanager.module.disable")){ // Check if player has permission
                    module.setEnabled(!module.isEnabled());
                    this.open();
                }else{
                    this.close();
                    this.getSuperUtils().sendMessage(clickAction.player, this.getSettings().getPrefix() + Base.NO_PERMISSION.toString());
                }
            }else{
                if(!module.isEnabled()){
                    this.close();
                    this.getSuperUtils().sendMessage(clickAction.player, this.getSettings().getPrefix() + L.MODULE_DISABLED.options().placeholder("{ModuleName}", module.getDisplay()).get());
                }else{
                    String moduleUsePermission =  "supermanager.module." + module.getIdentifier() + ".use";
                    if(Checkers.hasPermission(clickAction.player, moduleUsePermission)){ // Check if player has permission to use the module
                        module.onAction(clickAction.player);
                    }else{
                        this.close();
                        this.getSuperUtils().sendMessage(clickAction.player, this.getSettings().getPrefix() + L.NO_ACCESS_TO_MODULE
                                .options()
                                .placeholder("{ModuleName}", module.getDisplay())
                                .placeholder("{ModulePermission}", moduleUsePermission));
                    }
                }
            }
        });
    }

    @Override
    public void onBack(GuiAction clickAction) {
        this.close(); // Just close the gui, there is no back
    }

    @Override
    public GuiTitle getTitle() {
        return GuiTitle.of(String.format("SuperManager v%s", this.plugin.getPluginVersion()), true); // Show the title (SuperManager v....)
    }


}

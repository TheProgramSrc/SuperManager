package xyz.theprogramsrc.supermanager;

import xyz.theprogramsrc.supercoreapi.global.storage.DataBase;
import xyz.theprogramsrc.supercoreapi.global.storage.universal.UniversalStorage;
import xyz.theprogramsrc.supercoreapi.global.utils.Utils;
import xyz.theprogramsrc.supercoreapi.spigot.SpigotPlugin;
import xyz.theprogramsrc.supermanager.api.SuperManagerAPI;
import xyz.theprogramsrc.supermanager.commands.MainCommand;
import xyz.theprogramsrc.supermanager.managers.ModuleManager;
import xyz.theprogramsrc.supermanager.modules.chatchannels.ChatChannelsModule;
import xyz.theprogramsrc.supermanager.modules.filemanager.FileManager;
import xyz.theprogramsrc.supermanager.modules.pluginmanager.PluginManager;
import xyz.theprogramsrc.supermanager.modules.pluginmarketplace.PluginMarketplace;
import xyz.theprogramsrc.supermanager.modules.usermanager.UserManagerModule;
import xyz.theprogramsrc.supermanager.objects.Module;

import java.lang.reflect.InvocationTargetException;
import java.util.Arrays;
import java.util.LinkedHashMap;
import java.util.LinkedList;

public class SuperManager extends SpigotPlugin implements SuperManagerAPI {

    public static SuperManager i;
    private ModuleManager moduleManager;
    private LinkedList<Module> modules;
    private LinkedHashMap<String, Module> enabledModules;
    private DataBase dataBase;

    @Override
    public void onPluginLoad() {
        i = this;
        this.modules = new LinkedList<>();
        this.enabledModules = new LinkedHashMap<>();
        this.moduleManager = new ModuleManager();
    }

    @Override
    public void onPluginEnable() {
        UniversalStorage.register(this);
        this.registerTranslation(L.class);
        this.registerModules();
        new MainCommand();
    }

    @Override
    public void onPluginDisable() {

    }

    private void registerModules(){
        this.registerModule(PluginManager.class);
        this.registerModule(PluginMarketplace.class);
        this.registerModule(UserManagerModule.class);
        this.registerModule(ChatChannelsModule.class);
        this.registerModule(FileManager.class);
    }

    public ModuleManager getModuleManager() {
        return moduleManager;
    }

    public void registerModule(Class<? extends Module> clazz){
        try {
            Utils.notNull(clazz, "Module Class Cannot be null!");
            Module module = clazz.getConstructor().newInstance();
            if(module.getIdentifier() == null){
                this.log("&cThe identifier of the module '&b" + clazz.getSimpleName() + "&c' cannot be null!");
            }else{
                if(module.getDisplayItem() == null){
                    this.log("&cThe display item of the module '&b" + module.getIdentifier() + "&c' cannot be null!");
                }else{
                    this.modules.add(module);
                    if(module.isEnabled()){
                        if(!module.isRunning()){
                            module.enable();
                        }
                    }else{
                        if(module.isRunning()){
                            module.disable();
                        }
                    }

                    if(module.isEnabled() && module.isRunning()){
                        if(!this.enabledModules.containsKey(module.getIdentifier())){
                            this.enabledModules.put(module.getIdentifier(), module);
                        }
                    }else{
                        this.enabledModules.remove(module.getIdentifier());
                    }
                }
            }
        } catch (InstantiationException | IllegalAccessException | InvocationTargetException | NoSuchMethodException e) {
            this.log("&cError while enabling module:");
            this.addError(e);
            e.printStackTrace();
        }
    }

    @Override
    public Module[] getModules() {
        return modules.toArray(new Module[0]);
    }

    @Override
    public Module getModule(String id) {
        return Arrays.stream(this.getModules()).filter(m-> m.getIdentifier().equals(id)).findFirst().orElse(null);
    }

    public LinkedList<Module> getEnabledModules(){
        return new LinkedList<>(this.enabledModules.values());
    }

}
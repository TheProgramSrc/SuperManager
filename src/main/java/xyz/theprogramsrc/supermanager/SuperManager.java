package xyz.theprogramsrc.supermanager;

import java.lang.reflect.InvocationTargetException;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.concurrent.TimeUnit;

import xyz.theprogramsrc.supercoreapi.global.storage.DataBase;
import xyz.theprogramsrc.supercoreapi.global.storage.universal.UniversalStorage;
import xyz.theprogramsrc.supercoreapi.global.utils.Utils;
import xyz.theprogramsrc.supercoreapi.spigot.SpigotPlugin;
import xyz.theprogramsrc.supermanager.api.SuperManagerAPI;
import xyz.theprogramsrc.supermanager.commands.MainCommand;
import xyz.theprogramsrc.supermanager.managers.ModuleManager;
import xyz.theprogramsrc.supermanager.modules.backupmanager.BackupManager;
import xyz.theprogramsrc.supermanager.modules.chatchannels.ChatChannelsModule;
import xyz.theprogramsrc.supermanager.modules.filemanager.FileManager;
import xyz.theprogramsrc.supermanager.modules.pluginmanager.PluginManager;
import xyz.theprogramsrc.supermanager.modules.pluginmarketplace.PluginMarketplace;
import xyz.theprogramsrc.supermanager.modules.servermanager.ServerManager;
import xyz.theprogramsrc.supermanager.modules.usermanager.UserManagerModule;
import xyz.theprogramsrc.supermanager.objects.Module;

public class SuperManager extends SpigotPlugin implements SuperManagerAPI {

    public static String token;
    public static SuperManager i;
    private ModuleManager moduleManager;
    private LinkedList<Module> modules;

    @Override
    public void onPluginLoad() {
        i = this;
        this.modules = new LinkedList<>();
        this.moduleManager = new ModuleManager();
    }

    @Override
    public void onPluginEnable() {
        token = this.getSettingsStorage().getConfig().contains("songoda-token") ? this.getSettingsStorage().getConfig().getString("songoda-token") : "";
        UniversalStorage.register(this);
        this.registerTranslation(L.class);
        this.registerModules();
        new MainCommand();
    }

    @Override
    public void onPluginDisable() {
        if(BackupManager.task != null) BackupManager.task.stop();

    }

    private void registerModules(){
        this.registerModule(PluginManager.class);
        this.registerModule(PluginMarketplace.class);
        this.registerModule(UserManagerModule.class);
        this.registerModule(ChatChannelsModule.class);
        this.registerModule(FileManager.class);
        this.registerModule(BackupManager.class);
        this.registerModule(ServerManager.class);
    }

    public DataBase getDataBase() {
        return UniversalStorage.database();
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
                    this.debug("&aThe module &7" + module.getDisplay() + "&a was registered");
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

    public Module[] getEnabledModules(){
        return this.modules.stream().filter(Module::isEnabled).toArray(Module[]::new);
    }

    // Utils
    public static long getTimeSecondsFromString(String string) {
        return Arrays.stream(string.split(" ")).mapToLong(s -> getTimeFromWord(s)).sum();
    }

    private static long getTimeFromWord(String word) {
        if (word.length() < 2) return 0L;
        String timeUnitString = word.toCharArray()[word.length() - 1] + "";
        TimeUnit timeUnit = Arrays.stream(new TimeUnit[] { TimeUnit.DAYS, TimeUnit.HOURS, TimeUnit.MINUTES, TimeUnit.SECONDS }).filter((t) -> t.toString().toLowerCase().startsWith(timeUnitString)).findFirst().orElse(null);
        if(timeUnit == null) return 0L;
        try{
            return timeUnit == null ? 0L : timeUnit.toSeconds((long) Integer.parseInt(word.substring(0, word.length() - 1)));
        }catch(NumberFormatException e){
            return 0L;
        }
    }

    public static boolean validateToken(){
        if(token == null) return false;
        if(token.equals("")) return false;
        if(token.equals(" ")) return false;

        return token.matches("^[a-fA-F0-9]{32}$");
    }

}
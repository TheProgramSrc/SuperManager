package xyz.theprogramsrc.supermanager.managers;

import xyz.theprogramsrc.supercoreapi.global.files.JsonConfig;
import xyz.theprogramsrc.supermanager.SuperManager;
import xyz.theprogramsrc.supermanager.objects.Module;

import java.io.File;

public class ModuleManager extends JsonConfig {

    public ModuleManager(){
        super(new File(SuperManager.i.getPluginFolder(), "Modules.json")); // Create or Load file
    }

    public boolean isModuleEnabled(Module module){
        this.add(module.getIdentifier(), "false"); // Add module if doesn't exists
        return this.getBoolean(module.getIdentifier()); // Return if the module is enabled
    }

    public void setEnabled(Module module, boolean enabled){
        this.set(module.getIdentifier(), enabled+""); // Set module enabled/disabled (as string)
    }
}

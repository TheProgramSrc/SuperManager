package xyz.theprogramsrc.supermanager.objects;

import org.bukkit.entity.Player;
import org.bukkit.event.HandlerList;
import xyz.theprogramsrc.supercoreapi.global.utils.Utils;
import xyz.theprogramsrc.supercoreapi.spigot.SpigotModule;
import xyz.theprogramsrc.supercoreapi.spigot.guis.action.ClickAction;
import xyz.theprogramsrc.supercoreapi.spigot.items.SimpleItem;
import xyz.theprogramsrc.supermanager.SuperManager;

import java.io.File;

public abstract class Module extends SpigotModule {

    private final SuperManager superManager = SuperManager.i; // Declare main plugin instance
    private boolean running; // If true the module is currently running, otherwise is not

    public void onEnable(){

    }

    public void onDisable(){

    }

    public boolean isEnabled(){
        return this.superManager.getModuleManager().isModuleEnabled(this); // Check if module is enabled in the config
    }

    public boolean isRunning() {
        return running; // Check if the module is currently running
    }

    public abstract String getDisplay(); // Get the module display name

    public abstract String getIdentifier(); // Get the module identifier used for permissions and other things

    public abstract SimpleItem getDisplayItem(); // Get the module display item

    public String getPermission(){
        return null;
    }

    public abstract void onAction(Player player); // Executed when the module display item is clicked in the main gui

    protected void log(String message, boolean prefixed){
        if(prefixed){
            this.plugin.log(this.getDisplay() + "> " + message);
        }else{
            this.plugin.log(message);
        }
    }

    @Override
    protected void log(String message) {
        this.log(message, true);
    }

    public boolean requireInternetConnection(){ // Require internet connection?
        return false;
    }

    private void updateStatus(){ // If the module is enabled/disabled it will be executed the enable/disable actions
        if(this.isEnabled()){
            if(!this.isRunning()){
                this.enable();
            }
        }else{
            if(this.isRunning()){
                this.disable();
            }
        }
    }

    public void enable(){
        if(this.running && this.isEnabled()){ // Check if module is already running and enabled
            throw new IllegalStateException("The module '" + this.getDisplay() + "' is already enabled");
        }else{
            try{ // Try to run
                if(this.requireInternetConnection() && !Utils.isConnected()){
                    this.log("&cFailed to enable the module '&7" + this.getDisplay() + "&c': Internet connection is required");
                    this.running = false;
                }else{
                    HandlerList.unregisterAll(this);
                    this.listener(this);
                    this.onEnable();
                    this.running = true;
                    this.log("&aModule &3" + this.getDisplay() + "&a enabled.", false);
                }
            }catch (Exception e){
                this.log("&cError while enabling module &3" + this.getDisplay(), false);
                e.printStackTrace();
            }
        }
    }

    public void disable(){
        if(!this.running && !this.isEnabled()){ // Check if the module is already disabled nor running
            throw new IllegalStateException("The module '" + this.getDisplay() + "' is already disabled!");
        }else{
            try{ // Try to disable
                HandlerList.unregisterAll(this);
                this.onDisable();
                this.running = false;
                this.log("&aModule &3" + this.getDisplay() + "&a disabled.", false);
            }catch (Exception e){
                this.log("&cError while disabling module &3" + this.getDisplay(), false);
                e.printStackTrace();
            }
        }
    }

    public void setEnabled(boolean enabled){ // Set module enabled/disabled in config and update status
        this.superManager.getModuleManager().setEnabled(this, enabled);
        this.updateStatus();
    }

    public File getModuleFolder(){
        return Utils.folder(new File(this.getPluginFolder(), this.getIdentifier() + "/"));
    }
}

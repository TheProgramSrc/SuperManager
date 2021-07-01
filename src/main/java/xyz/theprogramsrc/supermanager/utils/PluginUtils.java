package xyz.theprogramsrc.supermanager.utils;

import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.PluginCommand;
import org.bukkit.command.SimpleCommandMap;
import org.bukkit.event.Event;
import org.bukkit.plugin.*;
import xyz.theprogramsrc.supermanager.SuperManager;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.Field;
import java.net.URLClassLoader;
import java.util.*;
import java.util.stream.Collectors;

public class PluginUtils {

    /**
     * Enable the plugin
     * @return true if it was enabled, false otherwise
     */
    public static boolean enable(String name){
        File pluginsFolder = new File(SuperManager.i.getServerFolder(), "plugins");

        File file = new File(pluginsFolder, name + ".jar");

        if(!file.exists() || !file.isFile()){
            // Try to find the plugin file
            for(File f : Arrays.stream(pluginsFolder.listFiles()).filter(File::isFile).filter(f-> f.getName().endsWith(".jar")).collect(Collectors.toList())){
                try{
                    PluginDescriptionFile descriptionFile = SuperManager.i.getPluginLoader().getPluginDescription(f);
                    if(descriptionFile.getName().equalsIgnoreCase(name)){
                        file = f;
                        break;
                    }
                }catch (InvalidDescriptionException e){
                    return false;
                }
            }
        }


        Plugin plugin = null;

        try{
            plugin = Bukkit.getPluginManager().loadPlugin(file);
        } catch (InvalidDescriptionException | InvalidPluginException e) {
            SuperManager.i.log("&cFailed to load the plugin '&7" + name + "&c'");
            e.printStackTrace();
            SuperManager.i.addError(e);
        }

        if(plugin == null){
            return false;
        }

        plugin.onLoad();
        Bukkit.getPluginManager().enablePlugin(plugin);

        return plugin.isEnabled();
    }

    /**
     * Disable the plugin
     * @return true if it was disabled, false otherwise
     */
    @SuppressWarnings("unchecked")
    public static boolean disable(Plugin plugin){
        String name = plugin.getName();
        PluginManager pluginManager = Bukkit.getPluginManager();
        SimpleCommandMap simpleCommandMap;
        List<Plugin> pluginList;
        Map<String, Plugin> pluginNamesMap;
        Map<String, Command> pluginCommands;
        Map<Event, SortedSet<RegisteredListener>> pluginListeners = new HashMap<>();

        boolean reloadlisteners = true;

        pluginManager.disablePlugin(plugin);

        try {

            Field pluginsField = Bukkit.getPluginManager().getClass().getDeclaredField("plugins");
            pluginsField.setAccessible(true);
            pluginList = (List<Plugin>) pluginsField.get(pluginManager);

            Field lookupNamesField = Bukkit.getPluginManager().getClass().getDeclaredField("lookupNames");
            lookupNamesField.setAccessible(true);
            pluginNamesMap = (Map<String, Plugin>) lookupNamesField.get(pluginManager);

            try {
                Field listenersField = Bukkit.getPluginManager().getClass().getDeclaredField("listeners");
                listenersField.setAccessible(true);
                pluginListeners = (Map<Event, SortedSet<RegisteredListener>>) listenersField.get(pluginManager);
            } catch (Exception e) {
                reloadlisteners = false;
            }

            Field commandMapField = Bukkit.getPluginManager().getClass().getDeclaredField("commandMap");
            commandMapField.setAccessible(true);
            simpleCommandMap = (SimpleCommandMap) commandMapField.get(pluginManager);

            Field knownCommandsField = SimpleCommandMap.class.getDeclaredField("knownCommands");
            knownCommandsField.setAccessible(true);
            pluginCommands = (Map<String, Command>) knownCommandsField.get(simpleCommandMap);
        } catch (NoSuchFieldException | IllegalAccessException e) {
            SuperManager.i.log("&Failed to unload plugin '" + name + "'");
            e.printStackTrace();
            SuperManager.i.addError(e);
            return false;
        }

        pluginManager.disablePlugin(plugin);

        if (pluginList != null)
            pluginList.remove(plugin);

        if (pluginNamesMap != null)
            pluginNamesMap.remove(name);

        if (pluginListeners != null && reloadlisteners) {
            for (SortedSet<RegisteredListener> set : pluginListeners.values()) {
                set.removeIf(value -> value.getPlugin() == plugin);
            }
        }

        if (simpleCommandMap != null) {
            for (Iterator<Map.Entry<String, Command>> it = pluginCommands.entrySet().iterator(); it.hasNext(); ) {
                Map.Entry<String, Command> entry = it.next();
                if (entry.getValue() instanceof PluginCommand) {
                    PluginCommand c = (PluginCommand) entry.getValue();
                    if (c.getPlugin() == plugin) {
                        c.unregister(simpleCommandMap);
                        it.remove();
                    }
                }
            }
        }

        // Attempt to close the classloader to unlock any handles on the plugin's jar file.
        ClassLoader cl = plugin.getClass().getClassLoader();

        if (cl instanceof URLClassLoader) {

            try {

                Field pluginField = cl.getClass().getDeclaredField("plugin");
                pluginField.setAccessible(true);
                pluginField.set(cl, null);

                Field pluginInitField = cl.getClass().getDeclaredField("pluginInit");
                pluginInitField.setAccessible(true);
                pluginInitField.set(cl, null);

            } catch (NoSuchFieldException | SecurityException | IllegalArgumentException | IllegalAccessException ex) {
                SuperManager.i.log("&cFailed to enable plugin '" + name + "':");
                SuperManager.i.addError(ex);
                ex.printStackTrace();
            }

            try {
                ((URLClassLoader) cl).close();
            } catch (IOException ex) {
                SuperManager.i.log("&cFailed to enable plugin '" + name + "':");
                SuperManager.i.addError(ex);
                ex.printStackTrace();
            }

            // Will not work on processes started with the -XX:+DisableExplicitGC flag, but lets try it anyway.
            // This tries to get around the issue where Windows refuses to unlock jar files that were previously loaded into the JVM.
            System.gc();

        }

        return true;
    }
}

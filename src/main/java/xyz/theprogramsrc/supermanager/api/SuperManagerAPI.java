package xyz.theprogramsrc.supermanager.api;

import xyz.theprogramsrc.supermanager.objects.Module;

public interface SuperManagerAPI {

    Module[] getModules();

    Module getModule(String id);

    void registerModule(Class<? extends Module> moduleClass);
}

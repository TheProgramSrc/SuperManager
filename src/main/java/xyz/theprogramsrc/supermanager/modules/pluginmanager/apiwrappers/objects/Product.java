package xyz.theprogramsrc.supermanager.modules.pluginmanager.apiwrappers.objects;

import java.time.Instant;
import java.util.Arrays;

public class Product {

    private final String id, name;
    private final Version[] versions;

    public Product(String id, String name, Version[] versions) {
        this.id = id;
        this.name = name;
        this.versions = versions;
    }

    public String getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public Version[] getVersions() {
        return versions;
    }

    public Version getLatestVersion() {
        return versions.length > 0 ? versions[0] : null;
    }

    public Instant lastUpdate(){
        return this.getLatestVersion().createdAt();
    }
    
    public Version versionFromName(String name){
        return Arrays.stream(this.versions).filter(v-> v.getName().equals(name)).findFirst().orElse(null);
    }
}

package xyz.theprogramsrc.supermanager.modules.pluginmanager.apiwrappers.objects;

import java.time.Instant;

public class Version {

    private final String productId, id, name, downloadUrl, fileName;
    private final long created_at;
    private final int downloads;

    public Version(String productId, String id, String name, long created_at, int downloads, String downloadUrl, String fileName) {
        this.productId = productId;
        this.id = id;
        this.name = name;
        this.created_at = created_at;
        this.downloads = downloads;
        this.downloadUrl = downloadUrl;
        this.fileName = fileName;
    }

    public String getProductId() {
        return productId;
    }

    public String getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public String getFileName() {
        return this.fileName;
    }

    public long getCreatedAt() {
        return created_at;
    }

    public Instant createdAt(){
        return Instant.ofEpochMilli(created_at);
    }

    public int getDownloads() {
        return downloads;
    }

    public String getDownloadUrl() {
        return downloadUrl;
    }
    
    public boolean shouldUpdateTo(Version other){
        if(this.created_at == other.created_at){
            return false;
        }

        return other.createdAt().isAfter(this.createdAt());
    }
}

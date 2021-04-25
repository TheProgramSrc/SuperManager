package xyz.theprogramsrc.supermanager.modules.filemanager.objects;

import xyz.theprogramsrc.supercoreapi.spigot.utils.storage.ConfigField;
import xyz.theprogramsrc.supercoreapi.spigot.utils.storage.SpigotYMLConfig;

import java.io.File;
import java.util.List;

public class YMLField {

    private final ConfigField field;
    private final SpigotYMLConfig cfg;

    public YMLField(File file, String path){
        this.cfg = new SpigotYMLConfig(file);
        this.field = new ConfigField(this.cfg.getConfig(), path);
    }

    public String getPath() {
        return this.field.getPath();
    }

    public Object get(){
        return this.field.asObject();
    }

    public boolean isString(){
        return this.get() instanceof String;
    }

    public boolean isNumber(){
        return this.get() instanceof Number;
    }

    public boolean isBoolean(){
        return this.get() instanceof Boolean;
    }

    public void set(Object value){
        this.cfg.set(this.getPath(), value);
    }

    public String asString(){
        return this.field.asString();
    }

    public boolean asBoolean(){
        return this.field.asBoolean();
    }

    public List<?> asList(){
        return this.field.asList();
    }

    public List<String> asStringList(){
        return this.field.asStringList();
    }

    public Number asNumber(){
        return ((Number) this.get());
    }

    public void toggle(){
        this.set(!this.asBoolean());
    }

    public void increase(Number amount){
        Number n = this.asNumber();
        if(n instanceof Double){
            this.set(n.doubleValue() + amount.doubleValue());
        }else if(n instanceof Float){
            this.set(n.floatValue() + amount.floatValue());
        }else if(n instanceof Long){
            this.set(n.longValue() + amount.longValue());
        }else{
            this.set(n.intValue() + amount.intValue());
        }
    }

    public void decrease(Number amount){
        Number n = this.asNumber();
        if(n instanceof Double){
            this.set(n.doubleValue() - amount.doubleValue());
        }else if(n instanceof Float){
            this.set(n.floatValue() - amount.floatValue());
        }else if(n instanceof Long){
            this.set(n.longValue() - amount.longValue());
        }else{
            this.set(n.intValue() - amount.intValue());
        }
    }

    public void setInStringList(int i, String value){
        List<String> list = this.field.asStringList();
        list.set(i, value);
        this.set(list);
    }

    public void removeInStringList(int i){
        List<String> list = this.field.asStringList();
        list.remove(i);
        this.set(list);
    }
}

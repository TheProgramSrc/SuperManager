package xyz.theprogramsrc.supermanager.modules.chatchannels.objects;

public class ChatChannelsSetting {

    private final String key, value;

    public ChatChannelsSetting(String key, String value) {
        this.key = key;
        this.value = value;
    }

    public String getKey() {
        return key;
    }

    public String getValue() {
        return value;
    }
}

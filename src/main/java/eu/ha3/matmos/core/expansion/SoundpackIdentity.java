package eu.ha3.matmos.core.expansion;

import com.google.gson.Gson;
import com.google.gson.JsonObject;

import eu.ha3.mc.haddon.implem.HaddonIdentity;
import eu.ha3.mc.haddon.implem.HaddonVersion;

public class SoundpackIdentity {
    
    private final String type;
    private final int engineVersion;
    private final String name;
    private final String author;
    private final String website;
    private final String uniqueName;
    private final HaddonVersion version;
    private final String updateJson;
    
    public SoundpackIdentity(JsonObject matPackRoot) {
        SerialMatPack serial = new Gson().fromJson(matPackRoot, SerialMatPack.class);
        type = serial.type;
        engineVersion = serial.engineversion;
        name = serial.metadata != null ? serial.metadata.name : null;
        author = serial.metadata != null ? serial.metadata.author : null;
        website = serial.metadata != null ? serial.metadata.website : null;
        uniqueName = serial.uniquename;
        version = serial.version != null ? new HaddonVersion(serial.version) : null;
        updateJson = serial.updatejson;
    }
    
    public static class SerialMatPack {
        String type;
        int engineversion;
        SerialMatPackMetadata metadata;
        String uniquename;
        String version;
        String updatejson;
    }
    
    public static class SerialMatPackMetadata {
        String name;
        String author;
        String website;
    }
}

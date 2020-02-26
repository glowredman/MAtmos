package eu.ha3.matmos.core.expansion;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;

import com.google.gson.Gson;
import com.google.gson.JsonObject;

import eu.ha3.mc.haddon.UpdatableIdentity;
import eu.ha3.mc.haddon.implem.HaddonIdentity;
import eu.ha3.mc.haddon.implem.HaddonVersion;

public class SoundpackIdentity implements UpdatableIdentity {
    
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

    @Override
    public String getHaddonName() {
        return name;
    }

    @Override
    public HaddonVersion getHaddonVersion() {
        return version;
    }

    @Override
    public String getHaddonMinecraftVersion() {
        return "";
    }

    @Override
    public String getHaddonAddress() {
        return website;
    }

    @Override
    public String getHaddonHumanVersion() {
        return getHaddonVersion().toString();
    }

    @Override
    public String getUniqueName() {
        return uniqueName;
    }

    @Override
    public List<String> getUpdateURLs() {
        return new ArrayList<String>(Arrays.asList(updateJson));
    }
    
    @Override
    public boolean equals(Object obj) {
        if(obj instanceof SoundpackIdentity) {
            SoundpackIdentity o = (SoundpackIdentity)obj;
            return  Objects.equals(type, o.type) &&
                    Objects.equals(engineVersion, o.engineVersion) &&
                    Objects.equals(name, o.name) &&
                    Objects.equals(author, o.author) &&
                    Objects.equals(website, o.website) &&
                    Objects.equals(uniqueName, o.uniqueName) &&
                    Objects.equals(version, o.version) &&
                    Objects.equals(updateJson, o.updateJson);
        }
        return super.equals(obj);
    }
    
    @Override
    public int hashCode() {
        return Objects.hash(type, engineVersion, name, author, website, uniqueName, version, updateJson);
    }
}

package eu.ha3.matmos.core.event;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import eu.ha3.matmos.MAtLog;
import eu.ha3.matmos.core.Component;
import eu.ha3.matmos.core.SoundRelay;
import net.minecraft.client.Minecraft;
import net.minecraft.client.resources.IResourcePack;
import net.minecraft.util.ResourceLocation;

public class Event extends Component implements EventInterface {
    private static Random random = new Random();

    public final List<String> paths;
    public final float volMin;
    public final float volMax;
    public final float pitchMin;
    public final float pitchMax;
    public final int distance;

    private final SoundRelay relay;

    public Event(String name, SoundRelay relay, List<String> paths, float volMin, float volMax, float pitchMin, float pitchMax, int distance) {
        super(name);
        this.relay = relay;

        this.paths = paths;
        this.volMin = volMin;
        this.volMax = volMax;
        this.pitchMin = pitchMin;
        this.pitchMax = pitchMax;
        this.distance = distance;
    }

    @Override
    public void cacheSounds(IResourcePack resourcePack) {
        IResourcePack def = Minecraft.getMinecraft().getResourcePackRepository().rprDefaultResourcePack;
        List<String> toRemove = new ArrayList<>();
        for (String path : paths) {
            ResourceLocation location = new ResourceLocation("minecraft", "sounds/" + path);
            if (resourcePack.resourceExists(location) || def.resourceExists(location)) {
                relay.cacheSound(path);
            } else {
                MAtLog.warning("File: " + path + " appears to be missing from: " + resourcePack.getPackName() + " [This sound will not be cached or played in-game]");
                toRemove.add(path);
            }
        }
        paths.removeAll(toRemove);
    }

    @Override
    public void playSound(float volMod, float pitchMod) {
        if (paths.isEmpty()) {
            return;
        }

        float volume = volMax - volMin;
        float pitch = pitchMax - pitchMin;
        volume = volMin + (volume > 0 ? random.nextFloat() * volume : 0);
        pitch = pitchMin + (pitch > 0 ? random.nextFloat() * pitch : 0);

        String path = paths.get(random.nextInt(paths.size()));

        volume = volume * volMod;
        pitch = pitch * pitchMod;

        relay.playSound(path, volume, pitch, distance);
    }
}

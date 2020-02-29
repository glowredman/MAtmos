package eu.ha3.matmos.core.sound;

import java.util.LinkedHashMap;
import java.util.Map;

import eu.ha3.matmos.Matmos;
import eu.ha3.matmos.core.expansion.Stable;
import eu.ha3.matmos.util.IDontKnowHowToCode;
import net.minecraft.client.Minecraft;
import net.minecraft.entity.Entity;
import net.minecraft.util.ResourceLocation;

public class SoundHelper implements SoundCapabilities, Stable {

    protected final Map<String, NoAttenuationMovingSound> streaming = new LinkedHashMap<>();

    private float volumeModulator;

    private boolean isInterrupt;
    
    private boolean isActivated;

    @Override
    public void playMono(String event, double xx, double yy, double zz, float volume, float pitch) {
        if (isInterrupt) {
            return;
        }

        playUnattenuatedSound(xx, yy, zz, event, volume * volumeModulator, pitch);
    }

    @Override
    public void playStereo(String event, float volume, float pitch) {
        if (isInterrupt) {
            return;
        }

        // Play the sound 2048 blocks above the player to keep support of mono sounds
        Entity e = Minecraft.getMinecraft().player;
        playUnattenuatedSound(e.posX, e.posY + 2048, e.posZ, event, volume * volumeModulator, pitch);
    }

    private void playUnattenuatedSound(double xx, double yy, double zz, String loc, float volume, float pitch) {
        NoAttenuationSound nas = new NoAttenuationSound(new ResourceLocation(loc), volume, pitch, (float)xx, (float)yy, (float)zz);

        Minecraft.getMinecraft().getSoundHandler().playSound(nas);

    }

    @Override
    public void registerStreaming(String customName, String path, float volume, float pitch, boolean isLooping, boolean usesPause, boolean underwater) {
        if (isInterrupt) {
            return;
        }

        String loc = path.replace(".ogg", "").replace('/', '.').replaceAll("[0-9]", "");
        NoAttenuationMovingSound nams = new NoAttenuationMovingSound(new ResourceLocation(loc), volume, pitch, isLooping, usesPause, underwater);

        streaming.put(customName, nams);
    }

    @Override
    public void playStreaming(String customName, float fadeIn) {
        if (isInterrupt) {
            return;
        }

        if (!streaming.containsKey(customName)) {
            IDontKnowHowToCode.warnOnce("Tried to play missing stream " + customName);
            return;
        }

        NoAttenuationMovingSound previous = streaming.get(customName);
        NoAttenuationMovingSound newSound = null;
        
        boolean reuse = false;
        
        if(previous.isDonePlaying()) {
            newSound = previous.copy();
            streaming.put(customName, newSound);
        } else {
            newSound = previous; // reuse previous sound
            reuse = true;
        }
        newSound.play(fadeIn);
        newSound.applyVolume(volumeModulator);
        
        boolean notYetPlayed = newSound.notYetPlayed();
        boolean isSoundPlaying = Minecraft.getMinecraft().getSoundHandler().isSoundPlaying(newSound);
        
        Matmos.LOGGER.debug("playStreaming " + newSound.getSoundLocation() + " (reuse=" + reuse + ", notYetPlayed = " + notYetPlayed + ", isSoundPlaying=" + isSoundPlaying + ")");
        
        if(notYetPlayed || !isSoundPlaying) {
            Minecraft.getMinecraft().getSoundHandler().playSound(newSound);
        }
    }

    @Override
    public void stopStreaming(String customName, float fadeOut) {
        if (isInterrupt) {
            return;
        }

        if (!streaming.containsKey(customName)) {
            IDontKnowHowToCode.warnOnce("Tried to stop missing stream " + customName);
            return;
        }
        
        NoAttenuationMovingSound sound = streaming.get(customName);
        
        Matmos.LOGGER.debug("stopStreaming " + sound.getSoundLocation());

        streaming.get(customName).stop(fadeOut);
    }

    @Override
    public void stop() {
        if (isInterrupt) {
            return;
        }

        for (StreamingSound sound : streaming.values()) {
            sound.dispose();
        }
    }

    @Override
    public void applyVolume(float volumeMod) {
        volumeModulator = volumeMod;
        
        for (StreamingSound sound : streaming.values()) {
            sound.applyVolume(volumeMod);
        }
    }

    @Override
    public void interrupt() {
        isInterrupt = true;
    }

    @Override
    public void cleanUp() {
        if (isInterrupt) {
            return;
        }

        for (StreamingSound sound : streaming.values()) {
            sound.dispose();
        }
        streaming.clear();
    }
    
    @Override
    public boolean isActivated() {
        return isActivated;
    }

    @Override
    public void activate() {
        if(isActivated) return;
        
        isActivated = true;
    }

    @Override
    public void deactivate() {
        if(!isActivated) return;
        
        streaming.keySet().forEach(s -> {stopStreaming(s, 2);});
        isActivated = false;
    }

    @Override
    public void dispose() {
        cleanUp();
    }

}

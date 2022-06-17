package eu.ha3.matmos.core.sound;

import java.util.HashMap;
import java.util.Map;

import eu.ha3.matmos.core.SoundRelay;
import eu.ha3.matmos.util.MAtUtil;
import net.minecraft.client.Minecraft;
import net.minecraft.entity.Entity;

public class SoundHelperRelay extends SoundHelper implements SoundRelay {
    private static int streamingToken;
    protected final Map<String, Integer> pathToToken = new HashMap<>();

    private final Map<String, String> paths = new HashMap<>();

    public SoundHelperRelay(LoopingStreamedSoundManager soundManager) {
        super(soundManager);
    }

    @Override
    public void routine() {
        super.routine();
    }

    @Override
    public void cacheSound(String path) {
        String dotted = path.replace(".ogg", "").replace('/', '.').replaceAll("[0-9]", "");
        paths.put(path, dotted);
    }

    @Override
    public void playSound(String path, float volume, float pitch, int meta) {
        // XXX 2014-01-12 TEMPORARY: USE MONO-STEREO
        // playStereo(this.paths.get(path), volume, pitch);
        Entity e = MAtUtil.getPlayer();

        if (meta <= 0) {
            playStereo(paths.get(path), volume, pitch);
        } else {
            playMono(paths.get(path), e.posX, e.posY, e.posZ, volume, pitch);
        }
    }
    
    @Override
    public void playSoundEvent(String path, float x, float y, float z, float volume, float pitch) {
        playMono(path, x, y, z, volume, pitch);
    }

    @Override
    public int getNewStreamingToken() {
        return SoundHelperRelay.streamingToken++;
    }

    @Override
    public boolean setupStreamingToken(int token, String path, float pitch, boolean isLooping, boolean usesPause,
            boolean underwater) {
        registerStreaming("" + token, path, 1f, pitch, isLooping, usesPause, underwater, this);

        return true;
    }

    @Override
    public void setVolume(int token, float newVolume, float fadeDuration) {
        super.setVolume("" + token, newVolume, fadeDuration);
    }

    @Override
    public void eraseStreamingToken(int token) {
    }

    @Override
    public int getStreamingTokenFor(String path, float volume, float pitch, boolean isLooping, boolean usesPause,
            boolean underwater) {
        if (pathToToken.containsKey(path)) {
            return pathToToken.get(path);
        }

        int token = getNewStreamingToken();

        registerStreaming("" + token, path, volume, pitch, isLooping, usesPause, underwater, this);

        pathToToken.put(path, token);

        return token;
    }
}

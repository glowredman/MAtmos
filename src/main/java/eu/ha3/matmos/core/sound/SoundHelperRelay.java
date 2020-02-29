package eu.ha3.matmos.core.sound;

import java.util.HashMap;
import java.util.Map;

import eu.ha3.matmos.core.SoundRelay;
import net.minecraft.client.Minecraft;
import net.minecraft.entity.Entity;

public class SoundHelperRelay extends SoundHelper implements SoundRelay {
    private static int streamingToken;

    private final Map<String, String> paths = new HashMap<>();

    @Override
    public void routine() {
    }

    @Override
    public void cacheSound(String path) {
        String dotted = path.replace(".ogg", "").replace('/', '.').replaceAll("[0-9]", "");
        paths.put(path, dotted);
    }

    @Override
    public void playSound(String path, float volume, float pitch, int meta) {
        // XXX 2014-01-12 TEMPORARY: USE MONO-STEREO
        //playStereo(this.paths.get(path), volume, pitch);
        Entity e = Minecraft.getMinecraft().player;

        if (meta <= 0) {
            playStereo(paths.get(path), volume, pitch);
        } else {
            playMono(paths.get(path), e.posX, e.posY, e.posZ, volume, pitch);
        }
    }

    @Override
    public int getNewStreamingToken() {
        return SoundHelperRelay.streamingToken++;
    }

    @Override
    public boolean setupStreamingToken(int token, String path, float volume, float pitch, boolean isLooping,
            boolean usesPause, boolean underwater) {
        registerStreaming("" + token, path, volume, pitch, isLooping, usesPause, underwater);

        return true;
    }

    @Override
    public void startStreaming(int token, float fadeDuration) {
        playStreaming(Integer.toString(token), fadeDuration);
    }

    @Override
    public void stopStreaming(int token, float fadeDuration) {
        stopStreaming(Integer.toString(token), fadeDuration);
    }

    @Override
    public void eraseStreamingToken(int token) {
    }

    @Override
    public boolean isPlaying(int token) {
        return Minecraft.getMinecraft().getSoundHandler().isSoundPlaying(streaming.get(String.valueOf(token)));
    }
}

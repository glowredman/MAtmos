package eu.ha3.matmos.core.sound;

import eu.ha3.matmos.Matmos;
import eu.ha3.matmos.core.SystemClock;
import eu.ha3.matmos.util.math.HelperFadeCalculator;
import net.minecraft.client.Minecraft;
import net.minecraft.client.audio.ISound;
import net.minecraft.client.audio.MovingSound;
import net.minecraft.client.audio.Sound;
import net.minecraft.entity.Entity;
import net.minecraft.init.SoundEvents;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.SoundCategory;

public class NoAttenuationMovingSound extends MovingSound implements StreamingSound {
    private boolean usesPause;
    private final HelperFadeCalculator helper = new HelperFadeCalculator(new SystemClock());
    private float desiredVolume;
    private float desiredPitch;
    private float volumeMod;
    private boolean underwater;

    private boolean notYetPlayed = true;

    protected NoAttenuationMovingSound(ResourceLocation myResource, float volume, float pitch, boolean isLooping,
            boolean usesPause, boolean underwater) {
        super(SoundEvents.AMBIENT_CAVE, underwater ? SoundCategory.MASTER : SoundCategory.AMBIENT);

        positionedSoundLocation = myResource;
        attenuationType = ISound.AttenuationType.NONE;
        repeat = isLooping;
        repeatDelay = 0;

        desiredVolume = volume;
        desiredPitch = pitch;
        this.volume = 0.00001f;
        this.pitch = pitch;

        this.usesPause = usesPause;
        this.underwater = underwater;
    }

    public NoAttenuationMovingSound copy() {
        return new NoAttenuationMovingSound(getSoundLocation(), desiredVolume, desiredPitch, repeat, usesPause,
                underwater);
    }

    @Override
    public void update() {
        Entity e = Minecraft.getMinecraft().player;

        xPosF = (float) e.posX;
        yPosF = (float) e.posY;
        zPosF = (float) e.posZ;

        volume = helper.calculateFadeFactor() * desiredVolume * volumeMod;

        if (volume < 0.01f && usesPause) {
            pitch = 0f;
        }

        if (volume > 0.01f && usesPause) {
            pitch = desiredPitch;
        }

        if (helper.isDoneFadingOut() && repeat && !isDonePlaying()) {
            dispose();
        }
    }

    @Override
    public void play(float fadeIn) {
        setVolume(desiredVolume, fadeIn);
    }

    @Override
    public void stop(float fadeOut) {
        setVolume(0, fadeOut);
    }

    public void setVolume(float volume, float fadeTime) {
        helper.fadeTo(volume / desiredVolume, (long) (fadeTime * 1000));
    }

    @Override
    public void applyVolume(float volumeMod) {
        this.volumeMod = volumeMod;
    }

    public float getTargetVolume() {
        return helper.getTargetFade() * desiredVolume;
    }

    @Override
    public void dispose() {
        donePlaying = true;
    }

    @Override
    public void interrupt() {
        donePlaying = true;
    }

    @Override
    public Sound getSound() {
        notYetPlayed = false;
        return super.getSound();
    }

    public boolean notYetPlayed() {
        return notYetPlayed;
    }

    public boolean popNotYetPlayed() {
        boolean wasNotYetPlayed = notYetPlayed;
        if (notYetPlayed) {
            notYetPlayed = false;
        }
        return wasNotYetPlayed;
    }

}

package eu.ha3.matmos.game;

import eu.ha3.matmos.engine.processor.VolumeModifier;
import net.minecraft.client.Minecraft;
import net.minecraft.client.audio.MovingSound;
import net.minecraft.util.ResourceLocation;

/**
 * @author dags_ <dags@dags.me>
 */

public class StreamingSound extends MovingSound
{
    private final VolumeModifier expansionVolume;
    private final float volMin;
    private final float pitchMax;
    private final int fadeInTicks;
    private final int fadeOutTicks;
    private final float fadeInInc;
    private final float fadeOutInc;

    private int fadeInTicker = 0;
    private int fadeOutTicker = 0;

    public StreamingSound(ResourceLocation location, VolumeModifier expVol, int fadeIn, int fadeOut, float volumeMin, float volumeMax, float pitch)
    {
        super(location);
        expansionVolume = expVol;
        volMin = volumeMin;
        pitchMax = pitch;
        fadeInTicks = fadeIn;
        fadeOutTicks = fadeOut;
        fadeInInc = (volumeMax - volumeMin) / fadeIn;
        fadeOutInc = volumeMax / fadeOut;
    }

    public StreamingSound play()
    {
        fadeOutTicker = 0;
        fadeInTicker = fadeInTicks;
        this.volume = volMin > 0 ? volMin : 0.001F;
        this.pitch = pitchMax;
        this.attenuationType = AttenuationType.NONE;
        Minecraft.getMinecraft().getSoundHandler().playSound(this);
        return this;
    }

    @Override
    public void update()
    {
        if (fadeInTicker > 0)
        {
            fadeInTicker--;
            super.volume += fadeInInc;
        }
        if (fadeOutTicker > 0)
        {
            super.volume -= fadeOutInc;
            --fadeOutTicker;
        }
        if (volume <= 0F)
        {
            super.donePlaying = true;
            Minecraft.getMinecraft().getSoundHandler().stopSound(this);
        }
        super.volume *= expansionVolume.getVolumeModifier();
    }

    public boolean isPlaying()
    {
        return Minecraft.getMinecraft().getSoundHandler().isSoundPlaying(this);
    }

    public void interrupt()
    {
        fadeInTicker = 0;
        fadeOutTicker = fadeOutTicks;
    }
}

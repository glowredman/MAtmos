package eu.ha3.matmos.engine.event;

import com.google.common.base.Optional;
import eu.ha3.matmos.MAtmos;
import eu.ha3.matmos.game.MCGame;
import eu.ha3.matmos.serialize.EventSerialize;
import eu.ha3.matmos.util.NumberUtil;
import net.minecraft.client.Minecraft;
import net.minecraft.client.audio.PositionedSoundRecord;
import net.minecraft.util.ResourceLocation;

/**
 * @author dags_ <dags@dags.me>
 */

public class SoundEvent extends EventProcessor
{
    public SoundEvent(String expansion, EventSerialize e, MAtmos mAtmos)
    {
        super(expansion, e, mAtmos);
    }

    @Override
    public boolean soundIsPlaying()
    {
        return false;
    }

    @Override
    public void interrupt()
    {}

    @Override
    public void trigger()
    {
        Optional<ResourceLocation> location = getRandomSound();
        if (location.isPresent())
        {
            float volume = NumberUtil.nextFloat(minVol, maxVol) * super.volume.getVolumeModifier();
            float pitch = NumberUtil.nextFloat(minPitch, maxPitch);
            float x = (float) MCGame.playerXpos + randomDistance();
            float y = (float) MCGame.playerYpos + randomDistance();
            float z = (float) MCGame.playerZpos + randomDistance();
            Minecraft.getMinecraft().getSoundHandler().playSound(new PositionedSoundRecord(location.get(), volume, pitch, x, y, z));
        }
    }

    private float randomDistance()
    {
        if (distance > 0)
            return NumberUtil.nextRanInt(0, distance);
        return 0;
    }
}

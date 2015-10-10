package eu.ha3.matmos.engine.event;

import eu.ha3.matmos.MAtmos;
import eu.ha3.matmos.game.MCGame;
import eu.ha3.matmos.serialize.EventSerialize;
import eu.ha3.matmos.util.NumberUtil;
import net.minecraft.client.Minecraft;
import net.minecraft.client.audio.PositionedSoundRecord;

/**
 * @author dags_ <dags@dags.me>
 */

public class SoundEvent extends EventProcessor
{
    public SoundEvent(EventSerialize e, MAtmos mAtmos)
    {
        super(e, mAtmos);
    }

    @Override
    public void interrupt()
    {}

    @Override
    public void trigger()
    {
        float volume = NumberUtil.nextFloat(minVol, maxVol);
        float pitch = NumberUtil.nextFloat(minPitch, maxPitch);
        float x = (float) MCGame.playerXpos + randomDistance();
        float y = (float) MCGame.playerYpos + randomDistance();
        float z = (float) MCGame.playerZpos + randomDistance();
        Minecraft.getMinecraft().getSoundHandler().playSound(new PositionedSoundRecord(getRandomSound(), volume, pitch, x, y, z));
    }

    private float randomDistance()
    {
        if (distance > 0)
            return NumberUtil.nextRanInt(0, distance);
        return 0;
    }
}

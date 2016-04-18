package eu.ha3.matmos.game.system;

import net.minecraft.client.audio.ISound;
import net.minecraft.client.audio.PositionedSoundRecord;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.SoundCategory;

/*
--filenotes-placeholder
*/

public class NoAttenuationSound extends PositionedSoundRecord
{
	public NoAttenuationSound(ResourceLocation loc, float volume, float pitch, float x, float y, float z)
	{
		super(loc, SoundCategory.MASTER, volume, pitch, false, 0, ISound.AttenuationType.NONE, x, y, z);
	}
}

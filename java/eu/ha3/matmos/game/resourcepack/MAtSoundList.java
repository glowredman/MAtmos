package eu.ha3.matmos.game.resourcepack;

import net.minecraft.client.audio.SoundList;

import java.util.ArrayList;
import java.util.List;

/**
 * @author dags_ <dags@dags.me>
 */

public class MAtSoundList extends SoundList
{
    private final List<SoundEntry> matSoundList = new ArrayList<SoundEntry>();

    public MAtSoundList(String entry)
    {
        SoundEntry e = new SoundEntry();
        e.setSoundEntryName(entry.replaceAll("\\.", "/"));
        e.setStreaming(entry.contains("stream_"));
        matSoundList.add(e);
    }

    @Override
    public List getSoundList()
    {
        return matSoundList;
    }
}

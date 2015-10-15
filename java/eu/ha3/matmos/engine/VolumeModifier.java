package eu.ha3.matmos.engine;

/**
 * @author dags_ <dags@dags.me>
 */

public class VolumeModifier
{
    private float volumeModifier = 1F;
    private final transient boolean master;

    protected VolumeModifier(boolean isMaster)
    {
        master = isMaster;
    }

    public VolumeModifier()
    {
        master = false;
    }

    public float getVolumeModifier()
    {
        return master ? volumeModifier : volumeModifier * ExpansionRegistry.masterVolume.getVolumeModifier();
    }

    public VolumeModifier setVolumeModifier(float f)
    {
        if (f >= 0 && f <= 2F)
        {
            volumeModifier = f;
        }
        return this;
    }
}

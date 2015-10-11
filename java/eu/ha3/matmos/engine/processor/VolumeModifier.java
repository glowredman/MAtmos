package eu.ha3.matmos.engine.processor;

/**
 * @author dags_ <dags@dags.me>
 */

public class VolumeModifier
{
    private float volumeModifier = 1F;

    public float getVolumeModifier()
    {
        return volumeModifier;
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

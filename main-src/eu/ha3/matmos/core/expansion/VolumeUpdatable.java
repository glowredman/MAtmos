package eu.ha3.matmos.core.expansion;

/*
 * --filenotes-placeholder
 */

public interface VolumeUpdatable extends VolumeContainer {
    public void setVolumeAndUpdate(float volume);

    public void updateVolume();
}

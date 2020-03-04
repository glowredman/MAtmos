package eu.ha3.matmos.core.expansion;

public interface VolumeUpdatable extends VolumeContainer {
    void setVolumeAndUpdate(float volume);

    void updateVolume();
}

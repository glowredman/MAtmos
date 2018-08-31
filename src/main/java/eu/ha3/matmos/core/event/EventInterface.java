package eu.ha3.matmos.core.event;

import net.minecraft.client.resources.IResourcePack;

public interface EventInterface {

    void cacheSounds(IResourcePack resourcePack);

    void playSound(float volMod, float pitchMod);

}

package eu.ha3.matmos.core.event;

import net.minecraft.client.resources.IResourcePack;

public interface EventInterface {

    public abstract void cacheSounds(IResourcePack resourcePack);

    public abstract void playSound(float volMod, float pitchMod);

}

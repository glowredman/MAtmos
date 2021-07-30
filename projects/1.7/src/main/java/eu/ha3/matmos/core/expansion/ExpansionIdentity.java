package eu.ha3.matmos.core.expansion;

import net.minecraft.client.resources.IResourcePack;
import net.minecraft.util.ResourceLocation;

public final class ExpansionIdentity {
    private final String uniqueName;
    private final String friendlyName;
    private final IResourcePack pack;
    private final ResourceLocation location;
    private final float volumeModifier;

    public ExpansionIdentity(String uniqueName, String friendlyName, IResourcePack pack, ResourceLocation location,
            float volumeModifier) {
        this.uniqueName = uniqueName;
        this.friendlyName = friendlyName;
        this.pack = pack;
        this.location = location;
        this.volumeModifier = volumeModifier;
    }

    public String getUniqueName() {
        return uniqueName;
    }

    public String getFriendlyName() {
        return friendlyName;
    }

    public IResourcePack getPack() {
        return pack;
    }

    public ResourceLocation getLocation() {
        return location;
    }

    public float getVolumeModifier() {
        return volumeModifier;
    }
}

package eu.ha3.matmos.core.expansion;

import net.minecraft.client.resources.IResourcePack;
import net.minecraft.util.ResourceLocation;

/*
 * --filenotes-placeholder
 */

public final class ExpansionIdentity {
    private final String uniqueName;
    private final String friendlyName;
    private final IResourcePack pack;
    private final ResourceLocation location;

    public ExpansionIdentity(String uniqueName, String friendlyName, IResourcePack pack, ResourceLocation location) {
        this.uniqueName = uniqueName;
        this.friendlyName = friendlyName;
        this.pack = pack;
        this.location = location;
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
}

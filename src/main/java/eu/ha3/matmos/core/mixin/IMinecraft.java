package eu.ha3.matmos.core.mixin;

import java.util.List;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

import net.minecraft.client.Minecraft;
import net.minecraft.client.resources.IResourcePack;

@Mixin(Minecraft.class)
public interface IMinecraft {
    @Accessor("defaultResourcePacks")
    List<IResourcePack> defaultResourcePacks();
}

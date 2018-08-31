package eu.ha3.matmos.data.modules.legacy;

import eu.ha3.matmos.core.sheet.DataPackage;
import eu.ha3.matmos.data.modules.MODULE_CONSTANTS;
import eu.ha3.matmos.data.modules.Module;
import eu.ha3.matmos.data.modules.ModuleProcessor;
import eu.ha3.matmos.util.MAtUtil;
import net.minecraft.client.Minecraft;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.util.math.RayTraceResult.Type;

import java.util.HashMap;
import java.util.Map;

/*
 * --filenotes-placeholder
 */

public class ModuleLegacyHitscan extends ModuleProcessor implements Module {
    private final Map<Type, String> equiv = new HashMap<Type, String>();

    public ModuleLegacyHitscan(DataPackage data) {
        super(data, "legacy_hitscan");

        // The ordinal values was different back then, "0" was the block.
        this.equiv.put(Type.MISS, "-1");
        this.equiv.put(Type.ENTITY, "1");
        this.equiv.put(Type.BLOCK, "0");
    }

    @Override
    protected void doProcess() {
        RayTraceResult mc = Minecraft.getMinecraft().objectMouseOver;

        if (mc == null || mc.typeOfHit == null) {
            setValue("mouse_over_something", false);
            setValue("mouse_over_what_remapped", -1);
            setValue("block_as_number", MODULE_CONSTANTS.LEGACY_NO_BLOCK_IN_THIS_CONTEXT);
            setValue("meta_as_number", MODULE_CONSTANTS.LEGACY_NO_BLOCK_IN_THIS_CONTEXT);

            return;
        }

        setValue("mouse_over_something", mc.typeOfHit != Type.MISS);
        setValue("mouse_over_what_remapped", this.equiv.get(mc.typeOfHit));
        setValue("block_as_number",
                mc.typeOfHit == Type.BLOCK
                        ? MAtUtil.legacyOf(MAtUtil.getBlockAt(mc.getBlockPos()))
                        : MODULE_CONSTANTS.LEGACY_NO_BLOCK_IN_THIS_CONTEXT);
        setValue("meta_as_number",
                mc.typeOfHit == Type.BLOCK ? MAtUtil.getMetaAt(mc.getBlockPos(), MODULE_CONSTANTS.LEGACY_NO_BLOCK_OUT_OF_BOUNDS)
                        : MODULE_CONSTANTS.LEGACY_NO_BLOCK_IN_THIS_CONTEXT);
    }
}

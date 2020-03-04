package eu.ha3.matmos.data.modules.player;

import java.util.HashMap;
import java.util.Map;

import eu.ha3.matmos.core.sheet.DataPackage;
import eu.ha3.matmos.data.modules.Module;
import eu.ha3.matmos.data.modules.ModuleProcessor;
import eu.ha3.matmos.util.MAtUtil;
import net.minecraft.client.Minecraft;
import net.minecraft.entity.EntityList;
import net.minecraft.util.math.RayTraceResult.Type;

/**
 * Processes values for the entity/block that a player focuses over with the cursor.
 */
public class ModuleHitscan extends ModuleProcessor implements Module {
    private final Map<Type, String> equiv = new HashMap<>();

    public ModuleHitscan(DataPackage data) {
        super(data, "ply_hitscan");
        equiv.put(Type.MISS, "");
        equiv.put(Type.ENTITY, "entity");
        equiv.put(Type.BLOCK, "block");
    }

    @Override
    protected void doProcess() {
        Minecraft mc = Minecraft.getMinecraft();

        if (mc.objectMouseOver == null || mc.objectMouseOver.typeOfHit == null) {
            setValue("mouse_over_something", false);
            setValue("mouse_over_what", "");
            setValue("block", NO_BLOCK_IN_THIS_CONTEXT);
            setValue("meta", NO_META);
            setValue("powermeta", NO_POWERMETA);
            setValue("entity_id", NO_ENTITY);

            return;
        }

        setValue("mouse_over_something", mc.objectMouseOver.typeOfHit != Type.MISS);
        setValue("mouse_over_what", equiv.get(mc.objectMouseOver.typeOfHit));
        setValue("block", mc.objectMouseOver.typeOfHit == Type.BLOCK ? MAtUtil.getNameAt(mc.objectMouseOver.getBlockPos(), NO_BLOCK_OUT_OF_BOUNDS) : NO_BLOCK_IN_THIS_CONTEXT);
        setValue("meta", mc.objectMouseOver.typeOfHit == Type.BLOCK ? MAtUtil.getMetaAsStringAt(mc.objectMouseOver.getBlockPos(), NO_BLOCK_OUT_OF_BOUNDS) : NO_BLOCK_IN_THIS_CONTEXT);
        setValue("powermeta", mc.objectMouseOver.typeOfHit == Type.BLOCK ? MAtUtil.getPowerMetaAt(mc.objectMouseOver.getBlockPos(), NO_BLOCK_OUT_OF_BOUNDS) : NO_BLOCK_IN_THIS_CONTEXT);
        setValue("entity_id", mc.objectMouseOver.typeOfHit == Type.ENTITY ? EntityList.getKey(mc.objectMouseOver.entityHit).toString() : NO_ENTITY);
    }
}

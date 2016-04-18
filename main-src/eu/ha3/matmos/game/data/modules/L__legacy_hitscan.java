package eu.ha3.matmos.game.data.modules;

import eu.ha3.matmos.engine.core.interfaces.Data;
import eu.ha3.matmos.game.data.MODULE_CONSTANTS;
import eu.ha3.matmos.game.data.abstractions.module.Module;
import eu.ha3.matmos.game.data.abstractions.module.ModuleProcessor;
import eu.ha3.matmos.game.system.MAtmosUtility;
import net.minecraft.client.Minecraft;
import net.minecraft.util.math.RayTraceResult.Type;

import java.util.HashMap;
import java.util.Map;

/*
--filenotes-placeholder
*/

public class L__legacy_hitscan extends ModuleProcessor implements Module
{
	private final Map<Type, String> equiv = new HashMap<Type, String>();
	
	public L__legacy_hitscan(Data data)
	{
		super(data, "legacy_hitscan");
		
		// The ordinal values was different back then, "0" was the block.
		this.equiv.put(Type.MISS, "-1");
		this.equiv.put(Type.ENTITY, "1");
		this.equiv.put(Type.BLOCK, "0");
	}
	
	@Override
	protected void doProcess()
	{
		Minecraft mc = Minecraft.getMinecraft();
		
		if (mc.objectMouseOver == null || mc.objectMouseOver.typeOfHit == null)
		{
			setValue("mouse_over_something", false);
			setValue("mouse_over_what_remapped", -1);
			setValue("block_as_number", MODULE_CONSTANTS.LEGACY_NO_BLOCK_IN_THIS_CONTEXT);
			setValue("meta_as_number", MODULE_CONSTANTS.LEGACY_NO_BLOCK_IN_THIS_CONTEXT);
			
			return;
		}

        // dag edits - getBlockPos().get..()
		setValue("mouse_over_something", mc.objectMouseOver.typeOfHit != Type.MISS);
		setValue("mouse_over_what_remapped", this.equiv.get(mc.objectMouseOver.typeOfHit));
		setValue(
			"block_as_number",
			mc.objectMouseOver.typeOfHit == Type.BLOCK
				? MAtmosUtility.legacyOf(MAtmosUtility.getBlockAt(
					mc.objectMouseOver.getBlockPos().getX(), mc.objectMouseOver.getBlockPos().getY(),
                    mc.objectMouseOver.getBlockPos().getZ()))
				: MODULE_CONSTANTS.LEGACY_NO_BLOCK_IN_THIS_CONTEXT);
		setValue(
			"meta_as_number",
			mc.objectMouseOver.typeOfHit == Type.BLOCK ? MAtmosUtility.getMetaAt(
				mc.objectMouseOver.getBlockPos().getX(), mc.objectMouseOver.getBlockPos().getY(),
                    mc.objectMouseOver.getBlockPos().getZ(),
				MODULE_CONSTANTS.LEGACY_NO_BLOCK_OUT_OF_BOUNDS) : MODULE_CONSTANTS.LEGACY_NO_BLOCK_IN_THIS_CONTEXT);
	}
}
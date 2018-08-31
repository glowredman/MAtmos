package eu.ha3.matmos.data.modules.world;

import eu.ha3.matmos.core.sheet.DataPackage;
import eu.ha3.matmos.data.modules.AbstractStringCountModule;
import eu.ha3.matmos.util.MAtUtil;
import net.minecraft.util.math.BlockPos;

public class ModuleCollission extends AbstractStringCountModule {
    private final BlockPos[] blocks = {
            new BlockPos(0, -1, 0), // bottom
            BlockPos.ORIGIN,
            new BlockPos(0, 1, 0), // body
            new BlockPos(0, 2, 0), // column
            new BlockPos(-1, 0, 0),
            new BlockPos(1, 0, 0), // x -- 0
            new BlockPos(0, 0, -1),
            new BlockPos(0, 0, 1), // z -- 0
            new BlockPos(-1, 1, 0),
            new BlockPos(1, 1, 0), // x -- 1
            new BlockPos(0, 1, -1),
            new BlockPos(0, 1, 1), // z -- 1 
    };

    public ModuleCollission(DataPackage dataIn) {
        super(dataIn, "block_contact", true);
        dataIn.getSheet(getModuleName()).setDefaultValue("0");
    }

    @Override
    protected void count() {
        for (BlockPos triplet : blocks) {
            BlockPos center = triplet.add(getPlayer().getPosition());
            increment(MAtUtil.getNameAt(center, ""));
            increment(MAtUtil.getPowerMetaAt(center, ""));
        }
    }
}

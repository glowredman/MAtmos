package eu.ha3.matmos.util.math;

import net.minecraft.util.math.BlockPos;

/**
 * @author dags_ <dags@dags.me>
 */
public class MAtMutableBlockPos extends BlockPos {
    private int x;
    private int y;
    private int z;

    public MAtMutableBlockPos() {
        super(0, 0, 0);
    }

    public MAtMutableBlockPos of(int xPos, int yPos, int zPos) {
        x = xPos;
        y = yPos;
        z = zPos;
        return this;
    }

    @Override
    public int getX() {
        return x;
    }

    @Override
    public int getY() {
        return y;
    }

    @Override
    public int getZ() {
        return z;
    }
}

package eu.ha3.matmos.game;

import net.minecraft.util.BlockPos;
import net.minecraft.util.MathHelper;

/**
 * @author dags_ <dags@dags.me>
 */

public class Position extends BlockPos
{
    private int x;
    private int y;
    private int z;

    public Position()
    {
        super(0, 0, 0);
    }

    public Position update(int xPos, int yPos, int zPos)
    {
        x = xPos;
        y = yPos;
        z = zPos;
        return this;
    }

    public Position update(double xPos, double yPos, double zPos)
    {
        x = MathHelper.floor_double(xPos);
        y = MathHelper.floor_double(yPos);
        z = MathHelper.floor_double(zPos);
        return this;
    }

    public int getX()
    {
        return x;
    }

    public int getY()
    {
        return y;
    }

    public int getZ()
    {
        return z;
    }

    public String serial()
    {
        return x + ":" + y + ":" + z;
    }
}

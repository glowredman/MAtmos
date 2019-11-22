package eu.ha3.matmos.game.data.abstractions.scanner;

import eu.ha3.matmos.game.system.MAtmosUtility;
import net.minecraft.client.Minecraft;
import net.minecraft.world.World;

/** This scanner is used to determine whether the player should hear indoors or outdoors ambiance. */
public class ScanAir extends Scan {

	private int scanDistance;
	
	private int startX, startY, startZ;
	
	private int xx, yy, zz;
	
	@Override
	void initScan(int x, int y, int z, int xsizeIn, int ysizeIn, int zsizeIn, int opspercallIn)
	{
		startX = x;
		startY = y;
		startZ = z;
		
		xx = 0;
		yy = 0;
		zz = 0;
		
		scanDistance = Math.min(Math.min((xsizeIn - 1) / 2, (ysizeIn - 1) / 2), (zsizeIn - 1) / 2);
		
		finalProgress = 1; // TODO proper progress tracking. for now it just sets it to 1 when finished
	}

	/** 
	 * It works by finding the nearest skylit block to the player. The outline of the way it works is the following:
	 * 1. It performs a breadth-first flood fill starting from the player, restricted to air blocks within
	 *    the region of blocks at a maximum taxicab distance of 15 (or whatever the parameter is).
	 * 2. It expands one block outwards (still staying inside the region), into solid blocks. Then it
	 *    continues expanding while restricted to air blocks.
	 *  
	 * We keep track of the distance of each block we step onto, subtracting 1 for air blocks and 4(?) for
	 * solid blocks.
	 * As a result of this algorithm, we obtain an evaluation of how well the player can hear each block.
	 * 
	 * If a skylit block is found with a distance below the threshold, the player is decided to hear
	 * outdoors ambiance.
	 */
	@Override
	protected boolean doRoutine()
	{
		int ops = 0;
		
		int scanSize = scanDistance * 2 + 1;
		
		World w = Minecraft.getMinecraft().theWorld;
		while(ops < opspercall && progress < finalProgress)
		{
			if(MAtmosUtility.isWithinBounds(yy)) {
				pipeline.input(startX - scanDistance + xx, startY - scanDistance + yy, startZ - scanDistance + zz);
			}
			xx = (xx + 1) % scanSize;
			if(xx == 0) {
				zz = (zz + 1) % scanSize;
				if(zz == 0) {
					yy = yy + 1;
					if(yy == scanSize) {
						progress = 1;
					}
				}
			}
			ops++;
		}
		
		return true;
	}
	
	
	
}

package eu.ha3.matmos.game.data.abstractions.scanner;

import java.util.ArrayDeque;
import java.util.LinkedList;
import java.util.Queue;

import eu.ha3.matmos.game.system.MAtmosUtility;
import eu.ha3.matmos.tools.ByteQueue;
import net.minecraft.block.Block;
import net.minecraft.block.BlockAir;
import net.minecraft.client.Minecraft;
import net.minecraft.world.EnumSkyBlock;
import net.minecraft.world.World;

/** This scanner is used to determine whether the player should hear indoors or outdoors ambiance. */
public class ScanAir extends Scan {

	private int scanDistance, scanSize;
	
	private int startX, startY, startZ;
	
	private int xx, yy, zz;
	
	private boolean[] visited;
	ByteQueue floodQueue;
	ByteQueue solidQueue;
	
	int nearest;
	int nearestX, nearestY, nearestZ;
	
	enum Stage {AIR1, SOLID, AIR2, FINISH};
	Stage stage = Stage.AIR1;
	
	@Override
	void initScan(int x, int y, int z, int xsizeIn, int ysizeIn, int zsizeIn, int opspercallIn)
	{
		startX = x;
		startY = y;
		startZ = z;
		
		xx = 0;
		yy = 0;
		zz = 0;
		
		final byte START_NEARNESS = 15;
		
		int newScanDistance = Math.min(Math.min((xsizeIn - 1) / 2, (ysizeIn - 1) / 2), (zsizeIn - 1) / 2);
		
		boolean reallocate = newScanDistance != scanDistance;
		scanDistance = newScanDistance;
		
		scanSize = scanDistance * 2 + 1;
		
		if(reallocate || visited == null) {
			visited = new boolean[scanSize * scanSize * scanSize];
		} else {
			for(int i = 0; i < visited.length; i++) {
				visited[i] = false;
			}
		}
		
		if(reallocate || floodQueue == null) {
			floodQueue = new ByteQueue(scanDistance * scanDistance * 6 * 4);
		} else {
			floodQueue.clear();
		}
		if(reallocate || solidQueue == null) {
			solidQueue = new ByteQueue(scanDistance * scanDistance * 6 * 4);
		} else {
			solidQueue.clear();
		}
		
		floodQueue.push4((byte)scanDistance, (byte)scanDistance, (byte)scanDistance, START_NEARNESS);
		
		nearest = -1;
		stage = Stage.AIR1;
		
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
		
		byte[] p = new byte[4];
		while(ops < opspercall && progress < finalProgress)
		{
			switch(stage) {
			case AIR1:
				if(floodQueue.pop4(p)) {
					int wx = startX - scanDistance + p[0];
					int wy = startY - scanDistance + p[1];
					int wz = startZ - scanDistance + p[2];
					if(MAtmosUtility.isWithinBounds(wy) && !getVisited(p[0], p[1], p[2])) {
						
						Block[] blockBuf = new Block[1];
						int[] metaBuf = new int[1];
						
						
						
						((ScannerModule)pipeline).inputAndReturnBlockMeta(wx, wy, wz, blockBuf, metaBuf);
						ops++;
						
						Block block = blockBuf[0];
						int meta = metaBuf[0];
						
						byte newN = (byte) (p[3] - 1);
						if(block instanceof BlockAir) {
							if(w.canBlockSeeTheSky(wx, wy, wz)) {
								if(nearest == -1 || p[3] < nearest) {
									nearest = p[3];
									nearestX = p[0];
									nearestY = p[1];
									nearestZ = p[2];
								}
							} else {
								// enqueue neighbors
								
								floodQueue.push4((byte)(p[0] - 1), 	p[1], 				p[2],				newN);
								floodQueue.push4((byte)(p[0] + 1), 	p[1],				p[2],				newN);
								floodQueue.push4(p[0], 				(byte)(p[1] - 1),	p[2],				newN);
								floodQueue.push4(p[0], 				(byte)(p[1] + 1),	p[2],				newN);
								floodQueue.push4(p[0], 				p[1], 				(byte)(p[2] - 1),	newN);
								floodQueue.push4(p[0], 				p[1], 				(byte)(p[2] + 1),	newN);
							}
						} else {
							solidQueue.push4(p[0], p[1], p[2], newN);
						}
						
						setVisited(p[0], p[1], p[2], true);
					}
				}
				if(floodQueue.length() == 0) {
					if(solidQueue.length() == 0) {
						stage = Stage.FINISH;
					} else {
						stage = Stage.SOLID;
					}
				}
				break;
			case SOLID:
				
				stage = Stage.AIR2;
				// TODO
				break;
			case AIR2:
				stage = Stage.FINISH;
				
				// TODO
				break;
			case FINISH:
				// TODO
				System.out.println("end. nearest: " + nearest);
				progress = 1;
				break;
			}
			
			/*if(MAtmosUtility.isWithinBounds(yy)) {
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
			ops++;*/
		}
		
		return true;
	}
	
	private boolean getVisited(int x, int y, int z) {
		return visited[x * scanSize * scanSize + y * scanSize + z];
	}
	
	private boolean setVisited(int x, int y, int z, boolean newValue) {
		visited[x * scanSize * scanSize + y * scanSize + z] = newValue;
		return true;
	}
	
}

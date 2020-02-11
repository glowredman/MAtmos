package eu.ha3.matmos.data.scanners;

import eu.ha3.matmos.util.ByteQueue;
import eu.ha3.matmos.util.MAtUtil;
import net.minecraft.block.Block;
import net.minecraft.block.BlockAir;
import net.minecraft.block.BlockDoor;
import net.minecraft.block.BlockGlass;
import net.minecraft.block.BlockPane;
import net.minecraft.client.Minecraft;
import net.minecraft.util.math.BlockPos;
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
	ByteQueue solidInfoQueue;
	ByteQueue finalQueue;
	
	int nearest;
	int nearestX, nearestY, nearestZ;
	
	private int score;
	
	enum Stage {AIR1, SOLID, AIR2, FINISH};
	Stage stage = Stage.AIR1;
	
	private int AIR_COST, SOLID_COST, PANE_COST; // these are meant to be final, but for ease of development are non-final for now(?)
	byte START_NEARNESS;
	
	@Override
	void initScan(int x, int y, int z, int xsizeIn, int ysizeIn, int zsizeIn, int opspercallIn)
	{
		startX = x;
		startY = y + 1;
		startZ = z;
		
		xx = 0;
		yy = 0;
		zz = 0;
		
		score = 0;
		
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
		if(reallocate || solidInfoQueue == null) {
            solidInfoQueue = new ByteQueue(scanDistance * scanDistance * 6);
        } else {
            solidInfoQueue.clear();
        }
		if(reallocate || finalQueue == null) {
			finalQueue = new ByteQueue(scanDistance * scanDistance * 6 * 4);
		} else {
			finalQueue.clear();
		}
		
		floodQueue.push4((byte)scanDistance, (byte)scanDistance, (byte)scanDistance, START_NEARNESS);
		
		nearest = -1;
		stage = Stage.AIR1;
		
		AIR_COST = 5;
		SOLID_COST = 30;
		PANE_COST = 10;
		START_NEARNESS = 80;
		
		finalProgress = 1; // TODO proper progress tracking. for now it just sets it to 1 when finished
	}

	/** 
	 * It works by finding the nearest skylit block to the player.
	 * The algorithm's phases in order are:
	 *   AIR1:
	 *     It performs a breadth-first flood fill starting from the player, restricted to air blocks within
	 *     the region of blocks at a maximum taxicab distance of 15 (or whatever the parameter is).
	 *   SOLID:
	 *     It expands one block outwards (still staying inside the region), into solid blocks.
	 *   AIR2:
	 *     Then it continues expanding while restricted to air blocks.
	 *  
	 * We keep track of the distance of each block we step onto, subtracting 1 for air blocks and 4(?) for
	 * solid blocks. When we reach a skylit block, we add its distance to the score.
	 * The algorithm terminates if the score reaches a certain value, or if we have fully scanned
	 * the area.
	 * 
	 * As a result of this algorithm, we obtain an evaluation score of how outdoors the player is.
	 * The player hears no (outdoors) ambiance, indoors ambiance, or outdoors ambiance as the score increases.
	 * 
	 * The reason this algorithm is used is because a traditional breadth first search algorithm would
	 * need to pass over the same block multiple times to give a correct result. This algorithm avoids that.
	 */
	@Override
	protected boolean doRoutine()
	{
		int ops = 0;
		
		int scanSize = scanDistance * 2 + 1;
		
		World w = Minecraft.getMinecraft().world;
		
		byte[] p = new byte[4];
		while(ops < opspercall && progress < finalProgress)
		{
			switch(stage) {
			case AIR1:
			case AIR2:
				if(floodQueue.pop4(p)) {
				    if(!(p[0] < 0 || p[0] >= scanSize || p[1] < 0 || p[1] >= scanSize || p[2] < 0 || p[2] >= scanSize)) {
    					int wx = startX - scanDistance + p[0];
    					int wy = startY - scanDistance + p[1];
    					int wz = startZ - scanDistance + p[2];
    					if(p[3] > 0 && MAtUtil.isWithinBounds(new BlockPos(0, wy, 0)) && !getVisited(p[0], p[1], p[2])) {
    						
    						Block[] blockBuf = new Block[1];
    						int[] metaBuf = new int[1];
    						
    						((ScannerModule)pipeline).inputAndReturnBlockMeta(wx, wy, wz, blockBuf, metaBuf);
    						ops++;
    						
    						Block block = blockBuf[0];
    						int meta = metaBuf[0];
    						
    						byte newN = (byte) (p[3] - AIR_COST);
    						if(block instanceof BlockAir) {
    							if(w.canBlockSeeSky(new BlockPos(wx, wy, wz))) {
    								score += newN;
    								if(score > 4000) {
    								    stage = Stage.FINISH;
    								}
    								//stage = Stage.FINISH;
    									
    								//finalQueue.push4(p[0], p[1], p[2], p[3]);
    							}
    							
    							// enqueue neighbors
    							floodQueue.push4((byte)(p[0] - 1), 	p[1], 				p[2],				newN);
    							floodQueue.push4((byte)(p[0] + 1), 	p[1],				p[2],				newN);
    							floodQueue.push4(p[0], 				(byte)(p[1] - 1),	p[2],				newN);
    							floodQueue.push4(p[0], 				(byte)(p[1] + 1),	p[2],				newN);
    							floodQueue.push4(p[0], 				p[1], 				(byte)(p[2] - 1),	newN);
    							floodQueue.push4(p[0], 				p[1], 				(byte)(p[2] + 1),	newN);
    						} else {
    							(stage == Stage.AIR1 ? solidQueue : finalQueue).push4(p[0], p[1], p[2], p[3]);
    							solidInfoQueue.push(isThinBlock(block) ? (byte)1 : (byte)0);
    						}
    						
    						setVisited(p[0], p[1], p[2], true);
    					}
				    }
				} else {
					if(solidQueue.length() == 0) {
						stage = Stage.FINISH;
					} else {
						stage = Stage.SOLID;
					}
				}
				break;
			case SOLID:
				if(solidQueue.pop4(p)) {
				    boolean isThin = solidInfoQueue.pop() == 1;
					int wy = startY - scanDistance + p[1];
					if(MAtUtil.isWithinBounds(new BlockPos(0, wy, 0))) {
						
						// enqueue neighbors
						byte newN = (byte)(p[3] - (isThin ? PANE_COST : SOLID_COST));
						
						floodQueue.push4((byte)(p[0] - 1), 	p[1], 				p[2],				newN);
						floodQueue.push4((byte)(p[0] + 1), 	p[1],				p[2],				newN);
						floodQueue.push4(p[0], 				(byte)(p[1] - 1),	p[2],				newN);
						floodQueue.push4(p[0], 				(byte)(p[1] + 1),	p[2],				newN);
						floodQueue.push4(p[0], 				p[1], 				(byte)(p[2] - 1),	newN);
						floodQueue.push4(p[0], 				p[1], 				(byte)(p[2] + 1),	newN);
						
						setVisited(p[0], p[1], p[2], true);
					}
				} else {
					stage = Stage.AIR2;
				}
				break;
			case FINISH:
				// We could scan the rest of the region here (expanding from finalQueue), with the ultimate
				// goal of outsourcing scanning it to us in scan_large's stead, avoiding scanning it twice.
				// But I'm not sure if it's worth the trouble for the optimization gain.
			    
				progress = 1;
				pipeline.setValue(".outdoorness_score", score);
				break;
			}
		}
		
		return true;
	}
	
	private boolean isThinBlock(Block block) {
	    return block instanceof BlockDoor || block instanceof BlockGlass || block instanceof BlockPane;
	}
	
	private boolean getVisited(int x, int y, int z) {
		return visited[x * scanSize * scanSize + y * scanSize + z];
	}
	
	private boolean setVisited(int x, int y, int z, boolean newValue) {
		visited[x * scanSize * scanSize + y * scanSize + z] = newValue;
		return true;
	}
}

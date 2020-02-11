package eu.ha3.matmos.data.scanners;

import java.util.Random;

import net.minecraft.block.Block;
import net.minecraft.client.Minecraft;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;

public class ScanRaycast extends Scan {
    
    private static final Random rnd = new Random();
    
    int startX, startY, startZ;
    Vec3d center;
    int xSize, ySize, zSize;
    
    int raysCast = 0;
    int raysToCast;
    int score;
    
    @Override
    void initScan(int x, int y, int z, int xsizeIn, int ysizeIn, int zsizeIn, int opspercallIn) {
        startX = x;
        startY = y + 1;
        startZ = z;
        
        center = new Vec3d(startX + 0.5, startY + 0.5, startZ + 0.5);
        
        xSize = xsizeIn;
        ySize = ysizeIn;
        zSize = zsizeIn;
        
        raysCast = 0;
        raysToCast = 100;
        
        finalProgress = 1;
        
        score = 0;
    }

    @Override
    protected boolean doRoutine() {
        opspercall = 20;
        raysToCast = opspercall * 20;
        for(int ops = 0; ops < opspercall && raysCast < raysToCast; ops++) {
            
            Vec3d v = Vec3d.fromPitchYaw(rnd.nextFloat() * 360f, rnd.nextFloat() * 360f);
            
            castRay(v);
            
            raysCast++;
        }
        if(raysCast >= raysToCast) {
            progress = 1;
            
            pipeline.setValue(".outdoorness_score", score/10000);
        }
        return true;
    }
    
    private void castRay(Vec3d dir) {
        int maxRange = 100;
        
        World w = Minecraft.getMinecraft().world;
        
        RayTraceResult result = Minecraft.getMinecraft().world.rayTraceBlocks(center, center.add(dir.scale(maxRange)), true, false, true);
        if(result != null) {
            BlockPos hit = result.getBlockPos();
            
            Block[] blockBuf = new Block[1];
            int[] metaBuf = new int[1];
            int[] pos = new int[3];
            
            boolean centerSolid = false;
            
            int startNearness = 60;
            
            int airPenalty = 0;
            int solidPenalty = 55;
            
            for(int scanDir = 0; scanDir < 6; scanDir++) {
                int nearness = startNearness;
                for(int offset = 0; offset <= 2; offset++) {
                    if(offset == 0 && scanDir != 0) {
                        continue;
                    }
                    
                    int scanAxis = scanDir >= 3 ? scanDir - 3 : scanDir; 
                    
                    pos[0] = hit.getX();
                    pos[1] = hit.getY();
                    pos[2] = hit.getZ();
                    pos[scanAxis] += offset * (scanDir >= 3 ? -1 : 1);
                    
                    BlockPos blockPos = new BlockPos(pos[0], pos[1], pos[2]);
                    
                    boolean solid = w.getBlockState(blockPos).getCollisionBoundingBox(w, blockPos) != Block.NULL_AABB;
                    
                    if(solid && offset == 0 && scanDir == 0) {
                        centerSolid = true;
                    } else if(centerSolid && scanDir != 0 && offset == 1){
                        nearness -= centerSolid ? solidPenalty : airPenalty;
                    }
                    
                    nearness -= solid ? solidPenalty : airPenalty;
                    
                    ((ScannerModule)pipeline).inputAndReturnBlockMeta(pos[0], pos[1], pos[2], blockBuf, metaBuf);
                    
                    if(nearness > 0 && !solid && w.canBlockSeeSky(blockPos)){
                        int dx = startX - pos[0];
                        int dy = startY - pos[1];
                        int dz = startZ - pos[2];
                        int distanceSq = dx*dx + dy*dy + dz*dz;
                        //int hitScore = Math.max(100*100 - distanceSq, 0);
                        int hitScore = nearness;
                        score += hitScore*1000;
                    }
                }
            }
        }
    }

}

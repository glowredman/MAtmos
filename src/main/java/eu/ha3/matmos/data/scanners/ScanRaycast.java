package eu.ha3.matmos.data.scanners;

import java.util.Random;

import net.minecraft.block.Block;
import net.minecraft.block.BlockLeaves;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.Minecraft;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
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
    
    private int THRESHOLD_SCORE;
    
    private Vec3d[] rays;
    
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
        
        opspercall = 20;
        raysToCast = opspercall * 20;
        
        if(rays == null || rays.length != raysToCast) {
            rnd.setSeed(0);
            rays = new Vec3d[raysToCast];
            for(int i = 0; i < raysToCast; i++) {
                double vx = 0, vy = 0, vz = 0;
                // avoid normalizing a vector of 0 length (impossible), or tiny length (numerically unstable)
                while(vx * vx + vy * vy + vz * vz < 0.01) {
                    vx = 2.0 * (rnd.nextDouble() - 0.5);
                    vy = 2.0 * (rnd.nextDouble() - 0.5);
                    vz = 2.0 * (rnd.nextDouble() - 0.5);
                }
                rays[i] = new Vec3d(vx, vy, vz).normalize();
            }
        }
        
        
        finalProgress = 1;
        
        score = 0;
        THRESHOLD_SCORE = 25000;
    }
    
    private Vec3d getRay(int index) {
        return rays[index];
    }

    @Override
    protected boolean doRoutine() {
        for(int ops = 0; ops < opspercall && raysCast < raysToCast && score <= THRESHOLD_SCORE; ops++) { 
            
            castRay(getRay(raysCast));
            
            raysCast++;
        }
        if(score > THRESHOLD_SCORE || raysCast >= raysToCast) {
            progress = 1;
            
            pipeline.setValue(".is_outdoors", score > THRESHOLD_SCORE ? 1 : 0);
            pipeline.setValue(".__score", score);
        }
        
        return true;
    }
    
    private int calculateWeight(int dx, int dy, int dz, int maxRange) {
        int distanceSquared = dx * dx + dy * dy + dz * dz;
        float distanceScale = 4f; // a block this far away will have a weight of 1/2
        float weight = MathHelper.clamp((1f / distanceScale) / distanceSquared, 0f, 1f);
        return (int)(weight*1000000f);
        
        //return maxRange * maxRange - distanceSquared;
    }
    
    public static RayTraceResult rayTraceNonSolid(Vec3d start, Vec3d dir, double maxRange) {
        World w = Minecraft.getMinecraft().world;
        Vec3d end = start.add(dir.scale(maxRange));
        RayTraceResult result = w.rayTraceBlocks(start, end, true, false, true);
        
        Vec3d delta = dir.scale(0.01);
        
        while(result != null && ScanAir.isTransparentToSound(w.getBlockState(result.getBlockPos()), w, result.getBlockPos(), true)) {
            result = w.rayTraceBlocks(result.hitVec.add(delta), end, true, true, true); 
        }
        return result;
    }
    
    private void castRay(Vec3d dir) {
        int maxRange = 100;
        
        World w = Minecraft.getMinecraft().world;
        
        RayTraceResult result = rayTraceNonSolid(center, dir, maxRange);
        
        int startNearness = 60;
        if(result != null) {
            BlockPos hit = result.getBlockPos();
            
            Block[] blockBuf = new Block[1];
            int[] metaBuf = new int[1];
            int[] pos = new int[3];
            
            boolean centerSolid = false;
            
            
            
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
                    
                    IBlockState blockState = w.getBlockState(blockPos);
                    
                    int dx = startX - pos[0];
                    int dy = startY - pos[1];
                    int dz = startZ - pos[2];
                    
                    ((ScannerModule)pipeline).inputAndReturnBlockMeta(pos[0], pos[1], pos[2], calculateWeight(dx, dy, dz, maxRange),
                            blockBuf, metaBuf);
                    Block block = blockBuf[0];
                    
                    boolean solid = blockState.getCollisionBoundingBox(w, blockPos) != Block.NULL_AABB &&
                    !(block instanceof BlockLeaves);
                    
                    if(solid && offset == 0 && scanDir == 0) {
                        centerSolid = true;
                    } else if(centerSolid && scanDir != 0 && offset == 1){
                        nearness -= centerSolid ? solidPenalty : airPenalty;
                    }
                    
                    nearness -= solid ? solidPenalty : airPenalty;
                    
                    
                    
                    if(nearness > 0 && !solid && w.canBlockSeeSky(blockPos)){
                        int distanceSq = dx*dx + dy*dy + dz*dz;
                        //int hitScore = Math.max(100*100 - distanceSq, 0);
                        int hitScore = nearness;
                        score += hitScore;
                    }
                }
            }
        } else {
            if(dir.y > 0) {
                score += startNearness * 13;
            }
        }
    }

}

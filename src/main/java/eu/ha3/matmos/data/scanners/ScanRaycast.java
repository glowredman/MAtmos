package eu.ha3.matmos.data.scanners;

import java.util.HashSet;
import java.util.Random;
import java.util.Set;

import eu.ha3.matmos.util.BlockPos;
import eu.ha3.matmos.util.MAtUtil;
import eu.ha3.matmos.util.Vec3d;
import net.minecraft.block.Block;
import net.minecraft.block.BlockAir;
import net.minecraft.block.BlockLeaves;
import net.minecraft.client.Minecraft;
import net.minecraft.util.MathHelper;
import net.minecraft.util.MovingObjectPosition;
import net.minecraft.world.World;

public class ScanRaycast extends Scan {
    
    private static final Random rnd = new Random();
    
    int startX, startY, startZ;
    Vec3d center;
    int xSize, ySize, zSize;
    
    int raysCast = 0;
    int raysToCast;
    int score;
    int distanceSqSum;
    
    private int THRESHOLD_SCORE;
    
    private Vec3d[] rays;
    
    private Set<BlockPos> scanned;
    
    @Override
    void initScan(int x, int y, int z, int xsizeIn, int ysizeIn, int zsizeIn, int opspercallIn) {
        startX = x;
        startY = y;
        startZ = z;
        
        center = new Vec3d(startX + 0.5, startY + 0.5, startZ + 0.5);
        
        xSize = xsizeIn;
        ySize = ysizeIn;
        zSize = zsizeIn;
        
        raysToCast = opspercall * 20;
        raysCast = 0;
        distanceSqSum = 0;
        
        if(rays == null || rays.length != raysToCast) {
            rnd.setSeed(1);
            rays = new Vec3d[raysToCast];
            for(int i = 0; i < raysToCast; i++) {
                double vx = 0, vy = 0, vz = 0;
                // avoid normalizing a vector of 0 length (impossible), or tiny length (numerically unstable)
                double squareDist;
                while((squareDist = vx * vx + vy * vy + vz * vz) < 0.01 || squareDist > 1) {
                    vx = 2.0 * (rnd.nextDouble() - 0.5);
                    vy = 2.0 * (rnd.nextDouble() - 0.5);
                    vz = 2.0 * (rnd.nextDouble() - 0.5);
                }
                rays[i] = new Vec3d(vx, vy, vz).normalize();
            }
        }
        
        if(scanned == null) {
            scanned = new HashSet<BlockPos>(raysToCast + 1, 1);
        }
        
        finalProgress = 1;
        
        score = 0;
        THRESHOLD_SCORE = 10000;
        
        scanned.clear();
    }
    
    private Vec3d getRay(int index) {
        return rays[index];
    }

    @Override
    protected boolean doRoutine() {
        for(int ops = 0; ops < opspercall && raysCast < raysToCast; ops++) { 
            castRay(getRay(raysCast));
            
            raysCast++;
        }
        
        if(raysCast >= raysToCast) {
            progress = 1;
            
            pipeline.setValue(".is_outdoors", score > THRESHOLD_SCORE ? 1 : 0);
            pipeline.setValue(".__score", score);
            pipeline.setValue(".spaciousness", distanceSqSum);
        }
        
        return true;
    }
    
    private int calculateWeight(int dx, int dy, int dz, int maxRange) {
        int distanceSquared = dx * dx + dy * dy + dz * dz;
        float distanceScale = 1f; // a block this^2 far away will have a weight of 1/2
        float weight = MathHelper.clamp_float(1f / (1 + (distanceSquared / distanceScale)), 0f, 1f);
        return (int)(weight*1000f);
    }
    
    public static MovingObjectPosition rayTraceNonSolid(Vec3d start, Vec3d dir, double maxRange) {
        World w = Minecraft.getMinecraft().theWorld;
        Vec3d end = start.add(dir.scale(maxRange));
        // Vec3d is immutable in 1.12.2 but Vec3 is not in 1.7.10
        MovingObjectPosition result = w.rayTraceBlocks(start.clone(), end.clone(), true, false, true);
        
        Vec3d delta = dir.scale(0.01);
        
        while(result != null && ScanAir.isTransparentToSound(MAtUtil.getBlockAt(
                new BlockPos(result.hitVec)), MAtUtil.getMetaAt(new BlockPos(result.hitVec), -1), w, new BlockPos(result.hitVec), true)) {
            result = w.rayTraceBlocks(delta.add(result.hitVec), end, true, true, true);
        }
        return result;
    }
    
    private void castRay(Vec3d dir) {
        int maxRange = 100;
        
        World w = Minecraft.getMinecraft().theWorld;
        
        MovingObjectPosition result = rayTraceNonSolid(center, dir, maxRange);
        
        int startNearness = 60;
        if(result != null) {
            BlockPos hit = new BlockPos(result.blockX, result.blockY, result.blockZ);
            
            distanceSqSum += hit.distanceSq(center.xCoord, center.yCoord, center.zCoord);
            
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
                    
                    if(!scanned.contains(blockPos)) {
                        scanned.add(blockPos);
                        
                        int dx = startX - pos[0];
                        int dy = startY - pos[1];
                        int dz = startZ - pos[2];
                        
                        ((ScannerModule)pipeline).inputAndReturnBlockMeta(pos[0], pos[1], pos[2], calculateWeight(dx, dy, dz, maxRange),
                                blockBuf, metaBuf);
                        
                        Block block = blockBuf[0];
                        
                        boolean solid = block.getCollisionBoundingBoxFromPool(w, blockPos.getX(), blockPos.getY(), blockPos.getZ()) != null &&
                        !(block instanceof BlockLeaves);
                        
                        if(solid && offset == 0 && scanDir == 0) {
                            centerSolid = true;
                        } else if(centerSolid && scanDir != 0 && offset == 1){
                            nearness -= centerSolid ? solidPenalty : airPenalty;
                        }
                        
                        nearness -= solid ? solidPenalty : airPenalty;
                        
                        if(nearness > 0 && block instanceof BlockAir && MAtUtil.canSeeSky(blockPos)){
                            int hitScore = nearness;
                            score += hitScore;
                        }
                    }
                }
            }
        } else { // ray didn't hit anything
            distanceSqSum += maxRange * maxRange;
            
            if(dir.yCoord > 0) { // and it's because we hit the sky, probably
                Vec3d rayEnd = center.add(dir.scale(maxRange));
                BlockPos rayEndBlockPos = new BlockPos(MathHelper.floor_double(rayEnd.xCoord), MathHelper.floor_double(rayEnd.yCoord), MathHelper.floor_double(rayEnd.zCoord));
                
                if(MAtUtil.canSeeSky(rayEndBlockPos)) {
                    score += startNearness * 13;
                }
            }
        }
    }

}

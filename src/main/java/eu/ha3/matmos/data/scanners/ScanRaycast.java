package eu.ha3.matmos.data.scanners;

import java.util.HashSet;
import java.util.Optional;
import java.util.Random;
import java.util.Set;

import eu.ha3.matmos.util.MAtUtil;
import net.minecraft.block.Block;
import net.minecraft.block.BlockAir;
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
    int startNearness;
    int maxRange;
    
    int raysCast = 0;
    int raysToCast;
    int directScore;
    int indirectScore;
    int distanceSqSum;
    
    private int THRESHOLD_SCORE;
    private int THRESHOLD_INDIRECT_SCORE;
    
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
        startNearness = 60;
        maxRange = 100;
        
        directScore = 0;
        indirectScore = 0;
        THRESHOLD_SCORE = 10000;
        THRESHOLD_INDIRECT_SCORE = 10;
        
        scanned.clear();
    }
    
    private Vec3d getRay(int index) {
        return rays[index];
    }
    
    private Optional<Vec3d> getNormalVector(RayTraceResult result) { 
        if(result == null) {
            return Optional.empty();
        }
        
        switch(result.sideHit) {
        case DOWN:
            return Optional.of(new Vec3d(0, -1, 0));
        case UP:
            return Optional.of(new Vec3d(0, 1, 0));
        case NORTH:
            return Optional.of(new Vec3d(0, 0, 1));
        case SOUTH:
            return Optional.of(new Vec3d(0, 0, -1));
        case WEST:
            return Optional.of(new Vec3d(-1, 0, 0));
        case EAST:
            return Optional.of(new Vec3d(1, 0, 0));
        default:
            return Optional.empty();
        }
    }

    @Override
    protected boolean doRoutine() {
        for(int ops = 0; ops < opspercall && raysCast < raysToCast; ops++) { 
            castRay(getRay(raysCast));
            
            raysCast++;
        }
        
        if(raysCast >= raysToCast) {
            progress = 1;
            
            pipeline.setValue(".is_outdoors", directScore > THRESHOLD_SCORE ? 1 : 0);
            pipeline.setValue(".__score", directScore);
            pipeline.setValue(".__indirect_score", indirectScore);
            pipeline.setValue("._is_near_surface_own", indirectScore > THRESHOLD_INDIRECT_SCORE ? 1 : 0);
            pipeline.setValue(".spaciousness", distanceSqSum);
        }
        
        return true;
    }
    
    private int calculateWeight(int dx, int dy, int dz, int maxRange) {
        int distanceSquared = dx * dx + dy * dy + dz * dz;
        float distanceScale = 1f; // a block this^2 far away will have a weight of 1/2
        float weight = MathHelper.clamp(1f / (1 + (distanceSquared / distanceScale)), 0f, 1f);
        return (int)(weight*1000f);
    }
    
    public static RayTraceResult rayTraceNonSolid(Vec3d start, Vec3d end) {
        World w = Minecraft.getMinecraft().world;
        
        RayTraceResult result = w.rayTraceBlocks(start, end, true, false, true);
        
        Vec3d delta = end.subtract(start);
        double infNorm = Math.max(Math.abs(delta.x), Math.max(Math.abs(delta.y), Math.abs(delta.z)));
        delta = delta.scale(0.01 / infNorm);
        
        IBlockState bs = w.getBlockState(result.getBlockPos());
        
        while(result != null && result.typeOfHit == RayTraceResult.Type.BLOCK
                && ScanAir.isTransparentToSound(bs, w, new BlockPos(result.hitVec), true)) {
            result = w.rayTraceBlocks(delta.add(result.hitVec), end, true, true, true);
        }
        return result;
    }
    
    private boolean scanNearRayHit(RayTraceResult result, int scanDistance, boolean direct) {
        World w = Minecraft.getMinecraft().world;
        
        BlockPos hitBlock = result.getBlockPos();
        
        distanceSqSum += hitBlock.distanceSq(center.x, center.y, center.z);
        
        Block[] blockBuf = new Block[1];
        int[] metaBuf = new int[1];
        int[] pos = new int[3];
        
        boolean centerSolid = false;
        boolean foundSky = false;
        
        
        int airPenalty = 0;
        int solidPenalty = 55;
        
        for(int scanDir = 0; scanDir < 6; scanDir++) {
            int nearness = startNearness;
            for(int offset = 0; offset <= scanDistance; offset++) {
                if(offset == 0 && scanDir != 0) {
                    continue;
                }
                
                int scanAxis = scanDir >= 3 ? scanDir - 3 : scanDir; 
                
                pos[0] = hitBlock.getX();
                pos[1] = hitBlock.getY();
                pos[2] = hitBlock.getZ();
                pos[scanAxis] += offset * (scanDir >= 3 ? -1 : 1);
                
                BlockPos blockPos = new BlockPos(pos[0], pos[1], pos[2]);
                
                if(!scanned.contains(blockPos)) {
                    scanned.add(blockPos);
                    
                    int dx = startX - pos[0];
                    int dy = startY - pos[1];
                    int dz = startZ - pos[2];
                    
                    if(direct) {
                        ((ScannerModule)pipeline).inputAndReturnBlockMeta(pos[0], pos[1], pos[2], calculateWeight(dx, dy, dz, maxRange),
                                blockBuf, metaBuf);
                    } else {
                        blockBuf[0] = MAtUtil.getBlockAt(new BlockPos(pos[0], pos[1], pos[2]));
                        metaBuf[0] = MAtUtil.getMetaAt(new BlockPos(pos[0], pos[1], pos[2]), -1);
                    }
                    
                    Block block = blockBuf[0];
                    
                    IBlockState bs = w.getBlockState(blockPos);
                    
                    boolean solid = bs.getCollisionBoundingBox(w, blockPos) != Block.NULL_AABB &&
                    !(block instanceof BlockLeaves);
                    
                    if(solid && offset == 0 && scanDir == 0) {
                        centerSolid = true;
                    } else if(centerSolid && scanDir != 0 && offset == 1){
                        nearness -= centerSolid ? solidPenalty : airPenalty;
                    }
                    
                    nearness -= solid ? solidPenalty : airPenalty;
                    
                    if(nearness > 0 && block instanceof BlockAir && MAtUtil.canSeeSky(blockPos)){
                        int hitScore = nearness;
                        if(direct) {
                            directScore += hitScore;
                        } else {
                            indirectScore += 1;
                        }
                        
                        foundSky = true;
                    }
                }
            }
        }
        return foundSky;
    }
    
    private void castRay(Vec3d dir) {
        Vec3d end = center.add(dir.scale(maxRange));
        RayTraceResult result = rayTraceNonSolid(center, end);
        
        if(result != null && result.typeOfHit == RayTraceResult.Type.BLOCK) { // ray hit a solid block
            boolean foundSky = scanNearRayHit(result, 2, true);
            
            if(!foundSky) {
                Vec3d normal = getNormalVector(result).orElse(Vec3d.ZERO);
                
                if(!normal.equals(Vec3d.ZERO)) {
                    Vec3d otherSide = normal.scale(-1.1).add(result.hitVec);
                    
                    RayTraceResult continuedResult = rayTraceNonSolid(otherSide, end);
                    
                    if(continuedResult != null) {
                        scanNearRayHit(continuedResult, 1, false);
                    } else if(dir.y > 0) {
                        indirectScore += 7;
                    }
                }

            }
        } else { // ray didn't hit anything
            distanceSqSum += maxRange * maxRange;
            
            if(dir.y > 0) { // and it's because we hit the sky, probably
                Vec3d rayEnd = center.add(dir.scale(maxRange));
                BlockPos rayEndBlockPos = new BlockPos(MathHelper.floor(rayEnd.x), MathHelper.floor(rayEnd.y), MathHelper.floor(rayEnd.z));
                
                if(MAtUtil.canSeeSky(rayEndBlockPos)) {
                    directScore += startNearness * 13;
                }
            }
        }
    }

}

package eu.ha3.matmos.data.scanners;

import java.util.Random;

import net.minecraft.client.Minecraft;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.util.math.Vec3d;

public class ScanRaycast extends Scan {
    
    private static final Random rnd = new Random();
    
    int startX, startY, startZ;
    Vec3d center;
    int xSize, ySize, zSize;
    
    int raysCast = 0;
    int raysToCast;
    
    @Override
    void initScan(int x, int y, int z, int xsizeIn, int ysizeIn, int zsizeIn, int opspercallIn) {
        startX = x;
        startY = y + 1;
        startZ = z;
        
        center = new Vec3d(startX, startY, startZ);
        
        xSize = xsizeIn;
        ySize = ysizeIn;
        zSize = zsizeIn;
        
        raysCast = 0;
        raysToCast = 100;
        
        finalProgress = 1;
    }

    @Override
    protected boolean doRoutine() {
        opspercall = 20;
        raysToCast = opspercall * 20;
        for(int ops = 0; ops < opspercall && raysCast < raysToCast; ops++) {
            /*EntityPlayerSP player = Minecraft.getMinecraft().player;
            Vec3d lookVec = player.getLookVec().scale(300);
            Vec3d playerCenter = player.getPositionVector().add(0, player.getEyeHeight(), 0);
            RayTraceResult result = Minecraft.getMinecraft().world.rayTraceBlocks(playerCenter, playerCenter.add(lookVec), true, true, true);
            System.out.println(result);*/
            
            
            
            castRay(Vec3d.fromPitchYaw(rnd.nextFloat() * 360f, rnd.nextFloat() * 360f));
            raysCast++;
        }
        if(raysCast == raysToCast) {
            progress = 1;
            
            pipeline.setValue(".outsideness_score", 42);
        }
        return true;
    }
    
    private void castRay(Vec3d dir) {
        RayTraceResult result = Minecraft.getMinecraft().world.rayTraceBlocks(center, center.add(dir.scale(100)), true, false, true);
        if(result != null) {
            BlockPos hit = result.getBlockPos();
            pipeline.input(hit.getX(), hit.getY(), hit.getZ());
        }
    }

}

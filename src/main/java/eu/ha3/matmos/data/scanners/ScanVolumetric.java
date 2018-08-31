package eu.ha3.matmos.data.scanners;

import net.minecraft.client.Minecraft;

/* x-placeholder */

public class ScanVolumetric implements Progress {
    private ScanOperations pipeline;

    private int xstart;
    private int ystart;
    private int zstart;

    private int xsize;
    private int ysize;
    private int zsize;

    private int opspercall;

    private boolean isScanning;

    private int finalProgress = 1;
    private int progress = 1; // We don't want progress to be zero, to avoid divide by zero

    //

    private int xx;
    private int yy;
    private int zz;

    public ScanVolumetric() {
        pipeline = null;
        isScanning = false;
    }

    public void setPipeline(ScanOperations pipelineIn) {
        pipeline = pipelineIn;
    }

    public void startScan(int x, int y, int z, int xsizeIn, int ysizeIn, int zsizeIn, int opspercallIn) //throws MAtScannerTooLargeException
    {
        if (isScanning) {
            return;
        }

        if (pipeline == null) {
            return;
        }

        if (opspercallIn <= 0) {
            throw new IllegalArgumentException();
        }

        int worldHeight = Minecraft.getMinecraft().world.getHeight();

        if (ysizeIn > worldHeight) {
            ysizeIn = worldHeight;
        }

        xsize = xsizeIn;
        ysize = ysizeIn;
        zsize = zsizeIn;

        y = y - ysize / 2;

        if (y < 0) {
            y = 0;
        } else if (y > worldHeight - ysize) {
            y = worldHeight - ysize;
        }

        xstart = x - xsize / 2;
        ystart = y; // ((y - ysize / 2)) already done before
        zstart = z - zsize / 2;
        opspercall = opspercallIn;

        progress = 0;
        finalProgress = xsize * ysize * zsize;

        xx = 0;
        yy = 0;
        zz = 0;

        pipeline.begin();
        isScanning = true;
    }

    public boolean routine() {
        if (!isScanning) {
            return false;
        }
        long ops = 0;
        while (ops < opspercall && progress < finalProgress) {
            pipeline.input(xstart + xx, ystart + yy, zstart + zz);

            xx = (xx + 1) % xsize;
            if (xx == 0) {
                zz = (zz + 1) % zsize;
                if (zz == 0) {
                    yy = yy + 1;
                    if (yy >= ysize && progress != finalProgress - 1) {
                        System.err.println("LOGIC ERROR");
                    }
                }
            }

            ops++;
            progress++;

        }

        if (progress >= finalProgress) {
            scanDoneEvent();
        }

        return true;
    }

    public void stopScan() {
        isScanning = false;
    }

    private void scanDoneEvent() {
        if (!isScanning) {
            return;
        }

        pipeline.finish();
        stopScan();
    }

    @Override
    public int getProgress_Current() {
        return progress;
    }

    @Override
    public int getProgress_Total() {
        return finalProgress;
    }

}

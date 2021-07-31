package eu.ha3.matmos.data.scanners;

public abstract class Scan implements Progress {

    protected ScanOperations pipeline;

    protected int opspercall;

    protected boolean isScanning;

    protected int finalProgress = 1;
    protected int progress = 1; // We don't want progress to be zero, to avoid divide by zero

    public Scan() {
        this.pipeline = null;
        this.isScanning = false;
    }

    public void setPipeline(ScanOperations pipelineIn) {
        this.pipeline = pipelineIn;
    }

    /** Performs generic preparations for starting a scan **/
    protected void startScan(int x, int y, int z, int xsizeIn, int ysizeIn, int zsizeIn, int opspercallIn) // throws
                                                                                                           // MAtScannerTooLargeException
    {
        if (this.isScanning)
            return;

        if (this.pipeline == null)
            return;

        if (opspercallIn <= 0)
            throw new IllegalArgumentException();

        this.opspercall = opspercallIn;
        this.progress = 0;
        this.finalProgress = 1; // placeholder to avoid divide by zero

        initScan(x, y, z, xsizeIn, ysizeIn, zsizeIn, opspercallIn);

        this.pipeline.begin();
        this.isScanning = true;
    }

    /** Subclass-specific scanner initialization goes here. **/
    abstract void initScan(int x, int y, int z, int xsizeIn, int ysizeIn, int zsizeIn, int opspercallIn);

    /** Performs generic housekeeping for the scanning routine **/
    public boolean routine() {
        if (!this.isScanning)
            return false;

        boolean result = doRoutine();

        if (this.progress >= this.finalProgress) {
            scanDoneEvent();
        }

        return result;
    }

    /** Subclass-specific routine implementation goes here **/
    protected abstract boolean doRoutine();

    @Override
    public int getProgress_Current() {
        return this.progress;
    }

    @Override
    public int getProgress_Total() {
        return this.finalProgress;
    }

    public void stopScan() {
        this.isScanning = false;
    }

    protected void scanDoneEvent() {
        if (!this.isScanning)
            return;

        this.pipeline.finish();
        stopScan();
    }
}

package eu.ha3.matmos.data.scanners;

import java.lang.reflect.InvocationTargetException;
import java.util.HashSet;
import java.util.Set;

import org.apache.commons.lang3.tuple.Pair;

import eu.ha3.matmos.Matmos;
import eu.ha3.matmos.core.sheet.DataPackage;
import eu.ha3.matmos.data.modules.AbstractThingCountModule;
import eu.ha3.matmos.data.modules.BlockCountModule;
import eu.ha3.matmos.data.modules.ExternalStringCountModule;
import eu.ha3.matmos.data.modules.PassOnceModule;
import eu.ha3.matmos.data.modules.ThousandStringCountModule;
import eu.ha3.matmos.data.modules.VirtualCountModule;
import eu.ha3.matmos.data.modules.VirtualModuleProcessor;
import eu.ha3.matmos.util.MAtUtil;
import eu.ha3.matmos.util.math.MAtMutableBlockPos;
import net.minecraft.block.Block;
import net.minecraft.client.Minecraft;
import net.minecraft.util.math.BlockPos;

public class ScannerModule implements PassOnceModule, ScanOperations, Progress {
    public static final String THOUSAND_SUFFIX = "_p1k";

    private static final int WORLD_LOADING_DURATION = 100;

    private final String passOnceName;
    private final boolean requireThousand;
    private final int movement;
    private final int pulse;

    private final int xS;
    private final int yS;
    private final int zS;
    private final int blocksPerCall;

    private final AbstractThingCountModule base;
    private final VirtualCountModule thousand;

    private final Set<String> subModules = new HashSet<>();

    private int ticksSinceBoot;
    private boolean firstScan;
    private boolean workInProgress;

    private int dimension = Integer.MIN_VALUE;
    private int xx = Integer.MIN_VALUE;
    private int yy = Integer.MIN_VALUE;
    private int zz = Integer.MIN_VALUE;

    private final Scan scanner;

    /**
     * Movement: Requires the player to move to another block to trigger a new scan. If movement is
     * zero, no scan until the player moves. If movement is negative, always scan even if the player
     * hasn't moved.
     */
              
    private ScannerModule(Class<Scan> scannerClass, Object scannerArgument, boolean hasScannerArgument,
            DataPackage data, String passOnceName,
            String baseName, boolean requireThousand,
            int movement, int pulse, int xS, int yS, int zS, int blocksPerCall) {
        this.passOnceName = passOnceName;
        this.requireThousand = requireThousand;
        this.movement = movement;
        this.pulse = pulse;

        this.xS = xS;
        this.yS = yS;
        this.zS = zS;
        this.blocksPerCall = blocksPerCall;

        if (requireThousand) {
            String thousandName = baseName + THOUSAND_SUFFIX;
            thousand = new VirtualCountModule(data, thousandName, true);
            subModules.add(thousandName);
            data.getSheet(thousandName).setDefaultValue("0");
        } else {
            thousand = null;
        }
        
        boolean useExternalStringCountModule = false;
        
        if(useExternalStringCountModule) {
            this.base = new ExternalStringCountModule(data, baseName, true);
        } else {
            this.base = new BlockCountModule(data, baseName, true, thousand);
        }
        this.subModules.add(baseName);
        data.getSheet(baseName).setDefaultValue("0");

        
        Scan theScanner = null;
        
        try
        {
            if(hasScannerArgument) {
                theScanner = (Scan)scannerClass.getConstructor(Object.class).newInstance(scannerArgument);
            } else {
                theScanner = (Scan)scannerClass.newInstance();
            }
        } catch (IllegalArgumentException | InvocationTargetException | NoSuchMethodException
                | SecurityException | InstantiationException | IllegalAccessException e)
        {
            e.printStackTrace();
        }
        scanner = theScanner;
        scanner.setPipeline(this);

        ticksSinceBoot = 0;
        firstScan = true;
    }
    
    /*** Constructor used to pass an argument to the to-be-instantiated scanner object */
    public ScannerModule(Class scannerClass, Object scannerArgument, DataPackage data, String passOnceName,
            String baseName, boolean requireThousand,
            int movement, int pulse, int xS, int yS, int zS, int blocksPerCall) {
        this(scannerClass, scannerArgument, true, data, passOnceName,
                baseName, requireThousand,
                movement, pulse, xS, yS, zS, blocksPerCall);
    }
    
    /*** Normal constructor */
    public ScannerModule(Class scannerClass, DataPackage data, String passOnceName,
            String baseName, boolean requireThousand,
            int movement, int pulse, int xS, int yS, int zS, int blocksPerCall) {
        this(scannerClass, null, false, data, passOnceName,
                baseName, requireThousand,
                movement, pulse, xS, yS, zS, blocksPerCall);
    }

    @Override
    public String getName() {
        return passOnceName;
    }

    @Override
    public Set<String> getSubModules() {
        return subModules;
    }

    @Override
    public void process() {
        if (tryToReboot()) {
            Matmos.LOGGER.info("Detected large movement or teleportation. Rebooted module " + getName());
            return;
        }

        if (ticksSinceBoot < WORLD_LOADING_DURATION) {
            ticksSinceBoot = ticksSinceBoot + 1;
            return;
        }

        tryToBoot();

        if (workInProgress) {
            scanner.routine();
        }
        ticksSinceBoot = ticksSinceBoot + 1;
    }

    private boolean tryToReboot() {
        int x = MAtUtil.getPlayerX();
        int y = MAtUtil.clampToBounds(MAtUtil.getPlayerY());
        int z = MAtUtil.getPlayerZ();

        if (Minecraft.getMinecraft().player.dimension != dimension) {
            reboot();
            return true;
        }

        int max = Math.max(Math.abs(xx - x), Math.abs(yy - y));
        max = Math.max(max, Math.abs(zz - z));

        if (max > 128) {
            reboot();
            return true;
        }

        return false;
    }

    private void reboot() {
        scanner.stopScan();
        workInProgress = false;

        ticksSinceBoot = 0;
        firstScan = true;

        dimension = Minecraft.getMinecraft().player.dimension;
        xx = MAtUtil.getPlayerX();
        yy = MAtUtil.clampToBounds(MAtUtil.getPlayerY());
        zz = MAtUtil.getPlayerZ();
    }

    private void tryToBoot() {
        if (workInProgress) {
            return;
        }

        if (ticksSinceBoot % pulse == 0) {
            boolean go = false;

            if (firstScan) {
                firstScan = false;

                go = true;
            } else if (movement >= 0) {
                int x = MAtUtil.getPlayerX();
                int y = MAtUtil.clampToBounds(MAtUtil.getPlayerY());
                int z = MAtUtil.getPlayerZ();

                int max = Math.max(Math.abs(xx - x), Math.abs(yy - y));
                max = Math.max(max, Math.abs(zz - z));

                go = max > movement;
            } else {
                go = true;
            }

            if (go) {
                workInProgress = true;

                xx = MAtUtil.getPlayerX();
                yy = MAtUtil.clampToBounds(MAtUtil.getPlayerY());
                zz = MAtUtil.getPlayerZ();

                scanner.startScan(xx, yy, zz, xS, yS, zS, blocksPerCall);
            }
        }
    }
    
    @Override
    public void input(int x, int y, int z) {
        inputAndReturnBlockMeta(x, y, z, null, null);
    }
    
    /** Not sure if this optimization is necessary */
    public void inputAndReturnBlockMeta(int x, int y, int z, Block[] blockOut, int[] metaOut) {
        Block block = null;
        int meta = -1;
        if(base instanceof BlockCountModule || blockOut != null || metaOut != null) {
            block = MAtUtil.getBlockAt(new BlockPos(x, y, z));
            meta = MAtUtil.getMetaAt(new BlockPos(x, y, z), -1);
        }
        
        if(base instanceof BlockCountModule) {
            base.increment(Pair.of(block, meta));
        } else if(base instanceof ExternalStringCountModule) {
            String name = MAtUtil.getNameAt(new BlockPos(x, y, z), "");
            base.increment(name);
            base.increment(MAtUtil.getPowerMetaAt(new BlockPos(x, y, z), ""));
            thousand.increment(name);
        }
        
        if(blockOut != null) {
            blockOut[0] = block;
        }
        if(metaOut != null) {
            metaOut[0] = meta;
        }
    }
    
    @Override
    public void begin()
    {
    }

    @Override
    public void finish() {
        base.apply();
        workInProgress = false;
    }

    @Override
    public int getProgress_Current() {
        return scanner.getProgress_Current();
    }

    @Override
    public int getProgress_Total() {
        return scanner.getProgress_Total();
    }

    @Override
    public void setValue(String key, int value) {
        if(!key.startsWith(".")) {
            Matmos.LOGGER.error("Illegal scanner sheet key name: \"" + key + "\". Key must start with a '.' character.");
        } else {
            base.setValue(key, value);
        }
    }
}

package eu.ha3.matmos.data.modules;

import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;
import java.util.TreeSet;

import eu.ha3.easy.StopWatchStatistic;
import eu.ha3.easy.TimeStatistic;
import eu.ha3.matmos.Matmos;
import eu.ha3.matmos.core.sheet.DataPackage;
import eu.ha3.matmos.core.sheet.GenericSheet;
import eu.ha3.matmos.core.sheet.SheetDataPackage;
import eu.ha3.matmos.data.IDataCollector;
import eu.ha3.matmos.data.IDataGatherer;
import eu.ha3.matmos.data.modules.items.ModulePotionDuration;
import eu.ha3.matmos.data.modules.items.ModulePotionStrength;
import eu.ha3.matmos.data.modules.legacy.ModuleDice;
import eu.ha3.matmos.data.modules.legacy.ModuleLegacy;
import eu.ha3.matmos.data.modules.legacy.ModuleLegacyColumn;
import eu.ha3.matmos.data.modules.legacy.ModuleLegacyHitscan;
import eu.ha3.matmos.data.modules.legacy.ModuleModData;
import eu.ha3.matmos.data.modules.mount.ModuleHorse;
import eu.ha3.matmos.data.modules.mount.ModuleRiding;
import eu.ha3.matmos.data.modules.mount.ModuleRidingMotion;
import eu.ha3.matmos.data.modules.player.ModuleAction;
import eu.ha3.matmos.data.modules.player.ModuleArmour;
import eu.ha3.matmos.data.modules.player.ModuleArmourEnchantment;
import eu.ha3.matmos.data.modules.player.ModuleHeldEnchantment;
import eu.ha3.matmos.data.modules.player.ModuleHitscan;
import eu.ha3.matmos.data.modules.player.ModuleInventory;
import eu.ha3.matmos.data.modules.player.ModuleLeashing;
import eu.ha3.matmos.data.modules.player.ModuleMotion;
import eu.ha3.matmos.data.modules.player.ModulePlayerStats;
import eu.ha3.matmos.data.modules.player.ModuleStats;
import eu.ha3.matmos.data.modules.world.ModuleBiome;
import eu.ha3.matmos.data.modules.world.ModuleCollission;
import eu.ha3.matmos.data.modules.world.ModuleColumn;
import eu.ha3.matmos.data.modules.world.ModuleLighting;
import eu.ha3.matmos.data.modules.world.ModuleDebug;
import eu.ha3.matmos.data.modules.world.ModulePosition;
import eu.ha3.matmos.data.modules.world.ModuleWorld;
import eu.ha3.matmos.data.scanners.Progress;
import eu.ha3.matmos.data.scanners.ScanVolumetric;
import eu.ha3.matmos.data.scanners.ScannerModule;
import eu.ha3.matmos.data.scanners.ScanAir;
import eu.ha3.matmos.data.scanners.ScanRaycast;
import eu.ha3.matmos.util.IDontKnowHowToCode;

public class ModuleRegistry implements IDataCollector, IDataGatherer {
    private final Matmos mod;

    private DataPackage data;
    private int ticksPassed;

    private StopWatchStatistic watch = new StopWatchStatistic();

    public static final String LEGACY_PREFIX = "legacy";

    private final Map<String, Module> modules;
    private final Map<String, Set<String>> passOnceModules;
    private final Set<String> passOnceSubmodules;
    private final Set<String> requiredModules;
    private final Set<String> iteratedThroughModules;
    private final Map<String, Set<String>> moduleStack;

    private ScannerModule largeScanner, mediumScanner, raycastScanner;

    public ModuleRegistry(Matmos mAtmosHaddon) {
        mod = mAtmosHaddon;

        modules = new TreeMap<>();
        passOnceModules = new TreeMap<>();
        passOnceSubmodules = new HashSet<>();
        requiredModules = new TreeSet<>();
        iteratedThroughModules = new TreeSet<>();
        moduleStack = new TreeMap<>();
    }

    private void addModule(Module module) {
        modules.put(module.getName(), module);
        if (module instanceof PassOnceModule) {
            passOnceModules.put(module.getName(), ((PassOnceModule) module).getSubModules());
            passOnceSubmodules.addAll(((PassOnceModule) module).getSubModules());
        }
    }

    /**
     * Adds a module after setting an cycle on it, if it's an instance of a
     * ProcessorModel. Cycle: Every n ticks. Cycle = 1: Every ticks.
     *
     * @param module
     * @param cycle
     */
    private void addModule(Module module, int cycle) {
        if (module instanceof ProcessorModel) {
            ((ProcessorModel) module).setInterval(cycle - 1);
        }
        addModule(module);
    }

    public void load() {
        data = new SheetDataPackage(GenericSheet.class);

        addModule(new ModuleLegacyColumn(data), 0);
        addModule(new ModuleLegacyHitscan(data), 0);
        addModule(new ModuleDice(data), 0);
        addModule(new ModuleLegacy(data), 0);
        addModule(new ModuleModData(data, mod), 0);
        addModule(new ModuleColumn(data), 0);
        addModule(new ModuleLighting(data), 0);
        addModule(new ModulePosition(data), 0);
        addModule(new ModuleContainer(data), 0);
        addModule(new ModuleAction(data), 0);
        addModule(new ModuleArmour(data), 0);
        addModule(new ModulePlayerStats(data), 0);
        addModule(new ModuleInventory(data), 0);
        addModule(new ModuleMotion(data), 0);
        addModule(new ModuleStats(data), 0);
        addModule(new ModuleRiding(data), 0);
        addModule(new ModuleHorse(data), 0);
        addModule(new ModuleRidingMotion(data), 0);
        addModule(new ModuleTimedRandom(data), 20);
        addModule(new ModuleBiome(data, mod), 20);
        addModule(new ModuleWorld(data), 0);
        addModule(new ModuleMetaOptions(data, mod), 200);
        addModule(new ModuleServerInfo(data), 200);
        addModule(new ModuleEntity(data, this, "detect_mindist", "detect_radius", 256, 2, 5, 10, 20, 50));

        for (int i = 0; i < 4; i++) {
            addModule(new ModuleArmourEnchantment(data, i));
        }

        addModule(new ModuleHeldEnchantment(data), 0);
        addModule(new ModuleHitscan(data), 0);
        addModule(new ModuleLeashing(data), 0);
        addModule(new ModulePotionDuration(data), 0);
        addModule(new ModulePotionStrength(data), 0);
        addModule(new ModuleCollission(data), 0);

        // this.frequent.add(new MAtProcessorEntityDetector(
        // this.mod, this.data, "DetectMinDist", "Detect", "_Deltas", ENTITYIDS_MAX, 2,
        // 5, 10, 20, 50));
        // 16 * 8 * 16
        largeScanner = new ScannerModule(ScanVolumetric.class, this.data, "_POM__scan_large", "scan_large", Arrays
                .asList(ScannerModule.Submodule.THOUSAND, ScannerModule.Submodule.ABOVE, ScannerModule.Submodule.BELOW),
                8, 10, 20 /* 256 */, 64, 32, 64, 16 * 8 * 16/* 64 * 64 * 2 */);
        addModule(largeScanner);

        addModule(new ScannerModule(ScanRaycast.class, this.data, "_POM__scan_raycast", "scan_raycast", Arrays
                .asList(ScannerModule.Submodule.WEIGHTED, ScannerModule.Submodule.ABOVE, ScannerModule.Submodule.BELOW),
                -1, -1, -1, 100, 100, 100, 10));
        // scan_raycast has to be added BEFORE scan_air, because scan_air depends on it

        this.mediumScanner = new ScannerModule(ScanAir.class, data.getSheet("scan_raycast"), this.data,
                "_POM__scan_air", "scan_air", Arrays.asList(ScannerModule.Submodule.THOUSAND), -1, -1, 20, 31, 31, 31,
                31 * 31 * 4);
        addModule(this.mediumScanner);

        // 16 * 4 * 16
        addModule(new ScannerModule(ScanVolumetric.class, this.data, "_POM__scan_small", "scan_small",
                Arrays.asList(ScannerModule.Submodule.THOUSAND), -1, -1, 2 /* 64 */, 16, 8, 16, 16 * 4 * 16));
        // Each ticks, check half of the small scan

        addModule(new ModuleDebug(data));

        Matmos.DEVLOGGER.info("Modules initialized: " + Arrays.toString(new TreeSet<>(modules.keySet()).toArray()));
    }

    public DataPackage getData() {
        return data;
    }

    @Override
    public void process() {
        TimeStatistic stat = new TimeStatistic();
        for (String requiredModule : iteratedThroughModules) {
            watch.reset();
            try {
                modules.get(requiredModule).process();
            } catch (Exception e) {
                IDontKnowHowToCode.whoops__printExceptionToChat(mod.getChatter(), e, requiredModule.hashCode());
            }
            watch.stop();
            if (watch.getMilliseconds() > 50 && mod.isDebugMode()) {
                Matmos.LOGGER.warn("Module " + requiredModule + " took " + stat.getMilliseconds() + "ms!!!");
            }
        }
        ticksPassed = ticksPassed + 1;
    }

    @Override
    public boolean requires(String moduleName) {
        return requiredModules.contains(moduleName);
    }

    @Override
    public void addModuleStack(String name, Set<String> requiredModules) {
        // Recompact required modules to piece deltas.
        Set<String> deltaModules = new HashSet<>();
        Set<String> actualModules = new HashSet<>();
        for (String module : requiredModules) {
            if (module.endsWith(ModuleProcessor.DELTA_SUFFIX)) {
                deltaModules.add(module);
                actualModules.add(module.substring(0, module.length() - ModuleProcessor.DELTA_SUFFIX.length()));
            }
        }
        requiredModules.removeAll(deltaModules);
        requiredModules.addAll(actualModules);

        // Find missing modules. We don't want to iterate and check through invalid
        // modules.
        Set<String> missingModules = new HashSet<>();
        for (String module : requiredModules) {
            if (!modules.containsKey(module) && !passOnceSubmodules.contains(module)) {
                if(!isDynamicModule(module)) {
                    Matmos.LOGGER.error("Stack " + name + " requires missing module " + module);
                }
                missingModules.add(module);
            }
        }

        for (String missingModule : missingModules) {
            requiredModules.remove(missingModule);
        }

        moduleStack.put(name, requiredModules);

        recomputeModuleStack();
    }
    
    private static boolean isDynamicModule(String module) {
        return module.equals("_DYNAMIC");
    }

    @Override
    public void removeModuleStack(String name) {
        moduleStack.remove(name);
        recomputeModuleStack();
    }

    public void forceRecomputeModuleStack_debugModeChanged() {
        recomputeModuleStack();
    }

    private void recomputeModuleStack() {
        if (mod.isDebugMode()) {
            requiredModules.clear();
            iteratedThroughModules.clear();

            requiredModules.addAll(modules.keySet());
            requiredModules.removeAll(passOnceModules.keySet());
            requiredModules.addAll(passOnceSubmodules);

            iteratedThroughModules.addAll(modules.keySet());

            return;
        }

        requiredModules.clear();
        iteratedThroughModules.clear();
        for (Set<String> stack : moduleStack.values()) {
            requiredModules.addAll(stack);
            iteratedThroughModules.addAll(stack);
        }

        for (Map.Entry<String, Set<String>> submodules : passOnceModules.entrySet()) {
            // if the submodules have something in common with the required modules
            if (!Collections.disjoint(submodules.getValue(), iteratedThroughModules)) {
                iteratedThroughModules.removeAll(submodules.getValue());
                iteratedThroughModules.add(submodules.getKey());
            }
        }
    }

    public Progress getLargeScanProgress() {
        return largeScanner;
    }
}

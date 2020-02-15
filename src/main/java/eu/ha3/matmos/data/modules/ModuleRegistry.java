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
import eu.ha3.matmos.data.modules.legacy.ModuleConfigVars;
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
import net.minecraft.client.Minecraft;
import net.minecraft.inventory.EntityEquipmentSlot;

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
            passOnceModules.put(module.getName(), ((PassOnceModule)module).getSubModules());
            passOnceSubmodules.addAll(((PassOnceModule)module).getSubModules());
        }
    }

    /**
     * Adds a module after setting an cycle on it, if it's an instance of a ProcessorModel. Cycle: Every
     * n ticks. Cycle = 1: Every ticks.
     *
     * @param module
     * @param cycle
     */
    private void addModule(Module module, int cycle) {
        if (module instanceof ProcessorModel) {
            ((ProcessorModel)module).setInterval(cycle - 1);
        }
        addModule(module);
    }

    public void load() {
        data = new SheetDataPackage(GenericSheet.class);

        addModule(new ModuleLegacyColumn(data));
        addModule(new ModuleLegacyHitscan(data));
        addModule(new ModuleDice(data));
        addModule(new ModuleLegacy(data));
        addModule(new ModuleModData(data, mod));
        addModule(new ModuleColumn(data));
        addModule(new ModuleLighting(data));
        addModule(new ModulePosition(data));
        addModule(new ModuleContainer(data));
        addModule(new ModuleAction(data));
        addModule(new ModuleArmour(data));
        addModule(new ModulePlayerStats(data));
        addModule(new ModuleInventory(data));
        addModule(new ModuleMotion(data));
        addModule(new ModuleStats(data));
        addModule(new ModuleRiding(data));
        addModule(new ModuleHorse(data));
        addModule(new ModuleRidingMotion(data));
        addModule(new ModuleTimedRandom(data), 20);
        addModule(new ModuleBiome(data, mod), 20);
        addModule(new ModuleWorld(data));
        addModule(new ModuleConfigVars(data, mod), 10000);
        addModule(new ModuleMetaOptions(data, mod), 200);
        addModule(new ModuleServerInfo(data), 200);
        addModule(new ModuleEntity(data, this, "detect_mindist", "detect_radius", 256, 2, 5, 10, 20, 50));

        for (int i = 0; i < 4; i++) {
            addModule(new ModuleArmourEnchantment(data, i));
        }

        addModule(new ModuleHeldEnchantment(data));
        addModule(new ModuleHitscan(data));
        addModule(new ModuleLeashing(data));
        addModule(new ModulePotionDuration(data));
        addModule(new ModulePotionStrength(data));
        addModule(new ModuleCollission(data));

        //this.frequent.add(new MAtProcessorEntityDetector(
        //	this.mod, this.data, "DetectMinDist", "Detect", "_Deltas", ENTITYIDS_MAX, 2, 5, 10, 20, 50));
        // 16 * 8 * 16
        largeScanner = new ScannerModule(
                ScanVolumetric.class, this.data, "_POM__scan_large", "scan_large", true, false, 8, 20 /*256*/, 64, 32, 64, 16 * 8 * 16/*64 * 64 * 2*/);
        addModule(largeScanner);
        
        this.mediumScanner = new ScannerModule(
                ScanAir.class, this.data, "_POM__scan_medium", "scan_medium", true, false, -1, 20, 31, 31, 31, 31*31*4);
        addModule(this.mediumScanner);
        
        // 16 * 4 * 16
        addModule(new ScannerModule(
                ScanVolumetric.class, this.data, "_POM__scan_small", "scan_small", true, false, -1, 2 /*64*/, 16, 8, 16, 16 * 4 * 16));
        // Each ticks, check half of the small scan

        addModule(new ScannerModule(
                ScanRaycast.class, this.data, "_POM__scan_raycast", "scan_raycast", false, true, -1, 20, 100, 100, 100, 10));
        
        
        addModule(new ModuleDebug(data));
        
        Matmos.LOGGER.info("Modules initialized: " + Arrays.toString(new TreeSet<>(modules.keySet()).toArray()));
    }

    public DataPackage getData() {
        return data;
    }

    @Override
    public void process() {
        Minecraft.getMinecraft().profiler.startSection("dataGatherer");
        TimeStatistic stat = new TimeStatistic();
        for (String requiredModule : iteratedThroughModules) {
            watch.reset();
            try {
                Minecraft.getMinecraft().profiler.startSection(requiredModule + "_process");
                modules.get(requiredModule).process();
                Minecraft.getMinecraft().profiler.endSection();
            } catch (Exception e) {
                e.printStackTrace();
                IDontKnowHowToCode.whoops__printExceptionToChat(mod.getChatter(), e, requiredModule.hashCode());
            }
            watch.stop();
            if (watch.getMilliseconds() > 50 && mod.isDebugMode()) {
                Matmos.LOGGER.warn("Module " + requiredModule + " took " + stat.getMilliseconds() + "ms!!!");
            }
        }
        ticksPassed = ticksPassed + 1;
        Minecraft.getMinecraft().profiler.endSection();
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

        // Find missing modules. We don't want to iterate and check through invalid modules.
        Set<String> missingModules = new HashSet<>();
        for (String module : requiredModules) {
            if (!modules.containsKey(module) && !passOnceSubmodules.contains(module)) {
                Matmos.LOGGER.error("Stack " + name + " requires missing module " + module);
                missingModules.add(module);
            }
        }

        for (String missingModule : missingModules) {
            requiredModules.remove(missingModule);
        }

        moduleStack.put(name, requiredModules);

        recomputeModuleStack();
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

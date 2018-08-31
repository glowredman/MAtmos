package eu.ha3.matmos.data.modules;

import eu.ha3.easy.StopWatchStatistic;
import eu.ha3.easy.TimeStatistic;
import eu.ha3.matmos.MAtLog;
import eu.ha3.matmos.MAtMod;
import eu.ha3.matmos.core.sheet.DataPackage;
import eu.ha3.matmos.core.sheet.GenericSheet;
import eu.ha3.matmos.core.sheet.SheetDataPackage;
import eu.ha3.matmos.data.Collector;
import eu.ha3.matmos.data.Processor;
import eu.ha3.matmos.data.modules.*;
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
import eu.ha3.matmos.data.modules.world.ModulePosition;
import eu.ha3.matmos.data.modules.world.ModuleWorld;
import eu.ha3.matmos.data.scanners.Progress;
import eu.ha3.matmos.data.scanners.ScannerModule;
import eu.ha3.matmos.util.IDontKnowHowToCode;
import net.minecraft.inventory.EntityEquipmentSlot;

import java.util.*;

public class ModuleRegistry implements Collector, Processor {
    private final MAtMod mod;

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

    private ScannerModule largeScanner;

    public ModuleRegistry(MAtMod mAtmosHaddon) {
        this.mod = mAtmosHaddon;

        this.modules = new TreeMap<String, Module>();
        this.passOnceModules = new TreeMap<String, Set<String>>();
        this.passOnceSubmodules = new HashSet<String>();
        this.requiredModules = new TreeSet<String>();
        this.iteratedThroughModules = new TreeSet<String>();
        this.moduleStack = new TreeMap<String, Set<String>>();
    }

    private void addModule(Module module) {
        this.modules.put(module.getModuleName(), module);
        if (module instanceof PassOnceModule) {
            this.passOnceModules.put(module.getModuleName(), ((PassOnceModule)module).getSubModules());
            this.passOnceSubmodules.addAll(((PassOnceModule)module).getSubModules());
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
        this.data = new SheetDataPackage(GenericSheet.class);

        addModule(new ModuleLegacyColumn(this.data));
        addModule(new ModuleLegacyHitscan(this.data));
        addModule(new ModuleDice(this.data));
        addModule(new ModuleLegacy(this.data));
        addModule(new ModuleModData(this.data, this.mod));
        addModule(new ModuleColumn(this.data));
        addModule(new ModuleLighting(this.data));
        addModule(new ModulePosition(this.data));
        addModule(new ModuleContainer(this.data));
        addModule(new ModuleAction(this.data));
        addModule(new ModuleArmour(this.data));
        addModule(new ModulePlayerStats(this.data, this.mod.util()));
        addModule(new ModuleInventory(this.data));
        addModule(new ModuleMotion(this.data));
        addModule(new ModuleStats(this.data));
        addModule(new ModuleRiding(this.data));
        addModule(new ModuleHorse(this.data));
        addModule(new ModuleRidingMotion(this.data));
        addModule(new ModuleTimedRandom(this.data), 20);
        addModule(new ModuleBiome(this.data, this.mod), 20);
        addModule(new ModuleWorld(this.data));
        addModule(new ModuleConfigVars(this.data, this.mod), 10000);
        addModule(new ModuleMetaOptions(this.data, this.mod), 200);
        addModule(new ModuleServerInfo(this.data), 200);
        addModule(new ModuleEntity(this.data, this, "detect_mindist", "detect_radius", 256, 2, 5, 10, 20, 50));

        for (EntityEquipmentSlot i : EntityEquipmentSlot.values()) {
            addModule(new ModuleArmourEnchantment(this.data, i));
        }

        addModule(new ModuleHeldEnchantment(this.data));
        addModule(new ModuleHitscan(this.data));
        addModule(new ModuleLeashing(this.data));
        addModule(new ModulePotionDuration(this.data));
        addModule(new ModulePotionStrength(this.data));
        addModule(new ModuleCollission(this.data));

        //this.frequent.add(new MAtProcessorEntityDetector(
        //	this.mod, this.data, "DetectMinDist", "Detect", "_Deltas", ENTITYIDS_MAX, 2, 5, 10, 20, 50));
        // 16 * 8 * 16
        this.largeScanner = new ScannerModule(
                this.data, "_POM__scan_large", "scan_large", true, 8, 20 /*256*/, 64, 32, 64, 1024/*64 * 64 * 2*/);
        addModule(this.largeScanner);
        // 16 * 4 * 16
        addModule(new ScannerModule(
                this.data, "_POM__scan_small", "scan_small", true, -1, 2 /*64*/, 16, 8, 16, 512));
        // Each ticks, check half of the small scan

        MAtLog.info("Modules initialized: " + Arrays.toString(new TreeSet<String>(this.modules.keySet()).toArray()));
    }

    public DataPackage getData() {
        return this.data;
    }

    @Override
    public void process() {
        TimeStatistic stat = new TimeStatistic();
        for (String requiredModule : this.iteratedThroughModules) {
            this.watch.reset();
            try {
                this.modules.get(requiredModule).process();
            } catch (Exception e) {
                e.printStackTrace();
                IDontKnowHowToCode.whoops__printExceptionToChat(this.mod.getChatter(), e, requiredModule.hashCode());
            }
            this.watch.stop();
            if (this.watch.getMilliseconds() > 50 && this.mod.isDebugMode()) {
                MAtLog.warning("WARNING: Module " + requiredModule + " took " + stat.getMilliseconds() + "ms!!!");
            }
        }
        this.ticksPassed = this.ticksPassed + 1;
    }

    @Override
    public boolean requires(String moduleName) {
        return this.requiredModules.contains(moduleName);
    }

    @Override
    public void addModuleStack(String name, Set<String> requiredModules) {
        // Recompact required modules to piece deltas.
        Set<String> deltaModules = new HashSet<String>();
        Set<String> actualModules = new HashSet<String>();
        for (String module : requiredModules) {
            if (module.endsWith(ModuleProcessor.DELTA_SUFFIX)) {
                deltaModules.add(module);
                actualModules.add(module.substring(0, module.length() - ModuleProcessor.DELTA_SUFFIX.length()));
            }
        }
        requiredModules.removeAll(deltaModules);
        requiredModules.addAll(actualModules);

        // Find missing modules. We don't want to iterate and check through invalid modules.
        Set<String> missingModules = new HashSet<String>();
        for (String module : requiredModules) {
            if (!this.modules.containsKey(module) && !this.passOnceSubmodules.contains(module)) {
                MAtLog.severe("Stack " + name + " requires missing module " + module);
                missingModules.add(module);
            }
        }

        for (String missingModule : missingModules) {
            requiredModules.remove(missingModule);
        }

        this.moduleStack.put(name, requiredModules);

        recomputeModuleStack();
    }

    @Override
    public void removeModuleStack(String name) {
        this.moduleStack.remove(name);
        recomputeModuleStack();
    }

    public void forceRecomputeModuleStack_debugModeChanged() {
        recomputeModuleStack();
    }

    private void recomputeModuleStack() {
        if (this.mod.isDebugMode()) {
            this.requiredModules.clear();
            this.iteratedThroughModules.clear();

            this.requiredModules.addAll(this.modules.keySet());
            this.requiredModules.removeAll(this.passOnceModules.keySet());
            this.requiredModules.addAll(this.passOnceSubmodules);

            this.iteratedThroughModules.addAll(this.modules.keySet());

            return;
        }

        this.requiredModules.clear();
        this.iteratedThroughModules.clear();
        for (Set<String> stack : this.moduleStack.values()) {
            this.requiredModules.addAll(stack);
            this.iteratedThroughModules.addAll(stack);
        }

        for (Map.Entry<String, Set<String>> submodules : this.passOnceModules.entrySet()) {
            // if the submodules have something in common with the required modules
            if (!Collections.disjoint(submodules.getValue(), this.iteratedThroughModules)) {
                this.iteratedThroughModules.removeAll(submodules.getValue());
                this.iteratedThroughModules.add(submodules.getKey());
            }
        }
    }

    public Progress getLargeScanProgress() {
        return this.largeScanner;
    }
}

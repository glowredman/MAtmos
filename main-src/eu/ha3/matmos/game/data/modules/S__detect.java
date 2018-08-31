package eu.ha3.matmos.game.data.modules;

import eu.ha3.matmos.engine.core.interfaces.Data;
import eu.ha3.matmos.game.data.abstractions.Collector;
import eu.ha3.matmos.game.data.abstractions.Processor;
import eu.ha3.matmos.game.data.abstractions.module.ModuleProcessor;
import eu.ha3.matmos.game.data.abstractions.module.PassOnceModule;
import eu.ha3.matmos.game.system.IDontKnowHowToCode;
import net.minecraft.client.Minecraft;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityList;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.AxisAlignedBB;

import java.util.*;

/* x-placeholder */

public class S__detect implements Processor, PassOnceModule {
    private final Set<String> submodules;
    private final Collector collector;

    private AxisAlignedBB bbox;

    private ModuleProcessor mindistModel;
    private Map<String, Double> minimumDistanceReports;

    private ModuleProcessor[] radiusSheets;
    private int[] radiusValuesSorted;
    private Map<String, Integer>[] entityCount;

    private int maxel;

    private boolean isRequired;

    @SuppressWarnings("unchecked")
    public S__detect(Data dataIn, Collector collector, String minDistModule, String radiModulePrefix, int max, int... radiis) {
        this.collector = collector;
        this.submodules = new LinkedHashSet<String>();

        this.mindistModel = new ModuleProcessor(dataIn, minDistModule) {
            @Override
            protected void doProcess() {
            }
        };
        dataIn.getSheet(minDistModule).setDefaultValue("0");
        this.submodules.add(minDistModule);
        this.minimumDistanceReports = new HashMap<String, Double>();

        this.radiusSheets = new ModuleProcessor[radiis.length];
        this.entityCount = (Map<String, Integer>[])new Map<?, ?>[radiis.length];

        this.radiusValuesSorted = Arrays.copyOf(radiis, radiis.length);
        Arrays.sort(this.radiusValuesSorted);
        this.maxel = this.radiusValuesSorted[this.radiusValuesSorted.length - 1] + 10;

        for (int i = 0; i < this.radiusValuesSorted.length; i++) {
            int radiNum = this.radiusValuesSorted[i];
            this.radiusSheets[i] = new ModuleProcessor(dataIn, radiModulePrefix + radiNum) {
                @Override
                protected void doProcess() {
                }
            };
            dataIn.getSheet(radiModulePrefix + radiNum).setDefaultValue(Integer.toString(Integer.MAX_VALUE));
            this.submodules.add(radiModulePrefix + radiNum);
            this.entityCount[i] = new HashMap<String, Integer>();
        }

        this.bbox = new AxisAlignedBB(0, 0, 0, 0, 0, 0);
    }

    private void refresh() {
        this.isRequired = false;
        this.isRequired = this.collector.requires(this.mindistModel.getModuleName());

        for (ModuleProcessor processor : this.radiusSheets) {
            this.isRequired = this.isRequired || this.collector.requires(processor.getModuleName());
        }

        for (Map<String, Integer> mappy : this.entityCount) {
            mappy.clear();
        }

        // Reset old things
        for (String type : this.minimumDistanceReports.keySet()) {
            for (int i = 0; i < this.radiusValuesSorted.length; i++) {
                this.radiusSheets[i].setValue(type, 0);
            }

            this.mindistModel.setValue(type, Integer.MAX_VALUE);
        }
        this.minimumDistanceReports.clear();
    }

    @Override
    public void process() {
        refresh();

        if (!this.isRequired) {
            IDontKnowHowToCode.warnOnce("EntityDetector is running but not required. Logic error?");
            return;
        }

        Minecraft mc = Minecraft.getMinecraft();

        double x = mc.player.posX;
        double y = mc.player.posY;
        double z = mc.player.posZ;

        // dag edit bbox.setBounds(..) -> bbox = AxisAlignedBB.fromBounds(..) ?
        this.bbox = new AxisAlignedBB(x - this.maxel, y - this.maxel, z - this.maxel, x + this.maxel, y + this.maxel, z + this.maxel);

        List<Entity> entityList = mc.world.getEntitiesWithinAABB(Entity.class, this.bbox);

        for (Entity e : entityList) {
            if (e != null && e != mc.player) {
                double dx = e.posX - x;
                double dy = e.posY - y;
                double dz = e.posZ - z;

                double distance = Math.sqrt(dx * dx + dy * dy + dz * dz);

                if (e instanceof EntityPlayer) {
                    reportDistance("minecraft:player", distance);
                } else {
                    ResourceLocation eID = EntityList.getKey(e);

                    if (eID != null) {
                        reportDistance(eID.toString(), distance);
                    }
                }

                int i = 0;
                boolean reported = false;
                while (i < this.radiusValuesSorted.length && !reported) {
                    if (distance <= this.radiusValuesSorted[i]) {
                        // If something is within 1 meter, it certainly also is within 5 meters:
                        // expand now and exit the loop.
                        if (!(e instanceof EntityPlayer)) {
                            ResourceLocation eID = EntityList.getKey(e);

                            if (eID != null) {
                                for (int above = i; above < radiusValuesSorted.length; above++) {
                                    addToEntityCount(above, eID.toString(), 1);
                                }
                            }
                        }
                        reported = true;
                    } else {
                        i++;
                    }
                }
            }
        }

        for (int i = 0; i < this.radiusValuesSorted.length; i++) {
            if (this.collector.requires(this.radiusSheets[i].getModuleName())) {
                for (String entityID : this.entityCount[i].keySet()) {
                    this.radiusSheets[i].setValue(entityID, this.entityCount[i].get(entityID));
                }
            }
        }

        if (this.collector.requires(this.mindistModel.getModuleName())) {
            for (String entityID : this.minimumDistanceReports.keySet()) {
                this.mindistModel.setValue(entityID, (int)Math.floor(this.minimumDistanceReports.get(entityID) * 1000));
            }
        }

        // Apply the virtual sheets
        if (this.collector.requires(this.mindistModel.getModuleName())) {
            this.mindistModel.process();
        }

        for (int i = 0; i < this.radiusValuesSorted.length; i++) {
            if (this.collector.requires(this.radiusSheets[i].getModuleName())) {
                this.radiusSheets[i].process();
            }
        }
    }

    protected void addToEntityCount(int radiIndex, String entityID, int count) {
        if (this.entityCount[radiIndex].containsKey(entityID)) {
            this.entityCount[radiIndex].put(entityID, this.entityCount[radiIndex].get(entityID) + count);
        } else {
            this.entityCount[radiIndex].put(entityID, count);
        }
    }

    protected void reportDistance(String type, double distance) {
        if (!this.minimumDistanceReports.containsKey(type) || this.minimumDistanceReports.get(type) > distance) {
            this.minimumDistanceReports.put(type, distance);
        }
    }

    @Override
    public String getModuleName() {
        return "_POM__entity_detector";
    }

    @Override
    public Set<String> getSubModules() {
        return this.submodules;
    }

}

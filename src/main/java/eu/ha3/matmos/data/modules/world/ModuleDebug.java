package eu.ha3.matmos.data.modules.world;

import eu.ha3.matmos.core.sheet.DataPackage;
import eu.ha3.matmos.data.modules.Module;
import eu.ha3.matmos.data.modules.ModuleProcessor;

/***
 * Use this to display expressions you find helpful in debugging in the OSD.
 * You can think of it as a MAtmos's version of the F3 debug screen.
 * 
 * @author makamys
 *
 */

public class ModuleDebug extends ModuleProcessor implements Module {
    
    DataPackage data;
    
    public ModuleDebug(DataPackage data) {
        super(data, "__DEBUG");
        this.data = data;
    }

    @Override
    protected void doProcess() {
        setValue("water", data.getSheet("scan_raycast").get("minecraft:water"));
        setValue("water_w", data.getSheet("scan_raycast_w").get("minecraft:water"));
    }
}

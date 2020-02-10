package eu.ha3.matmos.data.modules.world;

import eu.ha3.matmos.core.sheet.DataPackage;
import eu.ha3.matmos.core.sheet.Sheet;
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
    Sheet scanMedium, scanRaycast;
    
    public ModuleDebug(DataPackage data) {
        super(data, "__DEBUG");
        scanMedium = data.getSheet("scan_medium");
        scanRaycast = data.getSheet("scan_raycast");
    }

    @Override
    protected void doProcess() {
        setValue("raycast_medium", scanMedium.get(".outdoorness_score"));
        setValue("raycast_oudoorness", scanRaycast.get(".outdoorness_score"));
    }
}

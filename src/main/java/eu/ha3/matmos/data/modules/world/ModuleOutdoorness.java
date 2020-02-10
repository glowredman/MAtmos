package eu.ha3.matmos.data.modules.world;

import eu.ha3.matmos.core.sheet.DataPackage;
import eu.ha3.matmos.core.sheet.Sheet;
import eu.ha3.matmos.data.modules.Module;
import eu.ha3.matmos.data.modules.ModuleProcessor;

public class ModuleOutdoorness extends ModuleProcessor implements Module {
    Sheet scanMedium;
    
    public ModuleOutdoorness(DataPackage data) {
        super(data, "w_outdoorness");
        scanMedium = data.getSheet("scan_medium");
    }

    @Override
    protected void doProcess() {
        setValue("outdoorness", getOutdoorness());
    }
    
    /***
     * 
     * @return a score between 0 and 1000 indicating how well the player can hear outdoor ambience
     */
    private int getOutdoorness() {
        return Integer.parseInt(scanMedium.get(".outdoorness_score"));
    }
}

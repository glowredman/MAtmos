package eu.ha3.matmos.data.modules.world;

import eu.ha3.matmos.core.sheet.DataPackage;
import eu.ha3.matmos.data.modules.Module;
import eu.ha3.matmos.data.modules.ModuleProcessor;
import eu.ha3.matmos.data.scanners.Scan;
import eu.ha3.matmos.data.scanners.ScanAir;
import eu.ha3.matmos.util.MAtUtil;
import net.minecraft.client.Minecraft;
import net.minecraft.world.World;

public class ModuleOutdoorness extends ModuleProcessor implements Module {
    private ScanAir scanner;
    
    public ModuleOutdoorness(DataPackage data) {
        super(data, "w_outdoorness");
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
        World w = Minecraft.getMinecraft().world;
        
        if(scanner == null) {
            // Temporary implementation for testing
            return w.canSeeSky(MAtUtil.getPlayerPos()) ? 1000 : 0;
        } else {
            return scanner.getLastResult();
        }
    }
    
    public void setScanner(ScanAir scanner) {
        this.scanner = scanner;
    }
}

package eu.ha3.matmos.data.modules.legacy;

import java.util.Random;

import eu.ha3.matmos.core.sheet.DataPackage;
import eu.ha3.matmos.data.modules.Module;
import eu.ha3.matmos.data.modules.ModuleProcessor;

public class ModuleDice extends ModuleProcessor implements Module {

    private final char[] sides = "abcdef".toCharArray();

    private final Random random = new Random();

    public ModuleDice(DataPackage data) {
        super(data, "legacy_random");
    }

    @Override
    protected void doProcess() {
        for (char side : sides) {
            setValue("dice_" + side, 1 + random.nextInt(100));
        }
    }
}

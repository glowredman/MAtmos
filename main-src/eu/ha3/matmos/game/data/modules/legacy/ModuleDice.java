package eu.ha3.matmos.game.data.modules.legacy;

import eu.ha3.matmos.engine.core.interfaces.Data;
import eu.ha3.matmos.game.data.modules.Module;
import eu.ha3.matmos.game.data.modules.ModuleProcessor;

import java.util.Random;

public class ModuleDice extends ModuleProcessor implements Module {
    private final Random random = new Random();

    public ModuleDice(Data data) {
        super(data, "legacy_random");
    }

    @Override
    protected void doProcess() {
        setValue("dice_a", 1 + this.random.nextInt(100));
        setValue("dice_b", 1 + this.random.nextInt(100));
        setValue("dice_c", 1 + this.random.nextInt(100));
        setValue("dice_d", 1 + this.random.nextInt(100));
        setValue("dice_e", 1 + this.random.nextInt(100));
        setValue("dice_f", 1 + this.random.nextInt(100));
    }
}

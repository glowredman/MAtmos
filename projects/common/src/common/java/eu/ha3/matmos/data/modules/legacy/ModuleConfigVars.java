package eu.ha3.matmos.data.modules.legacy;

import java.io.File;
import java.io.IOException;

import eu.ha3.matmos.ConfigManager;
import eu.ha3.matmos.Matmos;
import eu.ha3.matmos.core.sheet.DataPackage;
import eu.ha3.matmos.data.modules.Module;
import eu.ha3.matmos.data.modules.ModuleProcessor;
import eu.ha3.matmos.util.IDontKnowHowToCode;
import eu.ha3.util.property.simple.ConfigProperty;

public class ModuleConfigVars extends ModuleProcessor implements Module {
    private final Matmos mod;

    private File defaultsConfig;
    private File userConfig;
    private ConfigProperty config;

    public ModuleConfigVars(DataPackage data, Matmos mod) {
        super(data, "legacy_configvars", true);
        this.mod = mod;

        defaultsConfig = new File(ConfigManager.getConfigFolder(), "dataconfigvars_defaults.cfg");
        ConfigManager.getDefaultConfigHelper().createDefaultConfigFileIfMissing(defaultsConfig, false);
        userConfig = new File(ConfigManager.getConfigFolder(), "dataconfigvars.cfg");

        config = new ConfigProperty();
        config.setSource(defaultsConfig.getAbsolutePath());
        config.load();

        config.setSource(userConfig.getAbsolutePath());
        if (!userConfig.exists()) {
            try {
                userConfig.createNewFile();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    @Override
    protected void doProcess() {
        for (String key : config.getAllProperties().keySet()) {
            try {
                setValue(key, config.getInteger(key));
            } catch (Exception e) {
                IDontKnowHowToCode.whoops__printExceptionToChat(mod.getChatter(), e, this);
            }
        }
    }
}

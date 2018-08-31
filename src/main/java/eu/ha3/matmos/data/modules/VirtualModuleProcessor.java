package eu.ha3.matmos.data.modules;

import eu.ha3.matmos.core.sheet.DataPackage;

/*
 * --filenotes-placeholder
 */

/**
 * Implements doProcess with nothing inside it. This class is used for placeholder modules where the
 * logic of updating the sheet is outsourced to another logic. One should call the set...() methods
 * of this class to prepare the new values, and calling process() will apply the virtual sheets that
 * this module processor contains.
 *
 * @author Hurry
 */
public class VirtualModuleProcessor extends ModuleProcessor {
    public VirtualModuleProcessor(DataPackage data, String name) {
        super(data, name);
    }

    public VirtualModuleProcessor(DataPackage data, String name, boolean doNotUseDelta) {
        super(data, name, doNotUseDelta);
    }

    @Override
    protected void doProcess() {
    }
}

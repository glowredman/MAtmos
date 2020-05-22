package eu.ha3.matmos.data.modules;

import eu.ha3.matmos.core.Named;
import eu.ha3.matmos.data.IDataGatherer;

public interface Module extends IDataGatherer, Named {
    int LEGACY_NO_ITEM = -1;
    int LEGACY_NO_BLOCK_IN_THIS_CONTEXT = 0;
    int LEGACY_NO_BLOCK_OUT_OF_BOUNDS = 0;

    /**
     * This is a magic value to designate a block that is outside the world's actual
     * bounds
     */
    String NO_BLOCK_OUT_OF_BOUNDS = "";

    /**
     * This is a magic value to designate a failed detection of block that is
     * unrelated to the world height.
     */
    String NO_BLOCK_IN_THIS_CONTEXT = "";

    /**
     * This is a magic value to designate the lack of equipped item.
     */
    String NO_ITEM = "";
    String NO_POWERMETA = "";
    String NO_NAME = "";
    String NO_ENTITY = "";

    int NO_META = -1;
}

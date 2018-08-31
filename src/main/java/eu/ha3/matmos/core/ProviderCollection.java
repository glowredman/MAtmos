package eu.ha3.matmos.core;

import eu.ha3.matmos.core.event.Event;
import eu.ha3.matmos.core.logic.Condition;
import eu.ha3.matmos.core.logic.Junction;
import eu.ha3.matmos.core.logic.Machine;
import eu.ha3.matmos.core.sheet.SheetCommander;

/*
 * --filenotes-placeholder
 */

public interface ProviderCollection {
    ReferenceTime getReferenceTime();

    SoundRelay getSoundRelay();

    SheetCommander<String> getSheetCommander();

    Provider<Condition> getCondition();

    Provider<Junction> getJunction();

    Provider<Machine> getMachine();

    Provider<Event> getEvent();

    Provider<Dynamic> getDynamic();
}

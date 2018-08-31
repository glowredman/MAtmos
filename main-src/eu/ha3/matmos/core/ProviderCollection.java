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
    public ReferenceTime getReferenceTime();

    public SoundRelay getSoundRelay();

    public SheetCommander<String> getSheetCommander();

    public Provider<Condition> getCondition();

    public Provider<Junction> getJunction();

    public Provider<Machine> getMachine();

    public Provider<Event> getEvent();

    public Provider<Dynamic> getDynamic();
}

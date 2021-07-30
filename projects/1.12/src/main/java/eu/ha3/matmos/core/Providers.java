package eu.ha3.matmos.core;

import eu.ha3.matmos.core.event.Event;
import eu.ha3.matmos.core.logic.Condition;
import eu.ha3.matmos.core.logic.Junction;
import eu.ha3.matmos.core.logic.Machine;
import eu.ha3.matmos.core.sheet.SheetCommander;

public class Providers implements ProviderCollection {
    private final ReferenceTime time;
    private final SoundRelay soundRelay;
    private final SheetCommander<String> commander;
    private final Provider<Condition> conditionProvider;
    private final Provider<Junction> junctionProvider;
    private final Provider<Machine> machineProvider;
    private final Provider<Event> eventProvider;
    private final Provider<Dynamic> dynamicProvider;

    public Providers(ReferenceTime time, SoundRelay soundRelay, SheetCommander<String> commander,
            Provider<Condition> conditionProvider, Provider<Junction> junctionProvider,
            Provider<Machine> machineProvider, Provider<Event> eventProvider, Provider<Dynamic> dynamicProvider) {
        this.time = time;
        this.soundRelay = soundRelay;
        this.commander = commander;

        this.conditionProvider = conditionProvider;
        this.junctionProvider = junctionProvider;
        this.machineProvider = machineProvider;
        this.eventProvider = eventProvider;
        this.dynamicProvider = dynamicProvider;
    }

    @Override
    public ReferenceTime getReferenceTime() {
        return time;
    }

    @Override
    public SoundRelay getSoundRelay() {
        return soundRelay;
    }

    @Override
    public SheetCommander<String> getSheetCommander() {
        return commander;
    }

    @Override
    public Provider<Condition> getCondition() {
        return conditionProvider;
    }

    @Override
    public Provider<Junction> getJunction() {
        return junctionProvider;
    }

    @Override
    public Provider<Machine> getMachine() {
        return machineProvider;
    }

    @Override
    public Provider<Event> getEvent() {
        return eventProvider;
    }

    @Override
    public Provider<Dynamic> getDynamic() {
        return dynamicProvider;
    }

}

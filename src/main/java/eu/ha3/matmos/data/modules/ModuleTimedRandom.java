package eu.ha3.matmos.data.modules;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Random;

import eu.ha3.matmos.core.sheet.DataPackage;

/**
 * @author dags_ <dags@dags.me>
 */

public class ModuleTimedRandom extends ModuleProcessor implements Module {
    private static final Random RANDOM = new Random();

    private final List<TimedValue> timedRandoms = new ArrayList<TimedValue>();

    public ModuleTimedRandom(DataPackage data) {
        super(data, "timed_random");
        timedRandoms.addAll(Arrays.asList(
            new TimedValue(1),
            new TimedValue(2),
            new TimedValue(5),
            new TimedValue(10),
            new TimedValue(15),
            new TimedValue(20),
            new TimedValue(30),
            new TimedValue(1 * 60),
            new TimedValue(2 * 60),
            new TimedValue(5 * 60),
            new TimedValue(10 * 60),
            new TimedValue(20 * 60)
        ));
    }

    @Override
    protected void doProcess() {
        for (TimedValue timedRandom : timedRandoms) {
            timedRandom.process(this);
        }
    }

    private static class TimedValue {
        private final String playlistId;
        private final long period;

        private int activeValue = -1;
        private long endTime;

        public TimedValue(int secs) {
            int minutes = secs / 60;
            int secRemainder = secs % 60;
            
            playlistId = "timed_random"
                    + (secRemainder == 0 ? String.format("_%02dminutes", minutes)
                            : String.format("_%02dm%02ds", minutes, secRemainder));
            
            period = 1000 * secs;
        }

        public void process(ModuleTimedRandom timedRandom) {
            if (activeValue != -1) {
                if (endTime < System.currentTimeMillis()) {
                    activeValue = RANDOM.nextInt(100);
                    endTime = System.currentTimeMillis() + period;
                    timedRandom.setValue(playlistId, activeValue);
                }
            } else {
                activeValue = RANDOM.nextInt(100);
            }
        }
    }
}

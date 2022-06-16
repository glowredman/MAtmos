package eu.ha3.matmos.util;

import eu.ha3.matmos.Matmos;

public class TickProfiler {
    
    private static final boolean ENABLED = Boolean.parseBoolean(System.getProperty("matmos.tickProfiler", "false"));
    
    private static long lastTickStart;
    private static long tickStart;
    
    private static long totalTickTime;
    private static long totalTotalTickTime;
    private static int measuredTicks = 0;
    
    public static void start() {
        if(!ENABLED) return;
        
        lastTickStart = tickStart;
        tickStart = System.nanoTime();
        
        if(lastTickStart != 0) {
            totalTotalTickTime += tickStart - lastTickStart;
            
            int interval = 1000;
            
            if(measuredTicks % interval == 0) {
                long avgTickTime = (totalTickTime / interval);
                long avgTotalTime = (totalTotalTickTime / interval);
                Matmos.LOGGER.info(avgTickTime + " / " + avgTotalTime + " = " + ((double)avgTickTime / (double)avgTotalTime));
                totalTickTime = totalTotalTickTime = 0;
            }
        }
    }
    
    public static void end() {
        if(!ENABLED) return;
        
        long tickEnd = System.nanoTime();
        totalTickTime += tickEnd - tickStart;
        measuredTicks++;
    }
    
}

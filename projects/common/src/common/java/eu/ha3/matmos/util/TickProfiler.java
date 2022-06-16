package eu.ha3.matmos.util;

import eu.ha3.matmos.Matmos;

public class TickProfiler {
    
    private static final boolean ENABLED = Boolean.parseBoolean(System.getProperty("matmos.tickProfiler", "false"));
    
    private static long lastTickStart;
    private static long tickStart;
    private static long sectionStart;
    
    private static long totalSectionTime;
    private static long totalTickTime;
    private static int measuredTicks = 0;
    
    public static void start() {
        start(false);
    }
    
    public static void start(boolean newFrame) {
        if(!ENABLED) return;
        
        if(newFrame) {
            lastTickStart = tickStart;
        }
        sectionStart = System.nanoTime();
        if(newFrame) {
            tickStart = sectionStart;
        }
        
        if(lastTickStart != 0) {
            if(newFrame) {
                totalTickTime += tickStart - lastTickStart;
                
                int interval = 400;
                
                if(measuredTicks++ % interval == 0) {
                    long avgSectionTime = (totalSectionTime / interval);
                    long avgTickTime = (totalTickTime / interval);
                    Matmos.LOGGER.info(avgSectionTime + " / " + avgTickTime + " = " + ((double)avgSectionTime / (double)avgTickTime));
                    totalSectionTime = totalTickTime = 0;
                }
            }
        }
    }
    
    public static void end() {
        if(!ENABLED) return;
        
        long sectionEnd = System.nanoTime();
        totalSectionTime += sectionEnd - sectionStart;
    }
    
}

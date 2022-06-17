package eu.ha3.matmos.util;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import eu.ha3.matmos.Matmos;

public class TickProfiler {
    
    private static final boolean ENABLED = Boolean.parseBoolean(System.getProperty("matmos.tickProfiler", "false"));
    
    private static long firstFrameStart;
    private static long framesMeasured;
    
    private static long lastTickStart;
    private static long tickStart;
    private static long sectionStart;
    
    private static long totalSectionTime;
    private static List<Long> sectionTimes = new ArrayList<>();
    private static List<Long> sortedSectionTimes = new ArrayList<>();
    
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
            
            if(firstFrameStart == 0) {
                firstFrameStart = sectionStart;
            }
        }
        
        if(lastTickStart != 0) {
            if(newFrame) {
                sectionTimes.add(totalSectionTime);
                totalSectionTime = 0;
                
                int interval = 400;
                int tickTime = 50_000_000;
                
                if(framesMeasured++ % interval == 0) {
                    sortedSectionTimes.clear();
                    sortedSectionTimes.addAll(sectionTimes);
                    Collections.sort(sortedSectionTimes);
                    
                    long avg50 = 0;
                    int counted50 = 0;
                    for(int i = (int)(sectionTimes.size() * 0.5f); i < sectionTimes.size(); i++) {
                        avg50 += sectionTimes.get(i);
                        counted50++;
                    }
                    
                    
                    long median = sortedSectionTimes.get(sortedSectionTimes.size() / 2);
                    Matmos.LOGGER.info("uptime: " + (sectionStart - firstFrameStart) / 1000000000.0 + "s"
                            + " median: " + ((double)median / (double)tickTime)
                            + " avg50%: " + (((double)avg50 / ((double)counted50) / (double)tickTime))
                        );
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

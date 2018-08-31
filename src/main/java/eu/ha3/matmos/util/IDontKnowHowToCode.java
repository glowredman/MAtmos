package eu.ha3.matmos.util;

import eu.ha3.matmos.MAtLog;
import eu.ha3.mc.quick.chat.Chatter;
import net.minecraft.util.text.TextFormatting;

import java.util.HashSet;
import java.util.Set;

/*
 * --filenotes-placeholder
 */

public class IDontKnowHowToCode {
    private static Set<Integer> crash = new HashSet<Integer>();
    private static Set<Integer> warning = new HashSet<Integer>();

    public static void warnOnce(String message) {
        if (warning.contains(message.hashCode())) return;
        warning.add(message.hashCode());

        MAtLog.warning(message);
    }

    public static void whoops__printExceptionToChat(Chatter chatter, Exception e, Object caller) {
        whoops__printExceptionToChat(chatter, e, caller.getClass().getName().hashCode());
    }

    /**
     * Call this to print an error to the player's chat. The crash token is meant to prevent the
     * exceptions from a single source to print multiple times.
     * 
     * @param chatter
     * @param e
     * @param crashToken
     */
    public static void whoops__printExceptionToChat(Chatter chatter, Exception e, int crashToken) {
        if (crash.contains(crashToken)) return;
        crash.add(crashToken);

        chatter.printChat(TextFormatting.RED, "MAtmos is crashing: ", TextFormatting.WHITE, e.getClass().getName(), ": ", e.getCause());

        int i = 0;
        for (StackTraceElement x : e.getStackTrace()) {
            if (i <= 5 || x.toString().contains("MAt") || x.toString().contains("eu.ha3.matmos.")) {
                chatter.printChat(TextFormatting.WHITE, x.toString());
            }
            i++;
        }

        chatter.printChat(TextFormatting.RED, "Please report this issue :(");
    }
}

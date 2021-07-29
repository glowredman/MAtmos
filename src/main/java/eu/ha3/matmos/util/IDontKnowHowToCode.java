package eu.ha3.matmos.util;

import java.util.HashSet;
import java.util.Set;

import eu.ha3.matmos.ConfigManager;
import eu.ha3.matmos.Matmos;
import eu.ha3.mc.quick.chat.Chatter;
import net.minecraft.util.EnumChatFormatting;

public class IDontKnowHowToCode {
    private static Set<Integer> crash = new HashSet<>();
    private static Set<Integer> exceptionClass = new HashSet<>();
    private static Set<Integer> warning = new HashSet<>();

    public static void warnOnce(String message) {
        if (warning.contains(message.hashCode())) {
            return;
        }
        warning.add(message.hashCode());

        Matmos.LOGGER.warn(message);
    }

    public static void whoops__printExceptionToChat(Chatter chatter, Exception e, Object caller) {
        whoops__printExceptionToChat(chatter, e, caller.getClass().getName().hashCode());
    }

    /**
     * Call this to print an error to the player's chat. The crash token is meant to
     * prevent the exceptions from a single source to print multiple times.
     *
     * @param chatter
     * @param e
     * @param crashToken
     */
    public static void whoops__printExceptionToChat(Chatter chatter, Exception e, int crashToken) {
    	boolean printToChat = ConfigManager.getConfig().getBoolean("log.printcrashestochat");
    	
    	int traceToken = e.getClass().getName().hashCode();
    	if(exceptionClass.contains(traceToken)) {
    		printToChat = false;
    	}
    	exceptionClass.add(traceToken);
    	
        if (crash.contains(crashToken)) {
            return;
        }
        crash.add(crashToken);

        e.printStackTrace(System.out);
        
        if(printToChat) {
	        chatter.printChat(EnumChatFormatting.RED, "MAtmos is crashing: ", EnumChatFormatting.WHITE,
	                e.getClass().getName(), ": ", e.getCause());
        } else {
        	Matmos.LOGGER.error("MAtmos is crashing: " + e.getClass().getName() + ": " + e.getCause());
        }
        
        if (printToChat && e.getStackTrace().length > 0) {
    		chatter.printChat(EnumChatFormatting.WHITE, e.getStackTrace()[0]);
        }

        if(printToChat) {
        	chatter.printChat(EnumChatFormatting.RED, "See the log for full information.");
        	chatter.printChat(EnumChatFormatting.RED, "Please report this issue :(");
        } else {
        	Matmos.LOGGER.error("Please report this issue :(");
        }
    }
}

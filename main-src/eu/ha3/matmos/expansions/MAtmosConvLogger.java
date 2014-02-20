package eu.ha3.matmos.expansions;

/* x-placeholder */

public class MAtmosConvLogger
{
	final private static String modName = "MAtmos";
	
	public final static int SEVERE = 3;
	public final static int WARNING = 2;
	public final static int INFO = 1;
	public final static int FINE = 0;
	
	private static int refinedness = 1;
	
	public static void setRefinedness(int refinedLevel)
	{
		refinedness = refinedLevel;
	}
	
	public static void fine(String message)
	{
		print(message, "FINE", FINE);
	}
	
	public static void info(String message)
	{
		print(message, "INFO", INFO);
	}
	
	public static void warning(String message)
	{
		printErr(message, "WARNING", WARNING);
	}
	
	public static void severe(String message)
	{
		printErr(message, "SEVERE", SEVERE);
	}
	
	private static void print(String message, String type, int refinedLevel)
	{
		if (refinedLevel >= refinedness)
		{
			System.out.println("(" + modName + ": " + type + ") " + message);
		}
	}
	
	private static void printErr(String message, String type, int refinedLevel)
	{
		if (refinedLevel >= refinedness)
		{
			System.err.println("(" + modName + ": " + type + ") " + message);
		}
	}
}

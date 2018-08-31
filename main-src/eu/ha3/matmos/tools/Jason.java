package eu.ha3.matmos.tools;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

/*
--filenotes-placeholder
*/

public final class Jason {
	private static final Gson ugly = new GsonBuilder().disableHtmlEscaping().create();
	private static final Gson pretty = new GsonBuilder().setPrettyPrinting().disableHtmlEscaping().create();
	
	public static String toJson(Object blob) {
		return ugly.toJson(blob);
	}
	
	public static String toJsonPretty(Object blob) {
		return pretty.toJson(blob);
	}
}

package eu.ha3.matmos.dealias;

import java.util.Map.Entry;

import eu.ha3.matmos.ConfigManager;
import eu.ha3.matmos.Matmos;

public class AliasEntry implements Entry<String, String>{
    private String key;
    private String value;
    private String path;
    private boolean showWarnings;
    private int lineno;
    
    public AliasEntry(String key, String value, String path, int lineno, boolean showWarnings) {
        this.key = key;
        this.value = value;
        this.path = path;
        this.lineno = lineno;
        this.showWarnings = showWarnings;
    }
    
    public AliasEntry(String key, String path, int lineno, String value) {
        this(key, value, path, lineno, true);
    }

    @Override
    public String getKey() {
        return key;
    }

    @Override
    public String getValue() {
        return value;
    }

    @Override
    public String setValue(String value) {
        String oldValue = this.value;
        this.value = value;
        return oldValue;
    }
    
    public boolean doesShowWarnings() {
        return showWarnings;
    }
    
    public void warn(String msg) {
        if(showWarnings || ConfigManager.getConfig().getBoolean("debug.verbosealiasparsing")) {
            Matmos.LOGGER.warn(path + ":" + lineno + ": " + msg);
        }
    }
    
    public String getPath() {
        return path;
    }
    
    public int getLineNumber() {
        return lineno;
    }
}
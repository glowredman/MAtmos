package eu.ha3.matmos.util;

import com.google.gson.GsonBuilder;
import eu.ha3.matmos.MAtmos;
import eu.ha3.matmos.engine.DataManager;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * @author dags_ <dags@dags.me>
 */

public class Dump
{
    private List<String> stringKeys = new ArrayList<String>();
    private List<String> boolKeys = new ArrayList<String>();
    private List<String> numKeys = new ArrayList<String>();

    public Dump(DataManager dataManager)
    {
        stringKeys.addAll(dataManager.stringData.keySet());
        boolKeys.addAll(dataManager.boolData.keySet());
        numKeys.addAll(dataManager.numData.keySet());
        Collections.sort(stringKeys);
        Collections.sort(boolKeys);
        Collections.sort(numKeys);
    }

    public void dumpToJson(File parentDir)
    {
        try
        {
            if (!parentDir.exists() && parentDir.mkdirs())
                MAtmos.log("Creating dir: " + parentDir);
            File outFile = new File(parentDir, "MAtmos-Dump.json");
            if (outFile.exists() && outFile.createNewFile())
                MAtmos.log("Creating file: " + outFile);
            String json = new GsonBuilder().setPrettyPrinting().disableHtmlEscaping().create().toJson(this);
            FileWriter writer = new FileWriter(outFile);
            writer.write(json);
            writer.flush();
            writer.close();
        }
        catch (IOException e)
        {
            e.printStackTrace();
        }
    }
}

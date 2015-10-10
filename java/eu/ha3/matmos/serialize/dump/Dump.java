package eu.ha3.matmos.serialize.dump;

import com.google.gson.GsonBuilder;
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
                System.out.println("Creating dir: " + parentDir);
            File outFile = new File(parentDir, "MAtmos-Dump.json");
            if (outFile.exists() && outFile.createNewFile())
                System.out.println("Creating file: " + outFile);
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

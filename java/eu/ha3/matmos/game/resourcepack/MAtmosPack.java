package eu.ha3.matmos.game.resourcepack;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import eu.ha3.matmos.MAtmos;
import eu.ha3.matmos.serialize.EventAdapter;
import eu.ha3.matmos.serialize.EventSerialize;

import java.io.*;
import java.util.*;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;

/**
 * @author dags_ <dags@dags.me>
 */

public class MAtmosPack
{
    private static final Gson gson = new GsonBuilder().setPrettyPrinting().registerTypeAdapter(EventSerialize.class, new EventAdapter<EventSerialize>()).create();
    private static final String separator = new StringBuilder().append("\\").append(File.separatorChar).toString();
    private static final String folderSounds = File.separator + "assets" + File.separator + "matmos" + File.separator + "sounds" + File.separator;
    private static final String folderData = File.separator + "assets" + File.separator + "matmos" + File.separator + "data" + File.separator;
    private static final String zipSounds = "assets/matmos/sounds/";
    private static final String zipData = "assets/matmos/data/";

    private final File packFile;
    private final Set<String> sounds = new HashSet<String>();
    private final Set<String> streams = new HashSet<String>();
    private final List<EventSerialize> data = new ArrayList<EventSerialize>();

    public MAtmosPack(File in)
    {
        packFile = in;
        read();
    }

    public Set<String> getSounds()
    {
        return sounds;
    }

    public Set<String> getStreams()
    {
        return streams;
    }

    public List<EventSerialize> getData()
    {
        return data;
    }

    private void addData(Reader reader) throws IOException
    {
        if (data.size() == 0)
        {
            MAtmos.log("Loading processor data for SoundPack: " + packFile.getName());
        }
        EventSerialize e = gson.fromJson(reader, EventSerialize.class);
        reader.close();
        data.add(e);
    }

    private void addSound(String sound)
    {
        if (sound.contains("stream_"))
        {
            if (streams.size() == 0)
            {
                MAtmos.log("Loading stream resources for SoundPack: " + packFile.getName());
            }
            streams.add(sound);
        }
        else
        {
            if (sounds.size() == 0)
            {
                MAtmos.log("Loading sound resources for SoundPack: " + packFile.getName());
            }
            sounds.add(sound);
        }
    }

    private void read()
    {
        if (packFile.isDirectory())
        {
            try
            {
                readDir(packFile);
            }
            catch (IOException e)
            {
                e.printStackTrace();
            }
        }
        else
        {
            try
            {
                readZip();
            }
            catch (IOException e)
            {
                e.printStackTrace();
            }
        }
    }

    private void readZip() throws IOException
    {
        ZipFile z = new ZipFile(packFile);
        Enumeration<? extends ZipEntry> entries = z.entries();
        while (entries.hasMoreElements())
        {
            ZipEntry e = entries.nextElement();
            if (e.isDirectory())
                continue;
            if (e.getName().startsWith(zipData) && e.getName().endsWith(".json"))
            {
                addData(new InputStreamReader(z.getInputStream(e)));
            }
            else if (e.getName().startsWith(zipSounds) && e.getName().endsWith(".ogg"))
            {
                addSound(e.getName().substring(zipSounds.length()));
            }
        }
        z.close();
    }

    private void readDir(File file) throws IOException
    {
        if (file.isDirectory())
        {
            File[] files = file.listFiles();
            if (files != null)
            {
                for (File f : files)
                {
                    readDir(f);
                }
            }
        }
        else
        {
            if (file.getPath().startsWith(packFile.getPath() + folderData) && file.getName().endsWith(".json"))
            {
                addData(new FileReader(file));
            }
            else if (file.getPath().startsWith(packFile.getPath() + folderSounds) && file.getName().endsWith(".ogg"))
            {
                String path = file.getPath();
                path = path.substring((packFile.getPath() + folderSounds).length(), path.length() - 4).replaceAll(separator, ".");
                addSound(path);
            }
        }
    }
}

package eu.ha3.matmos.engine;

import com.google.common.base.Optional;
import com.mumfrey.liteloader.client.overlays.ISoundHandler;
import eu.ha3.matmos.MAtmos;
import eu.ha3.matmos.game.resourcepack.MAtSoundList;
import eu.ha3.matmos.game.resourcepack.MAtmosPack;
import eu.ha3.matmos.serialize.Expansion;
import net.minecraft.client.Minecraft;
import net.minecraft.client.resources.IReloadableResourceManager;
import net.minecraft.client.resources.IResourceManager;
import net.minecraft.client.resources.IResourceManagerReloadListener;
import net.minecraft.client.resources.ResourcePackRepository;
import net.minecraft.util.ResourceLocation;

import java.io.File;
import java.util.HashMap;
import java.util.Map;

/**
 * @author dags_ <dags@dags.me>
 */

public class PackManager implements IResourceManagerReloadListener
{
    private static Map<String, ResourceLocation> streamLocations = new HashMap<String, ResourceLocation>();
    private static Map<String, ResourceLocation> soundLocations = new HashMap<String, ResourceLocation>();

    private final MAtmos mAtmos;

    public PackManager(MAtmos m)
    {
        mAtmos = m;
    }

    public void registerReloadable()
    {
        IReloadableResourceManager manager = (IReloadableResourceManager) Minecraft.getMinecraft().getResourceManager();
        manager.registerReloadListener(this);
    }

    @Override
    public void onResourceManagerReload(IResourceManager resourceManager)
    {
        reloadSoundPacks();
    }

    public void reloadSoundPacks()
    {
        mAtmos.reload();
        MAtmos.log("Reloading SoundPacks...");
        streamLocations.clear();
        soundLocations.clear();
        for (Object o : Minecraft.getMinecraft().getResourcePackRepository().getRepositoryEntries())
        {
            ResourcePackRepository.Entry entry = (ResourcePackRepository.Entry) o;
            if (entry.getResourcePack().getResourceDomains().contains("matmos"))
            {
                File dir = Minecraft.getMinecraft().getResourcePackRepository().getDirResourcepacks();
                File[] files = dir.listFiles();
                files = files == null ? new File[0] : files;
                for (File file : files)
                {
                    if (file.getName().equals(entry.getResourcePackName()) || !file.isDirectory() && file.getName().equals(entry.getResourcePackName() + ".zip"))
                    {
                        MAtmosPack pack = new MAtmosPack(file);
                        ISoundHandler soundHandler = (ISoundHandler) Minecraft.getMinecraft().getSoundHandler();
                        for (String s : pack.getSounds())
                        {
                            MAtSoundList list = new MAtSoundList(s);
                            ResourceLocation location = new ResourceLocation("matmos", s);
                            soundHandler.addSound(location, list);
                            soundLocations.put(s.toLowerCase(), location);
                        }
                        for (String s : pack.getStreams())
                        {
                            MAtSoundList list = new MAtSoundList(s);
                            ResourceLocation location = new ResourceLocation("matmos", s);
                            soundHandler.addSound(location, list);
                            streamLocations.put(s.toLowerCase(), location);
                        }
                        for (Expansion e : pack.getData())
                        {
                            e.register(mAtmos);
                        }
                    }
                }
            }
        }
    }

    public static Optional<ResourceLocation> getStream(String key)
    {
        return streamLocations.containsKey(key) ? Optional.of(streamLocations.get(key)) : Optional.<ResourceLocation>absent();
    }
    public static Optional<ResourceLocation> getSound(String key)
    {
        return soundLocations.containsKey(key) ? Optional.of(soundLocations.get(key)) : Optional.<ResourceLocation>absent();
    }
}

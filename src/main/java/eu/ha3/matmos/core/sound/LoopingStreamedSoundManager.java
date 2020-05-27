package eu.ha3.matmos.core.sound;

import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import eu.ha3.matmos.Matmos;
import eu.ha3.matmos.core.ducks.ISoundHandler;
import eu.ha3.matmos.core.ducks.ISoundManager;
import eu.ha3.matmos.core.mixin.ISoundManagerStaticAccessor;
import eu.ha3.mc.haddon.supporting.SupportsTickEvents;
import io.netty.util.internal.ThreadLocalRandom;
import net.minecraft.client.Minecraft;
import net.minecraft.client.audio.ISound;
import net.minecraft.client.audio.ITickableSound;
import net.minecraft.client.audio.Sound;
import net.minecraft.client.audio.SoundEventAccessor;
import net.minecraft.client.audio.SoundHandler;
import net.minecraft.client.audio.SoundManager;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.math.MathHelper;
import paulscode.sound.SoundSystem;

/**
 * <p>
 * A sound manager designed to work well with looping streamed sounds.
 * 
 * <p>
 * Minecraft's sound manager behaves oddly with looping streamed sounds. It is
 * possible for sounds that should be playing to stop playing, and for sounds
 * that should not be playing to keep playing. (For a possible explanation, see
 * below.)
 * 
 * <p>
 * This sound manager was created to work correctly with looping streamed
 * sounds. It's a modified version of Minecraft's SoundManager class.
 * 
 * <h1>What's the deal with Minecraft's sound manager?</h1>
 * 
 * <p>
 * Since Minecraft does not have any streamed looping sounds, it's not a
 * surprise if they don't work correctly. I have identified the following
 * possible issues. Note that they may not be entirely correct, I haven't been
 * verified these theories extensively.
 * 
 * <li>A polling method is used to check when sounds aren't playing. Since the
 * sound manager is on another thread, this can cause a desync to happen when
 * the game freezes. When this happens, the sound manager (correctly) reports
 * that the sounds aren't being played at the time... which causes Minecraft to
 * remove them from the list of playing sounds. Only, after the freeze ends, the
 * sounds continue playing, and since Minecraft doesn't acknowledge their
 * existence anymore, they never get stopped.
 * 
 * <li>The opposite problem can also happen (sometimes sounds stop playing), but
 * I'm not sure why.
 * 
 * <li>A HashBiMap is used to store the inverse map of playing sounds. According
 * to CreativeMD's research, it doesn't work correctly -- sometimes a desync can
 * occur between the map and its inverse, which can cause all sorts of mayhem.
 *
 */

public class LoopingStreamedSoundManager implements SupportsTickEvents {

    private Set<ResourceLocation> UNABLE_TO_PLAY = new HashSet<>();

    /**
     * Identifiers of all currently playing sounds. Type: HashBiMap<String, ISound>
     */
    private final Map<String, ISound> playingSounds = new HashMap<>();
    /**
     * Inverse map of currently playing sounds, automatically mirroring changes in
     * original map
     */
    private final Map<ISound, String> invPlayingSounds = new HashMap<>();
    /** A subset of playingSounds, this contains only ITickableSounds */
    private final List<ITickableSound> tickableSounds = new ArrayList<>();

    public void onTick() {
        for (ITickableSound itickablesound : this.tickableSounds) {
            itickablesound.update();

            if (itickablesound.isDonePlaying()) {
                this.stopSound(itickablesound);
            } else {
                String s = this.invPlayingSounds.get(itickablesound);
                getSoundSystem().setVolume(s, this.getClampedVolume(itickablesound));
                getSoundSystem().setPitch(s, this.getClampedPitch(itickablesound));
                getSoundSystem().setPosition(s, itickablesound.getXPosF(), itickablesound.getYPosF(),
                        itickablesound.getZPosF());
            }
        }
    }

    // logic copied from SoundManager.playSound
    public void playSound(ISound p_sound) {
        if (isSoundManagerLoaded()) {
            if (p_sound == null)
                return;

            SoundEventAccessor soundeventaccessor = p_sound.createAccessor(getSoundHandler());
            ResourceLocation resourcelocation = p_sound.getSoundLocation();

            if (soundeventaccessor == null) {
                if (UNABLE_TO_PLAY.add(resourcelocation)) {
                    Matmos.LOGGER.warn("Unable to play unknown soundEvent: {}", (Object) resourcelocation);
                }
            } else {
                if (getSoundSystem().getMasterVolume() <= 0.0F) {
                    Matmos.LOGGER.debug("Skipped playing soundEvent: {}, master volume was zero",
                            (Object) resourcelocation);
                } else {
                    Sound sound = p_sound.getSound();

                    if (sound == SoundHandler.MISSING_SOUND) {
                        if (UNABLE_TO_PLAY.add(resourcelocation)) {
                            Matmos.LOGGER.warn("Unable to play empty soundEvent: {}", (Object) resourcelocation);
                        }
                    } else {
                        float f3 = p_sound.getVolume();
                        float f = 16.0F;

                        if (f3 > 1.0F) {
                            f *= f3;
                        }

                        SoundCategory soundcategory = p_sound.getCategory();
                        float f1 = this.getClampedVolume(p_sound);
                        float f2 = this.getClampedPitch(p_sound);

                        if (f1 == 0.0F) {
                            Matmos.LOGGER.debug("Skipped playing sound {}, volume was zero.",
                                    (Object) sound.getSoundLocation());
                        } else {
                            boolean flag = p_sound.canRepeat() && p_sound.getRepeatDelay() == 0;
                            String s = MathHelper.getRandomUUID(ThreadLocalRandom.current()).toString();
                            ResourceLocation resourcelocation1 = sound.getSoundAsOggLocation();

                            if (sound.isStreaming()) {
                                getSoundSystem().newStreamingSource(false, s, getURLForSoundResource(resourcelocation1),
                                        resourcelocation1.toString(), flag, p_sound.getXPosF(), p_sound.getYPosF(),
                                        p_sound.getZPosF(), p_sound.getAttenuationType().getTypeInt(), f);
                            } else {
                                getSoundSystem().newSource(false, s, getURLForSoundResource(resourcelocation1),
                                        resourcelocation1.toString(), flag, p_sound.getXPosF(), p_sound.getYPosF(),
                                        p_sound.getZPosF(), p_sound.getAttenuationType().getTypeInt(), f);
                            }

                            Matmos.LOGGER.debug("Playing sound {} for event {} as channel {}", sound.getSoundLocation(),
                                    resourcelocation, s);
                            getSoundSystem().setPitch(s, f2);
                            getSoundSystem().setVolume(s, f1);
                            getSoundSystem().play(s);
                            this.playingSounds.put(s, p_sound);
                            this.invPlayingSounds.put(p_sound, s);

                            if (p_sound instanceof ITickableSound) {
                                this.tickableSounds.add((ITickableSound) p_sound);
                            }
                        }
                    }
                }
            }
        }
    }

    public void stopSound(ISound sound) {
        if (isSoundManagerLoaded()) {
            String s = this.invPlayingSounds.get(sound);

            if (s != null) {
                getSoundSystem().stop(s);

                playingSounds.remove(s);
                invPlayingSounds.remove(sound);
            }
        }
    }

    private SoundManager getSoundManager() {
        return ((ISoundHandler) getSoundHandler()).getSoundManager();
    }

    private SoundHandler getSoundHandler() {
        return Minecraft.getMinecraft().getSoundHandler();
    }

    private SoundSystem getSoundSystem() {
        return ((ISoundManager) getSoundManager()).getSoundSystem();
    }

    private boolean isSoundManagerLoaded() {
        return ((ISoundManager) getSoundManager()).isLoaded();
    }

    private float getClampedPitch(ISound soundIn) {
        return MathHelper.clamp(soundIn.getPitch(), 0.5F, 2.0F);
    }

    private float getClampedVolume(ISound soundIn) {
        return MathHelper.clamp(soundIn.getVolume() * this.getCategoryVolume(soundIn.getCategory()), 0.0F, 1.0F);
    }

    private float getCategoryVolume(SoundCategory category) {
        return ((ISoundManager) getSoundManager()).invokeGetVolume(category);
    }

    private URL getURLForSoundResource(final ResourceLocation p_148612_0_) {
        return ISoundManagerStaticAccessor.invokeGetURLForSoundResource(p_148612_0_);
    }
}

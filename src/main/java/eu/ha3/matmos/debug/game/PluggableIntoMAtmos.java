package eu.ha3.matmos.debug.game;

import java.io.File;

import com.google.common.base.Optional;

import eu.ha3.matmos.MAtMod;
import eu.ha3.matmos.core.expansion.Expansion;
import eu.ha3.matmos.debug.PluggableIntoMinecraft;
import eu.ha3.matmos.debug.expansions.FolderResourcePackEditableEDU;
import net.minecraft.util.text.TextFormatting;

/*
 * --filenotes-placeholder
 */

public class PluggableIntoMAtmos implements PluggableIntoMinecraft {
    private MAtMod mod;
    private String expansionName;

    private boolean isReadOnly;

    private File file;
    private File workingDirectory;

    public PluggableIntoMAtmos(MAtMod mod, Expansion expansion) {
        this.mod = mod;
        if (expansion.obtainDebugUnit() instanceof FolderResourcePackEditableEDU) {
            file = ((FolderResourcePackEditableEDU)expansion.obtainDebugUnit()).obtainExpansionFile();
            workingDirectory = ((FolderResourcePackEditableEDU)expansion.obtainDebugUnit()).obtainExpansionFolder();
            isReadOnly = false;
        } else {
            isReadOnly = true;
        }

        expansionName = expansion.getName();
    }

    @Override
    public void pushJason(String jason) {
        final String jasonString = jason;
        mod.queueForNextTick(() -> {
            Optional<Expansion> opt = mod.getExpansionEffort(expansionName);

            if (opt.isPresent()) {
                Expansion expansion = opt.get();

                mod.getChatter().printChat(
                        TextFormatting.AQUA,
                        "Reloading from editor state: " + expansion.getName() + " " + getTimestamp());
                expansion.pushDebugJasonAndRefreshKnowledge(jasonString);
            }
        });
    }

    @Override
    public void reloadFromDisk() {
        mod.queueForNextTick(() -> {
            Optional<Expansion> opt = mod.getExpansionEffort(expansionName);

            if (opt.isPresent()) {
                Expansion expansion = opt.get();

                mod.getChatter().printChat(
                        TextFormatting.BLUE,
                        "Reloading from disk: ", expansion.getName() + " " + getTimestamp());
                expansion.refreshKnowledge();
            }
        });
    }

    protected String getTimestamp() {
        return TextFormatting.BLACK + "(" + System.currentTimeMillis() + ")";
    }

    @Override
    public boolean isReadOnly() {
        return isReadOnly;
    }

    @Override
    public File getWorkingDirectoryIfAvailable() {
        return workingDirectory;
    }

    @Override
    public File getFileIfAvailable() {
        return file;
    }

    @Override
    public void onEditorClosed() {
    }
}

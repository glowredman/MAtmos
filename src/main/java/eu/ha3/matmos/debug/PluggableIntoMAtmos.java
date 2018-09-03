package eu.ha3.matmos.debug;

import java.io.File;
import java.util.Optional;

import eu.ha3.matmos.MAtMod;
import eu.ha3.matmos.core.expansion.Expansion;
import eu.ha3.matmos.core.expansion.FolderExpansionDebugUnit;
import net.minecraft.util.text.TextFormatting;

public class PluggableIntoMAtmos implements Pluggable {
    private MAtMod mod;
    private String expansionName;

    private boolean isReadOnly;

    private File file;
    private File workingDirectory;

    public PluggableIntoMAtmos(MAtMod mod, Expansion expansion) {
        this.mod = mod;
        if (expansion.obtainDebugUnit() instanceof FolderExpansionDebugUnit) {
            file = ((FolderExpansionDebugUnit)expansion.obtainDebugUnit()).getExpansionFile();
            workingDirectory = ((FolderExpansionDebugUnit)expansion.obtainDebugUnit()).getExpansionFolder();
            isReadOnly = false;
        } else {
            isReadOnly = true;
        }

        expansionName = expansion.getName();
    }

    @Override
    public void pushJson(String json) {
        mod.queueForNextTick(() -> {
            Optional<Expansion> opt = mod.getExpansionEffort(expansionName);

            if (opt.isPresent()) {
                Expansion expansion = opt.get();

                mod.getChatter().printChat(TextFormatting.AQUA, "Reloading from editor state: " + expansion.getName() + " " + getTimestamp());
                expansion.pushDebugJsonAndRefreshKnowledge(json);
            }
        });
    }

    @Override
    public void reloadFromDisk() {
        mod.queueForNextTick(() -> {
            mod.getExpansionEffort(expansionName).ifPresent(expansion -> {
                mod.getChatter().printChat(TextFormatting.BLUE, "Reloading from disk: ", expansion.getName() + " " + getTimestamp());
                expansion.refreshKnowledge();
            });
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

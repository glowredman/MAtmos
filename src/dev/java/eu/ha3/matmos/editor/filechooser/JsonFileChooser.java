package eu.ha3.matmos.editor.filechooser;

import javax.swing.*;
import javax.swing.filechooser.FileNameExtensionFilter;
import java.io.File;

/** Courtesy of
  * http://geek.starbean.net/?p=275
  *
  * << http://stackoverflow.com/questions/8581215/jfilechooser-and-checking-for-overwrite
  */
@SuppressWarnings("serial")
public class JsonFileChooser extends JFileChooser {
    protected String dotlessExtension;

    public JsonFileChooser(File dir) {
        this(dir, "json");
    }

    private JsonFileChooser(File dir, String dotlessExtension) {
        super(dir);
        this.dotlessExtension = dotlessExtension;
        addChoosableFileFilter(new FileNameExtensionFilter(String.format("%1$s Jason file (*.%1$s)", dotlessExtension), dotlessExtension));
    }
}

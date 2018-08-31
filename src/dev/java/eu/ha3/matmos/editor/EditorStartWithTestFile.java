package eu.ha3.matmos.editor;

import java.io.File;

public class EditorStartWithTestFile {
    private static final String PATH = "L:\\MCMod-C\\jars\\resourcepacks\\mat_breeze\\assets\\matmos\\expansions\\nature.json";
    
    public static void main(String[] args) {
        EditorMaster master = new EditorMaster(null, new File(PATH));
        master.run();
    }
}

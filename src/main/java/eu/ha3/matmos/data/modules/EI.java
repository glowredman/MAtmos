package eu.ha3.matmos.data.modules;

/*
 * --filenotes-placeholder
 */

public class EI {
    private String name;
    private String desc;

    public EI(String name, String description) {
        this.name = name;
        desc = description;
    }

    public String getName() {
        return name;
    }

    public String getDescription() {
        return desc;
    }
}

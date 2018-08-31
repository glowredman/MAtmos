package eu.ha3.matmos.core;

import java.util.List;

public class Possibilities extends Component implements PossibilityList {
    private final List<String> list;

    public Possibilities(String name, List<String> list) {
        super(name);

        this.list = list;
    }

    @Override
    public boolean listHas(String element) {
        return list.contains(element);
    }
}

package eu.ha3.matmos.game.scanner;

import java.util.Map;

/**
 * @author dags_ <dags@dags.me>
 */

public interface Scanner
{
    public void scan();

    public String displayId();

    public Map<String, Counter> getCounts();

    public int getCount(String lookUp);
}

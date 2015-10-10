package eu.ha3.matmos.game.gatherer;

import eu.ha3.matmos.engine.DataManager;

/**
 * @author dags_ <dags@dags.me>
 */

public interface DataGatherer
{
    public DataGatherer register(DataManager manager);

    public void update();
}

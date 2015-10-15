package eu.ha3.matmos.game.gatherer;

import eu.ha3.matmos.engine.DataRegistry;

/**
 * @author dags_ <dags@dags.me>
 */

public interface DataGatherer
{
    public DataGatherer register(DataRegistry manager);

    public void update();
}

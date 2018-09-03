package eu.ha3.matmos.core.expansion.agents;

import eu.ha3.matmos.core.Knowledge;
import eu.ha3.matmos.core.expansion.ExpansionIdentity;

public interface LoadingAgent {
    /**
     * Load whatever serialized data into the knowledge. It should be assumed the knowledge is empty
     * before this is called, however it may not necessarily be the case.
     *
     * @param  identity
     * @param  knowledge
     * @return
     */
    boolean load(ExpansionIdentity identity, Knowledge knowledge);
}

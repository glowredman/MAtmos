package eu.ha3.matmos.core.expansion.agents;

import eu.ha3.matmos.core.Knowledge;
import eu.ha3.matmos.core.expansion.ExpansionIdentity;
import eu.ha3.matmos.serialisation.JsonExpansions_EngineDeserializer;

/*
 * --filenotes-placeholder
 */

public class RawJasonLoadingAgent implements LoadingAgent {
    private final String jasonString;

    public RawJasonLoadingAgent(String jasonString) {
        this.jasonString = jasonString;
    }

    @Override
    public boolean load(ExpansionIdentity identity, Knowledge knowledge) {
        try {
            return new JsonExpansions_EngineDeserializer().loadJson(this.jasonString, identity, knowledge);
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

}

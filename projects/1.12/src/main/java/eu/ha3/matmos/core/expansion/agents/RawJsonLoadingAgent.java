package eu.ha3.matmos.core.expansion.agents;

import eu.ha3.matmos.core.Knowledge;
import eu.ha3.matmos.core.expansion.ExpansionIdentity;
import eu.ha3.matmos.serialisation.JsonExpansions_EngineDeserializer;

public class RawJsonLoadingAgent implements LoadingAgent {
    private final String jasonString;

    public RawJsonLoadingAgent(String jasonString) {
        this.jasonString = jasonString;
    }

    @Override
    public Exception load(ExpansionIdentity identity, Knowledge knowledge) {
        try {
            return new JsonExpansions_EngineDeserializer().loadJson(jasonString, identity, knowledge);
        } catch (Exception e) {
            e.printStackTrace();
            return e;
        }
    }

}

package eu.ha3.matmos.core.expansion.agents;

import java.util.Scanner;

import eu.ha3.matmos.core.Knowledge;
import eu.ha3.matmos.core.expansion.ExpansionIdentity;
import eu.ha3.matmos.serialisation.JsonExpansions_EngineDeserializer;

public class JsonLoadingAgent implements LoadingAgent {
    @Override
    public boolean load(ExpansionIdentity identity, Knowledge knowledge) {
        try (Scanner sc = new Scanner(identity.getPack().getInputStream(identity.getLocation()))) {
            return new JsonExpansions_EngineDeserializer().loadJson(sc.useDelimiter("\\Z").next(), identity, knowledge);
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

}

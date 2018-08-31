package eu.ha3.matmos.core.expansion.agents;

import java.util.Scanner;

import eu.ha3.matmos.core.Knowledge;
import eu.ha3.matmos.core.expansion.ExpansionIdentity;
import eu.ha3.matmos.serialisation.JsonExpansions_EngineDeserializer;

/*
 * --filenotes-placeholder
 */

public class JasonLoadingAgent implements LoadingAgent {
    @Override
    public boolean load(ExpansionIdentity identity, Knowledge knowledge) {
        //Solly edit - resource leak
        Scanner sc = null;
        try {
            sc = new Scanner(identity.getPack().getInputStream(identity.getLocation()));
            String jasonString = sc.useDelimiter("\\Z").next();
            return new JsonExpansions_EngineDeserializer().loadJson(jasonString, identity, knowledge);
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        } finally {
            if (sc != null) {
                sc.close();
            }
        }
    }

}

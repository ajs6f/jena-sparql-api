package org.aksw.jena_sparql_api.utils;

import java.util.List;

import com.google.common.base.Joiner;
import com.hp.hpl.jena.sparql.core.DatasetDescription;

public class DatasetDescriptionUtils {
    public static String getSingleDefaultGraphUri(DatasetDescription datasetDescription) {
        List<String> dgus = datasetDescription.getDefaultGraphURIs();

        String result = datasetDescription != null && dgus.size() == 1
                ? dgus.iterator().next()
                : null
                ;

        return result;
    }


    public static DatasetDescription createDefaultGraph(String defaultGraph) {
        DatasetDescription result = new DatasetDescription();
        result.addDefaultGraphURI(defaultGraph);
        return result;
    }

    public static String toString(DatasetDescription datasetDescription) {
        String result = datasetDescription == null
            ? null
            : "[defaultGraphs = " + Joiner.on(", ").join(datasetDescription.getDefaultGraphURIs()) + "]"
            + "[namedGraphs = " + Joiner.on(", ").join(datasetDescription.getNamedGraphURIs()) + "]";

        return result;
    }
}

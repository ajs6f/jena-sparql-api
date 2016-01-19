package org.aksw.jena_sparql_api.update;

import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import org.aksw.commons.collections.diff.Diff;
import org.aksw.commons.util.strings.StringUtils;
import org.aksw.jena_sparql_api.changeset.ChangeSet;
import org.aksw.jena_sparql_api.changeset.ChangeSetMetadata;
import org.aksw.jena_sparql_api.changeset.ChangeSetUtils;
import org.aksw.jena_sparql_api.core.DatasetListener;
import org.aksw.jena_sparql_api.core.QueryExecutionFactory;
import org.aksw.jena_sparql_api.core.SparqlService;
import org.aksw.jena_sparql_api.core.SparqlServiceReference;
import org.aksw.jena_sparql_api.core.UpdateContext;
import org.aksw.jena_sparql_api.core.UpdateExecutionFactory;
import org.aksw.jena_sparql_api.core.utils.UpdateExecutionUtils;

import com.hp.hpl.jena.graph.Node;
import com.hp.hpl.jena.graph.Triple;
import com.hp.hpl.jena.rdf.model.Model;
import com.hp.hpl.jena.rdf.model.ModelFactory;
import com.hp.hpl.jena.sparql.core.DatasetDescription;
import com.hp.hpl.jena.sparql.core.Quad;

public class DatasetListenerTrack
    implements DatasetListener
{
    //private Sink<Diff<? extends Iterable<Quad>>> sink;
    //private UpdateExecutionFactory uef;
    private SparqlService trackerService;
    private ChangeSetMetadata changesetMetadata;
    private String prefix;

    public DatasetListenerTrack(SparqlService trackerService) {
        this(trackerService, new ChangeSetMetadata());
    }

    public DatasetListenerTrack(SparqlService trackerService, ChangeSetMetadata changesetMetadata) {
        super();
        this.trackerService = trackerService;
        this.changesetMetadata = changesetMetadata;
    }

    @Override
    public void onPreModify(Diff<Set<Quad>> quadDiff, UpdateContext updateContext) {

        SparqlService sparqlService = updateContext.getSparqlService();
        DatasetDescription datasetDescription = sparqlService.getDatasetDescription();
        SparqlServiceReference ssr = new SparqlServiceReference(sparqlService.getServiceUri(), datasetDescription);


        Map<Node, Diff<Set<Triple>>> tripleDiff = DiffQuadUtils.partitionQuads(quadDiff);

        // Note the prefix must include service and target graph information
        String serviceUri = ssr.getServiceURL();

        prefix = "http://example.org/changeset-";

        for(Entry<Node, Diff<Set<Triple>>> entry : tripleDiff.entrySet()) {
            Node g = entry.getKey();
            String graphUri = g.getURI();

            if(Quad.defaultGraphIRI.equals(g) || Quad.defaultGraphNodeGenerated.equals(g)) {
//                g = DatasetDescriptionUtils.getSingleDefaultGraph(datasetDescription);
//                if(g == null) {
//                    throw new RuntimeException("A single default graph was expected, got: " + DatasetDescriptionUtils.toString(datasetDescription));
//                }
                throw new RuntimeException("Should not happen - default graph uris are assumed to have been replaced");
            }

            String p = prefix + StringUtils.urlEncode(serviceUri) + "-" + StringUtils.urlEncode(graphUri) + "-";

            Diff<Set<Triple>> diff = entry.getValue();

            QueryExecutionFactory qef = trackerService.getQueryExecutionFactory();
            Map<Node, ChangeSet> changesets = ChangeSetUtils.createChangeSets(qef, serviceUri, graphUri, changesetMetadata, diff, p);

            for(ChangeSet changeset : changesets.values()) {
                Model model = ModelFactory.createDefaultModel();
                ChangeSetUtils.write(model, changeset);

                ChangeSetUtils.enrichWithSource(model, g, ssr);
                UpdateExecutionFactory uef = trackerService.getUpdateExecutionFactory();
                UpdateExecutionUtils.executeInsert(uef, model);
            }
        }

        //sink.send(diff);
    }
}
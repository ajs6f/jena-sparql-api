package org.aksw.jena_sparql_api.batch.functions;

import java.util.Map.Entry;

import com.google.common.base.Function;
import com.hp.hpl.jena.graph.Node;
import com.hp.hpl.jena.rdf.model.Model;
import com.hp.hpl.jena.rdf.model.RDFNode;
import com.hp.hpl.jena.sparql.util.ModelUtils;

public class F_NodeToResource<T extends RDFNode>
    implements Function<Entry<? extends Node, ? extends Model>, T>
{

    @Override
    public T apply(Entry<? extends Node, ?extends Model> entry) {
        Node node = entry.getKey();
        Model model = entry.getValue();

        RDFNode tmp = ModelUtils.convertGraphNodeToRDFNode(node, model);
        @SuppressWarnings("unchecked")
        T result = (T)tmp;
        return result;
    }

    public static <T extends RDFNode> F_NodeToResource<T> create() {
        F_NodeToResource<T> result = new F_NodeToResource<T>();
        return result;
    }
}
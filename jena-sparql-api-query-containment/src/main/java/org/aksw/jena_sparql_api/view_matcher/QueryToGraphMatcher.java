package org.aksw.jena_sparql_api.view_matcher;

import java.util.Map;
import java.util.stream.Stream;

import org.aksw.jena_sparql_api.algebra.utils.AlgebraUtils;
import org.aksw.jena_sparql_api.algebra.utils.QuadFilterPatternCanonical;
import org.aksw.jena_sparql_api.jgrapht.transform.QueryToGraph;
import org.aksw.jena_sparql_api.jgrapht.wrapper.LabeledEdge;
import org.aksw.jena_sparql_api.jgrapht.wrapper.LabeledEdgeImpl;
import org.apache.jena.graph.Node;
import org.apache.jena.query.Query;
import org.apache.jena.sparql.core.Var;
import org.jgrapht.DirectedGraph;
import org.jgrapht.graph.SimpleDirectedGraph;

public class QueryToGraphMatcher {


    public static void toGraph(DirectedGraph<Node, LabeledEdge<Node, Node>> graph, QuadFilterPatternCanonical qfpc) {
        QueryToGraph.quadsToGraph(graph, qfpc.getQuads());
        QueryToGraph.equalExprsToGraph(graph, qfpc.getFilterDnf());
    }

    public static void toGraph(DirectedGraph<Node, LabeledEdge<Node, Node>> graph, Query query) {
        QuadFilterPatternCanonical qfpc = AlgebraUtils.fromQuery(query);
        toGraph(graph, qfpc);
    }

    public static DirectedGraph<Node, LabeledEdge<Node, Node>> toGraph(QuadFilterPatternCanonical qfpc) {
        //EdgeFactory <Node, LabeledEdge<Node, Node>> edgeFactory = (v, e) -> new LabeledEdgeImpl<>(v, e, null);
        DirectedGraph<Node, LabeledEdge<Node, Node>> graph = new SimpleDirectedGraph<>((v, e) -> new LabeledEdgeImpl<>(v, e, null));

        toGraph(graph, qfpc);

        return graph;
    }
    public static Stream<Map<Var, Var>> match(QuadFilterPatternCanonical view, QuadFilterPatternCanonical user) {
        DirectedGraph<Node, LabeledEdge<Node, Node>> a = toGraph(view);
        DirectedGraph<Node, LabeledEdge<Node, Node>> b = toGraph(user);

        Stream<Map<Var, Var>> result = QueryToGraph.match(a, b);
        return result;
    }

    /**
     * Convenience method for testing.
     * Only works for queries whose element is a BGP + filters.
     *
     * @param view
     * @param user
     * @return
     */
    public static boolean tryMatch(Query view, Query user) {
        DirectedGraph<Node, LabeledEdge<Node, Node>> a = new SimpleDirectedGraph<>((v, e) -> new LabeledEdgeImpl<>(v, e, null));
        DirectedGraph<Node, LabeledEdge<Node, Node>> b = new SimpleDirectedGraph<>((v, e) -> new LabeledEdgeImpl<>(v, e, null));

        toGraph(a, view);
        toGraph(b, user);


//		visualizeGraph(a);
//		visualizeGraph(b);
//		try(Scanner s = new Scanner(System.in)) { s.nextLine(); }



        Stream<Map<Var, Var>> tmp = QueryToGraph.match(a, b);
        tmp = tmp.peek(x -> System.out.println("Solution: " + x));
        boolean result = tmp.count() > 0;
        return result;
    }

}

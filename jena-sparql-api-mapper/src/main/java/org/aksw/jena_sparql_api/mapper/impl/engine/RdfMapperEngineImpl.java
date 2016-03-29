package org.aksw.jena_sparql_api.mapper.impl.engine;

import java.util.Collections;
import java.util.Map;
import java.util.Set;

import org.aksw.commons.collections.diff.Diff;
import org.aksw.jena_sparql_api.core.QueryExecutionFactory;
import org.aksw.jena_sparql_api.core.SparqlService;
import org.aksw.jena_sparql_api.core.UpdateExecutionFactory;
import org.aksw.jena_sparql_api.core.utils.UpdateDiffUtils;
import org.aksw.jena_sparql_api.core.utils.UpdateExecutionUtils;
import org.aksw.jena_sparql_api.lookup.LookupService;
import org.aksw.jena_sparql_api.lookup.LookupServiceUtils;
import org.aksw.jena_sparql_api.mapper.MappedConcept;
import org.aksw.jena_sparql_api.mapper.context.RdfEmitterContext;
import org.aksw.jena_sparql_api.mapper.context.RdfEmitterContextFrontier;
import org.aksw.jena_sparql_api.mapper.context.RdfPersistenceContext;
import org.aksw.jena_sparql_api.mapper.context.RdfPersistenceContextFrontier;
import org.aksw.jena_sparql_api.mapper.context.TypedNode;
import org.aksw.jena_sparql_api.mapper.impl.type.RdfClass;
import org.aksw.jena_sparql_api.mapper.impl.type.RdfTypeFactoryImpl;
import org.aksw.jena_sparql_api.mapper.model.RdfPopulatorProperty;
import org.aksw.jena_sparql_api.mapper.model.RdfType;
import org.aksw.jena_sparql_api.mapper.model.RdfTypeFactory;
import org.aksw.jena_sparql_api.mapper.proxy.MethodInterceptorRdf;
import org.aksw.jena_sparql_api.shape.ResourceShape;
import org.aksw.jena_sparql_api.shape.ResourceShapeBuilder;
import org.aksw.jena_sparql_api.util.frontier.Frontier;
import org.aksw.jena_sparql_api.util.frontier.FrontierImpl;
import org.aksw.jena_sparql_api.utils.DatasetDescriptionUtils;
import org.apache.jena.atlas.lib.Sink;
import org.apache.jena.riot.lang.SinkTriplesToGraph;
import org.springframework.beans.BeanUtils;

import org.apache.jena.graph.Graph;
import org.apache.jena.graph.GraphUtil;
import org.apache.jena.graph.Node;
import org.apache.jena.graph.NodeFactory;
import org.apache.jena.graph.Triple;
import org.apache.jena.sparql.core.DatasetDescription;
import org.apache.jena.sparql.core.DatasetGraph;
import org.apache.jena.sparql.core.DatasetGraphFactory;
import org.apache.jena.sparql.core.Prologue;
import org.apache.jena.sparql.core.Quad;
import org.apache.jena.sparql.graph.GraphFactory;

public class RdfMapperEngineImpl
    implements RdfMapperEngine, PersistenceContextSupplier
{
    protected Prologue prologue;
    //protected QueryExecutionFactory qef;
    protected SparqlService sparqlService;

    protected RdfTypeFactory typeFactory;
    protected RdfPersistenceContext persistenceContext;


    public RdfMapperEngineImpl(SparqlService sparqlService) {
        this(sparqlService, RdfTypeFactoryImpl.createDefault(), new Prologue(), null); //new RdfPopulationContextImpl());
    }

    public RdfMapperEngineImpl(SparqlService sparqlService, RdfTypeFactory typeFactory) {
        this(sparqlService, typeFactory, new Prologue(), null); //new RdfPopulationContextImpl());
    }

    public RdfMapperEngineImpl(SparqlService sparqlService, RdfTypeFactory typeFactory, Prologue prologue) {
        this(sparqlService, typeFactory, prologue, null); //new RdfPopulationContextImpl());
    }

//QueryExecutionFactory qef
    public RdfMapperEngineImpl(SparqlService sparqlService, RdfTypeFactory typeFactory, Prologue prologue, RdfPersistenceContext persistenceContext) {
        super();
        this.sparqlService = sparqlService;
        this.typeFactory = typeFactory;
        this.prologue = prologue;
        this.persistenceContext = persistenceContext != null ? persistenceContext : new RdfPersistenceContextFrontier(new FrontierImpl<TypedNode>());
    }

    @Override
    public RdfPersistenceContext getPersistenceContext() {
        return this.persistenceContext;
    };

    public Prologue getPrologue() {
        return prologue;
    }

//    public static resolvePopulation(QueryExecutionFactory qef) {
//
//    }

    public <T> LookupService<Node, T> getLookupService(Class<T> clazz) {
        return null;
    }

//    public ListService<Concept, Node, DatasetGraph> prepareListService(RdfClass rdfClass, Concept filterConcept) {
//

    @Override
    public <T> T find(Class<T> clazz, Node rootNode) {
        RdfType rootRdfType = typeFactory.forJavaType(clazz);



        //Frontier<TypedNode> frontier = new FrontierImpl<TypedNode>();
        //RdfPersistenceContext persistenceContext = new RdfPersistenceContextFrontier(frontier);

        EntityGraphMap entityGraphMap = persistenceContext.getEntityGraphMap();

        TypedNode first = new TypedNode(rootRdfType, rootNode);

        Frontier<TypedNode> frontier = persistenceContext.getFrontier();
        frontier.add(first);

        while(!frontier.isEmpty()) {
            TypedNode typedNode = frontier.next();

            RdfType rdfType = typedNode.getRdfType();
            Node node = typedNode.getNode();

            ResourceShapeBuilder builder = new ResourceShapeBuilder(prologue);
            rdfType.exposeShape(builder);


            // Fetch the graph
            QueryExecutionFactory qef = sparqlService.getQueryExecutionFactory();

            if(!rdfType.isSimpleType()) {
                ResourceShape shape = builder.getResourceShape();

    //            MappedConcept<DatasetGraph> mc = ResourceShape.createMappedConcept2(shape, null);
    //            LookupService<Node, DatasetGraph> ls = LookupServiceUtils.createLookupService(qef, mc);
    //            Map<Node, DatasetGraph> map = ls.apply(Collections.singleton(node));


                MappedConcept<Graph> mc = ResourceShape.createMappedConcept(shape, null, false);
                LookupService<Node, Graph> ls = LookupServiceUtils.createLookupService(qef, mc);
                Map<Node, Graph> map = ls.apply(Collections.singleton(node));

                //ListService<Concept, Node, Graph> ls = ListServiceUtils.createListServiceMappedConcept(qef, mc, true);

    //            MappedConcept<Graph> mc = ResourceShape.createMappedConcept(shape, null);
    //            ListService<Concept, Node, Graph> ls = ListServiceUtils.createListServiceMappedConcept(qef, mc, true);


                Graph graph = map.get(node);

                if(graph != null) {
                    //DatasetGraph datasetGraph = map.get(node);

                    Object entity = persistenceContext.entityFor(typedNode);
                    entityGraphMap.clearGraph(entity);

                    Graph refs = GraphFactory.createDefaultGraph();
                    Sink<Triple> refSink = new SinkTriplesToGraph(false, refs);
                    rdfType.populateEntity(persistenceContext, entity, graph, refSink);
                    refSink.close();

                    entityGraphMap.putAll(refs, entity);
                }
            }
        }

        @SuppressWarnings("unchecked")
        T result = (T)persistenceContext.getEntity(first);

        return result;
    }


//    public MappedConcept<DatasetGraph> getMappedQuery(ResourceShapeBuilder builder, RdfClass rdfClass) {
//
//        Collection<RdfProperty> rdfProperties = rdfClass.getRdfProperties();
//
//        for(RdfProperty rdfProperty : rdfProperties) {
//            processProperty(builder, rdfProperty);
//        }
//
//        ResourceShape shape = builder.getResourceShape();
//        MappedConcept<DatasetGraph> result = ResourceShape.createMappedConcept2(shape, null);
//        return result;
//    }


    public void processProperty(ResourceShapeBuilder builder, RdfPopulatorProperty rdfProperty) {
        //Relation relation = rdfProperty.getRelation();
        //Node predicate = rdfProperty.get
        //builder.outgoing(relation);

        //rdfProperty.getTargetRdfClass()
    }


    @Override
    public <T> T merge(T tmpEntity) {
        RdfType rootRdfType = typeFactory.forJavaType(tmpEntity.getClass());
        Node rootNode = rootRdfType.getRootNode(tmpEntity);
        Object entity = persistenceContext.entityFor(new TypedNode(rootRdfType, rootNode));

        if(entity != tmpEntity) {
            BeanUtils.copyProperties(tmpEntity, entity);
        }

        @SuppressWarnings("unchecked")
        T result = (T)entity;

        //Node rootNode = persistenceContext.getRootNode(tmpEntity);

        DatasetDescription datasetDescription = sparqlService.getDatasetDescription();

        String gStr = DatasetDescriptionUtils.getSingleDefaultGraphUri(datasetDescription);
        if(gStr == null) {
            throw new RuntimeException("No target graph specified");
        }
        Node g = NodeFactory.createURI(gStr);


        MethodInterceptorRdf interceptor = RdfClass.getMethodInterceptor(entity);

        DatasetGraph oldState = interceptor == null
                ? DatasetGraphFactory.createMem()
                : interceptor.getDatasetGraph()
                ;

                {
                    EntityGraphMap entityGraphMap = persistenceContext.getEntityGraphMap();
                    Graph graph = entityGraphMap.getGraphForEntity(entity);
                    Graph targetGraph = oldState.getGraph(g);
                    if(targetGraph == null) {
                        targetGraph = GraphFactory.createDefaultGraph();
                        oldState.addGraph(g, targetGraph);
                    }

                    if(graph != null) {
                        GraphUtil.addInto(targetGraph, graph);
                    }
                }


        //Class<?> clazz = tmpEntity.getClass();
        //RdfClass rdfClass = RdfClassFactory.createDefault(prologue).create(clazz);
        //RdfClass rdfClass = (RdfClass)typeFactory.forJavaType(clazz);


        DatasetGraph newState = DatasetGraphFactory.createMem();
        Graph outGraph = Quad.defaultGraphIRI.equals(g) ? newState.getDefaultGraph() : newState.getGraph(g);
        //rdfClass.emitTriples(out, entity);
        emitTriples(outGraph, entity);

//        System.out.println("oldState");
//        DatasetGraphUtils.write(System.out, oldState);
//
//        System.out.println("newState");
//        DatasetGraphUtils.write(System.out, newState);

        Diff<Set<Quad>> diff = UpdateDiffUtils.computeDelta(newState, oldState);
//        System.out.println("diff: " + diff);
        UpdateExecutionFactory uef = sparqlService.getUpdateExecutionFactory();
        UpdateExecutionUtils.executeUpdate(uef, diff);

        return result;
    }


    @Override
    public RdfTypeFactory getRdfTypeFactory() {
        return typeFactory;
    }


    @Override
    public void emitTriples(Graph outGraph, Object entity) {
        Frontier<Object> frontier = FrontierImpl.createIdentityFrontier();
        RdfEmitterContext emitterContext = new RdfEmitterContextFrontier(frontier);

        frontier.add(entity);

        while(!frontier.isEmpty()) {
            Object current = frontier.next();

            Class<?> clazz = current.getClass();
            RdfType rdfType = typeFactory.forJavaType(clazz);

            // TODO We now need to know which additional
            // (property) values also need to be emitted
            rdfType.emitTriples(persistenceContext, emitterContext, outGraph, entity);
        }
    }
}

package org.aksw.jena_sparql_api.mapper.model;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Set;
import java.util.function.Consumer;
import java.util.stream.Collectors;

import org.aksw.jena_sparql_api.mapper.context.RdfEmitterContext;
import org.aksw.jena_sparql_api.mapper.context.RdfPersistenceContext;
import org.aksw.jena_sparql_api.shape.ResourceShapeBuilder;
import org.apache.jena.graph.Graph;
import org.apache.jena.graph.Node;
import org.apache.jena.graph.Triple;

/**
 * RdfPopulators map bean properties to triples and vice versa.
 * In general RdfPopulators can implement arbitrary mappings between
 * bean properties and RDF triples, i.e.
 * beanProperties:triples -- 1:n, m:n, m:1
 *
 * Note that an RDF populator only populates the *immediate* properties.
 * Scheduling Lazy / Eager fetching is NOT up to the populator.
 *
 * @author raven
 *
 */
public interface RdfPopulator {

    /**
     * It may be useful giving property mappers names.
     * For mappers that map to a single java property, the mapper name can be that of the property.
     * But if a mapper writes to multiple java properties, a custom name could be given.
     * 
     * A default implementation could return a string from joining the ordered affected
     * property names alphabetically.
     *  
     */
    default String getName() {
        List<String> tmp = new ArrayList<>(getPropertyNames());
        if(tmp.isEmpty()) {
            throw new RuntimeException(RdfPopulator.class.getSimpleName() + " instance did not declare any affected property names");
        }
        Collections.sort(tmp);
        String result = tmp.stream().collect(Collectors.joining("-"));
        return result;
    }
    
    /**
     * Return the set of entity properties which are affected by this populator.
     * For instance, an RDF wktLiteral may map to two properties 'lat' and 'long'
     *
     * @return
     */
    Set<String> getPropertyNames();


    /**
     * Expose SPARQL patterns that identify the set of triples
     * that are needed to populate the *immediate* values of the affected entity properties.
     *
     *
     * @param shapeBuilder
     */
    void exposeShape(ResourceShapeBuilder shapeBuilder);


    /**
     * Emit triples from the object
     *
     * @param outGraph
     * @param bean
     * @param subject
     */
    void emitTriples(RdfEmitterContext emitterContext, Object entity, Node subject, Graph shapeGraph, Consumer<Triple> outSink);

    /**
     * Set entity property values from a given subject's RDF graph
     *
     * @param graph
     * @param subject
     * @param outSink the sub-set of triples in inGraph used to populate the entity
     * @return
     */
    void populateEntity(RdfPersistenceContext persistenceContext, Object entity, Graph inGraph, Node subject, Consumer<Triple> outSink);
}

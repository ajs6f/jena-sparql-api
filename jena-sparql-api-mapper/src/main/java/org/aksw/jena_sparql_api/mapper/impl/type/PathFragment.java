package org.aksw.jena_sparql_api.mapper.impl.type;

import org.aksw.jena_sparql_api.concepts.Relation;
import org.aksw.jena_sparql_api.mapper.model.RdfType;


/**
 * A path fragment exposes means that can be used to perform nested path resolutions.
 * These means have the following priorities:
 * - nextResolver
 * - rdfType
 * - javaClass: In this case, a resolver has to ask the RDF type mapping model to yield an appropriate rdfType for the java class.
 *
 * @author raven
 *
 */
public class PathFragment
//	implements PathResolver
{
    // TODO: We could add attributes to keep track of by which (RdfType, attributeName) this path fragement was obtained.

    protected Relation relation;

    // An RDF type can yield one of the following means (in descending priority) to report further resolution:
    protected PathResolver nextResolver;
    protected RdfType rdfType;
    protected Class<?> javaClass;

    public PathFragment(Relation relation, Class<?> javaClass, RdfType rdfType, PathResolver nextResolver) {
        super();
        this.relation = relation;
        this.javaClass = javaClass;
        this.rdfType = rdfType;
        this.nextResolver = nextResolver;
    }


    /**
     * Create a copy of the path fragment with the relation replaced.
     * Useful when variables needed to be renamed.
     *
     * @param newRelation
     * @return
     */
    public PathFragment cloneWithNewRelation(Relation newRelation) {
        return new PathFragment(newRelation, javaClass, rdfType, nextResolver);
    }

    //@Override
    public RdfType getRdfType() {
        return rdfType;
    }

    //@Override
    public Relation getRelation() {
        return relation;
    }

    //@Override
    public Class<?> getJavaClass() {
        return javaClass;
    }

    //@Override
    public PathResolver getNextResolver() {
        return nextResolver;
    }
}

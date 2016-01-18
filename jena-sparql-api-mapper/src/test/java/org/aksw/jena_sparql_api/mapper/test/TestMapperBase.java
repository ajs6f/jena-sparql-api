package org.aksw.jena_sparql_api.mapper.test;

import javax.persistence.EntityManager;

import org.aksw.jena_sparql_api.core.SparqlService;
import org.aksw.jena_sparql_api.mapper.impl.engine.RdfMapperEngineImpl;
import org.aksw.jena_sparql_api.mapper.jpa.core.EntityManagerJena;
import org.aksw.jena_sparql_api.stmt.SparqlQueryParserImpl;
import org.aksw.jena_sparql_api.update.FluentSparqlService;
import org.aksw.jena_sparql_api.utils.DatasetDescriptionUtils;
import org.aksw.jena_sparql_api.utils.transform.F_QueryTransformDatesetDescription;
import org.aksw.jena_sparql_api.utils.transform.F_QueryTransformLimit;
import org.junit.Before;

import com.hp.hpl.jena.query.Dataset;
import com.hp.hpl.jena.query.DatasetFactory;
import com.hp.hpl.jena.sparql.core.DatasetDescription;

public class TestMapperBase {
    protected String graphName;
    protected Dataset ds;
    protected SparqlService sparqlService;
    protected EntityManager entityManager;

    @Before
    public void beforeTest() {
        //String graphName = "http://ex.org/graph/";
        graphName = "http://ex.org/graph/";
        ds = DatasetFactory.createMem();
        DatasetDescription dd = DatasetDescriptionUtils.createDefaultGraph(graphName);
        sparqlService = FluentSparqlService.from(ds)
                .config()
                    .configQuery()
                        .withParser(SparqlQueryParserImpl.create())
                    .end()
                    .withDatasetDescription(dd, graphName)
                    .configQuery()
                        .withQueryTransform(F_QueryTransformDatesetDescription.fn)
                        .withQueryTransform(F_QueryTransformLimit.create(1000))
                    .end()
                .end()
                .create();

        entityManager = new EntityManagerJena(new RdfMapperEngineImpl(sparqlService));
    }
}
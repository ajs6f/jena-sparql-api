package org.aksw.jena_sparql_api.core.utils;

import org.aksw.jena_sparql_api.concepts.Concept;
import org.aksw.jena_sparql_api.concepts.ConceptUtils;
import org.aksw.jena_sparql_api.core.QueryExecutionFactory;
import org.aksw.jena_sparql_api.lookup.CountInfo;
import org.aksw.jena_sparql_api.utils.ResultSetUtils;

import com.hp.hpl.jena.query.Query;
import com.hp.hpl.jena.query.QueryExecution;
import com.hp.hpl.jena.query.ResultSet;
import com.hp.hpl.jena.sparql.core.Var;

public class ServiceUtils {
    public static Integer fetchInteger(QueryExecutionFactory qef, Query query, Var v) {
        QueryExecution qe = qef.createQueryExecution(query);
        Integer result = fetchInteger(qe, v);
        return result;
    }

    /**
     * Fetches the first column of the first row of a result set and parses it as int.
     *
     */
    public static Integer fetchInteger(QueryExecution qe, Var v) {
        ResultSet rs = qe.execSelect();
        Integer result = ResultSetUtils.resultSetToInt(rs, v);

        return result;
    }


    // NOTE: If there is a rowLimit, we can't determine whether there are more items or not
    public static CountInfo fetchCountConcept(QueryExecutionFactory sparqlService, Concept concept, Long itemLimit, Long rowLimit) {

        Var outputVar = ConceptUtils.freshVar(concept);

        long xitemLimit = itemLimit == null ? null : itemLimit + 1;
        long xrowLimit = rowLimit == null ? null : rowLimit + 1;

        Query countQuery = ConceptUtils.createQueryCount(concept, outputVar, xitemLimit, xrowLimit);

        //var qe = sparqlService.createQueryExecution(countQuery);

        Integer count = ServiceUtils.fetchInteger(sparqlService, countQuery, outputVar);
        boolean hasMoreItems = rowLimit != null
            ? null
            : (itemLimit != null ? count > itemLimit : false)
            ;

        Long c = hasMoreItems ? itemLimit : count;
        CountInfo result = new CountInfo(c, hasMoreItems, itemLimit);
        return result;
    }
}

package org.aksw.jena_sparql_api.utils.transform;

import org.aksw.jena_sparql_api.utils.QueryUtils;

import com.google.common.base.Function;
import com.hp.hpl.jena.query.Query;

public class F_QueryTransformLimit
	implements Function<Query, Query>
{
	protected Long limit;

    public F_QueryTransformLimit(long limit) {
		super();
		this.limit = limit;
	}


    @Override
    public Query apply(Query query) {
    	Query result = QueryUtils.applyLimit(query, limit, true);
    	return result;
	}

    public static F_QueryTransformLimit create(int limit) {
    	F_QueryTransformLimit result = new F_QueryTransformLimit(limit);
    	return result;
    }
}
package org.aksw.jena_sparql_api.web.servlets;

import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.core.Context;

import org.aksw.jena_sparql_api.core.SparqlService;
import org.aksw.jena_sparql_api.core.SparqlServiceFactory;
import org.aksw.jena_sparql_api.stmt.SparqlStmtUpdate;
import org.aksw.jena_sparql_api.web.utils.AuthenticatorUtils;
import org.aksw.jena_sparql_api.web.utils.DatasetDescriptionRequestUtils;
import org.apache.jena.atlas.web.auth.HttpAuthenticator;
import org.springframework.util.Assert;
import org.springframework.web.bind.ServletRequestBindingException;
import org.springframework.web.bind.ServletRequestUtils;

import com.hp.hpl.jena.query.Query;
import com.hp.hpl.jena.query.QueryExecution;
import com.hp.hpl.jena.sparql.core.DatasetDescription;
import com.hp.hpl.jena.update.UpdateProcessor;
import com.hp.hpl.jena.update.UpdateRequest;


/**
 * Proxy implementation based on a Sparql service object
 * @author raven
 *
 */
public abstract class ServletSparqlServiceBase
    extends SparqlEndpointBase
{
//    private static final Logger logger = LoggerFactory.getLogger(ServletSparqlServiceBase.class);

    protected @Context HttpServletRequest req;

    //protected abstract SparqlStmtParser sparqlStmtParser;

    protected abstract SparqlServiceFactory getSparqlServiceFactory();

    /**
     * Important: If no SPARQL service is specified, null is returned.
     * This means, that it is up to the SparqlServiceFactory to
     * - use a default service
     * - reject invalid service requests
     *
     *
     * @return
     */
    protected String getServiceUri() {
        String result;
        try {
            result = ServletRequestUtils.getStringParameter(req, "service-uri");
        } catch (ServletRequestBindingException e) {
            throw new RuntimeException(e);
        }
        return result;
    }

    protected HttpAuthenticator getAuthenticator() {
        HttpAuthenticator result = AuthenticatorUtils.parseAuthenticator(req);
        return result;
    }

    protected DatasetDescription getDatasetDescription() {
        DatasetDescription result = DatasetDescriptionRequestUtils.extractDatasetDescriptionAny(req);
        return result;
    }

    @Override
    public QueryExecution createQueryExecution(Query query) {
        SparqlServiceFactory ssf = getSparqlServiceFactory();
        Assert.notNull(ssf, "Got null for SparqlServiceFactory");

        String serviceUri = getServiceUri();

        DatasetDescription datasetDescription = getDatasetDescription();
        HttpAuthenticator authenticator = getAuthenticator();

        SparqlService ss = ssf.createSparqlService(serviceUri, datasetDescription, authenticator);
        QueryExecution result = ss.getQueryExecutionFactory().createQueryExecution(query);
        return result;
    }

    @Override
    public UpdateProcessor createUpdateProcessor(SparqlStmtUpdate stmt) {
        SparqlServiceFactory ssf = getSparqlServiceFactory();
        Assert.notNull(ssf, "Got null for SparqlServiceFactory");
        String serviceUri = getServiceUri();

        DatasetDescription datasetDescription = getDatasetDescription();
        HttpAuthenticator authenticator = getAuthenticator();

        SparqlService ss = ssf.createSparqlService(serviceUri, datasetDescription, authenticator);
        UpdateProcessor result;
        if(stmt.isParsed()) {
            UpdateRequest updateRequest = stmt.getUpdateRequest();
            result = ss.getUpdateExecutionFactory().createUpdateProcessor(updateRequest);
        } else {
            String updateRequestStr = stmt.getOriginalString();
            result = ss.getUpdateExecutionFactory().createUpdateProcessor(updateRequestStr);
        }
        return result;
    }

}
package org.aksw.jena_sparql_api.shape.api;

import org.apache.jena.sparql.core.Var;

/**
 * Name an element with a variable
 *
 * @author raven
 *
 */
public class ElementAlias
    extends Element1
{
    protected Var var;

    public ElementAlias(Element subElement, Var var) {
        super(subElement);
        this.var = var;
    }

    public Var getVar() {
        return var;
    }
}

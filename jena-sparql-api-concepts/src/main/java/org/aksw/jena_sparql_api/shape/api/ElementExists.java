package org.aksw.jena_sparql_api.shape.api;

import org.apache.jena.sparql.path.Path;

public class ElementExists
    extends ElementPathConstraint
{

    public ElementExists(Path path, Element filler) {
        super(path, filler);
    }
}

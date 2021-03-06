package org.aksw.jena_sparql_api.sparql.ext.json;

import com.google.gson.Gson;
import com.google.gson.JsonElement;
import org.apache.jena.datatypes.BaseDatatype;
import org.apache.jena.datatypes.DatatypeFormatException;
import org.apache.jena.vocabulary.XSD;

public class RDFDatatypeJson
    extends BaseDatatype
{
    private Gson gson;

    public RDFDatatypeJson() {
        this(XSD.getURI() + "json");
    }

    public RDFDatatypeJson(String uri) {
        this(uri, new Gson());
    }

    public RDFDatatypeJson(String uri, Gson gson) {
        super(uri);
        this.gson = gson;
    }

    @Override
    public Class<?> getJavaClass() {
        return Gson.class;
    }

    /**
     * Convert a value of this datatype out
     * to lexical form.
     */
    @Override
    public String unparse(Object value) {
        String result = gson.toJson(value);
        return result;
    }

    /**
     * Parse a lexical form of this datatype to a value
     * @throws DatatypeFormatException if the lexical form is not legal
     */
    @Override
    public JsonElement parse(String lexicalForm) throws DatatypeFormatException {
    	//Object result = gson.fromJson(lexicalForm, Object.class);
    	JsonElement result = gson.fromJson(lexicalForm, JsonElement.class);
        return result;
    }

}
package org.aksw.jena_sparql_api.sparql.ext.json;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.gson.Gson;
import com.google.gson.JsonElement;
import org.apache.jena.datatypes.RDFDatatype;
import org.apache.jena.datatypes.TypeMapper;
import org.apache.jena.graph.Node;
import org.apache.jena.graph.NodeFactory;
import org.apache.jena.sparql.expr.NodeValue;
import org.apache.jena.sparql.function.FunctionBase2;
import com.jayway.jsonpath.JsonPath;

/**
 * jsonLiteral jsonp(jsonLiteral, queryString)
 *
 * @author raven
 *
 */
public class E_JsonPath
    extends FunctionBase2
{

    private static final Logger logger = LoggerFactory.getLogger(E_JsonPath.class);

    private Gson gson;

    public E_JsonPath() {
        this(new Gson());
    }

    public E_JsonPath(Gson gson) {
        super();
        this.gson = gson;
    }

    public static JsonElement asJson(NodeValue nv) {
        Node asNode = nv.asNode();
        JsonElement result;
        if(nv instanceof NodeValueJson) {
            result = ((NodeValueJson)nv).getJson();
        } else if(asNode.getLiteralDatatype() instanceof RDFDatatypeJson) {
            result = (JsonElement)asNode.getLiteralValue();
//        } else if(nv.isString()) {
//            String str = nv.getString();
//            result = gson.fromJson(str, Object.class);
        } else {
            result = null;
        }

        return result;
    }

    public static NodeValue createPrimitiveNodeValue(Object o) {
        RDFDatatype dtype = TypeMapper.getInstance().getTypeByValue(o);
        Node node = NodeFactory.createUncachedLiteral(o, dtype);
        NodeValue result = NodeValue.makeNode(node);
        return result;
    }

    public static NodeValue jsonToNodeValue(Object o, Gson gson) {
        boolean isPrimitive = o instanceof Boolean || o instanceof Number || o instanceof String;

        NodeValue result;
        if(o == null) {
            result = NodeValue.nvNothing;
        } else if(isPrimitive) {
            result = createPrimitiveNodeValue(o);
        } else if(o instanceof JsonElement) {
            JsonElement e = (JsonElement)o;
            result = jsonToNodeValue(e, gson);
        } else {
            // Write the object to json and re-read it as a json-element
            String str = gson.toJson(o);
            JsonElement e = gson.fromJson(str, JsonElement.class);
            result = jsonToNodeValue(e, gson);
        }
//    	else {
//    		throw new RuntimeException("Unknown type: " + o);
//    	}

        return result;
    }

    public static NodeValue jsonToNodeValue(JsonElement e, Gson gson) {
        NodeValue result;
        if(e == null) {
            result = NodeValue.nvNothing;
        } else if(e.isJsonPrimitive()) {
            //JsonPrimitive p = e.getAsJsonPrimitive();
            Object o = gson.fromJson(e, Object.class); //JsonTransformerUtils.toJavaObject(p);

            if(o != null) {
                result = createPrimitiveNodeValue(o);
            } else {
                throw new RuntimeException("Datatype not supported " + e);
            }
        } else if(e.isJsonObject() || e.isJsonArray()) { // arrays are json objects / array e.isJsonArray() ||
            result = new NodeValueJson(e);
        } else {
            throw new RuntimeException("Datatype not supported " + e);
        }

        return result;
    }

//    public static NodeValue jsonToNodeValue(Object o) {
//    	NodeValue result;
//    	if(o == null) {
//    		result = NodeValue.nvNothing;
//    	} else if(o instanceof Number) {
//        	RDFDatatype dtype = TypeMapper.getInstance().getTypeByValue(o);
//        	Node node = NodeFactory.createUncachedLiteral(o, dtype);
//        	result = NodeValue.makeNode(node);
//        } else if(o instanceof String) {
//        	result = NodeValue.makeString((String)o);
//        } else {
//            result = new NodeValueJson(o);
//        }
//
//        return result;
//    }

    @Override
    public NodeValue exec(NodeValue nv, NodeValue query) {

        JsonElement json = asJson(nv);

        NodeValue result;
        if(query.isString() && json != null) {
            Object tmp = gson.fromJson(json, Object.class); //JsonTransformerObject.toJava.apply(json);
            String queryStr = query.getString();

            try {
                // If parsing the JSON fails, we return nothing, yet we log an error
                Object o = JsonPath.read(tmp, queryStr);
                result = jsonToNodeValue(o, gson);
            } catch(Exception e) {
                logger.warn(e.getLocalizedMessage());
                result = NodeValue.nvNothing;
            }

        } else {
            result = NodeValue.nvNothing;
        }

        return result;
    }
}
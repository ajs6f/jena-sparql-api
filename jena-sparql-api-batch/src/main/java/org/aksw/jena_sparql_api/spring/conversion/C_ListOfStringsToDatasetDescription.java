package org.aksw.jena_sparql_api.spring.conversion;

import java.util.List;

import org.springframework.core.convert.converter.Converter;

import org.apache.jena.sparql.core.DatasetDescription;


@AutoRegistered
public class C_ListOfStringsToDatasetDescription
	implements Converter<List<String>, DatasetDescription>
{
    public DatasetDescription convert(List<String> defaultGraphUris) {
    	DatasetDescription result = new DatasetDescription();
    	result.addAllDefaultGraphURIs(defaultGraphUris);
    	return result;
    }
}
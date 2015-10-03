package org.aksw.jena_sparql_api.batch.config;

import java.util.List;

import org.aksw.jena_sparql_api.batch.cli.main.MainBatchWorkflow;
import org.aksw.jena_sparql_api.core.SparqlServiceFactory;
import org.aksw.jena_sparql_api.core.SparqlServiceFactoryHttp;
import org.aksw.jena_sparql_api.spring.conversion.ConverterRegistryPostProcessor;
import org.aksw.jena_sparql_api.stmt.SparqlExprParser;
import org.aksw.jena_sparql_api.stmt.SparqlExprParserImpl;
import org.aksw.jena_sparql_api.stmt.SparqlQueryParser;
import org.aksw.jena_sparql_api.stmt.SparqlQueryParserImpl;
import org.aksw.jena_sparql_api.stmt.SparqlUpdateParser;
import org.aksw.jena_sparql_api.stmt.SparqlUpdateParserImpl;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.AutowiredAnnotationBeanPostProcessor;
import org.springframework.beans.factory.config.AutowireCapableBeanFactory;
import org.springframework.beans.factory.config.BeanFactoryPostProcessor;
import org.springframework.beans.factory.config.BeanPostProcessor;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.convert.converter.Converter;
import org.springframework.core.convert.support.ConfigurableConversionService;
import org.springframework.core.convert.support.DefaultConversionService;

import com.hp.hpl.jena.shared.PrefixMapping;
import com.hp.hpl.jena.shared.impl.PrefixMappingImpl;

@Configuration
@ComponentScan({"org.aksw.jena_sparql_api.spring.conversion"})
public class ConfigServicesCore
    implements ApplicationContextAware
{
    private ApplicationContext applicationContext;

    @Override
    public void setApplicationContext(ApplicationContext applicationContext)
            throws BeansException {
        this.applicationContext = applicationContext;
    }

    @Bean
    public ConfigurableConversionService conversionService() {
        ConfigurableConversionService result = new DefaultConversionService();
        return result;
    }

    @Bean
    public PrefixMapping defaultPrefixMapping() {
//        PrefixMapping result = new PrefixMappingImpl();
//
//        result.getNsPrefixMap().putAll(MainBatchWorkflow.getDefaultPrefixMapping().getNsPrefixMap());
        PrefixMapping result = MainBatchWorkflow.getDefaultPrefixMapping();
        return result;
    }

    @Bean
    public SparqlQueryParser defaultSparqlQueryParser() {
        SparqlQueryParser result = SparqlQueryParserImpl.create();
        return result;
    }

    @Bean
    public SparqlUpdateParser defaultSparqlUpdateParser() {
        SparqlUpdateParser result = SparqlUpdateParserImpl.create();
        return result;
    }

    @Bean
    @Autowired
    public SparqlExprParser defaultSparqlExprParser(PrefixMapping pm) {
        SparqlExprParser result = new SparqlExprParserImpl(pm);
        return result;
    }

    @Bean
    public SparqlServiceFactory defaultSparqlServiceFactory() {
        SparqlServiceFactory result = new SparqlServiceFactoryHttp();
        return result;
    }

    @Bean
    @Autowired
    public List<Converter<?, ?>> defaultConverters(List<Converter<?, ?>> converters) {
        AutowireCapableBeanFactory beanFactory = applicationContext.getAutowireCapableBeanFactory();
        for(Object item : converters) {
            beanFactory.autowireBean(item);
        }
        return converters;
    }

    @Bean
    public BeanFactoryPostProcessor beanFactoryPostProcessor() {
        BeanFactoryPostProcessor result = new ConverterRegistryPostProcessor();
        return result;
    }

    @Bean
    public BeanPostProcessor beanFactoryPostProcessorAutowire() {
        BeanPostProcessor result = new AutowiredAnnotationBeanPostProcessor();
        return result;
    }

}
